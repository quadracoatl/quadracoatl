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

package org.quadracoatl.framework.resources.colors;

public class Color {
	public double alpha = 1.0d;
	public double blue = 0.0d;
	public double green = 0.0d;
	public double red = 0.0d;
	
	public Color() {
		super();
	}
	
	public Color(double red, double green, double blue) {
		this();
		
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public Color(double red, double green, double blue, double alpha) {
		this(red, green, blue);
		
		this.alpha = alpha;
	}
	
	public boolean equals(double red, double green, double blue, double alpha) {
		return Double.doubleToLongBits(this.red) == Double.doubleToLongBits(red)
				&& Double.doubleToLongBits(this.green) == Double.doubleToLongBits(green)
				&& Double.doubleToLongBits(this.blue) == Double.doubleToLongBits(blue)
				&& Double.doubleToLongBits(this.alpha) == Double.doubleToLongBits(alpha);
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
		
		Color other = (Color)obj;
		
		if (Double.doubleToLongBits(alpha) != Double.doubleToLongBits(other.alpha)) {
			return false;
		}
		
		if (Double.doubleToLongBits(blue) != Double.doubleToLongBits(other.blue)) {
			return false;
		}
		
		if (Double.doubleToLongBits(green) != Double.doubleToLongBits(other.green)) {
			return false;
		}
		
		if (Double.doubleToLongBits(red) != Double.doubleToLongBits(other.red)) {
			return false;
		}
		
		return true;
	}
	
	public double getAlpha() {
		return alpha;
	}
	
	public double getBlue() {
		return blue;
	}
	
	public double getGreen() {
		return green;
	}
	
	public double getRed() {
		return red;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(alpha);
		result = prime * result + (int)(temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(blue);
		result = prime * result + (int)(temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(green);
		result = prime * result + (int)(temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(red);
		result = prime * result + (int)(temp ^ (temp >>> 32));
		return result;
	}
	
	public void set(Color color) {
		this.red = color.red;
		this.green = color.green;
		this.blue = color.blue;
		this.alpha = color.alpha;
	}
	
	public void set(double red, double green, double blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public void set(double red, double green, double blue, double alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	
	public void setBlue(double blue) {
		this.blue = blue;
	}
	
	public void setGreen(double green) {
		this.green = green;
	}
	
	public void setRed(double red) {
		this.red = red;
	}
	
	public boolean update(double red, double green, double blue, double alpha) {
		if (!equals(red, green, blue, alpha)) {
			set(red, green, blue, alpha);
			return true;
		}
		
		return false;
	}
}
