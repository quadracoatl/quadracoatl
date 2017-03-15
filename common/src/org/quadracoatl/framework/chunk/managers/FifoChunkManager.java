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

import org.quadracoatl.framework.chunk.Chunk;

public class FifoChunkManager extends ChunkManager {
	private Chunk[] buffer = null;
	private int currentIndex = 0;
	
	public FifoChunkManager(int bufferSize) {
		super();
		
		buffer = new Chunk[bufferSize];
	}
	
	@Override
	public void put(Chunk chunk) {
		super.put(chunk);
		
		currentIndex++;
		
		if (currentIndex >= buffer.length) {
			currentIndex = 0;
		}
		
		Chunk chunkToRemove = buffer[currentIndex];
		
		if (chunkToRemove != null) {
			remove(chunkToRemove);
		}
		
		buffer[currentIndex] = chunk;
	}
}
