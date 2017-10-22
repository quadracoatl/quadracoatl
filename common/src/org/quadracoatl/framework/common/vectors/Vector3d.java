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

public class Vector3d {
	public double x = 0;
	public double y = 0;
	public double z = 0;
	
	public Vector3d() {
		super();
	}
	
	public Vector3d(double x, double y, double z) {
		super();
		
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public boolean equals(double x, double y, double z) {
		return Double.doubleToLongBits(this.x) == Double.doubleToLongBits(x)
				&& Double.doubleToLongBits(this.y) == Double.doubleToLongBits(y)
				&& Double.doubleToLongBits(this.z) == Double.doubleToLongBits(z);
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
		Vector3d other = (Vector3d)obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) {
			return false;
		}
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) {
			return false;
		}
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z)) {
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
	
	public double getZ() {
		return z;
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
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int)(temp ^ (temp >>> 32));
		return result;
	}
	
	public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void set(Vector3d vector) {
		this.x = vector.x;
		this.y = vector.y;
		this.z = vector.z;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setZ(double z) {
		this.z = z;
	}
	
	@Override
	public String toString() {
		return LogUtil.getSimpleIdentity(this) + "[x=" + x + ", y=" + y + ", z=" + z + "]";
	}
	
	public boolean update(double x, double y, double z) {
		if (!equals(x, y, z)) {
			set(x, y, z);
			return true;
		}
		
		return false;
	}
}
