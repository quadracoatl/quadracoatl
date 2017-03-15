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
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;

public class AxisSpatial extends Node {
	
	public AxisSpatial(AssetManager assetManager) {
		super("axis");
		
		setCullHint(CullHint.Never);
		setQueueBucket(Bucket.Opaque);
		
		attachChild(new DirectionalArrow(assetManager, "+x", new Vector3f(1.0f, 0.0f, 0.0f)));
		attachChild(new DirectionalArrow(assetManager, "+y", new Vector3f(0.0f, 1.0f, 0.0f)));
		attachChild(new DirectionalArrow(assetManager, "+z", new Vector3f(0.0f, 0.0f, 1.0f)));
		
		attachChild(new DirectionalArrow(assetManager, "-x", new Vector3f(-0.5f, 0.0f, 0.0f)));
		attachChild(new DirectionalArrow(assetManager, "-y", new Vector3f(0.0f, -0.5f, 0.0f)));
		attachChild(new DirectionalArrow(assetManager, "-z", new Vector3f(0.0f, 0.0f, -0.5f)));
		
		addControl(new CameraAttachingControl(2.0f));
		
		updateGeometricState();
	}
}
