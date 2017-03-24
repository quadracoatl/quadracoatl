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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class ChangeBatch<TYPE> implements Iterable<Change<TYPE>> {
	public static final ChangeBatch<?> EMPTY_BATCH = new ChangeBatch<>(Collections.emptyList());
	
	private Collection<Change<TYPE>> changes = null;
	
	public ChangeBatch(ChangeBatch<TYPE> changeBatch, Collection<Change<TYPE>> changes) {
		this();
		
		this.changes = new ArrayList<>();
		changes.addAll(changeBatch.getChanges());
		changes.addAll(changes);
		
		// We copy the collection to make sure that it is not modified anymore.
		this.changes = Collections.unmodifiableCollection(this.changes);
	}
	
	public ChangeBatch(Collection<Change<TYPE>> changes) {
		this();
		
		// We copy the collection to make sure that it is not modified anymore.
		this.changes = Collections.unmodifiableCollection(new ArrayList<>(changes));
	}
	
	private ChangeBatch() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public static final <TYPE> ChangeBatch<TYPE> emptyBatch() {
		return (ChangeBatch<TYPE>)EMPTY_BATCH;
	}
	
	public Collection<Change<TYPE>> getChanges() {
		return changes;
	}
	
	@Override
	public Iterator<Change<TYPE>> iterator() {
		return changes.iterator();
	}
}
