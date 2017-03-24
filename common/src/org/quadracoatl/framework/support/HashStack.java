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

package org.quadracoatl.framework.support;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HashStack<TYPE> {
	private List<TYPE> orderedObjects = new ArrayList<>();
	private Set<TYPE> uniqueObjects = new HashSet<>();
	
	public HashStack() {
		super();
	}
	
	public boolean isEmpty() {
		return orderedObjects.isEmpty();
	}
	
	public synchronized TYPE pop() {
		if (!orderedObjects.isEmpty()) {
			TYPE poppedObject = orderedObjects.remove(0);
			uniqueObjects.remove(poppedObject);
			
			return poppedObject;
		}
		
		return null;
	}
	
	public synchronized void push(TYPE object) {
		if (!uniqueObjects.contains(object)) {
			uniqueObjects.add(object);
			orderedObjects.add(object);
		}
	}
}
