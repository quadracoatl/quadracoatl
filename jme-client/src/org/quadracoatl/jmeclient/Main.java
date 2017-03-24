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

package org.quadracoatl.jmeclient;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.quadracoatl.environments.ServerEnvironment;
import org.quadracoatl.framework.logging.LogLevel;
import org.quadracoatl.framework.logging.LoggerFactory;
import org.quadracoatl.framework.support.Arguments;
import org.quadracoatl.interlayer.Interlayer;
import org.quadracoatl.interlayer.kryonet.CloningInterlayer;
import org.quadracoatl.interlayer.kryonet.KryonetClient;

import com.jme3.system.AppSettings;

public final class Main {
	private Main() {
		
	}
	
	public static final void main(String[] args) {
		Arguments arguments = new Arguments();
		arguments.addString("mode", "single");
		arguments.addString("path", ".");
		arguments.addString("server-address", "localhost");
		arguments.addInteger("server-tcp-port", 7907);
		arguments.addInteger("server-udp-port", 7908);
		
		arguments.process(args);
		
		Path baseDirectory = Paths.get(arguments.getString("path"));
		
		Interlayer interlayer = null;
		
		LoggerFactory.setLogLevel(LogLevel.WARN);
		
		if (arguments.getString("mode").equals("single")) {
			ServerEnvironment serverEnvironment = new ServerEnvironment(baseDirectory);
			interlayer = new CloningInterlayer(serverEnvironment);
		} else if (arguments.getString("mode").equals("multi")) {
			interlayer = new KryonetClient(
					arguments.getString("server-address"),
					arguments.getInteger("server-tcp-port"),
					arguments.getInteger("server-udp-port"));
		}
		
		AppSettings appSettings = new AppSettings(true);
		appSettings.setFullscreen(false);
		appSettings.setHeight(600);
		appSettings.setResizable(true);
		appSettings.setTitle("Quadracoatl");
		appSettings.setVSync(false);
		appSettings.setWidth(800);
		
		QuadracoatlApplication application = new QuadracoatlApplication(arguments.getString("path"), interlayer);
		application.setSettings(appSettings);
		application.start(true);
		
		// Make sure that the application does correctly exit.
		application.stop();
	}
}
