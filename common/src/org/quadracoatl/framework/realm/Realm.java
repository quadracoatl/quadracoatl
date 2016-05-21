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

package org.quadracoatl.framework.realm;

import java.util.ArrayList;
import java.util.List;

import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.chunk.ChunkProvider;
import org.quadracoatl.framework.chunk.managers.ChunkManager;
import org.quadracoatl.framework.cosmos.Cosmos;
import org.quadracoatl.interlayer.Transferrable;
import org.quadracoatl.utils.Vector3i;

public @Transferrable class Realm {
	protected @Transferrable Vector3i blockSize = null;
	protected ChunkManager chunkManager = new ChunkManager();
	protected List<ChunkProvider> chunkProviders = new ArrayList<>();
	protected @Transferrable Vector3i chunkSize = null;
	protected @Transferrable String name;
	protected Cosmos parent;
	protected @Transferrable List<Sky> skies = new ArrayList<>();
	
	public Realm(
			String name,
			int chunkWidth,
			int chunkHeight,
			int chunkDepth,
			int blockWidth,
			int blockHeight,
			int blockDepth) {
		this();
		
		this.name = name;
		
		chunkSize = new Vector3i(chunkWidth, chunkHeight, chunkDepth);
		blockSize = new Vector3i(blockWidth, blockHeight, blockDepth);
	}
	
	private Realm() {
		super();
	}
	
	public void addChunkProvider(ChunkProvider chunkProvider) {
		chunkProviders.add(chunkProvider);
	}
	
	public void addSky(Sky sky) {
		skies.add(sky);
	}
	
	public int getBlockDepth() {
		return blockSize.z;
	}
	
	public int getBlockHeight() {
		return blockSize.y;
	}
	
	public int getBlockWidth() {
		return blockSize.x;
	}
	
	public Chunk getChunk(int indexX, int indexY, int indexZ) {
		Chunk chunk = chunkManager.get(indexX, indexY, indexZ);
		
		if (chunk == null) {
			chunk = getChunkFromProviders(indexX, indexY, indexZ);
			chunkManager.put(chunk);
		}
		
		return chunk;
	}
	
	public int getChunkDepth() {
		return chunkSize.z;
	}
	
	public Chunk getChunkGlobal(int x, int y, int z) {
		Chunk chunk = chunkManager.get(x, y, z);
		
		if (chunk == null) {
			if (chunk == null) {
				chunk = getChunkFromProviders(x, y, z);
				
				chunkManager.put(chunk);
			}
		}
		
		return chunk;
	}
	
	public int getChunkHeight() {
		return chunkSize.y;
	}
	
	public Vector3i getChunkStart(int x, int y, int z) {
		return new Vector3i(
				x - Math.floorMod(x, (chunkSize.x * blockSize.x)),
				y - Math.floorMod(y, (chunkSize.y * blockSize.y)),
				z - Math.floorMod(z, (chunkSize.z * blockSize.z)));
	}
	
	public int getChunkWidth() {
		return chunkSize.x;
	}
	
	public Vector3i getIndex(int x, int y, int z) {
		return new Vector3i(
				x / chunkSize.x,
				y / chunkSize.y,
				z / chunkSize.z);
	}
	
	public String getName() {
		return name;
	}
	
	public Cosmos getParent() {
		return parent;
	}
	
	public List<Sky> getSkies() {
		return skies;
	}
	
	public void setChunkByIndex(Chunk chunk, int x, int y, int z) {
		setChunkByIndex(chunk, x, y, z);
	}
	
	public void setChunkGlobal(Chunk chunk, int x, int y, int z) {
		chunkManager.put(chunk);
	}
	
	public void setParent(Cosmos parent) {
		this.parent = parent;
	}
	
	public void swapChunkManager(ChunkManager newChunkManager) {
		chunkManager.copyChunksTo(newChunkManager);
		
		chunkManager = newChunkManager;
	}
	
	protected Chunk getChunkFromProviders(int indexX, int indexY, int indexZ) {
		for (ChunkProvider chunkProvider : chunkProviders) {
			Chunk chunk = chunkProvider.provideChunk(this, indexX, indexY, indexZ);
			
			if (chunk != null) {
				return chunk;
			}
		}
		
		return null;
	}
}
