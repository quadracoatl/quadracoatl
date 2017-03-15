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

import org.luaj.vm2.LuaValue;
import org.quadracoatl.framework.common.Vector3d;
import org.quadracoatl.framework.resources.colors.Color;
import org.quadracoatl.framework.resources.lights.LightSource;
import org.quadracoatl.framework.resources.lights.LightSourceType;
import org.quadracoatl.framework.resources.textures.Texture;
import org.quadracoatl.framework.resources.textures.TextureType;

public final class LuaOneWay {
	private LuaOneWay() {
		// no instance needed.
	}
	
	public static final Color createColor(LuaValue color) {
		if (!color.isnil()) {
			if (color.isnumber()) {
				int colorValue = color.toint();
				
				return new Color(
						((colorValue >>> 24) & 0xff) / 255d,
						((colorValue >>> 16) & 0xff) / 255d,
						((colorValue >>> 8) & 0xff) / 255d,
						(colorValue & 0xff) / 255d);
			} else if (color.istable()) {
				if (!color.get("red").isnil()
						|| !color.get("green").isnil()
						|| !color.get("blue").isnil()
						|| !color.get("alpha").isnil()) {
					return new Color(
							LuaUtil.asDouble(color, "red", 1.0d),
							LuaUtil.asDouble(color, "green", 1.0d),
							LuaUtil.asDouble(color, "blue", 1.0d),
							LuaUtil.asDouble(color, "alpha", 1.0d));
				} else if (!color.get("r").isnil()
						|| !color.get("g").isnil()
						|| !color.get("b").isnil()
						|| !color.get("a").isnil()) {
					return new Color(
							LuaUtil.asDouble(color, "r", 1.0d),
							LuaUtil.asDouble(color, "g", 1.0d),
							LuaUtil.asDouble(color, "b", 1.0d),
							LuaUtil.asDouble(color, "a", 1.0d));
				} else {
					return new Color(
							LuaUtil.asDouble(color.get(0), 1.0d),
							LuaUtil.asDouble(color.get(1), 1.0d),
							LuaUtil.asDouble(color.get(2), 1.0d),
							LuaUtil.asDouble(color.get(3), 1.0d));
				}
			}
		}
		
		return null;
	}
	
	public static final LightSource createLightSource(LuaValue parentDefinition) {
		if (parentDefinition == null || parentDefinition.isnil()) {
			return null;
		}
		
		LuaValue lightSourceDefinition = parentDefinition.get("light");
		
		if (lightSourceDefinition.istable()) {
			return new LightSource(
					LuaUtil.asEnum(lightSourceDefinition, "type", LightSourceType.AMBIENT, LightSourceType.class),
					LuaOneWay.createColor(lightSourceDefinition.get("color")),
					LuaOneWay.createVector3d(lightSourceDefinition.get("direction")));
		}
		
		return null;
	}
	
	public static final Texture createTexture(LuaValue parentDefinition) {
		if (parentDefinition == null || parentDefinition.isnil()) {
			return null;
		}
		
		LuaValue textureDefinition = parentDefinition.get("texture");
		
		if (textureDefinition.isstring()) {
			return new Texture(
					LuaUtil.asEnum(parentDefinition, "textureType", TextureType.SINGLE, TextureType.class),
					textureDefinition.tojstring());
		} else if (textureDefinition.istable()) {
			if (textureDefinition.length() > 1) {
				if (!textureDefinition.get("front").isnil()) {
					return new Texture(
							TextureType.TILES,
							textureDefinition.get("front").tojstring(),
							textureDefinition.get("back").tojstring(),
							textureDefinition.get("left").tojstring(),
							textureDefinition.get("right").tojstring(),
							textureDefinition.get("top").tojstring(),
							textureDefinition.get("bottom").tojstring());
				} else {
					return new Texture(
							TextureType.TILES,
							textureDefinition.get(1).tojstring(),
							textureDefinition.get(2).tojstring(),
							textureDefinition.get(3).tojstring(),
							textureDefinition.get(4).tojstring(),
							textureDefinition.get(5).tojstring(),
							textureDefinition.get(6).tojstring());
				}
			} else {
				return new Texture(
						LuaUtil.asEnum(parentDefinition, "textureType", TextureType.SINGLE, TextureType.class),
						textureDefinition.get(0).tojstring());
			}
		}
		
		return null;
	}
	
	public static final Vector3d createVector3d(LuaValue vector) {
		if (vector == null || vector.isnil()) {
			return null;
		}
		
		if (vector.isnumber()) {
			double value = vector.todouble();
			
			return new Vector3d(value, value, value);
		} else if (vector.istable()) {
			if (vector.get("x").isnil()) {
				return new Vector3d(
						vector.get(1).todouble(),
						vector.get(2).todouble(),
						vector.get(3).todouble());
			} else {
				return new Vector3d(
						vector.get("x").todouble(),
						vector.get("y").todouble(),
						vector.get("z").todouble());
			}
		}
		
		return null;
	}
}
