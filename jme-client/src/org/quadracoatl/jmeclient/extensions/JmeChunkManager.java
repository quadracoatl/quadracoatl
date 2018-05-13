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

package org.quadracoatl.jmeclient.extensions;

import java.util.function.Consumer;

import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.chunk.managers.RingChunkManager;

public class JmeChunkManager extends RingChunkManager {
	private Consumer<Chunk> onRemove = null;
	
	public JmeChunkManager(Consumer<Chunk> onRemove) {
		// TODO Hardcoded limit here.
		super(8192);
		
		this.onRemove = onRemove;
	}
	
	@Override
	public void remove(Chunk chunk) {
		super.remove(chunk);
		
		onRemove.accept(chunk);
	}
}
