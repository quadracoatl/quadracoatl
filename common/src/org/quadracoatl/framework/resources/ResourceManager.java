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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pmw.tinylog.Logger;
import org.quadracoatl.framework.support.hashers.Md5Hasher;

public class ResourceManager {
	protected static final String KEY_SEPARATOR = "/";
	protected List<Resource> allResources = new ArrayList<>();
	protected Map<ResourceType, Map<String, Path>> paths = new HashMap<>();
	protected Map<ResourceType, Map<String, Resource>> resources = new HashMap<>();
	private List<Resource> allResourcesReadOnly = null;
	
	public ResourceManager() {
		super();
		
		allResourcesReadOnly = Collections.unmodifiableList(allResources);
		
		for (ResourceType type : ResourceType.values()) {
			paths.put(type, new HashMap<>());
			resources.put(type, new HashMap<>());
		}
	}
	
	public void addResource(Resource resource, Path path) {
		allResources.add(resource);
		resources.get(resource.getType()).put(resource.getKey(), resource);
		paths.get(resource.getType()).put(resource.getKey(), path);
	}
	
	public void gatherResources(Path baseDirectory) {
		for (ResourceType type : ResourceType.values()) {
			Path directory = Paths.get(baseDirectory.toString(), type.getDirectoryName()).toAbsolutePath();
			
			if (Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS)) {
				gatherResources(type, directory, directory);
			}
		}
	}
	
	public void gatherResources(ResourceType type, Path path, Path baseDirectory) {
		try (DirectoryStream<Path> children = Files.newDirectoryStream(path)) {
			for (Path child : children) {
				if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
					gatherResources(type, child, baseDirectory);
				} else if (Files.isRegularFile(child, LinkOption.NOFOLLOW_LINKS)) {
					String key = createKey(child.subpath(baseDirectory.getNameCount(), child.getNameCount()));
					Resource resource = createResource(type, key, child);
					
					addResource(resource, child);
				}
			}
		} catch (IOException e) {
			Logger.error(e, "Failed to read '{}' from '{}'.", type, path.toAbsolutePath());
		}
	}
	
	public Resource getResource(ResourceType type, String key) {
		return resources.get(type).get(key);
	}
	
	public byte[] getResourceContent(Resource resource) {
		return getResourceContent(resource.getType(), resource.getKey());
	}
	
	public byte[] getResourceContent(ResourceType type, String key) {
		try {
			return Files.readAllBytes(paths.get(type).get(key));
		} catch (IOException e) {
			Logger.error(e, "Failed to read resource '{}' with key '{}'.", type, key);
		}
		
		return null;
	}
	
	public List<Resource> getResources() {
		return allResourcesReadOnly;
	}
	
	public InputStream getResourceStream(Resource resource) {
		return getResourceStream(resource.getType(), resource.getKey());
	}
	
	public InputStream getResourceStream(ResourceType type, String key) {
		try {
			return new FileInputStream(paths.get(type).get(key).toFile());
		} catch (FileNotFoundException e) {
			Logger.error(e, "Failed to read resource '{}' with key '{}'.", type, key);
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		
		string.append(getClass().getSimpleName());
		string.append("@");
		string.append(Integer.toString(System.identityHashCode(this)));
		string.append("\n");
		
		for (ResourceType type : ResourceType.values()) {
			string.append("\t");
			string.append(type.toString());
			string.append("s\n");
			
			List<String> keys = new ArrayList<>(resources.get(type).keySet());
			keys.sort(null);
			
			for (String key : keys) {
				string.append("\t\t");
				string.append(key);
				string.append(" -> ");
				string.append(paths.get(type).get(key).toString());
				string.append("\n");
			}
		}
		
		return string.toString();
	}
	
	protected String createKey(Path path) {
		StringBuilder key = new StringBuilder();
		
		for (int index = 0; index < path.getNameCount(); index++) {
			key.append(path.getName(index));
			key.append(KEY_SEPARATOR);
		}
		
		// Remove the last separator.
		if (path.getNameCount() > 0) {
			key.delete(key.length() - KEY_SEPARATOR.length(), key.length());
		}
		
		return key.toString();
	}
	
	protected Path createPath(String key) {
		return Paths.get("", key.split(KEY_SEPARATOR));
	}
	
	protected Resource createResource(ResourceType type, String key, Path path) {
		try (InputStream stream = new FileInputStream(path.toFile())) {
			return new Resource(type, key, Md5Hasher.hash(stream));
		} catch (IOException e) {
			Logger.error(e, "Failed to read resource '{}' with key '{}' from '{}'.", type, key, path.toAbsolutePath());
		}
		
		return null;
	}
}
