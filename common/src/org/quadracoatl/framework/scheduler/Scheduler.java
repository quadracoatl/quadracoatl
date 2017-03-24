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

package org.quadracoatl.framework.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quadracoatl.framework.logging.Logger;
import org.quadracoatl.framework.logging.LoggerFactory;

/**
 * The {@link Scheduler} allows to schedule jobs to be executed at a regular
 * interval.
 */
public class Scheduler {
	private final Logger LOGGER = LoggerFactory.getLogger(this);
	private List<Runnable> nextJobs = new ArrayList<>();
	private List<ScheduledJob> scheduledJobs = new ArrayList<>();
	private Map<String, ScheduledJob> scheduledJobsByName = new HashMap<>();
	
	/**
	 * Creates a new instance of {@link Scheduler}.
	 */
	public Scheduler() {
		super();
	}
	
	/**
	 * Schedules the given job for execution.
	 * 
	 * @param name The name of the job. The name is used to uniquely identify
	 *        the job.
	 * @param interval The interval at which to execute the job, in
	 *        milliseconds.
	 * @param overshootPolicy The {@link OvershootPolicy} to apply.
	 * @param job The job itself.
	 */
	public void schedule(String name, long interval, OvershootPolicy overshootPolicy, Runnable job) {
		ScheduledJob scheduledJob = new ScheduledJob(name, interval, overshootPolicy, job);
		
		synchronized (scheduledJobs) {
			unschedule(name);
			
			LOGGER.debug("Scheduling job \"", name, "\" with interval ", Long.valueOf(interval), " and policy ", overshootPolicy, ".");
			
			scheduledJobsByName.put(name, scheduledJob);
			scheduledJobs.add(scheduledJob);
		}
	}
	
	/**
	 * Schedules the given job to be executed on the next {@link #step(long)}.
	 * 
	 * @param job The job to execute.
	 */
	public void scheduleNext(Runnable job) {
		synchronized (nextJobs) {
			LOGGER.debug("Scheduling next job.");
			nextJobs.add(job);
		}
	}
	
	/**
	 * Perform a step and execute the jobs.
	 * 
	 * @param elapsedNanoSecondsSinceLastStep The elapsed time since the last
	 *        call, in nanoseconds.
	 */
	public void step(long elapsedNanoSecondsSinceLastStep) {
		synchronized (nextJobs) {
			while (!nextJobs.isEmpty()) {
				LOGGER.debug("Executing next job.");
				
				try {
					nextJobs.remove(0).run();
				} catch (Throwable th) {
					LOGGER.error("Executing next job failed: ", th);
				}
			}
		}
		synchronized (scheduledJobs) {
			for (int index = 0; index < scheduledJobs.size(); index++) {
				scheduledJobs.get(index).step(elapsedNanoSecondsSinceLastStep);
			}
			
			boolean executeMore = false;
			
			do {
				executeMore = false;
				
				for (int index = 0; index < scheduledJobs.size(); index++) {
					ScheduledJob scheduledJob = scheduledJobs.get(index);
					
					if (scheduledJob.shouldBeExecuted()) {
						LOGGER.debug("Executing job \"", scheduledJob.getName(), "\".");
						
						try {
							scheduledJob.execute();
						} catch (Throwable th) {
							LOGGER.error("Executing job \"", scheduledJob.getName(), "\" failed: ", th);
						}
						
						executeMore = executeMore || scheduledJob.shouldBeExecuted();
					}
				}
			} while (executeMore);
		}
	}
	
	/**
	 * Unschedule the job with the given name.
	 * 
	 * @param name The name of the job.
	 */
	public void unschedule(String name) {
		synchronized (scheduledJobs) {
			if (scheduledJobsByName.containsKey(name)) {
				LOGGER.debug("Unscheduling job \"", name, "\".");
				scheduledJobs.remove(scheduledJobsByName.remove(name));
			}
		}
	}
}
