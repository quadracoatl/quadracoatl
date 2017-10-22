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

package org.quadracoatl.framework.entities.components;

import org.quadracoatl.framework.entities.Component;
import org.quadracoatl.framework.logging.LogUtil;
import org.quadracoatl.framework.resources.lights.LightSource;

public class LightSourceComponent extends AbstractComponent {
	private LightSource lightSource = null;
	
	public LightSourceComponent() {
		super();
		
		lightSource = new LightSource();
	}
	
	public LightSourceComponent(LightSource lightSource) {
		super();
		
		if (lightSource != null) {
			this.lightSource = lightSource;
		} else {
			this.lightSource = new LightSource();
		}
	}
	
	public LightSource getLightSource() {
		return lightSource;
	}
	
	@Override
	public void updateWith(Component component) {
		if (component instanceof LightSourceComponent) {
			LOGGER.debug("Updating with ", LogUtil.getSimpleIdentity(component), ".");
			
			LightSource otherLightSource = ((LightSourceComponent)component).getLightSource();
			
			lightSource.getColor().set(otherLightSource.getColor());
			lightSource.getDirection().set(otherLightSource.getDirection());
			lightSource.setType(otherLightSource.getType());
			
			markAsChanged();
		}
	}
}
