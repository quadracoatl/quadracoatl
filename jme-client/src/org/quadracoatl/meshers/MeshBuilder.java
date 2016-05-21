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

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

public class MeshBuilder {
	protected List<Integer> faces = new ArrayList<>();
	protected List<Vector3f> normals = new ArrayList<>();
	protected List<Vector2f> quadSizes = new ArrayList<>();
	protected List<Vector2f> textureCoordinates = new ArrayList<>();
	protected List<Vector2f> textureTileCoordinates = new ArrayList<>();
	protected List<Vector3f> vertices = new ArrayList<>();
	
	public MeshBuilder() {
		super();
	}
	
	public void addQuad(Vector3f a, Vector3f b, Vector3f c, Vector3f d) {
		vertices.add(a);
		vertices.add(b);
		vertices.add(c);
		vertices.add(d);
		
		faces.add(Integer.valueOf(vertices.size() - 4));
		faces.add(Integer.valueOf(vertices.size() - 3));
		faces.add(Integer.valueOf(vertices.size() - 2));
		faces.add(Integer.valueOf(vertices.size() - 4));
		faces.add(Integer.valueOf(vertices.size() - 2));
		faces.add(Integer.valueOf(vertices.size() - 1));
		
		Vector3f normal = calculateNormal(a, b, c);
		
		normals.add(normal);
		normals.add(normal);
		normals.add(normal);
		normals.add(normal);
		
		float quadWidth = 1.0f;
		float quadHeight = 1.0f;
		
		float textureStart = 0.0f;
		float textureWidth = 1.0f;
		
		float part = 1.0f / 6.0f;
		
		if (normal.x > 0.0f) {
			// Right
			
			quadWidth = Math.abs(a.z - c.z);
			quadHeight = Math.abs(a.y - c.y);
			
			textureStart = part * 4;
			textureWidth = textureStart + part;
		} else if (normal.x < 0.0f) {
			// Left
			
			quadWidth = Math.abs(a.z - c.z);
			quadHeight = Math.abs(a.y - c.y);
			
			textureStart = part * 2;
			textureWidth = textureStart + part;
		} else if (normal.y > 0.0f) {
			// Top
			
			quadWidth = Math.abs(a.x - c.x);
			quadHeight = Math.abs(a.z - c.z);
			
			textureStart = part * 0;
			textureWidth = textureStart + part;
		} else if (normal.y < 0.0f) {
			// Bottom
			
			quadWidth = Math.abs(a.x - c.x);
			quadHeight = Math.abs(a.z - c.z);
			
			textureStart = part * 1;
			textureWidth = textureStart + part;
		} else if (normal.z > 0.0f) {
			// Front
			
			quadWidth = Math.abs(a.x - c.x);
			quadHeight = Math.abs(a.y - c.y);
			
			textureStart = part * 3;
			textureWidth = textureStart + part;
		} else if (normal.z < 0.0f) {
			// Back
			
			quadWidth = Math.abs(a.x - c.x);
			quadHeight = Math.abs(a.y - c.y);
			
			textureStart = part * 5;
			textureWidth = textureStart + part;
		}
		
		Vector2f quadSize = new Vector2f(quadWidth, quadHeight);
		
		quadSizes.add(quadSize);
		quadSizes.add(quadSize);
		quadSizes.add(quadSize);
		quadSizes.add(quadSize);
		
		Vector2f textureTileCoordinate = new Vector2f(textureStart, 0.0f);
		
		textureTileCoordinates.add(textureTileCoordinate);
		textureTileCoordinates.add(textureTileCoordinate);
		textureTileCoordinates.add(textureTileCoordinate);
		textureTileCoordinates.add(textureTileCoordinate);
		
		textureCoordinates.add(new Vector2f(textureStart, 0.0f));
		textureCoordinates.add(new Vector2f(textureWidth, 0.0f));
		textureCoordinates.add(new Vector2f(textureWidth, 1.0f));
		textureCoordinates.add(new Vector2f(textureStart, 1.0f));
	}
	
	public void clear() {
		faces.clear();
		normals.clear();
		quadSizes.clear();
		textureCoordinates.clear();
		textureTileCoordinates.clear();
		vertices.clear();
	}
	
	public Mesh createMesh() {
		Mesh mesh = new Mesh();
		
		int[] indexes = new int[faces.size()];
		
		for (int index = 0; index < faces.size(); index++) {
			indexes[index] = faces.get(index).intValue();
		}
		
		mesh.setBuffer(Type.Position, 3,
				BufferUtils.createFloatBuffer(vertices.toArray(new Vector3f[vertices.size()])));
		mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexes));
		mesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals.toArray(new Vector3f[normals.size()])));
		mesh.setBuffer(Type.TexCoord, 2,
				BufferUtils.createFloatBuffer(textureCoordinates.toArray(new Vector2f[textureCoordinates.size()])));
		mesh.setBuffer(Type.TexCoord2, 2,
				BufferUtils.createFloatBuffer(quadSizes.toArray(new Vector2f[quadSizes.size()])));
		mesh.setBuffer(Type.TexCoord3, 2,
				BufferUtils.createFloatBuffer(
						textureTileCoordinates.toArray(new Vector2f[textureTileCoordinates.size()])));
		
		mesh.updateBound();
		
		return mesh;
	}
	
	protected Vector3f calculateNormal(Vector3f a, Vector3f b, Vector3f c) {
		return (b.subtract(a)).cross((c.subtract(a))).normalize();
	}
}
