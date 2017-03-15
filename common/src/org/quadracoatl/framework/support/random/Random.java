/*
 * Copyright 2017, Robert 'Bobby' Zenz
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

package org.quadracoatl.framework.support.random;

import java.security.SecureRandom;

import org.quadracoatl.framework.support.random.randoms.MersenneTwister;
import org.quadracoatl.framework.support.random.randoms.SplitMix64;
import org.quadracoatl.framework.support.random.randoms.Xorshift1024Star;

public class Random {
	private RandomWrapper random = null;
	private long seed = 0l;
	private RandomValueTransformer transformer = null;
	private RandomType type = null;
	
	public Random(RandomType type, long seed) {
		super();
		
		this.type = type;
		this.seed = seed;
		
		random = createRandom();
	}
	
	public Random(RandomType type, long seed, RandomValueTransformer transformer) {
		this(type, seed);
		
		this.transformer = transformer;
	}
	
	public double next() {
		double value = random.next();
		
		if (transformer != null) {
			value = transformer.transform(value);
		}
		
		return value;
	}
	
	private final RandomWrapper createRandom() {
		switch (type) {
			case MERSENNE_TWISTER:
				return new MersenneTwisterWrapper(seed);
			
			case PLATFORM:
				return new PlatformWrapper(seed);
			
			case SECURE:
				return new SecureWrapper();
			
			case SPLIT_MIX_64:
				return new SplitMix64Wrapper(seed);
			
			case XORSHIFT_1024_STAR:
				return new Xorshift1024StarWrapper(seed);
			
			default:
				return new Xorshift1024StarWrapper(seed);
			
		}
	}
	
	private static final class MersenneTwisterWrapper implements RandomWrapper {
		private MersenneTwister random = null;
		
		public MersenneTwisterWrapper(long seed) {
			super();
			
			random = new MersenneTwister(seed);
		}
		
		@Override
		public double next() {
			return (random.nextDouble(true, true) - 0.5d) * 2.0d;
		}
	}
	
	private static final class PlatformWrapper implements RandomWrapper {
		private java.util.Random random = null;
		
		public PlatformWrapper(long seed) {
			super();
			
			random = new java.util.Random(seed);
		}
		
		@Override
		public double next() {
			return (random.nextDouble() - 0.5d) * 2.0d;
		}
	}
	
	private interface RandomWrapper {
		public double next();
	}
	
	private static final class SecureWrapper implements RandomWrapper {
		private SecureRandom random = null;
		
		public SecureWrapper() {
			super();
			
			random = new SecureRandom();
		}
		
		@Override
		public double next() {
			return (random.nextDouble() - 0.5d) * 2.0d;
		}
	}
	
	private static final class SplitMix64Wrapper implements RandomWrapper {
		private SplitMix64 random = null;
		
		public SplitMix64Wrapper(long seed) {
			super();
			
			random = new SplitMix64(seed);
		}
		
		@Override
		public double next() {
			return (double)random.next() / Long.MAX_VALUE;
		}
	}
	
	private static final class Xorshift1024StarWrapper implements RandomWrapper {
		private Xorshift1024Star random = null;
		
		public Xorshift1024StarWrapper(long seed) {
			super();
			
			random = new Xorshift1024Star(seed);
		}
		
		@Override
		public double next() {
			return (double)random.next() / Long.MAX_VALUE;
		}
	}
}
