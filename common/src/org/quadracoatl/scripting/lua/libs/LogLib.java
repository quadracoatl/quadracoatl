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

import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.quadracoatl.framework.logging.LogLevel;
import org.quadracoatl.framework.logging.Logger;
import org.quadracoatl.framework.logging.LoggerFactory;
import org.quadracoatl.scripting.lua.LuaUtil;

public class LogLib extends TwoArgFunction {
	private Logger logger = LoggerFactory.getLogger("Lua");
	
	public LogLib() {
		super();
	}
	
	@Override
	public LuaValue call(LuaValue value, LuaValue env) {
		LuaUtil.loadEnum(env, LogLevel.class);
		
		LuaTable log = new LuaTable();
		log.set("debug", new log(LogLevel.DEBUG));
		log.set("error", new log(LogLevel.ERROR));
		log.set("fatal", new log(LogLevel.FATAL));
		log.set("info", new log(LogLevel.INFO));
		log.set("isEnabled", new isEnabled());
		log.set("log", new log(null));
		log.set("trace", new log(LogLevel.TRACE));
		log.set("warn", new log(LogLevel.WARN));
		
		env.set("log", log);
		
		return log;
	}
	
	private final class isEnabled extends OneArgFunction {
		public isEnabled() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue arg) {
			LogLevel logLevel = LuaUtil.asEnum(arg, LogLevel.TRACE, LogLevel.class);
			
			return LuaBoolean.valueOf(logger.isEnabled(logLevel));
		}
	}
	
	private final class log extends VarArgFunction {
		private LogLevel logLevel = null;
		
		public log(LogLevel logLevel) {
			super();
			
			this.logLevel = logLevel;
		}
		
		@Override
		public Varargs invoke(Varargs args) {
			int offset = 0;
			LogLevel currentLogLevel = logLevel;
			
			if (currentLogLevel == null) {
				offset = 1;
				currentLogLevel = LuaUtil.asEnum(args.arg(1), LogLevel.TRACE, LogLevel.class);
			}
			
			if (logger.isEnabled(currentLogLevel)) {
				if (args.narg() <= offset) {
					logger.log(currentLogLevel);
				} else {
					List<Object> javaArgs = new ArrayList<>(args.narg());
					
					for (int index = offset + 1; index <= args.narg(); index++) {
						javaArgs.add(LuaUtil.asJavaObject(args.arg(index)));
					}
					
					logger.log(currentLogLevel, javaArgs.toArray(new Object[javaArgs.size()]));
				}
			}
			
			return LuaValue.NIL;
		}
	}
}
