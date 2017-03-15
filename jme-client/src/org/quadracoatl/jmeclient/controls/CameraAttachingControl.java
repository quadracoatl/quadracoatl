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

package org.quadracoatl.jmeclient.controls;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class CameraAttachingControl extends AbstractControl {
	private float offset = 0.0f;
	
	public CameraAttachingControl() {
		super();
	}
	
	public CameraAttachingControl(float offset) {
		super();
		
		this.offset = offset;
	}
	
	public float getOffset() {
		return offset;
	}
	
	public void setOffset(float offset) {
		this.offset = offset;
	}
	
	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
		Vector3f cameraLocation = vp.getCamera().getLocation();
		Vector3f cameraDirection = vp.getCamera().getDirection();
		
		spatial.setLocalTranslation(
				cameraLocation.x + (cameraDirection.x * offset),
				cameraLocation.y + (cameraDirection.y * offset),
				cameraLocation.z + (cameraDirection.z * offset));
		
		spatial.updateGeometricState();
	}
	
	@Override
	protected void controlUpdate(float tpf) {
		// Nothing to do.
	}
}
