/*
 * Copyright 2017, Robert 'Bobby' Zenz
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

package org.quadracoatl.framework.resources.lights;

import org.quadracoatl.framework.common.vectors.Vector3d;
import org.quadracoatl.framework.resources.colors.Color;

public class LightSource {
	private Color color = null;
	private Vector3d direction = null;
	private LightSourceType type = null;
	
	public LightSource() {
		super();
		
		color = new Color(1.0d, 1.0d, 1.0d, 1.0d);
		direction = new Vector3d(0.0d, 0.0d, 0.0d);
		type = LightSourceType.AMBIENT;
	}
	
	public LightSource(LightSourceType type, Color color, Vector3d direction) {
		super();
		
		if (type != null) {
			this.type = type;
		} else {
			this.type = LightSourceType.AMBIENT;
		}
		
		if (color != null) {
			this.color = color;
		} else {
			this.color = new Color(1.0d, 1.0d, 1.0d);
		}
		
		if (direction != null) {
			this.direction = direction;
		} else {
			this.direction = new Vector3d(0.0d, 0.0d, 0.0d);
		}
	}
	
	public Color getColor() {
		return color;
	}
	
	public Vector3d getDirection() {
		return direction;
	}
	
	public LightSourceType getType() {
		return type;
	}
	
	public void setType(LightSourceType type) {
		this.type = type;
	}
}
