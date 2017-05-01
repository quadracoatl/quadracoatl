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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.chunk.ChunkManager;
import org.quadracoatl.framework.realm.Realm;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

public class SimpleChunkManager implements ChunkManager {
	protected TLongObjectMap<Chunk> indexedChunks = new TLongObjectHashMap<>();
	protected Realm realm = null;
	
	public SimpleChunkManager() {
		super();
	}
	
	@Override
	public void add(Chunk chunk) {
		if (chunk == null) {
			return;
		}
		
		indexedChunks.put(
				makeIndex(chunk.getIndexX(), chunk.getIndexY(), chunk.getIndexZ()),
				chunk);
	}
	
	@Override
	public void addAll(Iterable<Chunk> chunks) {
		if (chunks != null) {
			for (Chunk chunk : chunks) {
				add(chunk);
			}
		}
	}
	
	@Override
	public void attachTo(Realm realm) {
		detach();
		
		this.realm = realm;
	}
	
	@Override
	public void detach() {
		realm = null;
		
		indexedChunks.clear();
	}
	
	@Override
	public Chunk get(int x, int y, int z) {
		return indexedChunks.get(makeIndex(x, y, z));
	}
	
	@Override
	public List<Chunk> getChunks() {
		return Collections.unmodifiableList(new ArrayList<>(indexedChunks.valueCollection()));
	}
	
	@Override
	public void remove(Chunk chunk) {
		if (chunk == null) {
			return;
		}
		
		remove(chunk.getIndexX(), chunk.getIndexY(), chunk.getIndexZ());
	}
	
	@Override
	public void remove(int indexX, int indexY, int indexZ) {
		indexedChunks.remove(makeIndex(indexX, indexY, indexZ));
	}
	
	protected long makeIndex(int indexX, int indexY, int indexZ) {
		long index = 0;
		index = index | (packInto21bits(indexX));
		index = index | ((long)packInto21bits(indexY) << 21);
		index = index | ((long)packInto21bits(indexZ) << 42);
		
		return index;
	}
	
	protected final int packInto21bits(int value) {
		int smallValue = value;
		
		smallValue = smallValue & 0xFFFFF;
		
		if (value < 0) {
			smallValue = smallValue | 0x100000;
		}
		
		return smallValue;
	}
}
