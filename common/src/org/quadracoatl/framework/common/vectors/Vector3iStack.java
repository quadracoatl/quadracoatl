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

package org.quadracoatl.framework.common.vectors;

import java.util.Iterator;
import java.util.function.Predicate;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

public class Vector3iStack implements Iterable<Vector3i> {
	private TLongList list = new TLongArrayList();;
	private TLongSet set = new TLongHashSet();
	private ThreadLocal<Vector3i> threadItemCache = new ThreadLocal<>();
	private ThreadLocal<Vector3iStackIterator> threadIteratorCache = new ThreadLocal<>();
	
	public Vector3iStack() {
		super();
	}
	
	public boolean contains(int x, int y, int z) {
		long packed = VectorUtil.pack(x, y, z);
		
		synchronized (this) {
			return set.contains(packed);
		}
	}
	
	public boolean contains(Vector3i vector) {
		return contains(vector.x, vector.y, vector.z);
	}
	
	public Vector3i get(int index) {
		if (list.isEmpty()) {
			return null;
		}
		
		long value = 0;
		
		synchronized (list) {
			value = list.get(index);
		}
		
		return unpack(value);
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	@Override
	public Iterator<Vector3i> iterator() {
		Vector3iStackIterator iterator = threadIteratorCache.get();
		
		if (iterator == null) {
			iterator = new Vector3iStackIterator(this);
			threadIteratorCache.set(iterator);
		}
		
		iterator.reset();
		
		return iterator;
	}
	
	public Vector3i pop() {
		if (list.isEmpty()) {
			return null;
		}
		
		synchronized (this) {
			return popLast();
		}
		
	}
	
	public Vector3i pop(Predicate<Vector3i> primaryPredicate, Predicate<Vector3i> secondaryPredicate) {
		if (list.isEmpty()) {
			return null;
		}
		
		synchronized (this) {
			Vector3i value = pop(primaryPredicate);
			
			if (value == null) {
				value = pop(secondaryPredicate);
			}
			
			if (value == null) {
				value = pop();
			}
			
			return value;
		}
	}
	
	public boolean push(int x, int y, int z) {
		long packed = VectorUtil.pack(x, y, z);
		
		synchronized (this) {
			if (!set.contains(packed)) {
				set.add(packed);
				list.add(packed);
				
				return false;
			} else {
				return true;
			}
		}
	}
	
	public boolean push(Vector3i vector) {
		if (vector == null) {
			throw new IllegalArgumentException("vector");
		}
		
		return push(vector.x, vector.y, vector.z);
	}
	
	public int size() {
		return list.size();
	}
	
	protected Vector3i unpack(long value) {
		Vector3i cached = threadItemCache.get();
		
		if (cached == null) {
			cached = new Vector3i();
			threadItemCache.set(cached);
		}
		
		VectorUtil.unpackInto(value, cached);
		
		return cached;
	}
	
	private Vector3i pop(Predicate<Vector3i> predicate) {
		synchronized (this) {
			for (int index = list.size() - 1; index >= 0; index--) {
				Vector3i vector = unpack(list.get(index));
				
				if (predicate.test(vector)) {
					set.remove(list.removeAt(index));
					return vector;
				}
			}
		}
		
		return null;
	}
	
	private Vector3i popLast() {
		long value = 0l;
		
		synchronized (this) {
			value = list.removeAt(list.size() - 1);
			set.remove(value);
		}
		
		return unpack(value);
	}
	
	private static final class Vector3iStackIterator implements Iterator<Vector3i> {
		private int currentIndex = 0;
		private Vector3iStack stack = null;
		
		public Vector3iStackIterator(Vector3iStack stack) {
			super();
			
			this.stack = stack;
		}
		
		@Override
		public boolean hasNext() {
			return currentIndex < stack.size();
		}
		
		@Override
		public Vector3i next() {
			return stack.get(currentIndex++);
		}
		
		public void reset() {
			currentIndex = 0;
		}
	}
}
