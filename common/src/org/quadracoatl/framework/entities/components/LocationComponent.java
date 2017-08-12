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
import org.quadracoatl.framework.entities.AbstractComponent;
import org.quadracoatl.framework.entities.Component;
import org.quadracoatl.framework.logging.LogUtil;

public class LocationComponent extends AbstractComponent {
	private Vector3d location = null;
	
	public LocationComponent() {
		super();
		
		location = new Vector3d();
	}
	
	public LocationComponent(Vector3d location) {
		super();
		
		if (location != null) {
			this.location = location;
		} else {
			this.location = new Vector3d();
		}
	}
	
	public Vector3d getLocation() {
		return location;
	}
	
	@Override
	public void updateWith(Component component) {
		if (component instanceof LocationComponent) {
			LOGGER.debug("Updating with ", LogUtil.getIdentity(component), ".");
			
			Vector3d otherLocation = ((LocationComponent)component).location;
			
			location.set(
					otherLocation.x,
					otherLocation.y,
					otherLocation.z);
			
			markAsChanged();
		}
	}
}
