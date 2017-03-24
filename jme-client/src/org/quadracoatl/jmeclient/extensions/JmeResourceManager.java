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

package org.quadracoatl.jmeclient.extensions;

import java.io.IOException;
import java.io.InputStream;

import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.resources.ResourceCache;
import org.quadracoatl.framework.resources.ResourceType;

import com.jme3.material.Material;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class JmeResourceManager extends ResourceCache {
	private AWTLoader loader = new AWTLoader();
	private TIntObjectMap<Material> materials = new TIntObjectHashMap<>();
	
	public JmeResourceManager(String modsDirectory) {
		super(modsDirectory);
	}
	
	public Material getMaterial(Block block) {
		return materials.get(block.getId());
	}
	
	public Image loadImage(InputStream inputStream) {
		try {
			return loader.load(inputStream, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Texture loadTexture(String key) {
		return loadTexture(key, getResourceStream(ResourceType.TEXTURE, key));
	}
	
	public Texture loadTexture(String name, InputStream inputStream) {
		try {
			Texture2D texture = new Texture2D(loader.load(inputStream, true));
			texture.setName(name);
			return texture;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void setMaterial(Block block, Material material) {
		materials.put(block.getId(), material);
	}
}
