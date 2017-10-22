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

package org.quadracoatl.framework.entities.components;

import org.quadracoatl.framework.common.vectors.Vector3d;
import org.quadracoatl.framework.entities.Component;
import org.quadracoatl.framework.logging.LogUtil;

public class RotationComponent extends AbstractComponent {
	private Vector3d rotation = null;
	
	public RotationComponent() {
		super();
		
		rotation = new Vector3d();
	}
	
	public RotationComponent(Vector3d rotation) {
		super();
		
		if (rotation != null) {
			this.rotation = rotation;
		} else {
			this.rotation = new Vector3d();
		}
	}
	
	public Vector3d getRotation() {
		return rotation;
	}
	
	@Override
	public void updateWith(Component component) {
		if (component instanceof RotationComponent) {
			LOGGER.debug("Updating with ", LogUtil.getSimpleIdentity(component), ".");
			
			Vector3d otherRotation = ((RotationComponent)component).rotation;
			
			rotation.set(
					otherRotation.x,
					otherRotation.y,
					otherRotation.z);
			
			markAsChanged();
		}
	}
}
