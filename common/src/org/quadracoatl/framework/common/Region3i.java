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

public class Region3i {
	public Vector3i end = new Vector3i();
	public Vector3i start = new Vector3i();
	
	public Region3i() {
		super();
	}
	
	public Region3i(int startX, int startY, int startZ, int endX, int endY, int endZ) {
		super();
		
		start.x = startX;
		start.y = startY;
		start.z = startZ;
		end.x = endX;
		end.y = endY;
		end.z = endZ;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Region3i other = (Region3i)obj;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}
	
	public Vector3i getEnd() {
		return end;
	}
	
	public Vector3i getStart() {
		return start;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		return result;
	}
	
	public void set(int startX, int startY, int startZ, int endX, int endY, int endZ) {
		start.x = startX;
		start.y = startY;
		start.z = startZ;
		end.x = endX;
		end.y = endY;
		end.z = endZ;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [end=" + end + ", start=" + start + "]";
	}
	
}
