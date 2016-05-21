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

import java.util.ArrayList;
import java.util.List;

import org.quadracoatl.environments.ServerEnvironment;
import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.cosmos.Cosmos;
import org.quadracoatl.framework.realm.Realm;
import org.quadracoatl.framework.resources.Resource;
import org.quadracoatl.framework.resources.ResourceManager;
import org.quadracoatl.framework.resources.ResourceType;

import com.esotericsoftware.kryo.Kryo;

public class CloningInterlayer extends AbstractKryoBase implements Interlayer {
	private Cosmos cosmos = null;
	private Kryo kryo = null;
	private ResourceManager resourceManager = null;
	private ServerEnvironment serverEnvironment = null;
	
	public CloningInterlayer(ServerEnvironment serverEnvironment) {
		super();
		
		this.serverEnvironment = serverEnvironment;
		
		cosmos = serverEnvironment.getCosmos();
		resourceManager = serverEnvironment.getResourceManager();
		
		kryo = new Kryo();
		registerClasses(kryo);
	}
	
	@Override
	public Block getBlock(int blockId) {
		return kryo.copy(cosmos.getBlock(blockId));
	}
	
	@Override
	public Block getBlock(String blockName) {
		return kryo.copy(cosmos.getBlock(blockName));
	}
	
	@Override
	public List<Block> getBlocks() {
		return kryo.copy(new ArrayList<>(cosmos.getBlocks()));
	}
	
	@Override
	public Chunk getChunk(String realmName, int indexX, int indexY, int indexZ) {
		return kryo.copy(cosmos.getRealm(realmName).getChunk(indexX, indexY, indexZ));
	}
	
	@Override
	public Cosmos getCosmos() {
		return kryo.copy(cosmos);
	}
	
	@Override
	public Realm getRealm(String realmName) {
		return kryo.copy(cosmos.getRealm(realmName));
	}
	
	@Override
	public List<Realm> getRealms() {
		return kryo.copy(cosmos.getRealms());
	}
	
	@Override
	public Resource getResource(ResourceType type, String key) {
		// Resources are immutable and don't need to be cloned.
		return resourceManager.getResource(type, key);
	}
	
	@Override
	public byte[] getResourceContent(ResourceType type, String key) {
		// The resource content is not cached and can therefor be send to the
		// client without fearing that it might temper with the server state.
		return resourceManager.getResourceContent(type, key);
	}
	
	@Override
	public List<Resource> getResources() {
		// Resources are immutable and don't need to be cloned.
		return new ArrayList<>(resourceManager.getResources());
	}
}
