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
 * The {@link LoggerProvider} provides {@link Logger} implementations.
 */
public interface LoggerProvider {
	/**
	 * Provides the {@link Logger} for the given name.
	 * <p>
	 * Implementations can choose freely whether a new, cached or "recycled"
	 * instance is returned.
	 * 
	 * @param name The name of the {@link Logger}.
	 * @return The provided {@link Logger}.
	 */
	public Logger provideLogger(String name);
}
