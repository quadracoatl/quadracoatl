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
	 * hex-number with the leading fully qualified classname in the format
	 * "{@code com.package.class@11223344}". If the given {@link Object} is a
	 * {@link Class} the fully qualified classname will be returned. If the
	 * given {@link Object} is {@code null}, {@code "null"} will be returned.
	 * 
	 * @param object The {@link Object} whose identity to get.
	 * @return The identity hashcode of the given {@link Object} as 8-digit
	 *         hex-number with the leading classname in the format
	 *         "{@code com.package.class@11223344}", {@code "null"} if the
	 *         {@link Object} is {@code null}.
	 */
	public static final String getIdentity(Object object) {
		if (object == null) {
			return "null";
		}
		
		if (object instanceof Class<?>) {
			return ((Class<?>)object).getName();
		}
		
		return object.getClass().getName()
				+ "@"
				+ getIdentityAsHex(object);
	}
	
	/**
	 * Gets the identity hashcode of the given {@link Object} as 8-digit
	 * hex-number.
	 * 
	 * @param object The {@link Object} for which to get the identity as
	 *        hex-number.
	 * @return The identity hashcode of the given {@link Object} as 8-digit
	 *         hex-number.
	 */
	public static final String getIdentityAsHex(Object object) {
		return String.format("%08x", Integer.valueOf(System.identityHashCode(object)));
	}
	
	/**
	 * Gets the identity hashcode of the given {@link Object} as 8-digit
	 * hex-number with the leading simple classname in the format
	 * "{@code class@11223344}". If the given {@link Object} is a {@link Class}
	 * the simple classname will be returned. If the given {@link Object} is
	 * {@code null}, {@code "null"} will be returned.
	 * 
	 * @param object The {@link Object} whose identity to get.
	 * @return The identity hashcode of the given {@link Object} as 8-digit
	 *         hex-number with the leading classname in the format
	 *         "{@code class@11223344}", {@code "null"} if the {@link Object} is
	 *         {@code null}.
	 */
	public static final String getSimpleIdentity(Object object) {
		if (object == null) {
			return "null";
		}
		
		if (object instanceof Class<?>) {
			return ((Class<?>)object).getSimpleName();
		}
		
		return object.getClass().getSimpleName()
				+ "@"
				+ getIdentityAsHex(object);
	}
}
