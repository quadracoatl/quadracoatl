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
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.quadracoatl.framework.scheduler.OvershootPolicy;
import org.quadracoatl.framework.scheduler.Scheduler;
import org.quadracoatl.scripting.lua.LuaUtil;

public class SchedulerLib extends TwoArgFunction {
	private Scheduler scheduler = null;
	
	public SchedulerLib(Scheduler scheduler) {
		super();
		
		this.scheduler = scheduler;
	}
	
	@Override
	public LuaValue call(LuaValue value, LuaValue env) {
		LuaUtil.loadEnum(env, OvershootPolicy.class);
		
		LuaTable scheduler = new LuaTable();
		scheduler.set("schedule", new schedule());
		scheduler.set("scheduleNext", new scheduleNext());
		scheduler.set("unschedule", new unschedule());
		
		env.set("scheduler", scheduler);
		
		return scheduler;
	}
	
	private final class schedule extends OneArgFunction {
		public schedule() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue jobDefinition) {
			String name = LuaUtil.asString(jobDefinition, "name", "job");
			
			scheduler.schedule(
					name,
					LuaUtil.asInt(jobDefinition, "interval", 1000),
					LuaUtil.asEnum(jobDefinition, "overshootPolicy", OvershootPolicy.RUN_ONCE, OvershootPolicy.class),
					jobDefinition.get("job")::invoke);
			
			return LuaValue.NIL;
		}
	}
	
	private final class scheduleNext extends OneArgFunction {
		public scheduleNext() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue job) {
			scheduler.scheduleNext(job::invoke);
			
			return LuaValue.NIL;
		}
	}
	
	private final class unschedule extends OneArgFunction {
		public unschedule() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue job) {
			scheduler.unschedule(job.tojstring());
			
			return LuaValue.NIL;
		}
	}
}
