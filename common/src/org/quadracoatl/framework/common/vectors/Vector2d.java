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

package org.quadracoatl.framework.common.vectors;

import org.quadracoatl.framework.logging.LogUtil;

public class Vector2d {
	public double x = 0;
	public double y = 0;
	
	public Vector2d() {
		super();
	}
	
	public Vector2d(double x, double y) {
		super();
		
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(double x, double y) {
		return Double.doubleToLongBits(this.x) == Double.doubleToLongBits(x)
				&& Double.doubleToLongBits(this.y) == Double.doubleToLongBits(y);
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
		Vector2d other = (Vector2d)obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) {
			return false;
		}
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) {
			return false;
		}
		return true;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int)(temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int)(temp ^ (temp >>> 32));
		return result;
	}
	
	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(Vector2d vector) {
		this.x = vector.x;
		this.y = vector.y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	@Override
	public String toString() {
		return LogUtil.getSimpleIdentity(this) + "[x=" + x + ", y=" + y + "]";
	}
	
	public boolean update(double x, double y) {
		if (!equals(x, y)) {
			set(x, y);
			return true;
		}
		
		return false;
	}
}
