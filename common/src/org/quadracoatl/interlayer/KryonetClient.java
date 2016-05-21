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

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.cosmos.Cosmos;
import org.quadracoatl.framework.realm.Realm;
import org.quadracoatl.framework.resources.Resource;
import org.quadracoatl.framework.resources.ResourceType;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;

public class KryonetClient extends AbstractKryoBase implements Closeable, Interlayer {
	private Client client = null;
	private Interlayer connection = null;
	private Kryo kryo = null;
	
	public KryonetClient() {
		super();
		
		client = new Client(DEFAULT_BUFFER_SIZE, DEFAULT_BUFFER_SIZE);
		kryo = client.getKryo();
		
		registerClasses(kryo);
	}
	
	@Override
	public void close() {
		client.close();
		client.stop();
		
		connection = null;
	}
	
	public void connect(String serverAddress, int tcpPort, int udpPort) {
		try {
			client.start();
			client.connect(500, serverAddress, tcpPort, udpPort);
			
			connection = ObjectSpace.getRemoteObject(client, 1, Interlayer.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public Block getBlock(int blockId) {
		return connection.getBlock(blockId);
	}
	
	@Override
	public Block getBlock(String blockName) {
		return connection.getBlock(blockName);
	}
	
	@Override
	public List<Block> getBlocks() {
		return connection.getBlocks();
	}
	
	@Override
	public Chunk getChunk(String realmName, int indexX, int indexY, int indexZ) {
		return connection.getChunk(realmName, indexX, indexY, indexZ);
	}
	
	@Override
	public Cosmos getCosmos() {
		return connection.getCosmos();
	}
	
	@Override
	public Realm getRealm(String realmName) {
		return connection.getRealm(realmName);
	}
	
	@Override
	public List<Realm> getRealms() {
		return connection.getRealms();
	}
	
	@Override
	public Resource getResource(ResourceType type, String key) {
		return connection.getResource(type, key);
	}
	
	@Override
	public byte[] getResourceContent(ResourceType type, String key) {
		return connection.getResourceContent(type, key);
	}
	
	@Override
	public List<Resource> getResources() {
		return connection.getResources();
	}
}
