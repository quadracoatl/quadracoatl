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

import java.util.ArrayList;
import java.util.List;

import org.quadracoatl.framework.block.Side;
import org.quadracoatl.framework.resources.textures.Texture;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

public class QuadSizeMeshBuilder extends MeshBuilder {
	protected List<Vector2f> quadSizes = new ArrayList<>();
	
	public QuadSizeMeshBuilder(Texture texture) {
		super(texture);
	}
	
	@Override
	public void addQuad(Vector3f a, Vector3f b, Vector3f c, Vector3f d, Side side) {
		super.addQuad(a, b, c, d, side);
		
		Vector3f normal = calculateNormal(a, b, c);
		
		float quadWidth = 1.0f;
		float quadHeight = 1.0f;
		
		if (normal.x > 0.0f) {
			// Right
			quadWidth = Math.abs(a.z - c.z);
			quadHeight = Math.abs(a.y - c.y);
		} else if (normal.x < 0.0f) {
			// Left
			quadWidth = Math.abs(a.z - c.z);
			quadHeight = Math.abs(a.y - c.y);
		} else if (normal.y > 0.0f) {
			// Top
			quadWidth = Math.abs(a.x - c.x);
			quadHeight = Math.abs(a.z - c.z);
		} else if (normal.y < 0.0f) {
			// Bottom
			quadWidth = Math.abs(a.x - c.x);
			quadHeight = Math.abs(a.z - c.z);
		} else if (normal.z > 0.0f) {
			// Front
			quadWidth = Math.abs(a.x - c.x);
			quadHeight = Math.abs(a.y - c.y);
		} else if (normal.z < 0.0f) {
			// Back
			quadWidth = Math.abs(a.x - c.x);
			quadHeight = Math.abs(a.y - c.y);
		}
		
		Vector2f quadSize = new Vector2f(quadWidth, quadHeight);
		
		quadSizes.add(quadSize);
		quadSizes.add(quadSize);
		quadSizes.add(quadSize);
		quadSizes.add(quadSize);
		quadSizes.add(quadSize);
		quadSizes.add(quadSize);
	}
	
	@Override
	public void clear() {
		super.clear();
		
		quadSizes.clear();
	}
	
	@Override
	protected void processMesh(Mesh mesh) {
		super.processMesh(mesh);
		
		mesh.setBuffer(Type.TexCoord3, 2,
				BufferUtils.createFloatBuffer(quadSizes.toArray(new Vector2f[quadSizes.size()])));
	}
}
