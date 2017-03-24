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

package org.quadracoatl.framework.logging.loggers;

import org.quadracoatl.framework.logging.LogLevel;
import org.quadracoatl.framework.logging.Logger;

/**
 * A {@link Logger} implementation which logs all messages to {@link System#out}
 * .
 */
public class StandardOutLogger extends AbstractLogger {
	/**
	 * Creates a new instance of {@link StandardOutLogger}.
	 *
	 * @param name The name to use.
	 */
	public StandardOutLogger(String name) {
		super(name);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void print(LogLevel logLevel, Object... message) {
		print(logLevel, System.out, message);
	}
}
