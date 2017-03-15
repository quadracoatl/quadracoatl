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

package org.quadracoatl.scripting.lua.wrappers;

import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaValue;
import org.quadracoatl.framework.realm.TimeProvider;

public class LuaTimeProvider implements TimeProvider {
	
	private LuaValue luaTimeProviderFunction = null;
	
	public LuaTimeProvider(LuaValue luaTimeProviderFunction) {
		super();
		
		this.luaTimeProviderFunction = luaTimeProviderFunction;
	}
	
	@Override
	public double getTime(double currentTime, long elapsedNanoSecondsSinceLastUpdate) {
		LuaValue returnedValue = luaTimeProviderFunction.call(
				LuaNumber.valueOf(currentTime),
				LuaNumber.valueOf(elapsedNanoSecondsSinceLastUpdate));
		
		if (!returnedValue.isnumber()) {
			return 0.0d;
		} else {
			return returnedValue.todouble();
		}
	}
}
