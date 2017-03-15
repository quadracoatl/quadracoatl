/*
 * Copyright 2017, Robert 'Bobby' Zenz
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

package org.quadracoatl.framework.entities.changes;

import java.util.ArrayList;
import java.util.List;

import org.quadracoatl.framework.entities.Component;
import org.quadracoatl.framework.entities.Entity;

public class EntityChangeTracker {
	protected List<EntityChange> changes = new ArrayList<>();
	private List<ComponentChange> componentChanges = new ArrayList<>();
	private int lastEntityId = 0;
	
	public EntityChangeTracker() {
		super();
	}
	
	public void add(Entity entity) {
		synchronized (changes) {
			flushChangesIfNeeded();
			
			for (Component component : entity.getComponents()) {
				componentChanges.add(new ComponentChange(ChangeType.ADDED, component));
			}
			
			changes.add(new EntityChange(ChangeType.ADDED, entity.getId(), componentChanges));
		}
	}
	
	public EntityChangeBatch bakeIntoBatch() {
		if (hasChanges()) {
			synchronized (changes) {
				flushChangesIfNeeded();
				
				EntityChangeBatch batch = new EntityChangeBatch(changes);
				
				changes.clear();
				
				return batch;
			}
		} else {
			return EntityChangeBatch.EMPTY_BATCH;
		}
	}
	
	public void change(ChangeType changeType, Component component) {
		synchronized (changes) {
			int parentEntityId = component.getEntity().getId();
			
			flushChangesIfNeeded(parentEntityId);
			
			lastEntityId = parentEntityId;
			componentChanges.add(new ComponentChange(changeType, component));
		}
	}
	
	public void clear() {
		synchronized (changes) {
			changes.clear();
		}
	}
	
	public boolean hasChanges() {
		return !changes.isEmpty() || !componentChanges.isEmpty();
	}
	
	public void remove(Entity entity) {
		synchronized (changes) {
			flushChangesIfNeeded();
			
			changes.add(new EntityChange(ChangeType.REMOVED, entity.getId()));
		}
	}
	
	private void flushChangesIfNeeded() {
		flushChangesIfNeeded(0);
	}
	
	private void flushChangesIfNeeded(int nextEntityId) {
		if (nextEntityId != lastEntityId
				&& lastEntityId > 0
				&& !componentChanges.isEmpty()) {
			changes.add(new EntityChange(ChangeType.CHANGED, lastEntityId, componentChanges));
			
			lastEntityId = 0;
			componentChanges.clear();
		}
	}
}
