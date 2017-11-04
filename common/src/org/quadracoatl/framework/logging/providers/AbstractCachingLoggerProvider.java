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

package org.quadracoatl.framework.logging.providers;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.quadracoatl.framework.logging.Logger;
import org.quadracoatl.framework.logging.LoggerProvider;

/**
 * An abstract {@link LoggerProvider} implementation which caches the created
 * {@link Logger} s weakly.
 */
public abstract class AbstractCachingLoggerProvider implements LoggerProvider {
	/** The cache of the instances. */
	protected Map<String, WeakReference<Logger>> cache = new HashMap<>();
	
	/**
	 * Creates a new instance of {@link AbstractCachingLoggerProvider}.
	 */
	protected AbstractCachingLoggerProvider() {
		super();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Logger provideLogger(String name) {
		synchronized (cache) {
			// First, try to get the Logger from the cache.
			WeakReference<Logger> weakLogger = cache.get(name);
			
			if (weakLogger != null) {
				Logger logger = weakLogger.get();
				
				if (logger != null) {
					return logger;
				}
			}
			
			// Okay, no cached instance, create one.
			Logger logger = createLogger(name);
			cache.put(name, new WeakReference<>(logger));
			
			return logger;
		}
	}
	
	/**
	 * Creates the {@link Logger} for the given name.
	 * <p>
	 * This method is only called when there isn't a cached instance available.
	 * 
	 * @param name the name.
	 * @return the {@link Logger} for the given name.
	 */
	protected abstract Logger createLogger(String name);
}
