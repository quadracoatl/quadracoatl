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

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

public class ChunkManager {
	protected Set<Chunk> chunks = new HashSet<>();
	protected TLongObjectMap<Chunk> indexed = new TLongObjectHashMap<>();
	
	public ChunkManager() {
		super();
	}
	
	public boolean contains(int indexX, int indexY, int indexZ) {
		return indexed.containsKey(makeIndex(indexX, indexY, indexZ));
	}
	
	public void copyChunksTo(ChunkManager targetChunkManager) {
		for (Chunk chunk : chunks) {
			targetChunkManager.put(chunk);
		}
	}
	
	public synchronized Chunk get(int indexX, int indexY, int indexZ) {
		return indexed.get(makeIndex(indexX, indexY, indexZ));
	}
	
	public synchronized void put(Chunk chunk) {
		if (chunk == null) {
			return;
		}
		
		indexed.put(makeIndex(chunk.getIndexX(), chunk.getIndexY(), chunk.getIndexZ()), chunk);
		chunks.add(chunk);
	}
	
	public void remove(Chunk chunk) {
		remove(
				chunk.getIndexX(),
				chunk.getIndexY(),
				chunk.getIndexZ());
	}
	
	public synchronized void remove(int indexX, int indexY, int indexZ) {
		chunks.remove(indexed.remove(makeIndex(indexX, indexY, indexZ)));
	}
	
	protected long makeIndex(int indexX, int indexY, int indexZ) {
		long index = 0;
		index = index | (packInto21bits(indexX));
		index = index | ((long)packInto21bits(indexY) << 21);
		index = index | ((long)packInto21bits(indexZ) << 42);
		
		return index;
	}
	
	private final int packInto21bits(int value) {
		int smallValue = value;
		
		smallValue = smallValue & 0xFFFFF;
		
		if (value < 0) {
			smallValue = smallValue | 0x100000;
		}
		
		return smallValue;
	}
}
