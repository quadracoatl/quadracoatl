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

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.quadracoatl.framework.support.hashers.MurmurHasher;

public final class LuaUtil {
	
	private LuaUtil() {
		// No instance required.
	}
	
	public static boolean asBoolean(LuaValue luaValue, boolean defaultValue) {
		if (luaValue == null || luaValue.isnil() || !luaValue.isboolean()) {
			return defaultValue;
		}
		
		return luaValue.toboolean();
	}
	
	public static boolean asBoolean(LuaValue parentLuaValue, String key, boolean defaultValue) {
		if (parentLuaValue == null || parentLuaValue.isnil() || !parentLuaValue.istable()) {
			return defaultValue;
		}
		
		return asBoolean(parentLuaValue.get(key), defaultValue);
	}
	
	public static final double asDouble(LuaValue luaValue, double defaultValue) {
		if (luaValue == null || luaValue.isnil() || !luaValue.isnumber()) {
			return defaultValue;
		}
		
		return luaValue.todouble();
	}
	
	public static final double asDouble(LuaValue parentLuaValue, String key, double defaultValue) {
		if (parentLuaValue == null || parentLuaValue.isnil() || !parentLuaValue.istable()) {
			return defaultValue;
		}
		
		return asDouble(parentLuaValue.get(key), defaultValue);
	}
	
	public static final <ENUM extends Enum<?>> ENUM asEnum(LuaValue luaValue, ENUM defaultValue, Class<ENUM> enumClass) {
		if (luaValue == null || luaValue.isnil() || !luaValue.isstring()) {
			return defaultValue;
		}
		
		String stringValue = luaValue.tojstring();
		
		for (ENUM value : enumClass.getEnumConstants()) {
			if (value.toString().equals(stringValue)) {
				return value;
			}
		}
		
		return defaultValue;
	}
	
	public static final <ENUM extends Enum<?>> ENUM asEnum(LuaValue parentLuaValue, String key, ENUM defaultValue, Class<ENUM> enumClass) {
		if (parentLuaValue == null || parentLuaValue.isnil() || !parentLuaValue.istable()) {
			return defaultValue;
		}
		
		return asEnum(parentLuaValue.get(key), defaultValue, enumClass);
	}
	
	public static final int asInt(LuaValue luaValue, int defaultValue) {
		if (luaValue == null || luaValue.isnil() || !luaValue.isnumber()) {
			return defaultValue;
		}
		
		return luaValue.toint();
	}
	
	public static final int asInt(LuaValue parentLuaValue, String key, int defaultValue) {
		if (parentLuaValue == null || parentLuaValue.isnil() || !parentLuaValue.istable()) {
			return defaultValue;
		}
		
		return asInt(parentLuaValue.get(key), defaultValue);
	}
	
	public static Object asJavaObject(LuaValue luaValue) {
		if (luaValue == null || luaValue.isnil()) {
			return null;
		}
		
		if (luaValue.isstring()) {
			return luaValue.tojstring();
		}
		
		if (luaValue.isboolean()) {
			return Boolean.valueOf(luaValue.toboolean());
		}
		
		if (luaValue.isint()) {
			return Integer.valueOf(luaValue.toint());
		}
		
		if (luaValue.islong()) {
			return Long.valueOf(luaValue.tolong());
		}
		
		if (luaValue.isnumber()) {
			return Double.valueOf(luaValue.todouble());
		}
		
		return null;
	}
	
	public static final long asSeed(LuaValue luaValue) {
		if (!luaValue.isnil()) {
			if (luaValue.isnumber()) {
				return luaValue.tolong();
			} else if (luaValue.isstring()) {
				return MurmurHasher.hash(luaValue.tojstring());
			} else if (luaValue.istable()) {
				LuaValue seedValue = luaValue.get("seed");
				
				if (seedValue.isnumber()) {
					return seedValue.tolong();
				} else if (seedValue.isstring()) {
					return MurmurHasher.hash(seedValue.tojstring());
				}
			}
		}
		
		return System.nanoTime();
	}
	
	public static final String asString(LuaValue luaValue, String defaultValue) {
		if (luaValue == null || luaValue.isnil() || !luaValue.isstring()) {
			return defaultValue;
		}
		
		return luaValue.tojstring();
	}
	
	public static final String asString(LuaValue parentLuaValue, String key, String defaultValue) {
		if (parentLuaValue == null || parentLuaValue.isnil() || !parentLuaValue.istable()) {
			return defaultValue;
		}
		
		return asString(parentLuaValue.get(key), defaultValue);
	}
	
	public static final <ENUM extends Enum<?>> LuaValue coerceEnum(Class<ENUM> enumClass) {
		LuaTable coercedEnum = new LuaTable();
		
		for (ENUM value : enumClass.getEnumConstants()) {
			coercedEnum.set(value.toString(), value.toString());
		}
		
		return coercedEnum;
	}
	
	public static final <ENUM extends Enum<?>> void loadEnum(LuaValue env, Class<ENUM> enumClass) {
		env.set(enumClass.getSimpleName(), coerceEnum(enumClass));
	}
}
