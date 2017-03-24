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

package org.quadracoatl.framework.common;

import org.quadracoatl.framework.logging.LogUtil;

public class Vector2i {
	public int x = 0;
	public int y = 0;
	
	public Vector2i() {
		super();
	}
	
	public Vector2i(int x, int y) {
		super();
		
		this.x = x;
		this.y = y;
	}
	
	public Vector2i(Vector2i vector) {
		this(vector.x, vector.y);
	}
	
	public boolean equals(int x, int y) {
		return this.x == x
				&& this.y == y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Vector2i other = (Vector2i)obj;
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		return true;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	
	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(Vector2i vector) {
		this.x = vector.x;
		this.y = vector.y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public String toString() {
		return LogUtil.getIdentity(this) + "[x=" + x + ", y=" + y + "]";
	}
	
	public boolean update(int x, int y) {
		if (!equals(x, y)) {
			set(x, y);
			return true;
		}
		
		return false;
	}
}
