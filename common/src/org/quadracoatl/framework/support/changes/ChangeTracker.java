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

package org.quadracoatl.framework.support.changes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ChangeTracker<TYPE> implements Iterable<Change<TYPE>> {
	protected List<Change<TYPE>> changes = new ArrayList<>();
	private List<Change<TYPE>> readonlyChanges = null;
	
	public ChangeTracker() {
		super();
	}
	
	public void add(TYPE item) {
		synchronized (changes) {
			changes.add(new Change<TYPE>(ChangeType.ADDED, item));
		}
	}
	
	public ChangeBatch<TYPE> bakeIntoBatch() {
		if (hasChanges()) {
			synchronized (changes) {
				ChangeBatch<TYPE> batch = new ChangeBatch<>(changes);
				
				changes.clear();
				
				return batch;
			}
		} else {
			return ChangeBatch.emptyBatch();
		}
	}
	
	public void change(TYPE item) {
		synchronized (changes) {
			changes.add(new Change<TYPE>(ChangeType.CHANGED, item));
		}
	}
	
	public void clear() {
		synchronized (changes) {
			changes.clear();
		}
	}
	
	public List<Change<TYPE>> getChanges() {
		if (readonlyChanges == null) {
			readonlyChanges = Collections.unmodifiableList(changes);
		}
		
		return readonlyChanges;
	}
	
	public boolean hasChanges() {
		return !changes.isEmpty();
	}
	
	@Override
	public Iterator<Change<TYPE>> iterator() {
		return getChanges().iterator();
	}
	
	public void remove(TYPE item) {
		synchronized (changes) {
			changes.add(new Change<TYPE>(ChangeType.REMOVED, item));
		}
	}
}
