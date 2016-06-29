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

package org.quadracoatl;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.quadracoatl.environments.ServerEnvironment;
import org.quadracoatl.interlayer.CloningInterlayer;
import org.quadracoatl.interlayer.Interlayer;
import org.quadracoatl.interlayer.KryonetClient;
import org.quadracoatl.utils.Arguments;

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
		
		if (arguments.getString("mode").equals("single")) {
			ServerEnvironment serverEnvironment = new ServerEnvironment(baseDirectory);
			serverEnvironment.start();
			
			interlayer = new CloningInterlayer(serverEnvironment);
		} else if (arguments.getString("mode").equals("multi")) {
			interlayer = new KryonetClient();
			((KryonetClient)interlayer).connect(
					arguments.getString("server-address"),
					arguments.getInteger("server-tcp-port"),
					arguments.getInteger("server-udp-port"));
		}
		
		new TestApplication(arguments.getString("path"), interlayer).start();
	}
}
