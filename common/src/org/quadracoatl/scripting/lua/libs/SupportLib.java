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
import org.quadracoatl.framework.support.hashers.MurmurHasher;
import org.quadracoatl.framework.support.noises.Noise;
import org.quadracoatl.framework.support.noises.NoiseType;
import org.quadracoatl.framework.support.noises.NoiseValueTransformer;
import org.quadracoatl.framework.support.randoms.Random;
import org.quadracoatl.framework.support.randoms.RandomValueTransformer;
import org.quadracoatl.framework.support.randoms.SecureRandom;
import org.quadracoatl.scripting.lua.LuaUtil;

public class SupportLib extends TwoArgFunction {
	public SupportLib() {
		super();
	}
	
	private static long getSeed(LuaValue luaValue) {
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
	
	@Override
	public LuaValue call(LuaValue value, LuaValue env) {
		LuaUtil.loadEnum(env, NoiseType.class);
		
		LuaTable support = new LuaTable();
		support.set("createNoise", new createNoise());
		support.set("createRandom", new createRandom());
		support.set("createSecureRandom", new createSecureRandom());
		
		LuaValue engine = env.get("engine");
		engine.set("support", support);
		
		return support;
	}
	
	private final class createNoise extends OneArgFunction {
		
		@Override
		public LuaValue call(LuaValue noiseDefinition) {
			long seed = getSeed(noiseDefinition);
			NoiseType noiseType = LuaUtil.asEnum(noiseDefinition, "noiseType", NoiseType.OPEN_SIMPLEX, NoiseType.class);
			int octaves = LuaUtil.asInt(noiseDefinition, "octaves", 1);
			double persistence = LuaUtil.asDouble(noiseDefinition, "persistence", 0.5);
			double scaleX = LuaUtil.asDouble(noiseDefinition, "scaleX", 128);
			double scaleY = LuaUtil.asDouble(noiseDefinition, "scaleY", scaleX);
			double scaleZ = LuaUtil.asDouble(noiseDefinition, "scaleZ", scaleY);
			double scaleW = LuaUtil.asDouble(noiseDefinition, "scaleW", scaleZ);
			
			NoiseValueTransformer transformer = null;
			
			if (!noiseDefinition.isnil()) {
				LuaValue transformFunction = noiseDefinition.get("transform");
				
				if (transformFunction.isfunction()) {
					transformer = new LuaNoiseValueTransformer(transformFunction);
				}
			}
			
			Noise noise = new Noise(
					noiseType,
					seed,
					octaves,
					persistence,
					scaleX,
					scaleY,
					scaleZ,
					scaleW,
					transformer);
			
			return CoerceJavaToLua.coerce(noise);
		}
		
	}
	
	private final class createRandom extends OneArgFunction {
		
		@Override
		public LuaValue call(LuaValue randomDefinition) {
			RandomValueTransformer transformer = null;
			
			if (!randomDefinition.isnil() && randomDefinition.istable()) {
				LuaValue transformFunction = randomDefinition.get("transform");
				
				if (transformFunction.isfunction()) {
					transformer = new LuaRandomValueTransformer(transformFunction);
				}
			}
			
			Random random = new Random(getSeed(randomDefinition), transformer);
			
			return CoerceJavaToLua.coerce(random);
		}
		
	}
	
	private final class createSecureRandom extends OneArgFunction {
		
		@Override
		public LuaValue call(LuaValue secureRandomDefinition) {
			RandomValueTransformer transformer = null;
			
			if (!secureRandomDefinition.isnil() && secureRandomDefinition.istable()) {
				LuaValue transformFunction = secureRandomDefinition.get("transform");
				
				if (transformFunction.isfunction()) {
					transformer = new LuaRandomValueTransformer(transformFunction);
				}
			}
			
			SecureRandom secureRandom = new SecureRandom(transformer);
			
			return CoerceJavaToLua.coerce(secureRandom);
		}
		
	}
	
	private static final class LuaNoiseValueTransformer implements NoiseValueTransformer {
		private LuaValue luaFunction = null;
		
		public LuaNoiseValueTransformer(LuaValue luaFunction) {
			super();
			
			this.luaFunction = luaFunction;
		}
		
		@Override
		public double transform(double value, double x, double y, double z, double w) {
			return luaFunction.invoke(new LuaValue[] {
					LuaValue.valueOf(value),
					LuaValue.valueOf(x),
					LuaValue.valueOf(y),
					LuaValue.valueOf(z),
					LuaValue.valueOf(w) }).todouble(1);
		}
		
	}
	
	private static final class LuaRandomValueTransformer implements RandomValueTransformer {
		private LuaValue luaFunction = null;
		
		public LuaRandomValueTransformer(LuaValue luaFunction) {
			super();
			
			this.luaFunction = luaFunction;
		}
		
		@Override
		public double transform(double value) {
			return luaFunction.call(LuaValue.valueOf(value)).todouble();
		}
		
	}
}
