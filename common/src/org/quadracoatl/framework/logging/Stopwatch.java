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

package org.quadracoatl.framework.logging;

import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;

/**
 * A static utility which allows to fast and easily measure the time between
 * calls.
 */
public final class Stopwatch {
	/** The value that is returned if the stopwatch has not been started. */
	public static final double STOPWATCH_NOT_STARTED = -1.0d;
	
	/** The start times by name. */
	private static TObjectLongMap<String> startedStopwatches = new TObjectLongHashMap<>();
	
	/**
	 * No instance needed, static utility.
	 */
	private Stopwatch() {
		// No instance needed.
	}
	
	public static final void start(String name) {
		startedStopwatches.put(name, System.nanoTime());
	}
	
	public static final void start(String name, Logger logger, LogLevel logLevel) {
		if (logger.isEnabled(logLevel)) {
			start(name);
		}
	}
	
	public static final double stop(String name) {
		long end = System.nanoTime();
		long start = startedStopwatches.remove(name);
		
		if (start != Constants.DEFAULT_LONG_NO_ENTRY_VALUE) {
			return (end - start) / 1000 / 1000d;
		}
		
		return STOPWATCH_NOT_STARTED;
	}
	
	public static final void stop(String name, Logger logger, LogLevel logLevel) {
		if (logger.isEnabled(logLevel)) {
			double duration = stop(name);
			
			logger.log(logLevel, "Stopwatch \"", name, "\": ", Double.valueOf(duration), "ms");
		} else {
			startedStopwatches.remove(name);
		}
	}
}
