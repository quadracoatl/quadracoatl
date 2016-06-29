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

package org.quadracoatl.environments;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.quadracoatl.framework.cosmos.Cosmos;
import org.quadracoatl.framework.game.Game;
import org.quadracoatl.framework.game.GameManager;
import org.quadracoatl.framework.mod.Mod;
import org.quadracoatl.framework.resources.ResourceManager;
import org.quadracoatl.scripting.lua.LuaEnvironment;
import org.quadracoatl.scripting.lua.libs.CosmosLib;
import org.quadracoatl.scripting.lua.libs.EngineLib;
import org.quadracoatl.scripting.lua.libs.SupportLib;

public class ServerEnvironment {
	private Path baseDirectory = null;
	private Cosmos cosmos = null;
	private Game game = null;
	private GameManager gameManager = null;
	private LuaEnvironment luaEnvironment = null;
	private ResourceManager resourceManager = null;
	
	public ServerEnvironment(Path baseDirectory) {
		super();
		
		this.baseDirectory = baseDirectory;
		
		gameManager = new GameManager();
		gameManager.gatherGames(Paths.get(baseDirectory.toString(), "games"));
		
		// TODO Hardcoded game here.
		game = gameManager.getGames().get("quadraxample");
		
		resourceManager = new ResourceManager();
		
		cosmos = new Cosmos();
		
		luaEnvironment = new LuaEnvironment();
		luaEnvironment.load(new JseBaseLib());
		luaEnvironment.load(new CoroutineLib());
		
		luaEnvironment.load(new EngineLib());
		luaEnvironment.load(new CosmosLib(cosmos));
		luaEnvironment.load(new SupportLib());
	}
	
	public Cosmos getCosmos() {
		return cosmos;
	}
	
	public ResourceManager getResourceManager() {
		return resourceManager;
	}
	
	public void start() {
		for (Mod mod : game.getModManager().getModsInLoadOrder()) {
			resourceManager.gatherResources(mod.getDirectory());
			
			if (mod.hasInitFile()) {
				luaEnvironment.limitLoadingTo(mod.getDirectory());
				luaEnvironment.load(mod.getInitFile());
			}
		}
		
		luaEnvironment.limitLoadingTo(null);
	}
}
