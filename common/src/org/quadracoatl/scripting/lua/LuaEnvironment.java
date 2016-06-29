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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.luajc.LuaJC;

public class LuaEnvironment {
	private Globals environment = null;
	
	private Map<String, LuaValue> originalFunctions = new HashMap<>(3);
	
	public LuaEnvironment() {
		super();
		
		environment = new Globals();
		
		LoadState.install(environment);
		LuaC.install(environment);
		LuaJC.install(environment);
		
		environment.load(new PackageLib());
		
		environment.load(new Bit32Lib());
		environment.load(new StringLib());
		environment.load(new TableLib());
		
		environment.load(new JseMathLib());
	}
	
	public Globals getEnvironment() {
		return environment;
	}
	
	public void limitLoadingTo(Path path) {
		if (!originalFunctions.containsKey("loadfile")) {
			originalFunctions.put("loadfile", environment.get("loadfile"));
		}
		
		if (!originalFunctions.containsKey("dofile")) {
			originalFunctions.put("dofile", environment.get("dofile"));
		}
		
		if (path != null) {
			environment.set("loadfile", new PathLimitedFunction(path, originalFunctions.get("loadfile")));
			environment.set("dofile", new PathLimitedFunction(path, originalFunctions.get("dofile")));
		} else {
			environment.set("loadfile", LuaValue.NIL);
			environment.set("dofile", LuaValue.NIL);
		}
	}
	
	public void load(LuaValue luaValue) {
		environment.load(luaValue);
	}
	
	public void load(Path file) {
		environment.loadfile(file.toString()).call();
	}
	
	public void load(String code) {
		environment.load(code);
	}
	
	private static final class PathLimitedFunction extends OneArgFunction {
		private Path limit = null;
		private LuaValue originalFunction = null;
		
		public PathLimitedFunction(Path limit, LuaValue originalFunction) {
			super();
			
			this.limit = limit;
			this.originalFunction = originalFunction;
		}
		
		@Override
		public LuaValue call(LuaValue arg) {
			if (arg.isstring()) {
				String toLoad = arg.tojstring();
				
				toLoad = toLoad.replace("..", ".");
				toLoad = Paths.get(limit.toString(), toLoad).toString();
				
				return originalFunction.call(LuaValue.valueOf(toLoad));
			}
			
			return LuaValue.NIL;
		}
	}
}
