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

package org.quadracoatl.framework.resources;

import org.quadracoatl.interlayer.Transferrable;

public @Transferrable class Resource {
	private @Transferrable String hash = null;
	private @Transferrable String key = null;
	private @Transferrable ResourceType type = null;
	
	public Resource(ResourceType type, String key, String hash) {
		this();
		
		this.type = type;
		this.key = key;
		this.hash = hash;
	}
	
	private Resource() {
		super();
	}
	
	public String getHash() {
		return hash;
	}
	
	public String getKey() {
		return key;
	}
	
	public ResourceType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()
				+ "@"
				+ Integer.toString(System.identityHashCode(this))
				+ ": "
				+ type.toString()
				+ " "
				+ key
				+ " "
				+ hash;
	}
}
