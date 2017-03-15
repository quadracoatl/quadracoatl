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

public class Vector3i {
	public int x = 0;
	public int y = 0;
	public int z = 0;
	
	public Vector3i() {
		super();
	}
	
	public Vector3i(int x, int y, int z) {
		super();
		
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3i(Vector3i vector) {
		this(vector.x, vector.y, vector.z);
	}
	
	public boolean equals(int x, int y, int z) {
		return this.x == x
				&& this.y == y
				&& this.z == z;
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
		Vector3i other = (Vector3i)obj;
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		if (z != other.z) {
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
	
	public int getZ() {
		return z;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}
	
	public void set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void set(Vector3i vector) {
		this.x = vector.x;
		this.y = vector.y;
		this.z = vector.z;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setZ(int z) {
		this.z = z;
	}
	
	@Override
	public String toString() {
		return LogUtil.getIdentity(this) + "[x=" + x + ", y=" + y + ", z=" + z + "]";
	}
	
	public boolean update(int x, int y, int z) {
		if (!equals(x, y, z)) {
			set(x, y, z);
			return true;
		}
		
		return false;
	}
}
