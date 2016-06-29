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

package org.quadracoatl.framework.block;

import java.util.HashMap;
import java.util.Map;

import org.quadracoatl.framework.resources.textures.CubeTexture;
import org.quadracoatl.interlayer.Transferrable;

public @Transferrable class Block {
	public static final Block NONEXISTENT_BLOCK = new Block(-1, "NON_EXISTENT");
	public static final Block NULL_BLOCK = new Block(0, "NULL");
	private Map<String, Object> additionalState = new HashMap<>();
	private @Transferrable int id;
	private @Transferrable String name;
	private @Transferrable CubeTexture texture;
	
	public Block(int id, String name) {
		this();
		
		this.id = id;
		this.name = name;
	}
	
	private Block() {
		super();
	}
	
	public static boolean isNullBlock(Block block) {
		return block.id == NULL_BLOCK.id;
	}
	
	public Map<String, Object> getAdditionalState() {
		return additionalState;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public CubeTexture getTexture() {
		return texture;
	}
	
	public boolean isNullBlock() {
		return id == NULL_BLOCK.id;
	}
	
	public void setTexture(CubeTexture texture) {
		this.texture = texture;
	}
}
