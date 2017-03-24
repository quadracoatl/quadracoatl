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

package org.quadracoatl.framework.common;

public final class MathUtil {
	private MathUtil() {
		// No instance needed.
	}
	
	public static boolean between(double value, double min, double max) {
		if (min > max) {
			return value <= min && value >= max;
		} else {
			return value >= min && value <= max;
		}
	}
	
	public static final long cantorPairing(long firstValue, long secondValue) {
		return ((firstValue + secondValue) * (firstValue + secondValue + 1)) / 2 + secondValue;
	}
	
	public static double max(double... values) {
		double max = Double.MIN_VALUE;
		
		for (double value : values) {
			if (value > max) {
				max = value;
			}
		}
		
		return max;
	}
	
	public static final int max(int... values) {
		int max = Integer.MIN_VALUE;
		
		for (int value : values) {
			if (value > max) {
				max = value;
			}
		}
		
		return max;
	}
	
	/**
	 * Converts the given double value (between {@code 0.0d} and {@code 1.0d}
	 * and converts it into radians.
	 * 
	 * @param value the value, between {@code 0.0d} and {@code 1.0d}.
	 * @return the value in radians.
	 */
	public static final double toRadians(double value) {
		return value * Math.PI * 2;
	}
}
