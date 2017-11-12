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

package org.quadracoatl.jmeclient.spatials;

import java.util.ArrayList;
import java.util.List;

import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.realm.Realm;
import org.quadracoatl.jmeclient.extensions.JmeResourceManager;
import org.quadracoatl.jmeclient.meshers.Mesher;

import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class ChunkSpatial extends Node {
	private Chunk chunk = null;
	private List<Geometry> geometries = new ArrayList<>();
	
	public ChunkSpatial(Chunk chunk) {
		super();
		
		this.chunk = chunk;
		
		setName(chunk.toString());
		
		Realm realm = chunk.getRealm();
		
		int chunkWidth = realm.getChunkWidth();
		int chunkHeight = realm.getChunkHeight();
		int chunkDepth = realm.getChunkDepth();
		float blockWidth = (float)realm.getBlockWidth();
		float blockHeight = (float)realm.getBlockHeight();
		float blockDepth = (float)realm.getBlockDepth();
		
		setLocalScale(
				blockWidth,
				blockHeight,
				blockDepth);
		setLocalTranslation(
				chunk.getIndexX() * chunkWidth * blockWidth,
				chunk.getIndexY() * chunkHeight * blockHeight,
				chunk.getIndexZ() * chunkDepth * blockDepth);
	}
	
	public void createMesh(List<Mesher> meshers, JmeResourceManager resourceManager) {
		Realm realm = chunk.getRealm();
		
		TIntObjectMap<Mesh> meshes = new TIntObjectHashMap<>();
		
		for (Mesher mesher : meshers) {
			meshes.putAll(mesher.buildMeshes(chunk));
		}
		
		geometries.clear();
		
		for (int key : meshes.keys()) {
			Block block = realm.getCosmos().getBlocks().get(key);
			
			Geometry geometry = new Geometry(chunk.toString() + ":" + block.getName(), meshes.get(key));
			geometry.setMaterial(resourceManager.getMaterial(block));
			geometry.setQueueBucket(Bucket.Transparent);
			
			geometries.add(geometry);
		}
	}
	
	public Chunk getChunk() {
		return chunk;
	}
	
	public boolean updateMesh() {
		boolean updateRequired = false;
		
		if (!getChildren().isEmpty()) {
			detachAllChildren();
			updateRequired = true;
		}
		
		if (!geometries.isEmpty()) {
			for (Geometry geometry : geometries) {
				attachChild(geometry);
			}
			
			geometries.clear();
			
			updateRequired = true;
		}
		
		if (updateRequired) {
			updateGeometricState();
		}
		
		return updateRequired;
	}
}
