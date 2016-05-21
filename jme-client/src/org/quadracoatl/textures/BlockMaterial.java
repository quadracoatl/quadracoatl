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

package org.quadracoatl.textures;

import org.quadracoatl.framework.resources.ResourceManager;
import org.quadracoatl.framework.resources.ResourceType;
import org.quadracoatl.framework.resources.textures.CubeTexture;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;

public class BlockMaterial extends Material {
	private Texture texture = null;
	
	public BlockMaterial(AssetManager assetManager, ResourceManager resourceManager, CubeTexture texture) {
		super(assetManager, "/assets/shaders/SolidBlock.j3md");
		
		this.texture = new Texture2D(new AWTLoader().load(TextureCombiner.createMap(
				resourceManager.getResourceStream(ResourceType.TEXTURE, texture.getTop()),
				resourceManager.getResourceStream(ResourceType.TEXTURE, texture.getBottom()),
				resourceManager.getResourceStream(ResourceType.TEXTURE, texture.getLeft()),
				resourceManager.getResourceStream(ResourceType.TEXTURE, texture.getFront()),
				resourceManager.getResourceStream(ResourceType.TEXTURE, texture.getRight()),
				resourceManager.getResourceStream(ResourceType.TEXTURE, texture.getBack())), true));
		this.texture.setMagFilter(MagFilter.Nearest);
		this.texture.setMinFilter(MinFilter.NearestNoMipMaps);
		this.texture.setWrap(WrapMode.Repeat);
		
		setTexture("ColorMap", this.texture);
	}
}
