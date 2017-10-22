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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.quadracoatl.framework.logging.providers.NullLoggerProvider;
import org.quadracoatl.framework.logging.providers.StandardOutLoggerProvider;

/**
 * The {@link LoggerFactory} does provide {@link Logger}s and a global
 * {@link LogLevel} to use.
 */
public final class LoggerFactory {
	private static LoggerProvider loggerProvider = new StandardOutLoggerProvider();
	private static volatile LogLevel logLevel = LogLevel.ERROR;
	private static Map<String, LogLevel> logLevels = new HashMap<>();
	
	/**
	 * No instance needed, static utility.
	 */
	private LoggerFactory() {
		// No instance needed.
	}
	
	/**
	 * Gets the {@link Logger} for the given {@link Object}.
	 * <p>
	 * The returned {@link Logger} will have the
	 * {@link LogUtil#getIdentity(Object)} set as {@link Logger#getName() name}.
	 * 
	 * @param instance The {@link Object} for which to get the {@link Logger}.
	 * @return The {@link Logger} for the given {@link Object}.
	 */
	public static final Logger getLogger(Object instance) {
		return getLogger(LogUtil.getIdentity(instance));
	}
	
	/**
	 * Gets the {@link Logger} with the given name.
	 * 
	 * @param name The name for the {@link Logger}.
	 * @return The {@link Logger} with the given name.
	 */
	public static final Logger getLogger(String name) {
		return loggerProvider.provideLogger(name);
	}
	
	/**
	 * Gets the currently used {@link LoggerProvider}.
	 * 
	 * @return The currently used {@link LoggerProvider}.
	 */
	public static LoggerProvider getLoggerProvider() {
		return loggerProvider;
	}
	
	/**
	 * Gets the currently used {@link LogLevel}.
	 * 
	 * @return The currently used {@link LogLevel}.
	 */
	public static final LogLevel getLogLevel() {
		return logLevel;
	}
	
	/**
	 * Gets the {@link LogLevel} for the given name, returns the global one if
	 * there is none.
	 * 
	 * @param loggerName The name of the logger.
	 * @return The {@link LogLevel} for the given name.
	 */
	public static final LogLevel getLogLevel(String loggerName) {
		if (loggerName != null) {
			synchronized (logLevels) {
				if (!logLevels.isEmpty()) {
					for (Entry<String, LogLevel> entry : logLevels.entrySet()) {
						if (loggerName.startsWith(entry.getKey())) {
							return entry.getValue();
						}
					}
				}
			}
		}
		
		return logLevel;
	}
	
	/**
	 * Sets the to be used {@link LoggerProvider}.
	 * 
	 * @param loggerProvider The to be used {@link LoggerProvider}. Can be
	 *        {@code null} to fall back to the {@link NullLoggerProvider}.
	 */
	public static void setLoggerProvider(LoggerProvider loggerProvider) {
		if (loggerProvider == null) {
			LoggerFactory.loggerProvider = new NullLoggerProvider();
		} else {
			LoggerFactory.loggerProvider = loggerProvider;
		}
	}
	
	/**
	 * Sets the to be used {@link LogLevel}.
	 * 
	 * @param logLevel The to be used {@link LogLevel}, can not be {@code null}.
	 * @throws IllegalArgumentException If the given {@link LogLevel} is
	 *         {@code null}.
	 */
	public static final void setLogLevel(LogLevel logLevel) {
		if (logLevel == null) {
			throw new IllegalArgumentException("The global log-level can not be set to null.");
		}
		
		LoggerFactory.logLevel = logLevel;
	}
	
	/**
	 * Sets the {@link LogLevel} for the given package- or classname.
	 * 
	 * @param packageName The package- or classname.
	 * @param logLevel The {@link LogLevel} to set. {@code null} to unset it.
	 * @throws IllegalArgumentException If the given packagename is {@code null}
	 *         .
	 */
	public static final void setLogLevel(String packageName, LogLevel logLevel) {
		if (packageName == null) {
			throw new IllegalArgumentException("The package name cannot be null.");
		}
		
		synchronized (logLevels) {
			if (logLevel != null) {
				logLevels.put(packageName, logLevel);
			} else {
				logLevels.remove(packageName);
			}
		}
	}
}
