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

package org.quadracoatl.framework.support.randoms;

import org.quadracoatl.framework.support.hashers.MurmurHasher;

/**
 * {@link Random} is a simple utility class which provides a not cryptographic
 * secure pseudo random number generator.
 */
public class Random {
	/** The {@link Xorshift1024Star} that is used as source. */
	private Xorshift1024Star xorshift = null;
	
	/**
	 * Creates a new instance of {@link Random}.
	 *
	 * @param seed The seed.
	 */
	public Random(long seed) {
		super();
		
		xorshift = new Xorshift1024Star(seed);
	}
	
	/**
	 * Creates a new instance of {@link Random}.
	 *
	 * @param seed The seed.
	 */
	public Random(String seed) {
		this(MurmurHasher.hash(seed));
	}
	
	/**
	 * Gets the next value, a value between {@code -1d} and {@code 1d}.
	 *
	 * @return The next value, a value between {@code -1d} and {@code 1d}.
	 */
	public double next() {
		return xorshift.next() / (double)Long.MAX_VALUE;
	}
}
