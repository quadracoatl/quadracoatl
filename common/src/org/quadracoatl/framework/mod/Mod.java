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

import org.quadracoatl.framework.support.Configuration;

public class Mod {
	protected static final String INIT_LUA_FILE = "init.lua";
	protected static final String MOD_PROPERTIES_FILE = "mod.properties";
	protected Path directory = null;
	protected String displayName = null;
	protected Path initFile = null;
	protected String name = null;
	protected List<String> requires = null;
	
	public Mod(Path directory) {
		super();
		
		this.directory = directory;
		
		initFile = Paths.get(directory.toString(), INIT_LUA_FILE);
		
		readInfo();
	}
	
	public Path getDirectory() {
		return directory;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public Path getInitFile() {
		return initFile;
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getRequires() {
		return requires;
	}
	
	public boolean hasInitFile() {
		return Files.exists(initFile, LinkOption.NOFOLLOW_LINKS);
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
	
	protected void readInfo() {
		Configuration info = new Configuration(Paths.get(directory.toString(), MOD_PROPERTIES_FILE));
		
		name = info.getString("name");
		displayName = info.getString("displayName", name);
		requires = Collections.unmodifiableList(info.getList("requires", " "));
	}
}
