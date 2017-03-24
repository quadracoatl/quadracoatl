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
import java.util.List;

public class EntityChange {
	private List<? extends ComponentChange> componentChanges = Collections.emptyList();
	private int id = 0;
	private ChangeType type = null;
	
	public EntityChange(ChangeType type, int id) {
		super();
		
		this.type = type;
		this.id = id;
	}
	
	public EntityChange(ChangeType type, int id, Collection<? extends ComponentChange> componentChanges) {
		this();
		
		this.type = type;
		this.id = id;
		
		if (componentChanges != null && !componentChanges.isEmpty()) {
			this.componentChanges = Collections.unmodifiableList(new ArrayList<>(componentChanges));
		}
	}
	
	private EntityChange() {
		super();
	}
	
	public List<? extends ComponentChange> getComponentChanges() {
		return componentChanges;
	}
	
	public int getId() {
		return id;
	}
	
	public ChangeType getType() {
		return type;
	}
}
