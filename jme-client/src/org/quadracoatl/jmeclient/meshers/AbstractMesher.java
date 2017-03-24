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

package org.quadracoatl.jmeclient.meshers;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.block.BlockType;
import org.quadracoatl.framework.block.Side;
import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.realm.Realm;

import com.jme3.scene.Mesh;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public abstract class AbstractMesher implements Mesher {
	protected int chunkDepth = 0;
	protected int chunkHeight = 0;
	protected int chunkWidth = 0;
	protected final TIntObjectMap<Mesh> EMPTY_MESH_MAP = new TIntObjectHashMap<Mesh>();
	protected Realm realm = null;
	protected Set<Integer> supportedBlockIds = new HashSet<>();
	
	protected AbstractMesher(Realm realm, BlockType supportedBlockType) {
		super();
		
		this.realm = realm;
		
		chunkWidth = realm.getChunkWidth();
		chunkHeight = realm.getChunkHeight();
		chunkDepth = realm.getChunkDepth();
		
		for (Block block : realm.getCosmos().getBlocks()) {
			if (block.getBlockType() == supportedBlockType) {
				supportedBlockIds.add(Integer.valueOf(block.getId()));
			}
		}
	}
	
	public EnumSet<Side> getSides(Chunk chunk, int x, int y, int z) {
		if (chunk.get(x, y, z) <= 0) {
			return Side.NONE;
		}
		
		List<Block> blocks = chunk.getRealm().getCosmos().getBlocks();
		int currentBlockId = chunk.get(x, y, z);
		
		return Side.from(
				getSide(blocks, currentBlockId, chunk.get(x, y, z + 1), Side.FRONT),
				getSide(blocks, currentBlockId, chunk.get(x, y, z - 1), Side.BACK),
				getSide(blocks, currentBlockId, chunk.get(x - 1, y, z), Side.LEFT),
				getSide(blocks, currentBlockId, chunk.get(x + 1, y, z), Side.RIGHT),
				getSide(blocks, currentBlockId, chunk.get(x, y + 1, z), Side.TOP),
				getSide(blocks, currentBlockId, chunk.get(x, y - 1, z), Side.BOTTOM));
	}
	
	protected boolean containsSupportedBlocks(Chunk chunk) {
		for (Integer supportedBlockId : supportedBlockIds) {
			if (chunk.getBlockIds().contains(supportedBlockId)) {
				return true;
			}
		}
		
		return false;
	}
	
	protected TIntObjectMap<Mesh> convertToMeshes(TIntObjectMap<MeshBuilder> builders) {
		TIntObjectMap<Mesh> meshes = new TIntObjectHashMap<>(Math.max(1, builders.size()));
		
		for (int key : builders.keys()) {
			meshes.put(key, builders.get(key).createMesh());
		}
		
		return meshes;
	}
	
	protected MeshBuilder createMeshBuilder(Block block) {
		return new MeshBuilder(block.getTexture());
	}
	
	protected MeshBuilder getBuilder(TIntObjectMap<MeshBuilder> builders, Block block) {
		MeshBuilder builder = builders.get(block.getId());
		
		if (builder == null) {
			builder = createMeshBuilder(block);
			builders.put(block.getId(), builder);
		}
		
		return builder;
	}
	
	protected Side getSide(List<Block> blocks, int currentBlockId, int blockId, Side side) {
		if (blockId == Block.NONEXISTENT_BLOCK.getId()
				|| blockId == Block.NULL_BLOCK.getId()) {
			return side;
		}
		
		if (blockId != currentBlockId) {
			Block block = blocks.get(blockId);
			
			if (block.getBlockType() == BlockType.NONE) {
				return side;
			}
			
		}
		
		return null;
	}
}
