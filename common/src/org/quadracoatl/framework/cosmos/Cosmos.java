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

package org.quadracoatl.framework.cosmos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.realm.Realm;

public class Cosmos {
	private transient List<Block> blocks = new ArrayList<>();
	private transient Map<String, Block> blocksByName = new HashMap<String, Block>();
	private transient List<Block> readonlyBlocks = null;
	private transient Map<String, Realm> readonlyRealms = null;
	private transient Map<String, Realm> realms = new HashMap<>();
	private long seed = 0l;
	
	public Cosmos() {
		super();
		
		blocks.add(Block.NULL_BLOCK);
	}
	
	public Block getBlock(int id) {
		if (id < 0 || id >= blocks.size()) {
			return null;
		}
		
		return blocks.get(id);
	}
	
	public Block getBlock(String name) {
		return blocksByName.get(name);
	}
	
	public List<Block> getBlocks() {
		if (readonlyBlocks == null) {
			readonlyBlocks = Collections.unmodifiableList(blocks);
		}
		
		return readonlyBlocks;
	}
	
	public Realm getRealm(String name) {
		return realms.get(name);
	}
	
	public Map<String, Realm> getRealms() {
		if (readonlyRealms == null) {
			readonlyRealms = Collections.unmodifiableMap(realms);
		}
		
		return readonlyRealms;
	}
	
	public long getSeed() {
		return seed;
	}
	
	public void registerBlock(Block block) {
		if (Block.isNullBlock(block)) {
			return;
		}
		
		blocks.add(block);
		blocksByName.put(block.getName(), block);
	}
	
	public void registerBlocks(Iterable<Block> blocks) {
		for (Block block : blocks) {
			registerBlock(block);
		}
	}
	
	public void registerRealm(Realm realm) {
		realm.setCosmos(this);
		
		realms.put(realm.getName(), realm);
	}
	
	public void registerRealms(Iterable<Realm> realms) {
		for (Realm realm : realms) {
			registerRealm(realm);
		}
	}
	
	public void setSeed(long seed) {
		this.seed = seed;
	}
	
	public void update(long nanoSecondsSinceLastUpdate) {
		for (Realm realm : realms.values()) {
			realm.update(nanoSecondsSinceLastUpdate);
		}
	}
}
