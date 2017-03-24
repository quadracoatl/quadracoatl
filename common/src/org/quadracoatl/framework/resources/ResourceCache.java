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

package org.quadracoatl.framework.resources;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.quadracoatl.framework.logging.Logger;
import org.quadracoatl.framework.logging.LoggerFactory;

public class ResourceCache extends ResourceManager {
	private Path cacheDirectory = null;
	private final Logger LOGGER = LoggerFactory.getLogger(this);
	
	public ResourceCache(String cacheDirectory) {
		super();
		
		this.cacheDirectory = Paths.get(cacheDirectory).toAbsolutePath();
		
		gatherResources(this.cacheDirectory);
	}
	
	public void cache(Resource resource, byte[] content) {
		Path resourcePath = Paths.get(cacheDirectory.toString(), createPath(resource.getKey()).toString());
		
		try {
			Files.createDirectories(resourcePath.getParent());
			
			Files.write(resourcePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			LOGGER.error("Failed to write resource \"", resource.getKey(), "\" with key \"", resource.getKey(), "\".", e);
		}
		
		addResource(resource, resourcePath);
	}
	
	public void cache(Resource resource, InputStream inputStream) {
		Path resourcePath = Paths.get(cacheDirectory.toString(), createPath(resource.getKey()).toString());
		
		try {
			Files.createDirectories(resourcePath.getParent());
			
			try (FileOutputStream outputStream = new FileOutputStream(resourcePath.toFile(), false)) {
				byte[] buffer = new byte[4096];
				int read = 0;
				
				while ((read = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, read);
				}
			}
		} catch (IOException e) {
			LOGGER.error("Failed to write resource \"", resource.getKey(), "\" with key \"", resource.getKey(), "\".", e);
		}
		
		addResource(resource, resourcePath);
	}
	
	public boolean isCached(Resource resource) {
		Resource storedResource = resources.get(resource.getType()).get(resource.getKey());
		
		return storedResource != null
				&& storedResource.getHash().equals(resource.getHash());
	}
}
