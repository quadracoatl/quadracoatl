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
 * The {@link Logger} provides all the functionality needed to log messages.
 */
public interface Logger {
	/**
	 * Logs the given message at the {@link LogLevel#DEBUG} level.
	 * <p>
	 * If the {@link LogLevel#DEBUG} is not enabled on this {@link Logger}, this
	 * call has no effect.
	 * 
	 * @param message The message to log, can be any {@link Object}s, can also
	 *        be {@code null} to log nothing.
	 */
	public void debug(Object... message);
	
	/**
	 * Logs the given message at the {@link LogLevel#ERROR} level.
	 * <p>
	 * If the {@link LogLevel#ERROR} is not enabled on this {@link Logger}, this
	 * call has no effect.
	 * 
	 * @param message The message to log, can be any {@link Object}s, can also
	 *        be {@code null} to log nothing.
	 */
	public void error(Object... message);
	
	/**
	 * Logs the given message at the {@link LogLevel#FATAL} level.
	 * <p>
	 * If the {@link LogLevel#FATAL} is not enabled on this {@link Logger}, this
	 * call has no effect.
	 * 
	 * @param message The message to log, can be any {@link Object}s, can also
	 *        be {@code null} to log nothing.
	 */
	public void fatal(Object... message);
	
	/**
	 * Gets the {@link LogLevel} of this {@link Logger}, can return {@code null}
	 * to signify that no {@link LogLevel} is set, and that the {@link LogLevel}
	 * of the {@link LoggerFactory} is used.
	 * 
	 * @return The {@link LogLevel} of this {@link Logger}, {@code null} if the
	 *         value {@link LogLevel} of the {@link LoggerFactory} is used.
	 * @see #setLogLevel(LogLevel)
	 */
	public LogLevel getLogLevel();
	
	/**
	 * Gets the name of this {@link Logger}.
	 * <p>
	 * The name may be used to identify the logged messages.
	 * 
	 * @return The name of this {@link Logger}, can {@code null}.
	 * @see #setName(String)
	 */
	public String getName();
	
	/**
	 * Logs the given message at the {@link LogLevel#INFO} level.
	 * <p>
	 * If the {@link LogLevel#INFO} is not enabled on this {@link Logger}, this
	 * call has no effect.
	 * 
	 * @param message The message to log, can be any {@link Object}s, can also
	 *        be {@code null} to log nothing.
	 */
	public void info(Object... message);
	
	/**
	 * Checks if the given {@link LogLevel} will be logged by this
	 * {@link Logger}.
	 * 
	 * @param requestedLogLevel The {@link LogLevel} to check.
	 * @return {@code true} if this {@link Logger} is configured to log the
	 *         {@link LogLevel}.
	 * @see LogLevel#allows(LogLevel)
	 */
	public boolean isEnabled(LogLevel requestedLogLevel);
	
	/**
	 * Logs the given message at the given {@link LogLevel}.
	 * <p>
	 * If the given {@link LogLevel} is not enabled on this {@link Logger}, this
	 * call has no effect.
	 * 
	 * @param logLevel The {@link LogLevel} to use.
	 * @param message The message to log, can be any {@link Object}s, can also
	 *        be {@code null} to log nothing.
	 */
	public void log(LogLevel logLevel, Object... message);
	
	/**
	 * Sets the {@link LogLevel} at which this {@link Logger} should log
	 * messages, can be {@code null} to use the {@link LogLevel} of the
	 * {@link LoggerFactory}.
	 * 
	 * @param logLevel The {@link LogLevel} at which this {@link Logger} should
	 *        log messages, {@code null} to use the {@link LogLevel} of the
	 *        {@link LoggerFactory}.
	 */
	public void setLogLevel(LogLevel logLevel);
	
	/**
	 * Sets the name of this {@link Logger}.
	 * <p>
	 * The name may be used to identify the logged messages.
	 * 
	 * @param name The name of this {@link Logger}, can be {@code null} for
	 *        none.
	 */
	public void setName(String name);
	
	/**
	 * Logs the given message at the {@link LogLevel#TRACE} level.
	 * <p>
	 * If the {@link LogLevel#TRACE} is not enabled on this {@link Logger}, this
	 * call has no effect.
	 * 
	 * @param message The message to log, can be any {@link Object}s, can also
	 *        be {@code null} to log nothing.
	 */
	public void trace(Object... message);
	
	/**
	 * Logs the given message at the {@link LogLevel#WARN} level.
	 * <p>
	 * If the {@link LogLevel#WARN} is not enabled on this {@link Logger}, this
	 * call has no effect.
	 * 
	 * @param message The message to log, can be any {@link Object}s, can also
	 *        be {@code null} to log nothing.
	 */
	public void warn(Object... message);
}
