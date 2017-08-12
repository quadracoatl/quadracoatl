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

package org.quadracoatl.framework.common;

public final class VectorUtil {
	private static final int NEGATIVE_MASK = 0xfff00000;
	private static final int SIGN_MASK = 0x100000;
	private static final int VALUE_MASK = 0xfffff;
	
	private VectorUtil() {
		// No instance required.
	}
	
	public static long pack(int x, int y, int z) {
		long packed = 0;
		packed = packed | packInto21bits(x);
		packed = packed | ((long)packInto21bits(y) << 21);
		packed = packed | ((long)packInto21bits(z) << 42);
		
		return packed;
	}
	
	public static long pack(Vector3i vector) {
		return pack(vector.x, vector.y, vector.z);
	}
	
	public static Vector3i unpack(long value) {
		return unpackInto(value, new Vector3i());
	}
	
	public static Vector3i unpackInto(long value, Vector3i vector) {
		vector.x = unpack(value, 0);
		vector.y = unpack(value, 21);
		vector.z = unpack(value, 42);
		
		return vector;
	}
	
	private static int packInto21bits(int value) {
		int packed = value;
		packed = packed & VALUE_MASK;
		
		if (value < 0) {
			packed = packed | SIGN_MASK;
		}
		
		return packed;
	}
	
	private static int unpack(long value, int shift) {
		int shifted = (int)(value >>> shift);
		int unpacked = shifted & VALUE_MASK;
		
		if ((shifted & SIGN_MASK) == SIGN_MASK) {
			unpacked = unpacked | NEGATIVE_MASK;
		}
		
		return unpacked;
	}
}
