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

package org.quadracoatl.jmeclient.materials;

import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.block.BlockType;
import org.quadracoatl.framework.resources.colors.Color;
import org.quadracoatl.framework.resources.colors.ColorBlendMode;
import org.quadracoatl.jmeclient.extensions.JmeResourceManager;
import org.quadracoatl.jmeclient.utils.ShaderUtil;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.Vector4f;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture.WrapMode;

public class BlockMaterial extends Material {
	private Vector4f color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
	private Texture texture = null;
	
	public BlockMaterial(
			Block block,
			AssetManager assetManager,
			JmeResourceManager resourceManager) {
		super(assetManager, getShader(block.getBlockType()));
		
		if (block.getColor() != null) {
			Color blockColor = block.getColor();
			
			color.x = (float)blockColor.red;
			color.y = (float)blockColor.green;
			color.z = (float)blockColor.blue;
			color.w = (float)blockColor.alpha;
			
			setInt("ColorBlendMode", ShaderUtil.getColorBlendModeAsInteger(ColorBlendMode.NORMAL));
		} else {
			setInt("ColorBlendMode", ShaderUtil.COLOR_BLEND_MODE_INVALID);
		}
		
		setVector4("Color", color);
		
		if (block.getTexture() != null) {
			texture = resourceManager.loadTexture(block.getName(), block.getTexture().getAsResource(resourceManager));
		} else {
			texture = assetManager.loadTexture("/assets/images/white-pixel.png");
		}
		
		texture.setMagFilter(MagFilter.Nearest);
		texture.setMinFilter(MinFilter.NearestNoMipMaps);
		texture.setWrap(WrapMode.Repeat);
		
		setTexture("ColorMap", texture);
		
		getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
	}
	
	protected static final String getShader(BlockType blockType) {
		StringBuilder shaderName = new StringBuilder(blockType.toString().length());
		
		char[] chars = blockType.toString().toCharArray();
		
		shaderName.append(chars[0]);
		for (int index = 1; index < chars.length; index++) {
			char currentChar = chars[index];
			
			if (currentChar == '_') {
				shaderName.append(Character.toUpperCase(chars[++index]));
			} else {
				shaderName.append(Character.toLowerCase(currentChar));
			}
		}
		
		return "/assets/shaders/blocks/" + shaderName + ".j3md";
	}
}
