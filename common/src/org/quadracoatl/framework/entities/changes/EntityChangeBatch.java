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

package org.quadracoatl.framework.entities.changes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class EntityChangeBatch implements Iterable<EntityChange> {
	public static final EntityChangeBatch EMPTY_BATCH = new EntityChangeBatch(Collections.emptyList());
	
	private Collection<EntityChange> changes = null;
	
	public EntityChangeBatch(Collection<EntityChange> changes) {
		this();
		
		// We copy the collection to make sure that it is not modified anymore.
		this.changes = Collections.unmodifiableCollection(new ArrayList<>(changes));
	}
	
	public EntityChangeBatch(EntityChangeBatch changeBatch, Collection<EntityChange> changes) {
		this();
		
		this.changes = new ArrayList<>();
		changes.addAll(changeBatch.getChanges());
		changes.addAll(changes);
		
		// We copy the collection to make sure that it is not modified anymore.
		this.changes = Collections.unmodifiableCollection(this.changes);
	}
	
	private EntityChangeBatch() {
		super();
	}
	
	public Collection<EntityChange> getChanges() {
		return changes;
	}
	
	public boolean isEmpty() {
		return changes.isEmpty();
	}
	
	@Override
	public Iterator<EntityChange> iterator() {
		return changes.iterator();
	}
}
