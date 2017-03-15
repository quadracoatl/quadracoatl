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

package org.quadracoatl.jmeclient.spatials.debug;

import org.quadracoatl.jmeclient.controls.CameraAttachingControl;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Grid;

public class GridsSpatial extends Node {
	public GridsSpatial(AssetManager assetManager) {
		super("grid");
		
		setCullHint(CullHint.Never);
		setQueueBucket(Bucket.Opaque);
		
		attachChild(createGrid(assetManager, "xz", new Vector3f(1.0f, 0.0f, -1.0f), Vector3f.UNIT_X, Vector3f.UNIT_Y));
		attachChild(createGrid(assetManager, "yz", new Vector3f(1.0f, 1.0f, 0.0f), Vector3f.UNIT_Y, Vector3f.UNIT_Y));
		attachChild(createGrid(assetManager, "xz", new Vector3f(0.0f, -1.0f, 1.0f), Vector3f.UNIT_Z, Vector3f.UNIT_X));
		
		addControl(new CameraAttachingControl(2.0f));
		
		updateGeometricState();
	}
	
	private Geometry createGrid(
			AssetManager assetManager,
			String name,
			Vector3f plane,
			Vector3f direction,
			Vector3f up) {
		Vector4f color = new Vector4f(
				Math.abs(direction.x),
				Math.abs(direction.y),
				Math.abs(direction.z),
				1.0f);
		
		Material material = new Material(assetManager, "/Common/MatDefs/Misc/Unshaded.j3md");
		material.setVector4("Color", color);
		
		Grid grid = new Grid(8, 8, 1.0f);
		
		Geometry geometry = new Geometry(name, grid);
		geometry.lookAt(direction, up);
		geometry.setLocalTranslation(plane.mult(-4.0f));
		geometry.setMaterial(material);
		
		return geometry;
	}
}
