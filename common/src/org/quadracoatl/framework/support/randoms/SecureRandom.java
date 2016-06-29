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

/**
 * {@link SecureRandom} is a simple utility class which provides a cryptographic
 * secure pseudo random number generator.
 */
public class SecureRandom {
	/** The {@link java.security.SecureRandom} that is used as source. */
	private java.security.SecureRandom source = null;
	
	/** The {@link RandomValueTransformer} which transforms the values. */
	private RandomValueTransformer transformer = null;
	
	/**
	 * Creates a new instance of {@link SecureRandom}.
	 */
	public SecureRandom() {
		super();
		
		source = new java.security.SecureRandom();
	}
	
	/**
	 * Creates a new instance of {@link SecureRandom}.
	 *
	 * @param transformer The transforming function.
	 */
	public SecureRandom(RandomValueTransformer transformer) {
		this();
		
		this.transformer = transformer;
	}
	
	/**
	 * Gets the next value, a value between {@code -1d} and {@code 1d}.
	 *
	 * @return The next value, a value between {@code -1d} and {@code 1d}.
	 */
	public double next() {
		double value = (source.nextDouble() - 0.5d) * 2;
		
		if (transformer != null) {
			value = transformer.transform(value);
		}
		
		return value;
	}
}
