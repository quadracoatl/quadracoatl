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

import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.realm.Realm;
import org.quadracoatl.interlayer.Transferrable;
import org.quadracoatl.utils.Vector3i;

public @Transferrable class Chunk {
	private static final DecimalFormat decimalFormat = new DecimalFormat("+#;-#");
	
	protected @Transferrable int[][][] data = null;
	protected @Transferrable int depth = 0;
	protected @Transferrable int height = 0;
	protected @Transferrable Vector3i index = null;
	protected Realm realm = null;
	protected String stringValue = null;
	protected @Transferrable int width = 0;
	
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
				} else {
					return Block.NONEXISTENT_BLOCK.getId();
				}
			} else {
				return Block.NONEXISTENT_BLOCK.getId();
			}
		}
		
		return data[x][y][z];
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
