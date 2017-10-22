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
import org.quadracoatl.framework.resources.textures.Texture;

public class TextureComponent extends AbstractComponent {
	protected Texture texture = null;
	
	public TextureComponent() {
		super();
		
		texture = new Texture();
	}
	
	public TextureComponent(Texture texture) {
		super();
		
		if (texture != null) {
			this.texture = texture;
		} else {
			this.texture = new Texture();
		}
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	@Override
	public void updateWith(Component component) {
		if (component instanceof TextureComponent) {
			LOGGER.debug("Updating with ", LogUtil.getSimpleIdentity(component), ".");
			
			Texture otherTexture = ((TextureComponent)component).texture;
			
			texture.setTextures(
					otherTexture.getTextureType(),
					otherTexture.getTextures());
			
			markAsChanged();
		}
	}
}
