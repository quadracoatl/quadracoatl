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

import java.nio.file.Path;
import java.nio.file.Paths;

import org.quadracoatl.framework.mod.ModManager;
import org.quadracoatl.framework.support.Configuration;

public class Game {
	protected static final String GAME_PROPERTIES_FILE = "game.properties";
	protected static final String MODS_DIRECTORY = "mods";
	protected Path directory = null;
	protected String displayName = null;
	protected ModManager modManager = null;
	protected String name = null;
	
	public Game(Path directory) {
		super();
		
		this.directory = directory;
		
		readInfo();
		
		modManager = new ModManager();
		modManager.gatherMods(Paths.get(directory.toString(), MODS_DIRECTORY));
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
	
	protected void readInfo() {
		Configuration info = new Configuration(Paths.get(directory.toString(), GAME_PROPERTIES_FILE));
		
		name = info.getString("name");
		displayName = info.getString("displayName", name);
	}
}
