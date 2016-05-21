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

import org.quadracoatl.interlayer.Transferrable;
import org.quadracoatl.utils.Vector3i;

public @Transferrable class Chunk {
	private static final DecimalFormat decimalFormat = new DecimalFormat("+#;-#");
	
	protected @Transferrable int[][][] data = null;
	protected @Transferrable int depth = 0;
	protected @Transferrable int height = 0;
	protected @Transferrable Vector3i index = null;
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
	
	public int getBlock(int x, int y, int z) {
		if (x < 0 || x >= width
				|| y < 0 || y >= height
				|| z < 0 || z >= depth) {
			return -1;
		}
		
		return data[x][y][z];
	}
	
	public Vector3i getIndex() {
		return index;
	}
	
	public int[][][] getRawData() {
		return data;
	}
	
	public void setBlock(int x, int y, int z, int block) {
		data[x][y][z] = block;
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
	
}
