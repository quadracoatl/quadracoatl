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
import org.quadracoatl.utils.IntMap;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;

public class GreedyMesher extends AbstractMesher {
	private int firstMask[][] = null;
	private int secondMask[][] = null;
	
	public GreedyMesher(int chunkWidth, int chunkHeight, int chunkDepth, int blockWidth, int blockHeight,
			int blockDepth) {
		super(chunkWidth, chunkHeight, chunkDepth, blockWidth, blockHeight, blockDepth);
		
		int maxSize = Math.max(Math.max(chunkWidth, chunkHeight), chunkDepth);
		
		firstMask = new int[maxSize][maxSize];
		secondMask = new int[maxSize][maxSize];
	}
	
	@Override
	public IntMap<Mesh> buildMesh(Chunk chunk) {
		IntMap<MeshBuilder> builders = new IntMap<>();
		
		for (int x = 0; x < chunkWidth; x++) {
			fillMasksWithLeftRight(chunk, x);
			
			fillWithQuads(firstMask, blockDepth, blockHeight, blockWidth, x, builders, this::createLeftQuad);
			fillWithQuads(secondMask, blockDepth, blockHeight, blockWidth, x + 1, builders, this::createRightQuad);
		}
		
		for (int y = 0; y < chunkHeight; y++) {
			fillMasksWithBottomTop(chunk, y);
			
			fillWithQuads(firstMask, blockWidth, blockDepth, blockHeight, y, builders, this::createBottomQuad);
			fillWithQuads(secondMask, blockWidth, blockDepth, blockHeight, y + 1, builders, this::createTopQuad);
		}
		
		for (int z = 0; z < chunkDepth; z++) {
			fillMasksWithBackFront(chunk, z);
			
			fillWithQuads(firstMask, blockWidth, blockHeight, blockDepth, z, builders, this::createBackQuad);
			fillWithQuads(secondMask, blockWidth, blockHeight, blockDepth, z + 1, builders, this::createFrontQuad);
		}
		
		return convertToMeshes(builders);
	}
	
	private final void createBackQuad(MeshBuilder builder, int x, int y, int x2, int y2, int z) {
		builder.addQuad(
				new Vector3f(x2, y, z),
				new Vector3f(x, y, z),
				new Vector3f(x, y2, z),
				new Vector3f(x2, y2, z));
	}
	
	private final void createBottomQuad(MeshBuilder builder, int x, int z, int x2, int z2, int y) {
		builder.addQuad(
				new Vector3f(x2, y, z2),
				new Vector3f(x, y, z2),
				new Vector3f(x, y, z),
				new Vector3f(x2, y, z));
	}
	
	private final void createFrontQuad(MeshBuilder builder, int x, int y, int x2, int y2, int z) {
		builder.addQuad(
				new Vector3f(x, y, z),
				new Vector3f(x2, y, z),
				new Vector3f(x2, y2, z),
				new Vector3f(x, y2, z));
	}
	
	private final void createLeftQuad(MeshBuilder builder, int z, int y, int z2, int y2, int x) {
		builder.addQuad(
				new Vector3f(x, y, z),
				new Vector3f(x, y, z2),
				new Vector3f(x, y2, z2),
				new Vector3f(x, y2, z));
	}
	
	private final void createRightQuad(MeshBuilder builder, int z, int y, int z2, int y2, int x) {
		builder.addQuad(
				new Vector3f(x, y, z2),
				new Vector3f(x, y, z),
				new Vector3f(x, y2, z),
				new Vector3f(x, y2, z2));
	}
	
	private final void createTopQuad(MeshBuilder builder, int x, int z, int x2, int z2, int y) {
		builder.addQuad(
				new Vector3f(x, y, z2),
				new Vector3f(x2, y, z2),
				new Vector3f(x2, y, z),
				new Vector3f(x, y, z));
	}
	
	private final void fillMasksWithBackFront(Chunk chunk, int currentSlice) {
		for (int x = 0; x < chunkWidth; x++) {
			for (int y = 0; y < chunkHeight; y++) {
				EnumSet<Sides> sides = getSides(chunk, x, y, currentSlice);
				
				if (sides.contains(Sides.BACK)) {
					firstMask[x][y] = chunk.getBlock(x, y, currentSlice);
				} else {
					firstMask[x][y] = 0;
				}
				
				if (sides.contains(Sides.FRONT)) {
					secondMask[x][y] = chunk.getBlock(x, y, currentSlice);
				} else {
					secondMask[x][y] = 0;
				}
			}
		}
	}
	
	private final void fillMasksWithBottomTop(Chunk chunk, int currentSlice) {
		for (int x = 0; x < chunkWidth; x++) {
			for (int z = 0; z < chunkDepth; z++) {
				EnumSet<Sides> sides = getSides(chunk, x, currentSlice, z);
				
				if (sides.contains(Sides.BOTTOM)) {
					firstMask[x][z] = chunk.getBlock(x, currentSlice, z);
				} else {
					firstMask[x][z] = 0;
				}
				
				if (sides.contains(Sides.TOP)) {
					secondMask[x][z] = chunk.getBlock(x, currentSlice, z);
				} else {
					secondMask[x][z] = 0;
				}
			}
		}
	}
	
	private final void fillMasksWithLeftRight(Chunk chunk, int currentSlice) {
		for (int y = 0; y < chunkHeight; y++) {
			for (int z = 0; z < chunkDepth; z++) {
				EnumSet<Sides> sides = getSides(chunk, currentSlice, y, z);
				
				if (sides.contains(Sides.LEFT)) {
					firstMask[z][y] = chunk.getBlock(currentSlice, y, z);
				} else {
					firstMask[z][y] = 0;
				}
				
				if (sides.contains(Sides.RIGHT)) {
					secondMask[z][y] = chunk.getBlock(currentSlice, y, z);
				} else {
					secondMask[z][y] = 0;
				}
			}
		}
	}
	
	private final void fillWithQuads(int[][] mask, int width, int height, int depth, int currentSlice,
			IntMap<MeshBuilder> builders,
			QuadCreator quadCreator) {
		for (int x = 0; x < mask.length; x++) {
			int[] row = mask[x];
			
			for (int y = 0; y < row.length; y++) {
				if (row[y] != Block.NULL_BLOCK.getId()) {
					int currentId = row[y];
					
					int startX = x;
					int startY = y;
					
					int endY = findYEnd(row, currentId, startY);
					int endX = findXEnd(mask, currentId, startY, endY, x);
					
					y = endY - 1;
					
					MeshBuilder builder = getBuilder(builders, currentId);
					
					quadCreator.create(
							builder,
							startX * width,
							startY * height,
							endX * width,
							endY * height,
							currentSlice * depth);
				}
			}
		}
	}
	
	private final int findXEnd(int[][] mask, int currentId, int startY, int endY, int x) {
		int end = x + 1;
		
		while (end < mask.length) {
			for (int checkY = startY; checkY < endY; checkY++) {
				if (mask[end][checkY] != currentId) {
					return end;
				}
			}
			
			for (int checkY = startY; checkY < endY; checkY++) {
				mask[end][checkY] = 0;
			}
			
			end++;
		}
		
		return end;
	}
	
	private final int findYEnd(int[] row, int currentId, int startY) {
		int end = startY;
		
		while (end < row.length && row[end] == currentId) {
			row[end] = 0;
			end++;
		}
		
		return end;
	}
	
	private interface QuadCreator {
		public void create(
				MeshBuilder builder,
				int firstDimensionStart,
				int secondDimensionStart,
				int firstDimensionEnd,
				int secondDimensionEnd,
				int thirdDimension);
	}
}
