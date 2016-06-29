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

import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.realm.Realm;

import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public abstract class AbstractMesher implements Mesher {
	protected int chunkDepth = 0;
	protected int chunkHeight = 0;
	protected int chunkWidth = 0;
	protected Realm realm = null;
	private float blockDepth = 0;
	private float blockHeight = 0;
	private float blockWidth = 0;
	
	protected AbstractMesher(Realm realm) {
		super();
		
		this.realm = realm;
		
		chunkWidth = realm.getChunkWidth();
		chunkHeight = realm.getChunkHeight();
		chunkDepth = realm.getChunkDepth();
		blockWidth = (float)realm.getBlockWidth();
		blockHeight = (float)realm.getBlockHeight();
		blockDepth = (float)realm.getBlockDepth();
	}
	
	@Override
	public Node createGeometry(Chunk chunk) {
		Node node = new Node(chunk.toString());
		
		TIntObjectMap<Mesh> meshes = buildMesh(chunk);
		
		for (int key : meshes.keys()) {
			Block block = realm.getCosmos().getBlocks().get(key);
			
			Geometry geometry = new Geometry(chunk.toString() + ":" + block.getName(), meshes.get(key));
			geometry.setMaterial((Material)block.getAdditionalState().get("material"));
			
			node.attachChild(geometry);
		}
		
		node.setLocalScale(
				blockWidth,
				blockHeight,
				blockDepth);
		node.setLocalTranslation(
				chunk.getIndexX() * chunkWidth * blockWidth,
				chunk.getIndexY() * chunkHeight * blockHeight,
				chunk.getIndexZ() * chunkDepth * blockDepth);
		node.updateGeometricState();
		node.updateModelBound();
		
		return node;
	}
	
	public EnumSet<Sides> getSides(Chunk chunk, int x, int y, int z) {
		if (chunk.get(x, y, z) <= 0) {
			return Sides.NONE;
		}
		
		return EnumSet.of(
				chunk.get(x, y, z + 1) > 0 ? Sides.NO_SIDE : Sides.FRONT,
				chunk.get(x, y, z - 1) > 0 ? Sides.NO_SIDE : Sides.BACK,
				chunk.get(x - 1, y, z) > 0 ? Sides.NO_SIDE : Sides.LEFT,
				chunk.get(x + 1, y, z) > 0 ? Sides.NO_SIDE : Sides.RIGHT,
				chunk.get(x, y + 1, z) > 0 ? Sides.NO_SIDE : Sides.TOP,
				chunk.get(x, y - 1, z) > 0 ? Sides.NO_SIDE : Sides.BOTTOM);
	}
	
	protected TIntObjectMap<Mesh> convertToMeshes(TIntObjectMap<MeshBuilder> builders) {
		TIntObjectMap<Mesh> meshes = new TIntObjectHashMap<>(Math.max(1, builders.size()));
		
		for (int key : builders.keys()) {
			meshes.put(key, builders.get(key).createMesh());
		}
		
		return meshes;
	}
	
	protected MeshBuilder getBuilder(TIntObjectMap<MeshBuilder> builders, int id) {
		MeshBuilder builder = builders.get(id);
		
		if (builder == null) {
			builder = new MeshBuilder();
			builders.put(id, builder);
		}
		
		return builder;
	}
}
