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

package org.quadracoatl.jmeclient.utils;

import java.util.HashMap;
import java.util.Map;

public final class Timer {
	private static Map<String, Double> averageTimes = new HashMap<>();
	private static Map<String, Long> timers = new HashMap<>();
	
	private Timer() {
		// Static utility.
	}
	
	public static void start(String timerName) {
		timers.put(timerName, Long.valueOf(System.nanoTime()));
	}
	
	public static void stop(String timerName, String message) {
		long end = System.nanoTime();
		long start = timers.get(timerName).longValue();
		
		double duration = end - start;
		duration = Math.round(duration / 1000d) / 1000d;
		
		double average = duration;
		
		if (averageTimes.containsKey(timerName)) {
			average = averageTimes.get(timerName).doubleValue();
			average = average + duration;
			average = average / 2;
		}
		
		averageTimes.put(timerName, Double.valueOf(average));
		
		System.out.printf(message + ": %9.3fms | %9.3fms |%n", Double.valueOf(duration), Double.valueOf(average));
	}
}
