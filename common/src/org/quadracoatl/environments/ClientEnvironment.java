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

import java.util.ArrayList;

import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.chunk.ChunkDataProvider;
import org.quadracoatl.framework.chunk.managers.RingChunkManager;
import org.quadracoatl.framework.cosmos.Cosmos;
import org.quadracoatl.framework.entities.Entity;
import org.quadracoatl.framework.realm.Realm;
import org.quadracoatl.framework.resources.Resource;
import org.quadracoatl.framework.resources.ResourceCache;
import org.quadracoatl.interlayer.Interlayer;
import org.quadracoatl.interlayer.parts.CosmosPart;
import org.quadracoatl.interlayer.parts.EntityEventReceiver;
import org.quadracoatl.interlayer.parts.direct.DirectEntityEventReceiver;

public class ClientEnvironment extends AbstractThreadedUpdatable {
	private Cosmos cosmos = null;
	private CosmosPart cosmosPart = null;
	private Realm currentRealm = null;
	private Interlayer interlayer = null;
	private ResourceCache resourceCache = null;
	
	public ClientEnvironment(Interlayer interlayer, ResourceCache resourceCache) {
		super("client", 60);
		
		this.interlayer = interlayer;
		this.resourceCache = resourceCache;
		
		cosmosPart = interlayer.getPart(CosmosPart.ID, CosmosPart.class);
		
		cosmos = cosmosPart.getCosmos();
		cosmos.registerRealm(cosmosPart.getRealm(cosmosPart.getCurrentRealmName()));
		
		currentRealm = cosmos.getRealm(cosmosPart.getCurrentRealmName());
		currentRealm.swapChunkManager(new RingChunkManager(256));
		currentRealm.addChunkProvider(new InterlayerChunkDataProvider(cosmosPart));
	}
	
	public void fetchAllBlocks() {
		cosmos.registerBlocks(cosmosPart.getBlocks());
	}
	
	public void fetchAllEntities() {
		for (Entity entity : cosmosPart.getEntities(currentRealm.getName())) {
			entity.replaceComponents(new ArrayList<>(entity.getComponents()));
			currentRealm.getEntityManager().register(entity);
		}
	}
	
	public void fetchAllResources() {
		for (Resource resource : cosmosPart.getResources()) {
			if (!resourceCache.isCached(resource)) {
				resourceCache.cache(resource, cosmosPart.getResourceContent(resource.getType(), resource.getKey()));
			}
		}
	}
	
	public Chunk getChunk(double x, double y, double z) {
		return getChunk(currentRealm.getName(), x, y, z);
	}
	
	public Chunk getChunk(int indexX, int indexY, int indexZ) {
		return getChunk(currentRealm.getName(), indexX, indexY, indexZ);
	}
	
	public Chunk getChunk(String realmName, double x, double y, double z) {
		return cosmos.getRealm(realmName).getChunk(x, y, z);
	}
	
	public Chunk getChunk(String realmName, int indexX, int indexY, int indexZ) {
		return cosmos.getRealm(realmName).getChunk(indexX, indexY, indexZ);
	}
	
	public Cosmos getCosmos() {
		return cosmos;
	}
	
	public Realm getCurrentRealm() {
		return currentRealm;
	}
	
	public Interlayer getInterlayer() {
		return interlayer;
	}
	
	public ResourceCache getResourceCache() {
		return resourceCache;
	}
	
	@Override
	protected void init() throws Throwable {
		super.init();
		
		// TODO The realm might change.
		interlayer.putPart(EntityEventReceiver.ID, new DirectEntityEventReceiver(this));
	}
	
	private static final class InterlayerChunkDataProvider implements ChunkDataProvider {
		private CosmosPart cosmosPart = null;
		
		public InterlayerChunkDataProvider(CosmosPart cosmosPart) {
			super();
			
			this.cosmosPart = cosmosPart;
		}
		
		@Override
		public boolean provideChunkData(Realm realm, Chunk chunk) {
			Chunk providedChunk = cosmosPart.getChunk(
					realm.getName(),
					chunk.getIndexX(),
					chunk.getIndexY(),
					chunk.getIndexZ());
			
			if (providedChunk != null) {
				for (int x = 0; x < chunk.getWidth(); x++) {
					for (int y = 0; y < chunk.getHeight(); y++) {
						for (int z = 0; z < chunk.getDepth(); z++) {
							chunk.set(x, y, z, providedChunk.get(x, y, z));
						}
					}
				}
				
				chunk.updateStatistics();
				
				return true;
			}
			
			return false;
		}
	}
}
