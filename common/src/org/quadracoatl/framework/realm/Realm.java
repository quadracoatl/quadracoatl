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
import org.quadracoatl.framework.common.MathUtil;
import org.quadracoatl.framework.common.Region3d;
import org.quadracoatl.framework.common.Vector3d;
import org.quadracoatl.framework.common.Vector3i;
import org.quadracoatl.framework.cosmos.Cosmos;
import org.quadracoatl.framework.entities.EntityManager;
import org.quadracoatl.framework.entities.components.UpdateComponent;

public class Realm {
	protected Vector3d blockSize = null;
	protected Region3d bounds = null;
	protected transient ChunkManager chunkManager = new ChunkManager();
	protected transient List<ChunkDataProvider> chunkProviders = new ArrayList<>();
	protected Vector3i chunkSize = null;
	protected transient Cosmos cosmos;
	protected transient EntityManager entityManager = new EntityManager();
	protected String name;
	protected transient double time = 0.0d;
	protected transient TimeProvider timeProvider = null;
	
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
		double result = Math.IEEEremainder(divident, divisor);
		
		// Check if it is "not close" to zero.
		if (result < -1E-12) {
			return result + divisor;
		}
		
		return result;
	}
	
	public void addChunkProvider(ChunkDataProvider chunkProvider) {
		chunkProviders.add(chunkProvider);
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
	
	public Region3d getBounds() {
		return bounds;
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
		
		if (chunk == null
				&& spawnBehavior == SpawnBehavior.CREATE
				&& isInsideBounds(indexX, indexY, indexZ)) {
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
	
	public EntityManager getEntityManager() {
		return entityManager;
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
	
	public TimeProvider getTimeProvider() {
		return timeProvider;
	}
	
	public void setBlock(double x, double y, double z, int blockId) {
		Vector3d chunkStart = getChunkStart(x, y, z);
		
		int chunkIndexX = (int)((x - chunkStart.x) / blockSize.x);
		int chunkIndexY = (int)((y - chunkStart.y) / blockSize.y);
		int chunkIndexZ = (int)((z - chunkStart.z) / blockSize.z);
		
		getChunk(x, y, z).set(chunkIndexX, chunkIndexY, chunkIndexZ, blockId);
	}
	
	public void setBounds(Region3d bounds) {
		this.bounds = bounds;
	}
	
	public void setChunk(Chunk chunk) {
		chunkManager.put(chunk);
	}
	
	public void setCosmos(Cosmos parent) {
		this.cosmos = parent;
	}
	
	public void setTimeProvider(TimeProvider timeProvider) {
		this.timeProvider = timeProvider;
	}
	
	public void swapChunkManager(ChunkManager newChunkManager) {
		chunkManager.copyChunksTo(newChunkManager);
		
		chunkManager = newChunkManager;
	}
	
	public void update(long elapsedNanoSecondsSinceLastUpdate) {
		if (timeProvider != null) {
			time = timeProvider.getTime(time, elapsedNanoSecondsSinceLastUpdate);
		}
		
		for (UpdateComponent updateComponent : entityManager.getComponents(UpdateComponent.class)) {
			updateComponent.update(time, elapsedNanoSecondsSinceLastUpdate);
		}
	}
	
	protected void fillChunkByProviders(Chunk chunk) {
		for (ChunkDataProvider chunkProvider : chunkProviders) {
			if (chunkProvider.provideChunkData(this, chunk)) {
				return;
			}
		}
	}
	
	protected boolean isInsideBounds(int indexX, int indexY, int indexZ) {
		if (bounds == null) {
			return true;
		}
		
		double x = indexX * chunkSize.x * blockSize.x;
		double y = indexY * chunkSize.y * blockSize.y;
		double z = indexZ * chunkSize.z * blockSize.z;
		
		return MathUtil.between(x, bounds.start.x, bounds.end.x)
				&& MathUtil.between(y, bounds.start.y, bounds.end.y)
				&& MathUtil.between(z, bounds.start.z, bounds.end.z);
	}
}
