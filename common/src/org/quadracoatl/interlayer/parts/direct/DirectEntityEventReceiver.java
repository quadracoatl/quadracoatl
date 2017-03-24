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

package org.quadracoatl.interlayer.parts.direct;

import org.quadracoatl.environments.ClientEnvironment;
import org.quadracoatl.framework.entities.Component;
import org.quadracoatl.framework.entities.Entity;
import org.quadracoatl.framework.entities.EntityManager;
import org.quadracoatl.framework.entities.changes.ComponentChange;
import org.quadracoatl.framework.entities.changes.EntityChange;
import org.quadracoatl.framework.entities.changes.EntityChangeBatch;
import org.quadracoatl.interlayer.parts.CosmosPart;
import org.quadracoatl.interlayer.parts.EntityEventReceiver;

public class DirectEntityEventReceiver implements EntityEventReceiver {
	private transient ClientEnvironment clientEnvironment = null;
	private transient CosmosPart cosmosPart = null;
	
	public DirectEntityEventReceiver(ClientEnvironment clientEnvironment) {
		this();
		
		this.clientEnvironment = clientEnvironment;
		
		cosmosPart = clientEnvironment.getInterlayer().getPart(CosmosPart.ID, CosmosPart.class);
	}
	
	private DirectEntityEventReceiver() {
		super();
	}
	
	@Override
	public void receiveChanges(EntityChangeBatch changeBatch) {
		if (changeBatch == null || changeBatch.isEmpty()) {
			return;
		}
		
		EntityManager entityManager = clientEnvironment.getCurrentRealm().getEntityManager();
		
		for (EntityChange entityChange : changeBatch) {
			Entity entity = null;
			
			switch (entityChange.getType()) {
				case ADDED:
					entity = entityManager.register(entityChange.getId(), null);
					
					for (ComponentChange componentChange : entityChange.getComponentChanges()) {
						entity.addComponent(componentChange.getComponent());
					}
					break;
				
				case CHANGED:
					entity = entityManager.getEntity(entityChange.getId());
					
					if (entity == null) {
						// Entity does not exist? How curious...
						entity = entityManager.register(cosmosPart.getEntity(
								clientEnvironment.getCurrentRealm().getName(),
								entityChange.getId()));
					}
					
					for (ComponentChange componentChange : entityChange.getComponentChanges()) {
						switch (componentChange.getType()) {
							case ADDED:
								entity.addComponent(componentChange.getComponent());
								break;
							
							case CHANGED:
								Component component = entity.getComponent(componentChange.getComponent().getClass());
								
								if (component != null) {
									component.updateWith(componentChange.getComponent());
								} else {
									entity.addComponent(componentChange.getComponent());
								}
								break;
							
							case REMOVED:
								entity.removeComponent(componentChange.getComponent());
								break;
						}
					}
					break;
				
				case REMOVED:
					entityManager.unregister(entityChange.getId());
					break;
			}
		}
	}
	
	@Override
	public void receiveInitialState(Iterable<Entity> entities) {
		if (entities == null) {
			return;
		}
		
		EntityManager entityManager = clientEnvironment.getCurrentRealm().getEntityManager();
		
		for (Entity entity : entities) {
			entityManager.register(entity);
		}
	}
}
