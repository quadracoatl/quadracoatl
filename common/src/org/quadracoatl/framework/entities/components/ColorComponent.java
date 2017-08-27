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

import org.quadracoatl.framework.entities.Component;
import org.quadracoatl.framework.logging.LogUtil;
import org.quadracoatl.framework.resources.colors.Color;
import org.quadracoatl.framework.resources.colors.ColorBlendMode;

public class ColorComponent extends AbstractComponent {
	private Color color = null;
	private ColorBlendMode colorBlendMode = null;
	
	public ColorComponent() {
		super();
		
		color = new Color();
	}
	
	public ColorComponent(Color color, ColorBlendMode colorBlendMode) {
		super();
		
		if (color != null) {
			this.color = color;
		} else {
			this.color = new Color();
		}
		
		this.colorBlendMode = colorBlendMode;
	}
	
	public Color getColor() {
		return color;
	}
	
	public ColorBlendMode getColorBlendMode() {
		return colorBlendMode;
	}
	
	public void setColorBlendMode(ColorBlendMode colorMode) {
		this.colorBlendMode = colorMode;
	}
	
	@Override
	public void updateWith(Component component) {
		if (component instanceof ColorComponent) {
			LOGGER.debug("Updating with ", LogUtil.getIdentity(component), ".");
			
			Color otherColor = ((ColorComponent)component).color;
			
			color.set(
					otherColor.red,
					otherColor.green,
					otherColor.blue,
					otherColor.alpha);
			
			colorBlendMode = ((ColorComponent)component).colorBlendMode;
			
			markAsChanged();
		}
	}
}
