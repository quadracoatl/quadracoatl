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

package org.quadracoatl;

import java.io.InputStream;

import org.quadracoatl.framework.resources.Resource;
import org.quadracoatl.framework.resources.ResourceCache;
import org.quadracoatl.framework.resources.ResourceType;

import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;

public class JmeResourceManager extends ResourceCache {
	private AWTLoader loader = new AWTLoader();
	
	public JmeResourceManager(String modsDirectory) {
		super(modsDirectory);
	}
	
	public Texture loadTexture(String key) {
		Resource resource = resources.get(ResourceType.TEXTURE).get(key);
		
		try (InputStream stream = getResourceStream(resource)) {
			return new Texture2D(loader.load(stream, true));
		} catch (Throwable th) {
			th.printStackTrace();
		}
		
		return null;
	}
}
