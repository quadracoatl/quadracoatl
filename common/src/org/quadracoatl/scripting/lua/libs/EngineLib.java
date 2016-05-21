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

package org.quadracoatl.scripting.lua.libs;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.quadracoatl.framework.cosmos.Cosmos;

public class EngineLib extends TwoArgFunction {
	private Cosmos cosmos = null;
	
	public EngineLib(Cosmos cosmos) {
		super();
		
		this.cosmos = cosmos;
	}
	
	@Override
	public LuaValue call(LuaValue value, LuaValue env) {
		LuaTable engine = new LuaTable();
		engine.set("support", new SupportLib().call());
		engine.set("cosmos", new CosmosLib(cosmos).call());
		
		env.set("engine", engine);
		env.get("package").get("loaded").set("engine", engine);
		
		return engine;
	}
}
