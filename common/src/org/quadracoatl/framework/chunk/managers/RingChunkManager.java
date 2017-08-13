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

import java.lang.ref.WeakReference;

import org.quadracoatl.framework.chunk.Chunk;

public class RingChunkManager extends SimpleChunkManager {
	private WeakReference<Chunk>[] buffer = null;
	private int currentIndex = 0;
	
	@SuppressWarnings("unchecked")
	public RingChunkManager(int bufferSize) {
		super();
		
		buffer = new WeakReference[bufferSize];
	}
	
	@Override
	public void add(Chunk chunk) {
		super.add(chunk);
		
		currentIndex++;
		
		if (currentIndex >= buffer.length) {
			currentIndex = 0;
		}
		
		WeakReference<Chunk> chunkToRemoveReference = buffer[currentIndex];
		
		if (chunkToRemoveReference != null) {
			Chunk chunkToRemove = chunkToRemoveReference.get();
			
			if (chunkToRemove != null) {
				remove(chunkToRemove);
			}
		}
		
		buffer[currentIndex] = new WeakReference<Chunk>(chunk);
	}
	
	@Override
	public void detach() {
		super.detach();
		
		for (int index = 0; index < buffer.length; index++) {
			buffer[index] = null;
		}
		
		currentIndex = 0;
	}
}
