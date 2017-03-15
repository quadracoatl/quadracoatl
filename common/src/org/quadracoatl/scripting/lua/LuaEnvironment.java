/*
 * Copyright 2016, Robert 'Bobby' Zenz
 * 
 * This file is part of Quadracoatl.
 * 
 * Quadracoatl is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Quadracoatl is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Quadracoatl.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.quadracoatl.scripting.lua;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.luajc.LuaJC;
import org.quadracoatl.framework.cosmos.Cosmos;
import org.quadracoatl.framework.mod.Mod;
import org.quadracoatl.framework.scheduler.Scheduler;
import org.quadracoatl.scripting.ScriptEnvironment;
import org.quadracoatl.scripting.ScriptingFeature;
import org.quadracoatl.scripting.lua.libs.CosmosLib;
import org.quadracoatl.scripting.lua.libs.EngineLib;
import org.quadracoatl.scripting.lua.libs.LogLib;
import org.quadracoatl.scripting.lua.libs.SchedulerLib;
import org.quadracoatl.scripting.lua.libs.SupportLib;

public class LuaEnvironment implements ScriptEnvironment {
	private static final String INIT_LUA_FILENAME = "init.lua";
	
	private Cosmos cosmos = null;
	private PathLimitedFunction doFileFunction = null;
	private Globals environment = null;
	private PathLimitedFunction loadFileFunction = null;
	private LuaTable modsTable = null;
	private Scheduler scheduler = null;
	
	public LuaEnvironment() {
		super();
		
		initEnvironment();
		loadDefaultLibs();
		sandboxEnvironment();
	}
	
	@Override
	public void enableFeatures(EnumSet<ScriptingFeature> features) {
		if (features != null && !features.isEmpty()) {
			for (ScriptingFeature feature : features) {
				switch (feature) {
					case FRAMEWORK_COSMOS:
						environment.load(new CosmosLib(cosmos));
						break;
					
					case FRAMEWORK_LOGGING:
						environment.load(new LogLib());
						break;
					
					case FRAMEWORK_SCHEDULER:
						environment.load(new SchedulerLib(scheduler));
						break;
					
					case FRAMEWORK_SUPPORT:
						environment.load(new SupportLib());
						break;
					
					case THREADS:
						environment.load(new CoroutineLib());
						break;
				}
			}
		}
	}
	
	@Override
	public Cosmos getCosmos() {
		return cosmos;
	}
	
	public Globals getEnvironment() {
		return environment;
	}
	
	@Override
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	@Override
	public void load(Mod mod) {
		Path initFile = Paths.get(mod.getDirectory().toString(), INIT_LUA_FILENAME);
		
		if (Files.exists(initFile, LinkOption.NOFOLLOW_LINKS)) {
			LuaTable requiresTable = new LuaTable();
			for (String requires : mod.getRequires()) {
				requiresTable.set(requiresTable.length() + 1, requires);
			}
			
			LuaTable modTable = new LuaTable();
			modTable.set("name", mod.getName());
			modTable.set("displayName", mod.getDisplayName());
			modTable.set("requires", requiresTable);
			
			modsTable.set(mod.getName(), modTable);
			
			doFileFunction.setLimit(mod.getDirectory());
			loadFileFunction.setLimit(mod.getDirectory());
			
			load(initFile);
			
			doFileFunction.setLimit(null);
			loadFileFunction.setLimit(null);
		}
	}
	
	public void load(Path file) {
		environment.loadfile(file.toString()).call();
	}
	
	public void load(String code) {
		environment.load(code);
	}
	
	@Override
	public void setCosmos(Cosmos cosmos) {
		this.cosmos = cosmos;
	}
	
	@Override
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	private void initEnvironment() {
		environment = new Globals();
		
		LoadState.install(environment);
		LuaC.install(environment);
		LuaJC.install(environment);
		
		modsTable = new LuaTable();
		environment.set("mods", modsTable);
	}
	
	private void loadDefaultLibs() {
		environment.load(new PackageLib());
		
		environment.load(new Bit32Lib());
		environment.load(new StringLib());
		environment.load(new TableLib());
		
		environment.load(new JseBaseLib());
		environment.load(new JseMathLib());
		
		environment.load(new EngineLib());
	}
	
	private void sandboxEnvironment() {
		doFileFunction = new PathLimitedFunction(environment.get("dofile"));
		loadFileFunction = new PathLimitedFunction(environment.get("loadfile"));
		
		environment.set("dofile", doFileFunction);
		environment.set("loadfile", loadFileFunction);
		
		environment.set("require", ForbiddenFunction.INSTANCE);
	}
	
	private static final class ForbiddenFunction extends ZeroArgFunction {
		public static final ForbiddenFunction INSTANCE = new ForbiddenFunction();
		
		public ForbiddenFunction() {
			super();
		}
		
		@Override
		public LuaValue call() {
			throw new LuaError("Calling this function is not allowed.");
		}
	}
	
	private static final class PathLimitedFunction extends OneArgFunction {
		private Path limit = null;
		private LuaValue originalFunction = null;
		
		public PathLimitedFunction(LuaValue originalFunction) {
			super();
			
			this.originalFunction = originalFunction;
		}
		
		@Override
		public LuaValue call(LuaValue arg) {
			if (limit == null) {
				throw new LuaError("Calling this function is not allowed in the current state.");
			}
			
			if (arg.isstring()) {
				String toLoad = arg.tojstring();
				
				toLoad = toLoad.replaceAll("\\.+", ".");
				toLoad = Paths.get(limit.toString(), toLoad).toString();
				
				return originalFunction.call(LuaValue.valueOf(toLoad));
			}
			
			return LuaValue.NIL;
		}
		
		public void setLimit(Path limit) {
			this.limit = limit;
		}
	}
}
