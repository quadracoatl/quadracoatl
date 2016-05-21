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

import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.utils.IntMap;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;

public class SimpleMesher extends AbstractMesher {
	public SimpleMesher(int chunkWidth, int chunkHeight, int chunkDepth, int blockWidth, int blockHeight,
			int blockDepth) {
		super(chunkWidth, chunkHeight, chunkDepth, blockWidth, blockHeight, blockDepth);
	}
	
	@Override
	public IntMap<Mesh> buildMesh(Chunk chunk) {
		IntMap<MeshBuilder> builders = new IntMap<>();
		
		for (int x = 0; x < chunkWidth; x++) {
			for (int y = 0; y < chunkHeight; y++) {
				for (int z = 0; z < chunkDepth; z++) {
					int currentId = chunk.getBlock(x, y, z);
					
					if (currentId > 0) {
						MeshBuilder builder = getBuilder(builders, currentId);
						EnumSet<Sides> sides = getSides(chunk, x, y, z);
						
						if (Sides.hasSide(sides)) {
							for (Sides side : sides) {
								switch (side) {
									case BACK:
										builder.addQuad(
												new Vector3f(x + 1, y, z),
												new Vector3f(x, y, z),
												new Vector3f(x, y + 1, z),
												new Vector3f(x + 1, y + 1, z));
										break;
									
									case BOTTOM:
										builder.addQuad(
												new Vector3f(x + 1, y, z + 1),
												new Vector3f(x, y, z + 1),
												new Vector3f(x, y, z),
												new Vector3f(x + 1, y, z));
										break;
									
									case FRONT:
										builder.addQuad(
												new Vector3f(x, y, z + 1),
												new Vector3f(x + 1, y, z + 1),
												new Vector3f(x + 1, y + 1, z + 1),
												new Vector3f(x, y + 1, z + 1));
										break;
									
									case LEFT:
										builder.addQuad(
												new Vector3f(x, y, z),
												new Vector3f(x, y, z + 1),
												new Vector3f(x, y + 1, z + 1),
												new Vector3f(x, y + 1, z));
										break;
									
									case RIGHT:
										builder.addQuad(
												
												new Vector3f(x + 1, y, z + 1),
												new Vector3f(x + 1, y, z),
												new Vector3f(x + 1, y + 1, z),
												new Vector3f(x + 1, y + 1, z + 1));
										break;
									
									case TOP:
										builder.addQuad(
												new Vector3f(x, y + 1, z + 1),
												new Vector3f(x + 1, y + 1, z + 1),
												new Vector3f(x + 1, y + 1, z),
												new Vector3f(x, y + 1, z));
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
