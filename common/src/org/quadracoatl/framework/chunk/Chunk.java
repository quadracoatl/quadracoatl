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

package org.quadracoatl.framework.chunk;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.common.vectors.Vector3i;
import org.quadracoatl.framework.realm.Realm;

public class Chunk {
	private static final DecimalFormat decimalFormat = new DecimalFormat("+#;-#");
	
	protected transient Set<Integer> blockIds = new HashSet<>();
	protected int[][][] data = null;
	protected int depth = 0;
	protected int height = 0;
	protected Vector3i index = null;
	protected transient Set<Integer> readonlyBlockIds = null;
	protected transient Realm realm = null;
	protected transient String stringValue = null;
	protected int width = 0;
	
	public Chunk(int indexX, int indexY, int indexZ, int width, int height, int depth) {
		this();
		
		this.width = width;
		this.height = height;
		this.depth = depth;
		
		index = new Vector3i(indexX, indexY, indexZ);
		
		data = new int[width][height][depth];
	}
	
	private Chunk() {
		super();
	}
	
	public int get(int x, int y, int z) {
		if (x < 0 || x >= width
				|| y < 0 || y >= height
				|| z < 0 || z >= depth) {
			if (realm != null) {
				Chunk neighbour = realm.getChunk(
						index.x + modIndex(x, width),
						index.y + modIndex(y, height),
						index.z + modIndex(z, depth),
						SpawnBehavior.DO_NOT_CREATE);
				
				if (neighbour != null) {
					return neighbour.get(
							modLocation(x, width, neighbour.width),
							modLocation(y, height, neighbour.height),
							modLocation(z, depth, neighbour.depth));
				}
			}
			
			return Block.NONEXISTENT_BLOCK.getId();
		}
		
		return data[x][y][z];
	}
	
	public Set<Integer> getBlockIds() {
		if (readonlyBlockIds == null) {
			readonlyBlockIds = Collections.unmodifiableSet(blockIds);
		}
		
		return readonlyBlockIds;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getIndexX() {
		return index.x;
	}
	
	public int getIndexY() {
		return index.y;
	}
	
	public int getIndexZ() {
		return index.z;
	}
	
	public Realm getRealm() {
		return realm;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void set(int x, int y, int z, int blockId) {
		data[x][y][z] = blockId;
	}
	
	public void setRealm(Realm realm) {
		this.realm = realm;
	}
	
	@Override
	public String toString() {
		if (stringValue == null) {
			stringValue = "chunk@"
					+ decimalFormat.format(index.getX())
					+ decimalFormat.format(index.getY())
					+ decimalFormat.format(index.getZ());
		}
		
		return stringValue;
	}
	
	public void updateStatistics() {
		blockIds.clear();
		
		for (int[][] yz : data) {
			for (int[] z : yz) {
				for (int blockId : z) {
					blockIds.add(Integer.valueOf(blockId));
				}
			}
		}
	}
	
	protected int modIndex(int index, int maximum) {
		if (index < 0) {
			return -1;
		} else if (index >= maximum) {
			return 1;
		} else {
			return 0;
		}
	}
	
	protected int modLocation(int value, int ourSize, int neighbourSize) {
		if (value < 0) {
			return value + neighbourSize;
		} else if (value >= ourSize) {
			return value - ourSize;
		} else {
			return value;
		}
	}
	
}
