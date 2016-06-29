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

package org.quadracoatl.framework.resources.textures;

import org.quadracoatl.interlayer.Transferrable;

public @Transferrable class CubeTexture {
	private @Transferrable String back;
	private @Transferrable String bottom;
	private @Transferrable String front;
	private @Transferrable String left;
	private @Transferrable String right;
	private @Transferrable String top;
	
	public CubeTexture(String texture) {
		this();
		
		this.top = texture;
		this.bottom = texture;
		this.front = texture;
		this.back = texture;
		this.left = texture;
		this.right = texture;
	}
	
	public CubeTexture(String top, String bottom, String front, String back, String left, String right) {
		this();
		
		this.top = top;
		this.bottom = bottom;
		this.front = front;
		this.back = back;
		this.left = left;
		this.right = right;
	}
	
	private CubeTexture() {
		super();
	}
	
	public String getBack() {
		return back;
	}
	
	public String getBottom() {
		return bottom;
	}
	
	public String getFront() {
		return front;
	}
	
	public String getLeft() {
		return left;
	}
	
	public String getRight() {
		return right;
	}
	
	public String getTop() {
		return top;
	}
}
