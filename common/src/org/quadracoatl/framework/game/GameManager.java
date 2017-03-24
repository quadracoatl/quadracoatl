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

import org.quadracoatl.framework.common.io.DirectoriesOnlyFilter;
import org.quadracoatl.framework.logging.Logger;
import org.quadracoatl.framework.logging.LoggerFactory;

public class GameManager {
	protected Map<String, Game> games = new HashMap<>();
	private final Logger LOGGER = LoggerFactory.getLogger(this);
	private Map<String, Game> readonlyGames = null;
	
	public GameManager() {
		super();
		
		readonlyGames = Collections.unmodifiableMap(games);
	}
	
	public void gatherGames(Path gamesDirectory) {
		try (DirectoryStream<Path> directories = Files.newDirectoryStream(gamesDirectory.toAbsolutePath(), DirectoriesOnlyFilter.INSTANCE)) {
			for (Path gameDirectory : directories) {
				Game game = new Game(gameDirectory);
				
				games.put(game.getName(), game);
			}
		} catch (IOException e) {
			LOGGER.error("Failed to read games from \"", gamesDirectory.toAbsolutePath(), "\".", e);
		}
	}
	
	public Map<String, Game> getGames() {
		return readonlyGames;
	}
	
	public void removeAllGames() {
		games.clear();
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
