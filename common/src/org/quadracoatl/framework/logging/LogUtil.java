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

package org.quadracoatl.framework.logging;

/**
 * The {@link LogUtil} provides common functionality to be used in combination
 * with the logging framework.
 */
public final class LogUtil {
	/**
	 * No instance needed, static utility.
	 */
	private LogUtil() {
		// No instance needed.
	}
	
	/**
	 * Gets the identity hashcode of the given {@link Object} as 8-digit
	 * hex-number with the leading classname in the format
	 * "{@code class@11223344}". If the given {@link Object} is {@code null},
	 * {@code "null"} will be returned.
	 * 
	 * @param object The {@link Object} whose identity to get.
	 * @return The identity hashcode of the given {@link Object} as 8-digit
	 *         hex-number with the leading classname in the format
	 *         "{@code class@11223344}", {@code "null"} if the {@link Object} is
	 *         {@code null}.
	 */
	public static final String getIdentity(Object object) {
		if (object == null) {
			return "null";
		}
		
		return object.getClass().getSimpleName()
				+ "@"
				+ String.format("%08x", Integer.valueOf(System.identityHashCode(object)));
	}
}
