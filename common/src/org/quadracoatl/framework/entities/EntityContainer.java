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

package org.quadracoatl.framework.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.quadracoatl.framework.entities.changes.EntityChangeTracker;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class EntityContainer {
	protected transient EntityChangeTracker changes = new EntityChangeTracker();
	protected TIntObjectMap<Entity> entities = new TIntObjectHashMap<>();
	private transient Collection<Entity> readonlyEntities = null;
	
	public EntityContainer() {
		super();
	}
	
	public EntityChangeTracker getChanges() {
		return changes;
	}
	
	public <COMPONENT extends Component> List<COMPONENT> getComponents(Class<COMPONENT> componentClass) {
		List<COMPONENT> foundComponents = new ArrayList<>();
		
		for (Entity entity : entities.valueCollection()) {
			if (entity.hasComponent(componentClass)) {
				foundComponents.add(entity.getComponent(componentClass));
			}
		}
		
		return foundComponents;
	}
	
	public Collection<Entity> getEntities() {
		if (readonlyEntities == null) {
			readonlyEntities = Collections.unmodifiableCollection(entities.valueCollection());
		}
		
		return readonlyEntities;
	}
	
	public <COMPONENT extends Component> List<Entity> getEntities(Class<COMPONENT> componentClass) {
		List<Entity> foundEntities = new ArrayList<>();
		
		for (Entity entity : entities.valueCollection()) {
			if (entity.hasComponent(componentClass)) {
				foundEntities.add(entity);
			}
		}
		
		return foundEntities;
	}
	
	public Entity getEntity(int id) {
		return entities.get(id);
	}
	
	public boolean hasEntity(int id) {
		return entities.containsKey(id);
	}
	
	public Entity register(Entity entity) {
		if (entity == null) {
			return null;
		}
		
		entities.put(entity.getId(), entity);
		entity.attach(this);
		changes.add(entity);
		
		return entity;
	}
	
	public Entity register(int id, Iterable<? extends Component> components) {
		Entity entity = register(new Entity(id));
		
		if (components != null) {
			for (Object component : components) {
				entity.addComponent((Component)component);
			}
		}
		
		return entity;
	}
	
	public void register(Iterable<Entity> entities) {
		if (entities == null) {
			return;
		}
		
		for (Entity entity : entities) {
			register(entity);
		}
	}
	
	public synchronized void unregister(Entity entity) {
		unregister(entity.getId());
	}
	
	public synchronized void unregister(int id) {
		Entity entity = entities.remove(id);
		entity.detach();
		changes.remove(entity);
	}
	
	public synchronized void unregisterAll() {
		for (Entity entity : entities.valueCollection()) {
			entity.detach();
			changes.remove(entity);
		}
		
		entities.clear();
	}
}
