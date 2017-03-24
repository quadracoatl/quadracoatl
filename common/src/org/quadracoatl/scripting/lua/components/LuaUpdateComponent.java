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

package org.quadracoatl.scripting.lua.components;

import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaValue;
import org.quadracoatl.framework.entities.Entity;
import org.quadracoatl.framework.entities.components.UpdateComponent;
import org.quadracoatl.interlayer.Transient;
import org.quadracoatl.scripting.lua.wrappers.entities.EntityWrapper;

public @Transient class LuaUpdateComponent extends UpdateComponent {
	private EntityWrapper entityWrapper = null;
	private LuaValue luaUpdateFunction = null;
	
	public LuaUpdateComponent(LuaValue luaUpdateFunction) {
		super();
		
		this.luaUpdateFunction = luaUpdateFunction;
	}
	
	@Override
	public void attach(Entity entity) {
		entityWrapper = new EntityWrapper(entity);
		
		super.attach(entity);
	}
	
	@Override
	public void detach() {
		super.detach();
		
		entityWrapper = null;
	}
	
	public EntityWrapper getEntityWrapper() {
		return entityWrapper;
	}
	
	@Override
	public void update(double currentTime, long elapsedNanoSecondsSinceLastUpdate) {
		super.update(currentTime, elapsedNanoSecondsSinceLastUpdate);
		
		luaUpdateFunction.call(
				entityWrapper,
				LuaNumber.valueOf(currentTime),
				LuaNumber.valueOf(elapsedNanoSecondsSinceLastUpdate));
	}
}
