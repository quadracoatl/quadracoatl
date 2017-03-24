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

package org.quadracoatl.framework.mod;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.quadracoatl.framework.resources.ResourceManager;
import org.quadracoatl.framework.support.Configuration;

public class Mod {
	protected static final String MOD_PROPERTIES_FILE = "mod.properties";
	protected static final String NAME_SANITIZATION_EXPRESSION = "[^a-zA-Z0-9_]";
	protected static final String NAME_SANITIZATION_REPLACEMENT = "_";
	protected Path directory = null;
	protected String displayName = null;
	protected String name = null;
	protected List<String> requires = null;
	protected ResourceManager resourceManager = new ResourceManager();
	
	public Mod(Path directory) {
		super();
		
		this.directory = directory;
		
		readInfo();
	}
	
	public Path getDirectory() {
		return directory;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getRequires() {
		return requires;
	}
	
	public ResourceManager getResourceManager() {
		return resourceManager;
	}
	
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		
		string.append("Mod@");
		string.append(Integer.toString(System.identityHashCode(this)));
		string.append("\n");
		
		string.append("\tName: ").append(name).append("\n");
		string.append("\tDisplay name: ").append(displayName).append("\n");
		
		string.append("\tRequires:\n");
		for (String item : requires) {
			string.append("\t\t");
			string.append(item);
			string.append("\n");
		}
		
		return string.toString();
	}
	
	protected void gatherResources() {
		resourceManager.gatherResources(directory);
	}
	
	protected void readInfo() {
		Path propertiesPath = Paths.get(directory.toString(), MOD_PROPERTIES_FILE);
		
		if (Files.exists(propertiesPath, LinkOption.NOFOLLOW_LINKS)) {
			Configuration info = new Configuration(propertiesPath);
			
			name = sanitizeName(info.getString("name", directory.getName(directory.getNameCount() - 1).toString()));
			displayName = info.getString("displayName", name);
			requires = Collections.unmodifiableList(info.getList("requires", " "));
		} else {
			name = sanitizeName(directory.getName(directory.getNameCount() - 1).toString());
			displayName = name;
			requires = Collections.emptyList();
		}
		
		gatherResources();
	}
	
	protected String sanitizeName(String name) {
		return name.replaceAll(NAME_SANITIZATION_EXPRESSION, NAME_SANITIZATION_REPLACEMENT);
	}
}
