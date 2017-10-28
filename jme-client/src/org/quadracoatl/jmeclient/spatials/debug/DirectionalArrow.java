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

package org.quadracoatl.jmeclient.spatials.debug;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.debug.Arrow;

public class DirectionalArrow extends Geometry {
	private Arrow arrow = null;
	private Vector4f color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
	
	public DirectionalArrow(AssetManager assetManager, String name, Vector3f direction) {
		super(name, new Arrow(direction));
		
		arrow = (Arrow)getMesh();
		
		setMaterial(new Material(assetManager, "/Common/MatDefs/Misc/Unshaded.j3md"));
		
		updateDirection(direction);
	}
	
	public DirectionalArrow(AssetManager assetManager, String name, Vector3f location, Vector3f target) {
		this(assetManager, name, Vector3f.ZERO);
		
		updateDirection(location, target);
	}
	
	public void updateDirection(Vector3f direction) {
		arrow.setArrowExtent(direction);
		
		color.x = Math.abs(direction.x);
		color.y = Math.abs(direction.y);
		color.z = Math.abs(direction.z);
		color.normalizeLocal();
		
		getMaterial().setVector4("Color", color);
		
		updateGeometricState();
	}
	
	public void updateDirection(Vector3f location, Vector3f target) {
		setLocalTranslation(location);
		
		updateDirection(new Vector3f(
				target.x - location.x,
				target.y - location.y,
				target.z - location.z));
	}
}
