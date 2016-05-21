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

package org.quadracoatl.meshers;

import java.util.EnumSet;
import java.util.List;

import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.utils.IntMap;
import org.quadracoatl.utils.IntMap.Entry;
import org.quadracoatl.utils.Vector3i;

import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;

public abstract class AbstractMesher implements Mesher {
	protected int blockDepth = 0;
	protected int blockHeight = 0;
	protected int blockWidth = 0;
	protected int chunkDepth = 0;
	protected int chunkHeight = 0;
	protected int chunkWidth = 0;
	
	protected AbstractMesher(int chunkWidth, int chunkHeight, int chunkDepth, int blockWidth, int blockHeight,
			int blockDepth) {
		super();
		
		this.chunkWidth = chunkWidth;
		this.chunkHeight = chunkHeight;
		this.chunkDepth = chunkDepth;
		this.blockWidth = blockWidth;
		this.blockHeight = blockHeight;
		this.blockDepth = blockDepth;
	}
	
	@Override
	public Node createGeometry(Chunk chunk, List<Block> blocks) {
		Node node = new Node(chunk.toString());
		
		for (Entry<Mesh> entry : buildMesh(chunk)) {
			Block block = blocks.get(entry.getKey());
			
			Vector3i chunkIndex = chunk.getIndex();
			
			Geometry geometry = new Geometry(chunk.toString() + ":" + block.getName(), entry.getValue());
			geometry.setMaterial((Material)block.getAdditionalState().get("material"));
			geometry.setLocalTranslation(
					chunkIndex.getX() * chunkWidth,
					chunkIndex.getY() * chunkHeight,
					chunkIndex.getZ() * chunkDepth);
			geometry.updateModelBound();
			
			node.attachChild(geometry);
		}
		
		return node;
	}
	
	public EnumSet<Sides> getSides(Chunk chunk, int x, int y, int z) {
		if (chunk.getBlock(x, y, z) <= 0) {
			return Sides.NONE;
		}
		
		return EnumSet.of(
				chunk.getBlock(x, y, z + 1) > 0 ? Sides.NO_SIDE : Sides.FRONT,
				chunk.getBlock(x, y, z - 1) > 0 ? Sides.NO_SIDE : Sides.BACK,
				chunk.getBlock(x - 1, y, z) > 0 ? Sides.NO_SIDE : Sides.LEFT,
				chunk.getBlock(x + 1, y, z) > 0 ? Sides.NO_SIDE : Sides.RIGHT,
				chunk.getBlock(x, y + 1, z) > 0 ? Sides.NO_SIDE : Sides.TOP,
				chunk.getBlock(x, y - 1, z) > 0 ? Sides.NO_SIDE : Sides.BOTTOM);
	}
	
	protected IntMap<Mesh> convertToMeshes(IntMap<MeshBuilder> builders) {
		IntMap<Mesh> meshes = new IntMap<>(Math.max(1, builders.size()));
		
		for (Entry<MeshBuilder> entry : builders) {
			meshes.put(entry.getKey(), entry.getValue().createMesh());
		}
		
		return meshes;
	}
	
	protected MeshBuilder getBuilder(IntMap<MeshBuilder> builders, int id) {
		MeshBuilder builder = builders.get(id);
		
		if (builder == null) {
			builder = new MeshBuilder();
			builders.put(id, builder);
		}
		
		return builder;
	}
}
