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

package org.quadracoatl.interlayer.parts.direct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.quadracoatl.environments.ServerEnvironment;
import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.common.Client;
import org.quadracoatl.framework.cosmos.Cosmos;
import org.quadracoatl.framework.entities.Entity;
import org.quadracoatl.framework.realm.Realm;
import org.quadracoatl.framework.resources.Resource;
import org.quadracoatl.framework.resources.ResourceType;
import org.quadracoatl.interlayer.parts.CosmosPart;

public class DirectCosmosPart implements CosmosPart {
	private transient Client client = null;
	private transient ServerEnvironment serverEnvironment = null;
	
	public DirectCosmosPart(ServerEnvironment serverEnvironment, Client client) {
		this();
		
		this.serverEnvironment = serverEnvironment;
		this.client = client;
	}
	
	private DirectCosmosPart() {
		super();
	}
	
	@Override
	public Block getBlock(int blockId) {
		return serverEnvironment.getCosmos().getBlock(blockId);
	}
	
	@Override
	public Block getBlock(String blockName) {
		return serverEnvironment.getCosmos().getBlock(blockName);
	}
	
	@Override
	public Collection<Block> getBlocks() {
		return serverEnvironment.getCosmos().getBlocks();
	}
	
	@Override
	public Chunk getChunk(String realmName, int indexX, int indexY, int indexZ) {
		return serverEnvironment.getCosmos().getRealm(realmName).getChunk(indexX, indexY, indexZ);
	}
	
	@Override
	public Cosmos getCosmos() {
		return serverEnvironment.getCosmos();
	}
	
	@Override
	public String getCurrentRealmName() {
		return client.getCurrentRealmName();
	}
	
	@Override
	public Collection<Entity> getEntities(String realmName) {
		return Collections.unmodifiableList(new ArrayList<>(serverEnvironment.getCosmos().getRealm(realmName).getEntityManager().getEntities()));
	}
	
	@Override
	public Entity getEntity(String realmName, int entityId) {
		return serverEnvironment.getCosmos().getRealm(realmName).getEntityManager().getEntity(entityId);
	}
	
	@Override
	public Realm getRealm(String realmName) {
		return serverEnvironment.getCosmos().getRealm(realmName);
	}
	
	@Override
	public Map<String, Realm> getRealms() {
		return serverEnvironment.getCosmos().getRealms();
	}
	
	@Override
	public Resource getResource(ResourceType type, String key) {
		return serverEnvironment.getGame().getResourceManager().getResource(type, key);
	}
	
	@Override
	public byte[] getResourceContent(ResourceType type, String key) {
		return serverEnvironment.getGame().getResourceManager().getResourceContent(type, key);
	}
	
	@Override
	public Collection<Resource> getResources() {
		return serverEnvironment.getGame().getResourceManager().getResources();
	}
}
