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

import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.quadracoatl.framework.support.hashers.Md5Hasher;
import org.quadracoatl.framework.support.hashers.MurmurHasher;
import org.quadracoatl.framework.support.noise.Noise;
import org.quadracoatl.framework.support.noise.NoiseType;
import org.quadracoatl.framework.support.noise.NoiseValueTransformer;
import org.quadracoatl.framework.support.random.Random;
import org.quadracoatl.framework.support.random.RandomType;
import org.quadracoatl.framework.support.random.RandomValueTransformer;
import org.quadracoatl.scripting.lua.LuaUtil;

public class SupportLib extends TwoArgFunction {
	public SupportLib() {
		super();
	}
	
	@Override
	public LuaValue call(LuaValue value, LuaValue env) {
		LuaUtil.loadEnum(env, NoiseType.class);
		LuaUtil.loadEnum(env, RandomType.class);
		
		LuaTable support = new LuaTable();
		support.set("createNoise", new createNoise());
		support.set("createRandom", new createRandom());
		support.set("md5", new md5());
		support.set("murmur", new murmur());
		
		LuaValue engine = env.get("engine");
		engine.set("support", support);
		
		return support;
	}
	
	private final class createNoise extends OneArgFunction {
		public createNoise() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue noiseDefinition) {
			long seed = LuaUtil.asSeed(noiseDefinition);
			NoiseType noiseType = LuaUtil.asEnum(noiseDefinition, "type", NoiseType.OPEN_SIMPLEX, NoiseType.class);
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
		public createRandom() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue randomDefinition) {
			RandomType randomType = LuaUtil.asEnum(randomDefinition, "type", RandomType.XORSHIFT_1024_STAR, RandomType.class);
			RandomValueTransformer transformer = null;
			
			if (randomDefinition.istable()) {
				LuaValue transformFunction = randomDefinition.get("transform");
				
				if (transformFunction.isfunction()) {
					transformer = new LuaRandomValueTransformer(transformFunction);
				}
			} else if (randomDefinition.isfunction()) {
				transformer = new LuaRandomValueTransformer(randomDefinition);
			}
			
			Random random = new Random(randomType, LuaUtil.asSeed(randomDefinition), transformer);
			
			return CoerceJavaToLua.coerce(random);
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
	
	private final class md5 extends OneArgFunction {
		public md5() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue value) {
			if (value.isstring()) {
				return LuaString.valueOf(Md5Hasher.hash(value.tojstring()));
			}
			
			return LuaString.valueOf(Md5Hasher.NULL_HASH);
		}
	}
	
	private final class murmur extends OneArgFunction {
		public murmur() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue value) {
			if (value.isstring()) {
				return LuaNumber.valueOf(MurmurHasher.hash(value.tojstring()));
			}
			
			return LuaNumber.ZERO;
		}
	}
}
