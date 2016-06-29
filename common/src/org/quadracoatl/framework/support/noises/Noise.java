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

package org.quadracoatl.framework.support.noises;

import java.util.ArrayList;
import java.util.List;

public class Noise {
	private List<Octave> noises = null;
	private NoiseType noiseType = NoiseType.OPEN_SIMPLEX;
	private int octaves = 1;
	private double persistence = 0.5;
	private double scaleMod = 1.0;
	private double scaleW = 1.0;
	private double scaleX = 1.0;
	private double scaleY = 1.0;
	private double scaleZ = 1.0;
	private long seed = 0;
	private NoiseValueTransformer transformer = null;
	
	public Noise(
			NoiseType noiseType,
			long seed,
			int octaves,
			double persistence,
			double scaleX,
			double scaleY,
			double scaleZ,
			double scaleW) {
		super();
		
		this.noiseType = noiseType;
		this.seed = seed;
		this.octaves = octaves;
		this.persistence = persistence;
		this.scaleX = 1 / scaleX;
		this.scaleY = 1 / scaleY;
		this.scaleZ = 1 / scaleZ;
		this.scaleW = 1 / scaleW;
		
		initOctaves();
	}
	
	public Noise(
			NoiseType noiseType,
			long seed,
			int octaves,
			double persistence,
			double scaleX,
			double scaleY,
			double scaleZ,
			double scaleW,
			NoiseValueTransformer transformer) {
		this(noiseType, seed, octaves, persistence, scaleX, scaleY, scaleZ, scaleW);
		
		this.transformer = transformer;
	}
	
	public double get(double x) {
		double value = 0.0;
		
		for (Octave noise : noises) {
			value = value + noise.get(x);
		}
		
		value = value / scaleMod;
		
		if (transformer != null) {
			value = transformer.transform(value, x, Double.NaN, Double.NaN, Double.NaN);
		}
		
		return value;
	}
	
	public double get(double x, double y) {
		double value = 0.0;
		
		for (Octave noise : noises) {
			value = value + noise.get(x, y);
		}
		
		value = value / scaleMod;
		
		if (transformer != null) {
			value = transformer.transform(value, x, y, Double.NaN, Double.NaN);
		}
		
		return value;
	}
	
	public double get(double x, double y, double z) {
		double value = 0.0;
		
		for (Octave noise : noises) {
			value = value + noise.get(x, y, z);
		}
		
		value = value / scaleMod;
		
		if (transformer != null) {
			value = transformer.transform(value, x, y, z, Double.NaN);
		}
		
		return value;
	}
	
	public double get(double x, double y, double z, double w) {
		double value = 0.0;
		
		for (Octave noise : noises) {
			value = value + noise.get(x, y, z, w);
		}
		
		value = value / scaleMod;
		
		if (transformer != null) {
			value = transformer.transform(value, x, y, z, w);
		}
		
		return value;
	}
	
	private NoiseWrapper createNoise(long seed) {
		switch (noiseType) {
			case IMPROVED:
				return new ImprovedWrapper(seed);
			
			case OPEN_SIMPLEX:
				return new OpenSimplexWrapper(seed);
			
			case SIMPLEX:
				return new SimplexWrapper(seed);
			
			default:
				return new OpenSimplexWrapper(seed);
			
		}
	}
	
	private void initOctaves() {
		noises = new ArrayList<>(octaves);
		
		double amplitude = 1.0;
		
		for (int octave = 0; octave < octaves; octave++) {
			noises.add(new Octave(
					createNoise(seed + octave),
					amplitude,
					scaleX * (octave + 1),
					scaleY * (octave + 1),
					scaleZ * (octave + 1),
					scaleW * (octave + 1)));
			
			amplitude = amplitude * persistence;
		}
		
		scaleMod = Math.pow(2, 1 - octaves) * (Math.pow(2, octaves) - 1);
	}
	
	private static final class ImprovedWrapper implements NoiseWrapper {
		private ImprovedNoise noise = null;
		
		public ImprovedWrapper(long seed) {
			super();
			
			this.noise = new ImprovedNoise(seed);
		}
		
		@Override
		public final double get(double x) {
			return noise.noise(x, 0, 0);
		}
		
		@Override
		public final double get(double x, double y) {
			return noise.noise(x, y, 0);
		}
		
		@Override
		public final double get(double x, double y, double z) {
			return noise.noise(x, y, z);
		}
		
		@Override
		public final double get(double x, double y, double z, double w) {
			return noise.noise(x, y, z);
		}
	}
	
	private interface NoiseWrapper {
		public double get(double x);
		
		public double get(double x, double y);
		
		public double get(double x, double y, double z);
		
		public double get(double x, double y, double z, double w);
	}
	
	private static final class Octave {
		private double amplitude = 1.0;
		private NoiseWrapper noise = null;
		private double scaleW = 1.0;
		private double scaleX = 1.0;
		private double scaleY = 1.0;
		private double scaleZ = 1.0;
		
		public Octave(
				NoiseWrapper noise,
				double amplitude,
				double scaleX,
				double scaleY,
				double scaleZ,
				double scaleW) {
			super();
			
			this.noise = noise;
			this.amplitude = amplitude;
			this.scaleX = scaleX;
			this.scaleY = scaleY;
			this.scaleZ = scaleZ;
			this.scaleW = scaleW;
		}
		
		public final double get(double x) {
			return noise.get(x * scaleX, 0) * amplitude;
		}
		
		public final double get(double x, double y) {
			return noise.get(x * scaleX, y * scaleY) * amplitude;
		}
		
		public final double get(double x, double y, double z) {
			return noise.get(x * scaleX, y * scaleY, z * scaleZ) * amplitude;
		}
		
		public final double get(double x, double y, double z, double w) {
			return noise.get(x * scaleX, y * scaleY, z * scaleZ, w * scaleW) * amplitude;
		}
	}
	
	private static final class OpenSimplexWrapper implements NoiseWrapper {
		private OpenSimplexNoise noise = null;
		
		public OpenSimplexWrapper(long seed) {
			super();
			
			this.noise = new OpenSimplexNoise(seed);
		}
		
		@Override
		public final double get(double x) {
			return noise.eval(x, 0);
		}
		
		@Override
		public final double get(double x, double y) {
			return noise.eval(x, y);
		}
		
		@Override
		public final double get(double x, double y, double z) {
			return noise.eval(x, y, z);
		}
		
		@Override
		public final double get(double x, double y, double z, double w) {
			return noise.eval(x, y, z, w);
		}
	}
	
	private static final class SimplexWrapper implements NoiseWrapper {
		private SimplexNoise noise = null;
		
		public SimplexWrapper(long seed) {
			super();
			
			this.noise = new SimplexNoise(seed);
		}
		
		@Override
		public final double get(double x) {
			return noise.noise(x, 0);
		}
		
		@Override
		public final double get(double x, double y) {
			return noise.noise(x, y);
		}
		
		@Override
		public final double get(double x, double y, double z) {
			return noise.noise(x, y, z);
		}
		
		@Override
		public final double get(double x, double y, double z, double w) {
			return noise.noise(x, y, z, w);
		}
	}
}
