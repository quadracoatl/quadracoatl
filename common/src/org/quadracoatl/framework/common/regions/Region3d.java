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

package org.quadracoatl.framework.common.regions;

import org.quadracoatl.framework.common.vectors.Vector3d;

public class Region3d {
	public Vector3d end = new Vector3d();
	public Vector3d start = new Vector3d();
	
	public Region3d() {
		super();
	}
	
	public Region3d(double startX, double startY, double startZ, double endX, double endY, double endZ) {
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
		Region3d other = (Region3d)obj;
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
	
	public Vector3d getEnd() {
		return end;
	}
	
	public Vector3d getStart() {
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
	
	public void set(double startX, double startY, double startZ, double endX, double endY, double endZ) {
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
