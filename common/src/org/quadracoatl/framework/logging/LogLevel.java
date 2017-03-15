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
 * The {@link LogLevel} at which log messages can be logged.
 */
public enum LogLevel {
	/**
	 * The log messages are required for debugging.
	 */
	DEBUG(5),
	
	/**
	 * The log messages contain error messages, which notify that something was
	 * very wrong, though, the application can continue to function, but maybe
	 * with limited functionality.
	 */
	ERROR(2),
	
	/**
	 * The log messages contain fatal error messages, which means that execution
	 * of the application can not be guaranteed anymore and might be interrupted
	 * (either willfully or as consequence of the fatal errors) at any time.
	 */
	FATAL(1),
	
	/**
	 * The log messages contain information which are required to diagnose
	 * problems.
	 */
	INFO(4),
	
	/**
	 * The log messages are required for developing and diagnosing.
	 */
	TRACE(6),
	
	/**
	 * The log messages contain warnings, which notify that something is wrong
	 * and should be taken care of some time in the future, execution of the
	 * application can continue without incident.
	 */
	WARN(3);
	
	/**
	 * The value of this level, which is used to determine if a level does allow
	 * another to be logged.
	 */
	private int value = 0;
	
	/**
	 * Creates a new instance of {@link LogLevel}.
	 *
	 * @param value The value of this level, which is used to determine if a
	 *        level does allow another to be logged.
	 */
	private LogLevel(int value) {
		this.value = value;
	}
	
	/**
	 * Checks if the current {@link LogLevel} does allow to log the requested
	 * {@link LogLevel}.
	 * 
	 * @param requestedLevel The {@link LogLevel} which should be logged.
	 * @return {@code true} if the current {@link LogLevel} does allow to log
	 *         the requested {@link LogLevel}.
	 */
	public boolean allows(LogLevel requestedLevel) {
		if (requestedLevel == null) {
			return false;
		}
		
		return requestedLevel.value <= value;
	}
}
