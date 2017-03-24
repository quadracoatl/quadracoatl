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

import org.quadracoatl.interlayer.Interlayer;
import org.quadracoatl.interlayer.InterlayerException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;

public class KryonetClient extends AbstractKryoBase implements Interlayer {
	private Client client = null;
	private Kryo kryo = null;
	private ObjectSpace objectSpace = null;
	private String serverAddress = null;
	private int tcpPort = 0;
	private int udpPort = 0;
	
	public KryonetClient(String serverAddress, int tcpPort, int udpPort) {
		super();
		
		this.serverAddress = serverAddress;
		this.tcpPort = tcpPort;
		
		this.udpPort = udpPort;
		
		client = new Client(DEFAULT_BUFFER_SIZE, DEFAULT_BUFFER_SIZE);
		
		kryo = client.getKryo();
		setup(kryo);
		
		objectSpace = new ObjectSpace(client);
	}
	
	@Override
	public <PART> PART getPart(int partId, Class<PART> partClass) {
		return ObjectSpace.getRemoteObject(client, partId, partClass);
	}
	
	@Override
	public boolean isStarted() {
		return client.isConnected();
	}
	
	@Override
	public <PART> void putPart(int partId, PART part) {
		objectSpace.register(partId, part);
	}
	
	@Override
	public void start() throws InterlayerException {
		try {
			client.start();
			client.connect(2000, serverAddress, tcpPort, udpPort);
		} catch (IOException e) {
			throw new InterlayerException("Failed to connect to server.", e);
		}
	}
	
	@Override
	public void stop() {
		client.close();
		client.stop();
	}
}
