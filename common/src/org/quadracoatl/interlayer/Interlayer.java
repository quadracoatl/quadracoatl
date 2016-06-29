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

import java.util.List;

import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.cosmos.Cosmos;
import org.quadracoatl.framework.realm.Realm;
import org.quadracoatl.framework.resources.Resource;
import org.quadracoatl.framework.resources.ResourceType;

public interface Interlayer {
	public Block getBlock(int blockId);
	
	public Block getBlock(String blockName);
	
	public List<Block> getBlocks();
	
	public Chunk getChunk(String realmName, int indexX, int indexY, int indexZ);
	
	public Cosmos getCosmos();
	
	public Realm getRealm(String realmName);
	
	public List<Realm> getRealms();
	
	public Resource getResource(ResourceType type, String key);
	
	public byte[] getResourceContent(ResourceType type, String key);
	
	public List<Resource> getResources();
}
