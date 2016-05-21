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

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pmw.tinylog.Logger;
import org.quadracoatl.utils.io.DirectoriesOnlyFilter;

public class ModManager {
	protected Map<String, Mod> mods = new HashMap<>();
	protected List<Mod> modsInLoadOrder = new ArrayList<>();
	protected List<Mod> modsInLoadOrderReadOnly = null;
	private Map<String, Mod> modsReadOnly = null;
	
	public ModManager() {
		super();
		
		modsReadOnly = Collections.unmodifiableMap(mods);
		modsInLoadOrderReadOnly = Collections.unmodifiableList(modsInLoadOrder);
	}
	
	public void gatherMods(Path modsDirectory) {
		try (DirectoryStream<Path> directories = Files.newDirectoryStream(modsDirectory.toAbsolutePath(), DirectoriesOnlyFilter.INSTANCE)) {
			for (Path modDirectory : directories) {
				Mod mod = new Mod(modDirectory);
				
				mods.put(mod.getName(), mod);
			}
		} catch (IOException e) {
			Logger.error(e, "Failed to read mods from '{}'.", modsDirectory.toAbsolutePath());
		}
		
		detectLoadOrder();
	}
	
	public Map<String, Mod> getMods() {
		return modsReadOnly;
	}
	
	public List<Mod> getModsInLoadOrder() {
		return modsInLoadOrderReadOnly;
	}
	
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		
		string.append(getClass().getSimpleName());
		string.append("@");
		string.append(Integer.toString(System.identityHashCode(this)));
		string.append("\n");
		
		string.append("\tMods: ");
		for (Mod mod : mods.values()) {
			string.append(mod.getName());
			string.append(" ");
		}
		string.append("\n");
		
		string.append("\tLoad order: ");
		for (Mod mod : modsInLoadOrder) {
			string.append(mod.getName());
			string.append(" ");
		}
		string.append("\n");
		
		return string.toString();
	}
	
	protected void detectLoadOrder() {
		modsInLoadOrder.clear();
		
		Set<String> loadedModsNames = new HashSet<>();
		List<Mod> modsToLoad = new ArrayList<>(mods.values());
		List<Mod> modsFoundThisRound = new ArrayList<>();
		
		do {
			for (Mod mod : modsFoundThisRound) {
				modsInLoadOrder.add(mod);
				loadedModsNames.add(mod.getName());
				modsToLoad.remove(mod);
			}
			
			modsFoundThisRound.clear();
			
			for (Mod mod : modsToLoad) {
				if (loadedModsNames.containsAll(mod.getRequires())) {
					modsFoundThisRound.add(mod);
				}
			}
		} while (!modsToLoad.isEmpty() && !modsFoundThisRound.isEmpty());
	}
}
