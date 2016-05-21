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
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.quadracoatl.framework.support.noises.Noise;
import org.quadracoatl.framework.support.randoms.Random;

public class SupportLib extends TwoArgFunction {
	public SupportLib() {
		super();
	}
	
	@Override
	public LuaValue call(LuaValue value, LuaValue env) {
		LuaTable support = new LuaTable();
		support.set("createNoise", new createNoise());
		support.set("createRandom", new createRandom());
		
		return support;
	}
	
	private final class createNoise extends OneArgFunction {
		
		@Override
		public LuaValue call(LuaValue noiseDefinition) {
			LuaValue seedValue = noiseDefinition.get("seed");
			
			int octaves = noiseDefinition.get("octaves").toint();
			double persistence = noiseDefinition.get("persistence").todouble();
			double scaleX = noiseDefinition.get("scaleX").todouble();
			double scaleY = noiseDefinition.get("scaleY").todouble();
			double scaleZ = noiseDefinition.get("scaleZ").todouble();
			double scaleW = noiseDefinition.get("scaleW").todouble();
			
			Noise noise = null;
			
			if (seedValue.isnumber()) {
				noise = new Noise(
						seedValue.tolong(),
						octaves,
						persistence,
						scaleX,
						scaleY,
						scaleZ,
						scaleW);
			} else if (seedValue.isstring()) {
				noise = new Noise(
						seedValue.tojstring(),
						octaves,
						persistence,
						scaleX,
						scaleY,
						scaleZ,
						scaleW);
			}
			
			return CoerceJavaToLua.coerce(noise);
		}
		
	}
	
	private final class createRandom extends OneArgFunction {
		
		@Override
		public LuaValue call(LuaValue randomDefinition) {
			LuaValue seedValue = randomDefinition.get("seed");
			
			Random random = null;
			
			if (seedValue.isnumber()) {
				random = new Random(seedValue.tolong());
			} else if (seedValue.isstring()) {
				random = new Random(seedValue.tojstring());
			}
			
			return CoerceJavaToLua.coerce(random);
		}
		
	}
}
