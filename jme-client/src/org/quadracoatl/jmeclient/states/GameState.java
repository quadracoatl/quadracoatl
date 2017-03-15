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

package org.quadracoatl.jmeclient.states;

import java.util.HashSet;
import java.util.Set;

import org.quadracoatl.environments.ClientEnvironment;
import org.quadracoatl.framework.entities.changes.EntityChangeBatch;
import org.quadracoatl.framework.entities.changes.EntityChangeProcessor;
import org.quadracoatl.jmeclient.extensions.JmeResourceManager;
import org.quadracoatl.jmeclient.states.sub.CelestialState;
import org.quadracoatl.jmeclient.states.sub.ChunkState;
import org.quadracoatl.jmeclient.states.sub.GameInputState;
import org.quadracoatl.jmeclient.states.sub.LightingState;

import com.jme3.app.state.AppState;

public class GameState extends AbstractManagingAppState {
	private ClientEnvironment clientEnvironment = null;
	private Set<EntityChangeProcessor> entityChangeProcessors = new HashSet<>();
	
	public GameState(ClientEnvironment clientEnvironment) {
		super();
		
		this.clientEnvironment = clientEnvironment;
		
		addAppState(new CelestialState(clientEnvironment));
		addAppState(new ChunkState(clientEnvironment));
		addAppState(new GameInputState(
				(JmeResourceManager)clientEnvironment.getResourceCache(),
				clientEnvironment.getCosmos()));
		addAppState(new LightingState(clientEnvironment));
	}
	
	@Override
	public void addAppState(AppState state) {
		super.addAppState(state);
		
		if (state instanceof EntityChangeProcessor) {
			entityChangeProcessors.add((EntityChangeProcessor)state);
		}
	}
	
	@Override
	public void removeAppState(AppState state) {
		if (state instanceof EntityChangeProcessor) {
			entityChangeProcessors.remove(state);
		}
		
		super.removeAppState(state);
	}
	
	@Override
	public void update(float tpf) {
		super.update(tpf);
		
		EntityChangeBatch entityChangeBatch = clientEnvironment.getCurrentRealm().getEntityManager().getChanges().bakeIntoBatch();
		
		for (EntityChangeProcessor entityChangeProcessor : entityChangeProcessors) {
			entityChangeProcessor.process(entityChangeBatch);
		}
	}
}
