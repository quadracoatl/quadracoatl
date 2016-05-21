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

package org.quadracoatl.environments;

import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.chunk.managers.FifoChunkManager;
import org.quadracoatl.framework.cosmos.Cosmos;
import org.quadracoatl.framework.realm.Realm;
import org.quadracoatl.framework.resources.Resource;
import org.quadracoatl.framework.resources.ResourceCache;
import org.quadracoatl.interlayer.Interlayer;

public class ClientEnvironment {
	private Cosmos cosmos = null;
	private Realm currentRealm = null;
	private Interlayer interlayer = null;
	private ResourceCache resourceCache = null;
	
	public ClientEnvironment(Interlayer interlayer, ResourceCache resourceCache) {
		super();
		
		this.interlayer = interlayer;
		this.resourceCache = resourceCache;
		
		cosmos = interlayer.getCosmos();
		// TODO Hardcoded default here.
		cosmos.registerRealm(interlayer.getRealm("default"));
		
		currentRealm = cosmos.getRealm("default");
		currentRealm.swapChunkManager(new FifoChunkManager(64));
	}
	
	public void fetchAllBlocks() {
		cosmos.registerBlocks(interlayer.getBlocks());
	}
	
	public void fetchAllResources() {
		for (Resource resource : interlayer.getResources()) {
			if (!resourceCache.isCached(resource)) {
				resourceCache.cache(resource, interlayer.getResourceContent(resource.getType(), resource.getKey()));
			}
		}
	}
	
	public Chunk getChunk(int indexX, int indexY, int indexZ) {
		return getChunk(currentRealm.getName(), indexX, indexY, indexZ);
	}
	
	public Chunk getChunk(String realmName, int indexX, int indexY, int indexZ) {
		Chunk chunk = cosmos.getRealm(realmName).getChunkGlobal(indexX, indexY, indexZ);
		
		if (chunk == null) {
			chunk = interlayer.getChunk(realmName, indexX, indexY, indexZ);
			cosmos.getRealm(realmName).setChunkGlobal(chunk, indexX, indexY, indexZ);
		}
		
		return chunk;
	}
	
	public Cosmos getCosmos() {
		return cosmos;
	}
	
	public Realm getCurrentRealm() {
		return currentRealm;
	}
	
	public ResourceCache getResourceCache() {
		return resourceCache;
	}
}
