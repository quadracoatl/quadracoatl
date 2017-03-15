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

package org.quadracoatl.server;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.quadracoatl.environments.ServerEnvironment;
import org.quadracoatl.framework.logging.LogLevel;
import org.quadracoatl.framework.logging.Logger;
import org.quadracoatl.framework.logging.LoggerFactory;
import org.quadracoatl.framework.support.Arguments;
import org.quadracoatl.interlayer.InterlayerException;
import org.quadracoatl.interlayer.kryonet.KryonetServer;

public final class Main {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
	private Main() {
	}
	
	public static final void main(String[] args) {
		LoggerFactory.setLogLevel(LogLevel.TRACE);
		
		Arguments arguments = new Arguments();
		arguments.addString("path", ".");
		arguments.addInteger("tcp-port", 7907);
		arguments.addInteger("udp-port", 7908);
		
		arguments.process(args);
		
		Path baseDirectory = Paths.get(arguments.getString("path"));
		
		ServerEnvironment serverEnvironment = new ServerEnvironment(baseDirectory);
		serverEnvironment.start();
		
		KryonetServer server = new KryonetServer(
				serverEnvironment,
				arguments.getInteger("tcp-port"),
				arguments.getInteger("udp-port"));
		
		try {
			server.start();
		} catch (InterlayerException e) {
			LOGGER.fatal("Failed to start server, exiting.", e);
			
			System.exit(1);
		}
	}
}
