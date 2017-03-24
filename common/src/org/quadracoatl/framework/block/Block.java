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

import org.quadracoatl.framework.resources.colors.Color;
import org.quadracoatl.framework.resources.textures.Texture;

public class Block {
	public transient static final Block NONEXISTENT_BLOCK = new Block(-1, "NON_EXISTENT", BlockType.NONE);
	public transient static final Block NULL_BLOCK = new Block(0, "NULL", BlockType.NONE);
	private BlockType blockType = BlockType.NONE;
	private Color color = null;
	private int id = -1;
	private String name = null;
	private Texture texture = null;
	private Object typeParameters = null;
	
	public Block(int id, String name, BlockType blockType) {
		this();
		
		this.id = id;
		this.name = name;
		this.blockType = blockType;
	}
	
	private Block() {
		super();
	}
	
	public static boolean isNullBlock(Block block) {
		return block.id == NULL_BLOCK.id;
	}
	
	public BlockType getBlockType() {
		return blockType;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public Object getTypeParameters() {
		return typeParameters;
	}
	
	public boolean isNullBlock() {
		return id == NULL_BLOCK.id;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public void setTypeParameters(Object typeParameters) {
		this.typeParameters = typeParameters;
	}
}
