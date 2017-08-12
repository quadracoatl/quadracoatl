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

public class SizeComponent extends AbstractComponent {
	private Vector3d size = null;
	
	public SizeComponent() {
		super();
		
		size = new Vector3d(1.0d, 1.0d, 1.0d);
	}
	
	public SizeComponent(Vector3d size) {
		super();
		
		if (size != null) {
			this.size = size;
		} else {
			this.size = new Vector3d(1.0d, 1.0d, 1.0d);
		}
	}
	
	public Vector3d getSize() {
		return size;
	}
	
	@Override
	public void updateWith(Component component) {
		if (component instanceof SizeComponent) {
			LOGGER.debug("Updating with ", LogUtil.getIdentity(component), ".");
			
			Vector3d otherSize = ((SizeComponent)component).size;
			
			size.set(
					otherSize.x,
					otherSize.y,
					otherSize.z);
			
			markAsChanged();
		}
	}
}
