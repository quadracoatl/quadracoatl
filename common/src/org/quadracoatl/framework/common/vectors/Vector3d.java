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
	
	public static final Vector3d center(Vector3d pointA, Vector3d pointB, Vector3d pointC) {
		return center(pointA, pointB, pointC, new Vector3d());
	}
	
	public static final Vector3d center(Vector3d pointA, Vector3d pointB, Vector3d pointC, Vector3d target) {
		target.x = (pointA.x + pointB.x + pointC.x) / 3.0d;
		target.y = (pointA.y + pointB.y + pointC.y) / 3.0d;
		target.z = (pointA.z + pointB.z + pointC.z) / 3.0d;
		
		return target;
	}
	
	public static final double dot(Vector3d vectorA, Vector3d vectorB) {
		return (vectorA.x * vectorB.x)
				+ (vectorA.y * vectorB.y)
				+ (vectorA.z * vectorB.z);
	}
	
	public static final Vector3d normal(Vector3d pointA, Vector3d pointB, Vector3d pointC) {
		return normal(pointA, pointB, pointC, new Vector3d());
	}
	
	public static final Vector3d normal(Vector3d pointA, Vector3d pointB, Vector3d pointC, Vector3d target) {
		double vx = pointB.x - pointA.x;
		double vy = pointB.y - pointA.y;
		double vz = pointB.z - pointA.z;
		
		double wx = pointC.x - pointA.x;
		double wy = pointC.y - pointA.y;
		double wz = pointC.z - pointA.z;
		
		target.x = (vy * wz) - (vz * wy);
		target.y = (vz * wx) - (vx * wz);
		target.z = (vx * wy) - (vy * wx);
		target.normalize();
		
		return target;
	}
	
	public void add(double value) {
		add(value, value, value);
	}
	
	public void add(double x, double y, double z) {
		this.x = this.x + x;
		this.y = this.y + y;
		this.z = this.z + z;
	}
	
	public void add(Vector3d vector) {
		add(vector.x, vector.y, vector.z);
	}
	
	public void divide(double value) {
		divide(value, value, value);
	}
	
	public void divide(double x, double y, double z) {
		this.x = this.x / x;
		this.y = this.y / y;
		this.z = this.z / z;
	}
	
	public void divide(Vector3d vector) {
		divide(vector.x, vector.y, vector.z);
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
	
	public double getLength() {
		return Math.sqrt((x * x) + (y * y) + (z * z));
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
	
	public void multiply(double value) {
		multiply(value, value, value);
	}
	
	public void multiply(double x, double y, double z) {
		this.x = this.x * x;
		this.y = this.y * y;
		this.z = this.z * z;
	}
	
	public void multiply(Vector3d vector) {
		multiply(vector.x, vector.y, vector.z);
	}
	
	public void normalize() {
		double length = getLength();
		
		if (length != 0.0d) {
			x = x / length;
			y = y / length;
			z = z / length;
		}
	}
	
	public void rotate(double radiansX, double radiansY, double radiansZ) {
		// TODO Rotation around X is missing.
		
		double length = getLength();
		double theta = Math.acos(y / length);
		double phi = Math.atan2(z, x);
		
		if (phi >= 0.0d) {
			theta = theta + radiansZ;
		} else {
			theta = theta - radiansZ;
		}
		
		phi = phi + radiansY;
		
		x = length * Math.sin(theta) * Math.cos(phi);
		y = length * Math.cos(theta);
		z = length * Math.sin(theta) * Math.sin(phi);
	}
	
	public void rotate(Vector3d vector) {
		rotate(vector.x, vector.y, vector.z);
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
	
	public void subtract(double value) {
		subtract(value, value, value);
	}
	
	public void subtract(double x, double y, double z) {
		this.x = this.x - x;
		this.y = this.y - y;
		this.z = this.z - z;
	}
	
	public void subtract(Vector3d vector) {
		subtract(vector.x, vector.y, vector.z);
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
