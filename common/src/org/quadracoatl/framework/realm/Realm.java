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
import org.quadracoatl.framework.chunk.ChunkDataProvider;
import org.quadracoatl.framework.chunk.SpawnBehavior;
import org.quadracoatl.framework.chunk.managers.ChunkManager;
import org.quadracoatl.framework.cosmos.Cosmos;
import org.quadracoatl.interlayer.Transferrable;
import org.quadracoatl.utils.Vector3d;
import org.quadracoatl.utils.Vector3i;

public @Transferrable class Realm {
	protected @Transferrable Vector3d blockSize = null;
	protected ChunkManager chunkManager = new ChunkManager();
	protected List<ChunkDataProvider> chunkProviders = new ArrayList<>();
	protected @Transferrable Vector3i chunkSize = null;
	protected Cosmos cosmos;
	protected @Transferrable String name;
	protected @Transferrable List<Sky> skies = new ArrayList<>();
	
	public Realm(
			String name,
			int chunkWidth,
			int chunkHeight,
			int chunkDepth,
			double blockWidth,
			double blockHeight,
			double blockDepth) {
		this();
		
		this.name = name;
		
		chunkSize = new Vector3i(chunkWidth, chunkHeight, chunkDepth);
		blockSize = new Vector3d(blockWidth, blockHeight, blockDepth);
	}
	
	private Realm() {
		super();
	}
	
	protected static final double mod(double divident, double divisor) {
		double result = divident % divisor;
		
		if (result < 0) {
			return result + divisor;
		}
		
		return result;
	}
	
	public void addChunkProvider(ChunkDataProvider chunkProvider) {
		chunkProviders.add(chunkProvider);
	}
	
	public void addSky(Sky sky) {
		skies.add(sky);
	}
	
	public int getBlock(double x, double y, double z) {
		Vector3d chunkStart = getChunkStart(x, y, z);
		
		return getChunk(x, y, z).get(
				(int)((x - chunkStart.x) / blockSize.x),
				(int)((y - chunkStart.y) / blockSize.y),
				(int)((z - chunkStart.z) / blockSize.z));
	}
	
	public double getBlockDepth() {
		return blockSize.z;
	}
	
	public double getBlockHeight() {
		return blockSize.y;
	}
	
	public double getBlockWidth() {
		return blockSize.x;
	}
	
	public Chunk getChunk(double x, double y, double z) {
		return getChunk(
				(int)Math.floor(x / blockSize.x / chunkSize.x),
				(int)Math.floor(y / blockSize.y / chunkSize.y),
				(int)Math.floor(z / blockSize.z / chunkSize.z));
	}
	
	public Chunk getChunk(int indexX, int indexY, int indexZ) {
		return getChunk(indexX, indexY, indexZ, SpawnBehavior.CREATE);
	}
	
	public Chunk getChunk(int indexX, int indexY, int indexZ, SpawnBehavior spawnBehavior) {
		Chunk chunk = chunkManager.get(indexX, indexY, indexZ);
		
		if (chunk == null && spawnBehavior == SpawnBehavior.CREATE) {
			chunk = new Chunk(
					indexX,
					indexY,
					indexZ,
					chunkSize.x,
					chunkSize.y,
					chunkSize.z);
			
			chunk.setRealm(this);
			chunkManager.put(chunk);
			
			fillChunkByProviders(chunk);
		}
		
		return chunk;
	}
	
	public int getChunkDepth() {
		return chunkSize.z;
	}
	
	public int getChunkHeight() {
		return chunkSize.y;
	}
	
	public Vector3d getChunkStart(double x, double y, double z) {
		return new Vector3d(
				x - mod(x, chunkSize.x * blockSize.x),
				y - mod(y, chunkSize.y * blockSize.y),
				z - mod(z, chunkSize.z * blockSize.z));
	}
	
	public Vector3d getChunkStart(int indexX, int indexY, int indexZ) {
		return new Vector3d(
				indexX * chunkSize.x * blockSize.x,
				indexY * chunkSize.y * blockSize.y,
				indexZ * chunkSize.z * blockSize.z);
	}
	
	public int getChunkWidth() {
		return chunkSize.x;
	}
	
	public Cosmos getCosmos() {
		return cosmos;
	}
	
	public Vector3i getIndex(double x, double y, double z) {
		return new Vector3i(
				(int)(x / blockSize.x / chunkSize.x),
				(int)(y / blockSize.y / chunkSize.y),
				(int)(z / blockSize.z / chunkSize.z));
	}
	
	public String getName() {
		return name;
	}
	
	public List<Sky> getSkies() {
		return skies;
	}
	
	public void setBlock(double x, double y, double z, int blockId) {
		Vector3d chunkStart = getChunkStart(x, y, z);
		
		try {
			getChunk(x, y, z).set(
					(int)((x - chunkStart.x) / blockSize.x),
					(int)((y - chunkStart.y) / blockSize.y),
					(int)((z - chunkStart.z) / blockSize.z),
					blockId);
		} catch (Throwable th) {
			throw th;
		}
	}
	
	public void setChunk(Chunk chunk) {
		chunkManager.put(chunk);
	}
	
	public void setCosmos(Cosmos parent) {
		this.cosmos = parent;
	}
	
	public void swapChunkManager(ChunkManager newChunkManager) {
		chunkManager.copyChunksTo(newChunkManager);
		
		chunkManager = newChunkManager;
	}
	
	protected void fillChunkByProviders(Chunk chunk) {
		for (ChunkDataProvider chunkProvider : chunkProviders) {
			if (chunkProvider.provideChunkData(this, chunk)) {
				return;
			}
		}
	}
}
