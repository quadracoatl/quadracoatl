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

import org.quadracoatl.framework.support.hashers.MurmurHasher;

public class Noise {
	private List<NoiseWrapper> noises = null;
	private int octaves = 1;
	private double persistence = 0.5;
	private double scaleMod = 1.0;
	private double scaleW = 1.0;
	private double scaleX = 1.0;
	private double scaleY = 1.0;
	private double scaleZ = 1.0;
	private long seed = 0;
	
	public Noise(
			long seed,
			int octaves,
			double persistence,
			double scaleX,
			double scaleY,
			double scaleZ,
			double scaleW) {
		super();
		
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
			String seed,
			int octaves,
			double persistence,
			double scaleX,
			double scaleY,
			double scaleZ,
			double scaleW) {
		this(
				MurmurHasher.hash(seed),
				octaves,
				persistence,
				scaleX,
				scaleY,
				scaleZ,
				scaleW);
	}
	
	public double get(double x) {
		double value = 0.0;
		
		for (NoiseWrapper noise : noises) {
			value = value + noise.get(x);
		}
		
		return value / scaleMod;
	}
	
	public double get(double x, double y) {
		double value = 0.0;
		
		for (NoiseWrapper noise : noises) {
			value = value + noise.get(x, y);
		}
		
		return value / scaleMod;
	}
	
	public double get(double x, double y, double z) {
		double value = 0.0;
		
		for (NoiseWrapper noise : noises) {
			value = value + noise.get(x, y, z);
		}
		
		return value / scaleMod;
	}
	
	public double get(double x, double y, double z, double w) {
		double value = 0.0;
		
		for (NoiseWrapper noise : noises) {
			value = value + noise.get(x, y, z, w);
		}
		
		return value / scaleMod;
	}
	
	private void initOctaves() {
		noises = new ArrayList<>(octaves);
		
		double amplitude = 1.0;
		
		for (int octave = 0; octave < octaves; octave++) {
			noises.add(new NoiseWrapper(
					seed + octave,
					amplitude,
					scaleX * (octave + 1),
					scaleY * (octave + 1),
					scaleZ * (octave + 1),
					scaleW * (octave + 1)));
			
			amplitude = amplitude * persistence;
		}
		
		scaleMod = Math.pow(2, 1 - octaves) * (Math.pow(2, octaves) - 1);
	}
	
	private static final class NoiseWrapper {
		private double amplitude = 1.0;
		private OpenSimplexNoise noise = null;
		private double scaleW = 1.0;
		private double scaleX = 1.0;
		private double scaleY = 1.0;
		private double scaleZ = 1.0;
		
		public NoiseWrapper(
				long seed,
				double amplitude,
				double scaleX,
				double scaleY,
				double scaleZ,
				double scaleW) {
			super();
			
			this.amplitude = amplitude;
			this.scaleX = scaleX;
			this.scaleY = scaleY;
			this.scaleZ = scaleZ;
			this.scaleW = scaleW;
			
			noise = new OpenSimplexNoise(seed);
		}
		
		public final double get(double x) {
			return noise.eval(x * scaleX, 0) * amplitude;
		}
		
		public final double get(double x, double y) {
			return noise.eval(x * scaleX, y * scaleY) * amplitude;
		}
		
		public final double get(double x, double y, double z) {
			return noise.eval(x * scaleX, y * scaleY, z * scaleZ) * amplitude;
		}
		
		public final double get(double x, double y, double z, double w) {
			return noise.eval(x * scaleX, y * scaleY, z * scaleZ, w * scaleW) * amplitude;
		}
	}
}
