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

package org.quadracoatl.interlayer.kryonet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.quadracoatl.environments.ServerEnvironment;
import org.quadracoatl.framework.common.Client;
import org.quadracoatl.interlayer.InterlayerClient;
import org.quadracoatl.interlayer.InterlayerException;
import org.quadracoatl.interlayer.parts.EntityEventReceiver;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.esotericsoftware.kryonet.rmi.RemoteObject;

public class KryonetServer extends AbstractKryoBase {
	private Map<Connection, Client> clients = new HashMap<>();
	private Kryo kryo = null;
	private Server server = null;
	private ServerEnvironment serverEnvironment = null;
	private int tcpPort = 0;
	private int udpPort = 0;
	
	public KryonetServer(ServerEnvironment serverEnvironment, int tcpPort, int udpPort) {
		super();
		
		this.serverEnvironment = serverEnvironment;
		this.tcpPort = tcpPort;
		this.udpPort = udpPort;
		
		server = new Server(DEFAULT_BUFFER_SIZE, DEFAULT_BUFFER_SIZE);
		
		kryo = server.getKryo();
		setup(kryo);
		
		server.addListener(new AddingListener());
	}
	
	public void start() throws InterlayerException {
		server.start();
		
		try {
			server.bind(tcpPort, udpPort);
		} catch (IOException e) {
			server.stop();
			
			throw new InterlayerException("Server could not be bound to ports.", e);
		}
	}
	
	public void stop() {
		server.stop();
	}
	
	private final class AddingListener implements Listener {
		public AddingListener() {
			super();
		}
		
		@Override
		public void connected(Connection connection) {
			InterlayerClient interlayerClient = new KryonetConnectionClient(connection);
			
			Client client = new Client();
			client.setInterlayerClient(interlayerClient);
			
			clients.put(connection, client);
			serverEnvironment.registerClient(client);
		}
		
		@Override
		public void disconnected(Connection connection) {
			serverEnvironment.unregisterClient(clients.get(connection));
		}
	}
	
	private static final class KryonetConnectionClient extends InterlayerClient {
		private Connection connection = null;
		private ObjectSpace objectSpace = null;
		
		public KryonetConnectionClient(Connection connection) {
			super();
			
			this.connection = connection;
			
			objectSpace = new ObjectSpace(connection);
		}
		
		@Override
		public <PART> PART getPart(int partId, Class<PART> partClass) {
			PART part = super.getPart(partId, partClass);
			
			if (part == null) {
				part = ObjectSpace.getRemoteObject(connection, partId, partClass);
				
				if (part != null) {
					super.putPart(partId, part);
					
					if (partId == EntityEventReceiver.ID) {
						makeAsynchron((RemoteObject)part);
					}
				}
			}
			
			return part;
		}
		
		@Override
		public <PART> void putPart(int partId, PART part) {
			super.putPart(partId, part);
			
			objectSpace.register(partId, part);
		}
		
		private void makeAsynchron(RemoteObject remoteObject) {
			remoteObject.setUDP(true);
		}
	}
}
