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

package org.quadracoatl.scripting.lua.wrappers.entities;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.OneArgFunction;
import org.quadracoatl.framework.common.Vector3d;
import org.quadracoatl.framework.entities.Entity;
import org.quadracoatl.framework.entities.components.ColorComponent;
import org.quadracoatl.framework.entities.components.LightSourceComponent;
import org.quadracoatl.framework.entities.components.RotationComponent;
import org.quadracoatl.framework.entities.components.SizeComponent;
import org.quadracoatl.framework.resources.colors.Color;
import org.quadracoatl.framework.resources.colors.ColorBlendMode;
import org.quadracoatl.framework.resources.lights.LightSource;
import org.quadracoatl.framework.resources.lights.LightSourceType;
import org.quadracoatl.scripting.lua.LuaUtil;

public class EntityWrapper extends LuaTable {
	private Entity entity = null;
	
	public EntityWrapper(Entity entity) {
		super();
		
		this.entity = entity;
		
		set("getColor", new getColor());
		set("getColorBlendMode", new getColorBlendMode());
		set("getLight", new getLight());
		set("getRotation", new getRotation());
		set("getSize", new getSize());
		set("setColor", new setColor());
		set("setColorMode", new setColorBlendMode());
		set("setLight", new setLight());
		set("setRotation", new setRotation());
		set("setSize", new setSize());
	}
	
	private static final boolean update(Color color, LuaValue luaColor) {
		double red = luaColor.get("red").todouble();
		double green = luaColor.get("green").todouble();
		double blue = luaColor.get("blue").todouble();
		double alpha = luaColor.get("alpha").todouble();
		
		return color.update(red, green, blue, alpha);
	}
	
	private static final boolean update(Vector3d vector3d, LuaValue luaVector) {
		double x = luaVector.get("x").todouble();
		double y = luaVector.get("y").todouble();
		double z = luaVector.get("z").todouble();
		
		return vector3d.update(x, y, z);
	}
	
	private final class getColor extends OneArgFunction {
		public getColor() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue dummy) {
			ColorComponent colorComponent = entity.getComponent(ColorComponent.class);
			
			LuaTable colorTable = new LuaTable();
			colorTable.set("red", colorComponent.getColor().red);
			colorTable.set("green", colorComponent.getColor().green);
			colorTable.set("blue", colorComponent.getColor().blue);
			colorTable.set("alpha", colorComponent.getColor().alpha);
			
			return colorTable;
		}
	}
	
	private final class getColorBlendMode extends OneArgFunction {
		public getColorBlendMode() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue dummy) {
			ColorComponent colorComponent = entity.getComponent(ColorComponent.class);
			
			return LuaValue.valueOf(colorComponent.getColorBlendMode().toString());
		}
	}
	
	private final class getLight extends OneArgFunction {
		public getLight() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue dummy) {
			LightSourceComponent lightSourceComponent = entity.getComponent(LightSourceComponent.class);
			LightSource lightSource = lightSourceComponent.getLightSource();
			
			Color color = lightSource.getColor();
			
			LuaTable colorTable = new LuaTable();
			colorTable.set("red", color.red);
			colorTable.set("green", color.green);
			colorTable.set("blue", color.blue);
			colorTable.set("alpha", color.alpha);
			
			Vector3d direction = lightSource.getDirection();
			
			LuaTable directionTable = new LuaTable();
			directionTable.set("x", direction.x);
			directionTable.set("y", direction.y);
			directionTable.set("z", direction.z);
			
			LuaTable lightTable = new LuaTable();
			lightTable.set("color", colorTable);
			lightTable.set("direction", directionTable);
			lightTable.set("type", lightSource.getType().toString());
			
			return lightTable;
		}
	}
	
	private final class getRotation extends OneArgFunction {
		public getRotation() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue dummy) {
			RotationComponent rotationComponent = entity.getComponent(RotationComponent.class);
			
			LuaTable vectorTable = new LuaTable();
			vectorTable.set("x", rotationComponent.getRotation().x);
			vectorTable.set("y", rotationComponent.getRotation().y);
			vectorTable.set("z", rotationComponent.getRotation().z);
			
			return vectorTable;
		}
	}
	
	private final class getSize extends OneArgFunction {
		public getSize() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue dummy) {
			SizeComponent sizeComponent = entity.getComponent(SizeComponent.class);
			
			LuaTable vectorTable = new LuaTable();
			vectorTable.set("x", sizeComponent.getSize().x);
			vectorTable.set("y", sizeComponent.getSize().y);
			vectorTable.set("z", sizeComponent.getSize().z);
			
			return vectorTable;
		}
	}
	
	private final class setColor extends LibFunction {
		public setColor() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue dummy, LuaValue color) {
			ColorComponent colorComponent = entity.getComponent(ColorComponent.class);
			
			if (update(colorComponent.getColor(), color)) {
				colorComponent.markAsChanged();
			}
			
			return LuaValue.NIL;
		}
	}
	
	private final class setColorBlendMode extends LibFunction {
		public setColorBlendMode() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue dummy, LuaValue colorBlendMode) {
			ColorComponent colorComponent = entity.getComponent(ColorComponent.class);
			
			ColorBlendMode mode = LuaUtil.asEnum(colorBlendMode, null, ColorBlendMode.class);
			
			if (colorComponent.getColorBlendMode() != mode) {
				colorComponent.setColorBlendMode(mode);
				colorComponent.markAsChanged();
			}
			
			return LuaValue.NIL;
		}
	}
	
	private final class setLight extends LibFunction {
		public setLight() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue dummy, LuaValue light) {
			LightSourceComponent lightSourceComponent = entity.getComponent(LightSourceComponent.class);
			LightSource lightSource = lightSourceComponent.getLightSource();
			
			boolean changed = false;
			
			if (update(lightSource.getColor(), light.get("color"))) {
				changed = true;
			}
			
			if (update(lightSource.getDirection(), light.get("direction"))) {
				changed = true;
			}
			
			LightSourceType type = LuaUtil.asEnum(light.get("type"), lightSource.getType(), LightSourceType.class);
			
			if (lightSource.getType() != type) {
				lightSource.setType(type);
				changed = true;
			}
			
			if (changed) {
				lightSourceComponent.markAsChanged();
			}
			
			return LuaValue.NIL;
		}
	}
	
	private final class setRotation extends LibFunction {
		public setRotation() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue dummy, LuaValue rotation) {
			RotationComponent rotationComponent = entity.getComponent(RotationComponent.class);
			
			if (update(rotationComponent.getRotation(), rotation)) {
				rotationComponent.markAsChanged();
			}
			
			return LuaValue.NIL;
		}
	}
	
	private final class setSize extends LibFunction {
		public setSize() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue dummy, LuaValue size) {
			SizeComponent sizeComponent = entity.getComponent(SizeComponent.class);
			
			if (update(sizeComponent.getSize(), size)) {
				sizeComponent.markAsChanged();
			}
			
			return LuaValue.NIL;
		}
	}
}
