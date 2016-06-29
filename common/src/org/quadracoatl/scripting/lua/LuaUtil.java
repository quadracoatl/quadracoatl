package org.quadracoatl.scripting.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public final class LuaUtil {
	
	private LuaUtil() {
		// No instance required.
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
