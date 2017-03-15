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

/**
 * Represents a scheduled job.
 */
class ScheduledJob {
	private long elapsedTime = 0l;
	private long interval = 0l;
	private Runnable job = null;
	private String name = null;
	private OvershootPolicy overshootPolicy = OvershootPolicy.RUN_ONCE;
	private int requiredExecutions = 0;
	
	/**
	 * Creates a new instance of {@link ScheduledJob}.
	 *
	 * @param name The name of the job.
	 * @param interval The interval at which to execute the job, in
	 *        milliseconds.
	 * @param overshootPolicy The {@link OvershootPolicy} to apply.
	 * @param job The job itself.
	 */
	public ScheduledJob(String name, long interval, OvershootPolicy overshootPolicy, Runnable job) {
		super();
		
		this.name = name;
		this.interval = interval * 1000 * 1000;
		this.overshootPolicy = overshootPolicy;
		this.job = job;
	}
	
	/**
	 * Executes this job.
	 */
	public void execute() {
		requiredExecutions = Math.max(0, requiredExecutions - 1);
		
		job.run();
	}
	
	/**
	 * Gets the name of this job.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets if this job should be executed.
	 * 
	 * @return {@code true} if this job should be executed.
	 */
	public boolean shouldBeExecuted() {
		return requiredExecutions > 0;
	}
	
	/**
	 * Perform a step.
	 *
	 * @param elapsedNanoSecondsSinceLastStep The elapsed time since the last
	 *        call, in nanoseconds.
	 */
	public void step(long elapsedNanoSecondsSinceLastStep) {
		if (interval == 0l) {
			requiredExecutions = requiredExecutions + 1;
			return;
		}
		
		elapsedTime = elapsedTime + elapsedNanoSecondsSinceLastStep;
		
		if (elapsedTime >= interval) {
			if (overshootPolicy == OvershootPolicy.CATCH_UP) {
				requiredExecutions = requiredExecutions + (int)(elapsedTime / interval);
			} else {
				requiredExecutions = requiredExecutions + 1;
			}
			
			elapsedTime = elapsedTime % interval;
		}
	}
}
