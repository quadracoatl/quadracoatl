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

package org.quadracoatl.framework.support;

import org.quadracoatl.framework.logging.Logger;
import org.quadracoatl.framework.logging.LoggerFactory;

/**
 * A {@link Thread} extension which records a stacktrace when it is started and
 * outputs this together with any thrown exception which terminated this
 * {@link Thread}.
 */
public class TraceableThread extends Thread {
	/** The static {@link Logger} instance. */
	protected static final Logger LOGGER = LoggerFactory.getLogger(TraceableThread.class);
	
	/** The {@link Exception} which holds the "start" stacktrace. */
	protected Exception startException = null;
	
	/** The {@link Runnable} to execute. */
	protected Runnable target = null;
	
	/**
	 * Creates a new instance of {@link TraceableThread}.
	 *
	 * @param target The {@link Runnable} to run.
	 * @param name The name of the thread.
	 */
	public TraceableThread(Runnable target, String name) {
		super(name);
		
		this.target = target;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		try {
			target.run();
		} catch (Throwable th) {
			Exception exception = new Exception(
					"Thread \"" + getName() + "\" has terminated with an exception: " + th.getMessage(),
					th);
			exception.setStackTrace(startException.getStackTrace());
			
			LOGGER.fatal(exception);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void start() {
		startException = new Exception();
		
		super.start();
	}
}
