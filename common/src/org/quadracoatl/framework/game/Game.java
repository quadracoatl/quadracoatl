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

package org.quadracoatl.framework.game;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.quadracoatl.framework.mod.Mod;
import org.quadracoatl.framework.mod.ModManager;
import org.quadracoatl.framework.resources.ResourceManager;
import org.quadracoatl.framework.support.Configuration;

public class Game {
	protected static final String GAME_PROPERTIES_FILE = "game.properties";
	protected static final String MODS_DIRECTORY = "mods";
	protected Path directory = null;
	protected String displayName = null;
	protected ModManager modManager = new ModManager();
	protected String name = null;
	protected ResourceManager resourceManager = new ResourceManager();
	protected boolean valid = true;
	
	public Game(Path directory) {
		super();
		
		this.directory = directory;
		
		readInfo();
		
		modManager.gatherMods(Paths.get(directory.toString(), MODS_DIRECTORY));
		
		for (Mod mod : modManager.getMods().values()) {
			resourceManager.addResources(mod.getResourceManager());
		}
	}
	
	public Path getDirectory() {
		return directory;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public ModManager getModManager() {
		return modManager;
	}
	
	public String getName() {
		return name;
	}
	
	public ResourceManager getResourceManager() {
		return resourceManager;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	protected void readInfo() {
		Path configurationPath = Paths.get(directory.toString(), GAME_PROPERTIES_FILE);
		
		if (!Files.exists(configurationPath, LinkOption.NOFOLLOW_LINKS)) {
			name = directory.getName(directory.getNameCount() - 1).toString();
			displayName = name;
			valid = false;
			return;
		}
		
		Configuration info = new Configuration(configurationPath);
		
		name = info.getString("name", directory.getName(directory.getNameCount() - 1).toString());
		displayName = info.getString("displayName", name);
	}
}
