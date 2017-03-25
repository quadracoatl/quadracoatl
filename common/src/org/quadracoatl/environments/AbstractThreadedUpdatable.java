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

package org.quadracoatl.environments;

import java.util.ArrayList;
import java.util.List;

import org.quadracoatl.framework.logging.Logger;
import org.quadracoatl.framework.logging.LoggerFactory;
import org.quadracoatl.framework.scheduler.Scheduler;

public abstract class AbstractThreadedUpdatable {
	protected List<Runnable> invokeNext = new ArrayList<>();
	protected Logger logger = null;
	protected volatile boolean running = false;
	protected Scheduler scheduler = new Scheduler();
	private String name = null;
	private volatile boolean started = false;
	private Thread thread = null;
	private int updatesPerSecond = 0;
	
	protected AbstractThreadedUpdatable(String name, int updatesPerSecond) {
		super();
		
		this.name = name;
		this.updatesPerSecond = updatesPerSecond;
		
		logger = LoggerFactory.getLogger(name);
	}
	
	public String getName() {
		return name;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	public int getUpdatesPerSecond() {
		return updatesPerSecond;
	}
	
	public void invokeNextUpdate(Runnable runnable) {
		synchronized (invokeNext) {
			invokeNext.add(runnable);
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public void setUpdatesPerSecond(int updatesPerSecond) {
		this.updatesPerSecond = updatesPerSecond;
	}
	
	public void start() {
		if (running) {
			return;
		}
		
		running = true;
		
		thread = new Thread(this::runUpdateLoop);
		thread.setName(name);
		thread.start();
		
		try {
			while (!started) {
				Thread.sleep(16);
			}
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}
	
	public void stop() {
		if (!running) {
			return;
		}
		
		running = false;
		
		try {
			thread.join();
		} catch (InterruptedException e) {
			logger.error(e);
		}
		
		thread = null;
	}
	
	protected void destroy() {
		logger.info("Destroying...");
	}
	
	protected void init() {
		logger.info("Initializing...");
	}
	
	protected void update(long elapsedNanoSecondsSinceLastUpdate) {
		synchronized (invokeNext) {
			for (Runnable runnable : invokeNext) {
				runnable.run();
			}
			
			invokeNext.clear();
		}
		
		scheduler.step(elapsedNanoSecondsSinceLastUpdate);
	}
	
	private void runUpdateLoop() {
		try {
			logger.info("Starting.");
			
			init();
			
			started = true;
			
			long lastRun = System.nanoTime();
			
			logger.info("Starting loop.");
			
			while (running) {
				long start = System.nanoTime();
				
				update(start - lastRun);
				
				lastRun = System.nanoTime();
				
				long end = System.nanoTime();
				long duration = end - start;
				duration = duration / 1000 / 1000;
				
				long pause = (1000 / updatesPerSecond) - duration;
				
				if (pause > 0) {
					Thread.sleep(pause);
				}
			}
			
			started = false;
			
			logger.info("Ending loop.");
			
			destroy();
			
			logger.info("Stopped.");
		} catch (Throwable th) {
			logger.fatal("Exception in thread occurred, exiting.", th);
			return;
		} finally {
			running = false;
			started = false;
		}
	}
}
