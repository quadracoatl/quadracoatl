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

package org.quadracoatl.interlayer;

import java.io.IOException;

import org.quadracoatl.environments.ServerEnvironment;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;

public class KryonetServer extends AbstractKryoBase {
	private Interlayer connection = null;
	private Kryo kryo = null;
	private ObjectSpace objectSpace = null;
	private Server server = null;
	
	public KryonetServer(ServerEnvironment serverEnvironment) {
		super();
		
		server = new Server(DEFAULT_BUFFER_SIZE, DEFAULT_BUFFER_SIZE);
		kryo = server.getKryo();
		
		registerClasses(kryo);
		
		connection = new DirectInterlayer(serverEnvironment);
		
		objectSpace = new ObjectSpace();
		objectSpace.register(1, connection);
		
		server.addListener(new AddingListener(objectSpace));
	}
	
	public void start(int tcpPort, int udpPort) {
		server.start();
		
		try {
			server.bind(tcpPort, udpPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stop() {
		server.stop();
	}
	
	private static final class AddingListener extends Listener {
		private ObjectSpace objectSpace = null;
		
		public AddingListener(ObjectSpace objectSpace) {
			super();
			
			this.objectSpace = objectSpace;
		}
		
		@Override
		public void connected(Connection connection) {
			objectSpace.addConnection(connection);
		}
		
		@Override
		public void disconnected(Connection connection) {
			objectSpace.removeConnection(connection);
		}
		
	}
}
