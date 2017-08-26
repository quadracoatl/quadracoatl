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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.quadracoatl.framework.common.Client;
import org.quadracoatl.framework.cosmos.Cosmos;
import org.quadracoatl.framework.entities.changes.EntityChangeBatch;
import org.quadracoatl.framework.game.Game;
import org.quadracoatl.framework.mod.Mod;
import org.quadracoatl.framework.realm.Realm;
import org.quadracoatl.interlayer.parts.CosmosPart;
import org.quadracoatl.interlayer.parts.EntityEventReceiver;
import org.quadracoatl.interlayer.parts.direct.DirectCosmosPart;
import org.quadracoatl.scripting.ScriptEnvironment;
import org.quadracoatl.scripting.ScriptingFeature;
import org.quadracoatl.scripting.lua.LuaEnvironment;

public class ServerEnvironment extends AbstractThreadedUpdatable {
	private Set<Client> clients = new HashSet<Client>();
	private Cosmos cosmos = null;
	private Game game = null;
	private ScriptEnvironment scriptEnvironment = null;
	
	public ServerEnvironment(Game game) {
		super("server", 16);
		
		this.game = game;
		
		cosmos = new Cosmos();
	}
	
	public Cosmos getCosmos() {
		return cosmos;
	}
	
	public Game getGame() {
		return game;
	}
	
	public void invokeNextUpdate(Client client, Consumer<Client> toRun) {
		invokeNextUpdate(new ClientRunnable(client, toRun));
	}
	
	public void registerClient(Client client) {
		// TODO Hardcoded realm name here.
		client.setCurrentRealmName("default");
		
		client.getInterlayerClient().putPart(CosmosPart.ID, new DirectCosmosPart(this, client));
		
		synchronized (clients) {
			clients.add(client);
		}
		
		invokeNextUpdate(client, this::pushAllEntities);
	}
	
	public void unregisterClient(Client client) {
		synchronized (clients) {
			clients.remove(client);
		}
	}
	
	@Override
	protected void init() throws Throwable {
		super.init();
		
		logger.info("Game: ", game.getDisplayName());
		logger.info("Directory: ", game.getDirectory());
		
		scriptEnvironment = new LuaEnvironment();
		scriptEnvironment.setCosmos(cosmos);
		scriptEnvironment.setScheduler(scheduler);
		
		scriptEnvironment.enableFeatures(ScriptingFeature.ALL);
		
		for (Mod mod : game.getModManager().getModsInLoadOrder()) {
			logger.info("Loading mod: " + mod.getName());
			
			scriptEnvironment.load(mod);
		}
	}
	
	@Override
	protected void update(long elapsedNanoSecondsSinceLastUpdate) {
		super.update(elapsedNanoSecondsSinceLastUpdate);
		
		cosmos.update(elapsedNanoSecondsSinceLastUpdate);
		
		Map<String, EntityChangeBatch> changedEntitiesByRealm = new HashMap<>();
		
		for (Realm realm : cosmos.getRealms().values()) {
			changedEntitiesByRealm.put(realm.getName(), realm.getEntityManager().getChanges().bakeIntoBatch());
		}
		
		synchronized (clients) {
			for (Client client : clients) {
				EntityEventReceiver entityEventReceiver = client.getInterlayerClient().getPart(EntityEventReceiver.ID, EntityEventReceiver.class);
				
				if (entityEventReceiver != null) {
					entityEventReceiver.receiveChanges(changedEntitiesByRealm.get(client.getCurrentRealmName()));
				}
			}
		}
	}
	
	private void pushAllEntities(Client client) {
		EntityEventReceiver entityEventReceiver = client.getInterlayerClient().getPart(EntityEventReceiver.ID, EntityEventReceiver.class);
		
		if (entityEventReceiver != null) {
			entityEventReceiver.receiveInitialState(cosmos.getRealm(client.getCurrentRealmName()).getEntityManager().getEntities());
		}
	}
	
	private static final class ClientRunnable implements Runnable {
		private Client client = null;
		private Consumer<Client> toRun = null;
		
		public ClientRunnable(Client client, Consumer<Client> toRun) {
			super();
			this.client = client;
			this.toRun = toRun;
		}
		
		@Override
		public void run() {
			toRun.accept(client);
		}
	}
}
