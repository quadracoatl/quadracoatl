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

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.pmw.tinylog.Logger;
import org.quadracoatl.utils.io.DirectoriesOnlyFilter;

public class GameManager {
	protected Map<String, Game> games = new HashMap<>();
	private Map<String, Game> gamesReadOnly = null;
	
	public GameManager() {
		super();
		
		gamesReadOnly = Collections.unmodifiableMap(games);
	}
	
	public void gatherGames(Path gamesDirectory) {
		try (DirectoryStream<Path> directories = Files.newDirectoryStream(gamesDirectory.toAbsolutePath(), DirectoriesOnlyFilter.INSTANCE)) {
			for (Path gameDirectory : directories) {
				Game game = new Game(gameDirectory);
				
				games.put(game.getName(), game);
			}
		} catch (IOException e) {
			Logger.error(e, "Failed to read games from '{}'.", gamesDirectory.toAbsolutePath());
		}
	}
	
	public Map<String, Game> getGames() {
		return gamesReadOnly;
	}
	
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		
		string.append(getClass().getSimpleName());
		string.append("@");
		string.append(Integer.toString(System.identityHashCode(this)));
		string.append("\n");
		
		string.append("\tGames: ");
		for (Game game : games.values()) {
			string.append(game.getName());
			string.append(" ");
		}
		string.append("\n");
		
		return string.toString();
	}
}
