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

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.luajc.LuaJC;
import org.quadracoatl.framework.cosmos.Cosmos;
import org.quadracoatl.scripting.lua.libs.EngineLib;

public class LuaEnvironment {
	private Cosmos cosmos = null;
	private Globals environment = null;
	
	public LuaEnvironment(Cosmos cosmos) {
		super();
		
		this.cosmos = cosmos;
		
		environment = new Globals();
		
		LoadState.install(environment);
		LuaC.install(environment);
		LuaJC.install(environment);
		
		loadDefaultLibs();
		loadEngineLib();
	}
	
	public void load(Path file) {
		environment.loadfile(file.toString()).call();
	}
	
	private void loadDefaultLibs() {
		environment.load(new PackageLib());
		
		environment.load(new Bit32Lib());
		environment.load(new CoroutineLib());
		environment.load(new StringLib());
		environment.load(new TableLib());
		
		environment.load(new JseBaseLib());
		environment.load(new JseMathLib());
	}
	
	private void loadEngineLib() {
		environment.load(new EngineLib(cosmos));
	}
}
