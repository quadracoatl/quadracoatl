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
import org.quadracoatl.framework.block.type_parameters.DisplacedCubeParameters;
import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.common.Vector3d;
import org.quadracoatl.framework.realm.Realm;
import org.quadracoatl.framework.support.noise.noises.OpenSimplexNoise;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class DisplacingSimpleMesher extends SimpleMesher {
	private Vector3d displacementStrength = null;
	private OpenSimplexNoise xNoise = new OpenSimplexNoise(0l);
	private float xOffset = 0.0f;
	private OpenSimplexNoise yNoise = new OpenSimplexNoise(1l);
	private float yOffset = 0.0f;
	private OpenSimplexNoise zNoise = new OpenSimplexNoise(2l);
	private float zOffset = 0.0f;
	
	public DisplacingSimpleMesher(Realm realm) {
		super(realm, BlockType.DISPLACED_CUBE);
	}
	
	@Override
	public TIntObjectMap<Mesh> buildMeshes(Chunk chunk) {
		if (!containsSupportedBlocks(chunk)) {
			return EMPTY_MESH_MAP;
		}
		
		xOffset = chunk.getIndexX() * chunkWidth * (float)realm.getBlockWidth();
		yOffset = chunk.getIndexY() * chunkHeight * (float)realm.getBlockHeight();
		zOffset = chunk.getIndexZ() * chunkDepth * (float)realm.getBlockDepth();
		
		TIntObjectMap<MeshBuilder> builders = new TIntObjectHashMap<>();
		
		for (int x = 0; x < chunkWidth; x++) {
			for (int y = 0; y < chunkHeight; y++) {
				for (int z = 0; z < chunkDepth; z++) {
					int currentId = chunk.get(x, y, z);
					Block block = realm.getCosmos().getBlock(currentId);
					
					if (block.getBlockType() == BlockType.DISPLACED_CUBE) {
						if (block.getTypeParameters() instanceof DisplacedCubeParameters) {
							displacementStrength = ((DisplacedCubeParameters)block.getTypeParameters()).getDisplacementStrength();
						} else {
							displacementStrength = null;
						}
						
						MeshBuilder builder = getBuilder(builders, block);
						EnumSet<Side> sides = getSides(chunk, x, y, z);
						
						if (!sides.isEmpty()) {
							for (Side side : sides) {
								switch (side) {
									case BACK:
										builder.addQuad(
												createDisplacedVector(x + 1, y, z),
												createDisplacedVector(x, y, z),
												createDisplacedVector(x, y + 1, z),
												createDisplacedVector(x + 1, y + 1, z),
												side);
										break;
									
									case BOTTOM:
										builder.addQuad(
												createDisplacedVector(x + 1, y, z + 1),
												createDisplacedVector(x, y, z + 1),
												createDisplacedVector(x, y, z),
												createDisplacedVector(x + 1, y, z),
												side);
										break;
									
									case FRONT:
										builder.addQuad(
												createDisplacedVector(x, y, z + 1),
												createDisplacedVector(x + 1, y, z + 1),
												createDisplacedVector(x + 1, y + 1, z + 1),
												createDisplacedVector(x, y + 1, z + 1),
												side);
										break;
									
									case LEFT:
										builder.addQuad(
												createDisplacedVector(x, y, z),
												createDisplacedVector(x, y, z + 1),
												createDisplacedVector(x, y + 1, z + 1),
												createDisplacedVector(x, y + 1, z),
												side);
										break;
									
									case RIGHT:
										builder.addQuad(
												createDisplacedVector(x + 1, y, z + 1),
												createDisplacedVector(x + 1, y, z),
												createDisplacedVector(x + 1, y + 1, z),
												createDisplacedVector(x + 1, y + 1, z + 1),
												side);
										break;
									
									case TOP:
										builder.addQuad(
												createDisplacedVector(x, y + 1, z + 1),
												createDisplacedVector(x + 1, y + 1, z + 1),
												createDisplacedVector(x + 1, y + 1, z),
												createDisplacedVector(x, y + 1, z),
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
	
	private final Vector3f createDisplacedVector(int x, int y, int z) {
		if (displacementStrength != null) {
			return new Vector3f(
					x + (float)(xNoise.eval(xOffset + x, yOffset + y, zOffset + z) * displacementStrength.x),
					y + (float)(yNoise.eval(xOffset + x, yOffset + y, zOffset + z) * displacementStrength.y),
					z + (float)(zNoise.eval(xOffset + x, yOffset + y, zOffset + z) * displacementStrength.z));
		} else {
			return new Vector3f(x, y, z);
		}
	}
}
