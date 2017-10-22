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

package org.quadracoatl.framework.logging.loggers;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import org.quadracoatl.framework.logging.LogLevel;
import org.quadracoatl.framework.logging.Logger;
import org.quadracoatl.framework.logging.LoggerFactory;

/**
 * The {@link AbstractLogger} provides basic logging functionality and makes it
 * easy to log messages to a stream.
 */
public abstract class AbstractLogger implements Logger {
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	protected LogLevel logLevel = null;
	protected String name = null;
	protected String outputName = null;
	
	/**
	 * Creates a new instance of {@link AbstractLogger}.
	 *
	 * @param name The name of this {@link AbstractLogger}.
	 */
	protected AbstractLogger(String name) {
		super();
		
		setName(name);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void debug(Object... message) {
		if (isEnabled(LogLevel.DEBUG)) {
			print(LogLevel.DEBUG, message);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void error(Object... message) {
		if (isEnabled(LogLevel.ERROR)) {
			print(LogLevel.ERROR, message);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fatal(Object... message) {
		if (isEnabled(LogLevel.FATAL)) {
			print(LogLevel.FATAL, message);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LogLevel getLogLevel() {
		return logLevel;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void info(Object... message) {
		if (isEnabled(LogLevel.INFO)) {
			print(LogLevel.INFO, message);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled(LogLevel requestedLogLevel) {
		if (logLevel != null) {
			return logLevel.allows(requestedLogLevel);
		} else {
			return LoggerFactory.getLogLevel(name).allows(requestedLogLevel);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void log(LogLevel logLevel, Object... message) {
		if (isEnabled(logLevel)) {
			print(logLevel, message);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String name) {
		this.name = name;
		this.outputName = name;
		
		if (name != null) {
			int lastDotIndex = name.lastIndexOf('.');
			
			if (lastDotIndex >= 0) {
				outputName = name.substring(lastDotIndex + 1);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void trace(Object... message) {
		if (isEnabled(LogLevel.TRACE)) {
			print(LogLevel.TRACE, message);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void warn(Object... message) {
		if (isEnabled(LogLevel.WARN)) {
			print(LogLevel.WARN, message);
		}
	}
	
	/**
	 * Appends the given {@link Object} to the given {@link StringBuilder}.
	 * <p>
	 * This method is invoked for each argument passed to the
	 * {@link #createLogMessage(LogLevel, Object...)} function.
	 * 
	 * @param logMessage the {@link StringBuilder}.
	 * @param object the {@link Object} to append.
	 */
	protected void appendObject(StringBuilder logMessage, Object object) {
		if (object instanceof Throwable) {
			Throwable throwable = (Throwable)object;
			
			while (throwable != null) {
				logMessage.append("\n\t");
				logMessage.append(throwable.toString());
				
				if (throwable.getStackTrace() != null) {
					for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
						logMessage.append("\n\t\t");
						logMessage.append(stackTraceElement.toString());
					}
				} else {
					logMessage.append("\n\t\tNo stacktrace available.");
				}
				
				throwable = throwable.getCause();
			}
		} else {
			logMessage.append(Objects.toString(object));
		}
	}
	
	/**
	 * Creates a new log message from the given {@link LogLevel} and message.
	 * The created message has the format:
	 * "{@code [level][thread][name] message...}" with exceptions appearing on
	 * new lines with their full stacktrace.
	 * 
	 * @param logLevel The {@link LogLevel}.
	 * @param message The message.
	 * @return The created log message in the format
	 *         "{@code [level][thread][name] message...}".
	 */
	protected String createLogMessage(LogLevel logLevel, Object... message) {
		StringBuilder logMessage = new StringBuilder();
		
		logMessage.append("[");
		logMessage.append(dateFormat.format(new Date()));
		logMessage.append("]");
		
		if (logLevel != null) {
			logMessage.append("[");
			logMessage.append(logLevel.toString());
			if (logLevel == LogLevel.INFO || logLevel == LogLevel.WARN) {
				logMessage.append(" ");
			}
			logMessage.append("]");
		}
		
		logMessage.append("[");
		if (Thread.currentThread().getName() != null) {
			logMessage.append(Thread.currentThread().getName());
		} else {
			logMessage.append(Thread.currentThread().getId());
		}
		logMessage.append("]");
		
		if (name != null) {
			logMessage.append("[").append(outputName).append("]");
		}
		
		if (message != null && message.length > 0) {
			logMessage.append(" ");
			
			for (Object item : message) {
				appendObject(logMessage, item);
			}
		}
		
		return logMessage.toString();
	}
	
	/**
	 * Invoked from all logging methods to actually log the message.
	 * <p>
	 * Extending classes can hook their custom logic here. This method is only
	 * invoked if the given {@link LogLevel} is actually being logged.
	 * 
	 * @param logLevel The {@link LogLevel}.
	 * @param message The message to log.
	 */
	protected abstract void print(LogLevel logLevel, Object... message);
	
	/**
	 * A convenience method to log the message to a {@link PrintStream}.
	 * 
	 * @param logLevel The {@link LogLevel}.
	 * @param stream The {@link PrintStream} to receive the log message.
	 * @param message The message to log.
	 */
	protected void print(LogLevel logLevel, PrintStream stream, Object... message) {
		stream.println(createLogMessage(logLevel, message));
	}
}
