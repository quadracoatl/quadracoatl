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

import org.quadracoatl.framework.common.vectors.Vector2i;

public class Region2i {
	public Vector2i end = new Vector2i();
	public Vector2i start = new Vector2i();
	
	public Region2i() {
		super();
	}
	
	public Region2i(int startX, int startY, int endX, int endY) {
		super();
		
		start.x = startX;
		start.y = startY;
		end.x = endX;
		end.y = endY;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Region2i other = (Region2i)obj;
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
	
	public Vector2i getEnd() {
		return end;
	}
	
	public Vector2i getStart() {
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
	
	public void set(int startX, int startY, int endX, int endY) {
		start.x = startX;
		start.y = startY;
		end.x = endX;
		end.y = endY;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [end=" + end + ", start=" + start + "]";
	}
}
