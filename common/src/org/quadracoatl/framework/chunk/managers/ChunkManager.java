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

package org.quadracoatl.framework.chunk.managers;

import java.util.HashSet;
import java.util.Set;

import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.utils.IntMap;
import org.quadracoatl.utils.Vector3i;

public class ChunkManager {
	protected Set<Chunk> chunks = new HashSet<>();
	protected IntMap<IntMap<IntMap<Chunk>>> indexed = new IntMap<>();
	
	public ChunkManager() {
		super();
	}
	
	public boolean contains(int indexX, int indexY, int indexZ) {
		IntMap<IntMap<Chunk>> yPart = indexed.get(indexX);
		
		if (yPart == null) {
			return false;
		}
		
		IntMap<Chunk> zPart = yPart.get(indexY);
		
		if (zPart == null) {
			return false;
		}
		
		return zPart.containsKey(indexZ);
	}
	
	public void copyChunksTo(ChunkManager targetChunkManager) {
		for (Chunk chunk : chunks) {
			targetChunkManager.put(chunk);
		}
	}
	
	public Chunk get(int indexX, int indexY, int indexZ) {
		IntMap<IntMap<Chunk>> yPart = indexed.get(indexX);
		
		if (yPart == null) {
			yPart = new IntMap<>();
			indexed.put(indexX, yPart);
		}
		
		IntMap<Chunk> zPart = yPart.get(indexY);
		
		if (zPart == null) {
			zPart = new IntMap<>();
			yPart.put(indexZ, zPart);
		}
		
		return zPart.get(indexZ);
	}
	
	public void put(Chunk chunk) {
		if (chunk == null) {
			return;
		}
		
		Vector3i index = chunk.getIndex();
		
		IntMap<IntMap<Chunk>> yPart = indexed.get(index.getX());
		
		if (yPart == null) {
			yPart = new IntMap<>();
			indexed.put(index.getX(), yPart);
		}
		
		IntMap<Chunk> zPart = yPart.get(index.getY());
		
		if (zPart == null) {
			zPart = new IntMap<>();
			yPart.put(index.getZ(), zPart);
		}
		
		chunks.add(zPart.put(index.getZ(), chunk));
	}
	
	public void remove(Chunk chunk) {
		remove(
				chunk.getIndex().getX(),
				chunk.getIndex().getY(),
				chunk.getIndex().getZ());
	}
	
	public void remove(int indexX, int indexY, int indexZ) {
		IntMap<IntMap<Chunk>> yPart = indexed.get(indexX);
		
		if (yPart == null) {
			return;
		}
		
		IntMap<Chunk> zPart = yPart.get(indexY);
		
		if (zPart == null) {
			return;
		}
		
		chunks.remove(zPart.remove(indexZ));
	}
}
