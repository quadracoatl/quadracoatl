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

import org.quadracoatl.framework.common.vectors.Vector2d;

public class Region2d {
	public Vector2d end = new Vector2d();
	public Vector2d start = new Vector2d();
	
	public Region2d() {
		super();
	}
	
	public Region2d(double startX, double startY, double endX, double endY) {
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
		Region2d other = (Region2d)obj;
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
	
	public Vector2d getEnd() {
		return end;
	}
	
	public Vector2d getStart() {
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
	
	public void set(double startX, double startY, double endX, double endY) {
		start.x = startX;
		start.y = startY;
		end.x = endX;
		end.y = endY;
	}
	
	public void setEnd(double x, double y) {
		end.x = x;
		end.y = y;
	}
	
	public void setStart(double x, double y) {
		start.x = x;
		start.y = y;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [end=" + end + ", start=" + start + "]";
	}
}
