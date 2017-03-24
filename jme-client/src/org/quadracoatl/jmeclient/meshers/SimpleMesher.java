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

import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.block.BlockType;
import org.quadracoatl.framework.block.Side;
import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.realm.Realm;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class SimpleMesher extends AbstractMesher {
	public SimpleMesher(Realm realm, BlockType supportedBlockType) {
		super(realm, supportedBlockType);
	}
	
	@Override
	public TIntObjectMap<Mesh> buildMeshes(Chunk chunk) {
		TIntObjectMap<MeshBuilder> builders = new TIntObjectHashMap<>();
		
		for (int x = 0; x < chunkWidth; x++) {
			for (int y = 0; y < chunkHeight; y++) {
				for (int z = 0; z < chunkDepth; z++) {
					int currentId = chunk.get(x, y, z);
					
					if (currentId > 0) {
						Block block = realm.getCosmos().getBlock(currentId);
						MeshBuilder builder = getBuilder(builders, block);
						EnumSet<Side> sides = getSides(chunk, x, y, z);
						
						if (!sides.isEmpty()) {
							for (Side side : sides) {
								switch (side) {
									case BACK:
										builder.addQuad(
												new Vector3f(x + 1, y, z),
												new Vector3f(x, y, z),
												new Vector3f(x, y + 1, z),
												new Vector3f(x + 1, y + 1, z),
												side);
										break;
									
									case BOTTOM:
										builder.addQuad(
												new Vector3f(x + 1, y, z + 1),
												new Vector3f(x, y, z + 1),
												new Vector3f(x, y, z),
												new Vector3f(x + 1, y, z),
												side);
										break;
									
									case FRONT:
										builder.addQuad(
												new Vector3f(x, y, z + 1),
												new Vector3f(x + 1, y, z + 1),
												new Vector3f(x + 1, y + 1, z + 1),
												new Vector3f(x, y + 1, z + 1),
												side);
										break;
									
									case LEFT:
										builder.addQuad(
												new Vector3f(x, y, z),
												new Vector3f(x, y, z + 1),
												new Vector3f(x, y + 1, z + 1),
												new Vector3f(x, y + 1, z),
												side);
										break;
									
									case RIGHT:
										builder.addQuad(
												new Vector3f(x + 1, y, z + 1),
												new Vector3f(x + 1, y, z),
												new Vector3f(x + 1, y + 1, z),
												new Vector3f(x + 1, y + 1, z + 1),
												side);
										break;
									
									case TOP:
										builder.addQuad(
												new Vector3f(x, y + 1, z + 1),
												new Vector3f(x + 1, y + 1, z + 1),
												new Vector3f(x + 1, y + 1, z),
												new Vector3f(x, y + 1, z),
												side);
										break;
								}
							}
						}
					}
				}
			}
		}
		
		return convertToMeshes(builders);
	}
}
