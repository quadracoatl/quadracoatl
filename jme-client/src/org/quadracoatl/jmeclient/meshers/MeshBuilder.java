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
import org.quadracoatl.framework.common.regions.Region2d;
import org.quadracoatl.framework.resources.textures.Texture;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

public class MeshBuilder {
	protected List<Integer> faces = new ArrayList<>();
	protected List<Vector3f> normals = new ArrayList<>();
	protected Texture texture = null;
	protected List<Vector2f> textureCoordinates = new ArrayList<>();
	protected List<Vector4f> textureTileCoordinates = new ArrayList<>();
	protected List<Vector3f> vertices = new ArrayList<>();
	
	public MeshBuilder(Texture texture) {
		super();
		
		this.texture = texture;
	}
	
	public void addQuad(Vector3f a, Vector3f b, Vector3f c, Vector3f d, Side side) {
		vertices.add(a);
		vertices.add(b);
		vertices.add(c);
		
		faces.add(Integer.valueOf(vertices.size() - 3));
		faces.add(Integer.valueOf(vertices.size() - 2));
		faces.add(Integer.valueOf(vertices.size() - 1));
		
		normals.add(calculateNormal(a, b, c));
		normals.add(calculateNormal(b, c, a));
		normals.add(calculateNormal(c, a, b));
		
		vertices.add(a);
		vertices.add(c);
		vertices.add(d);
		
		faces.add(Integer.valueOf(vertices.size() - 3));
		faces.add(Integer.valueOf(vertices.size() - 2));
		faces.add(Integer.valueOf(vertices.size() - 1));
		
		normals.add(calculateNormal(a, c, d));
		normals.add(calculateNormal(c, d, a));
		normals.add(calculateNormal(d, a, c));
		
		Vector2f textureStart = new Vector2f(0.0f, 0.0f);
		Vector2f textureEnd = new Vector2f(0.0f, 0.0f);
		Vector4f textureTileCoordinate = new Vector4f(0.0f, 0.0f, 1.0f, 1.0f);
		
		if (texture != null) {
			Region2d textureCoordinate = texture.getRelativeCoordinates(side);
			
			textureStart.x = (float)textureCoordinate.start.x;
			textureStart.y = (float)textureCoordinate.start.y;
			textureEnd.x = (float)textureCoordinate.end.x;
			textureEnd.y = (float)textureCoordinate.end.y;
			
			textureTileCoordinate.x = (float)textureCoordinate.start.x;
			textureTileCoordinate.y = (float)textureCoordinate.start.y;
			textureTileCoordinate.z = (float)textureCoordinate.end.x;
			textureTileCoordinate.w = (float)textureCoordinate.end.y;
		}
		
		textureTileCoordinates.add(textureTileCoordinate);
		textureTileCoordinates.add(textureTileCoordinate);
		textureTileCoordinates.add(textureTileCoordinate);
		textureTileCoordinates.add(textureTileCoordinate);
		textureTileCoordinates.add(textureTileCoordinate);
		textureTileCoordinates.add(textureTileCoordinate);
		
		textureCoordinates.add(new Vector2f(textureStart.x, textureStart.y));
		textureCoordinates.add(new Vector2f(textureEnd.x, textureStart.y));
		textureCoordinates.add(new Vector2f(textureEnd.x, textureEnd.y));
		
		textureCoordinates.add(new Vector2f(textureStart.x, textureStart.y));
		textureCoordinates.add(new Vector2f(textureEnd.x, textureEnd.y));
		textureCoordinates.add(new Vector2f(textureStart.x, textureEnd.y));
	}
	
	public void clear() {
		faces.clear();
		normals.clear();
		textureCoordinates.clear();
		textureTileCoordinates.clear();
		vertices.clear();
	}
	
	public Mesh createMesh() {
		Mesh mesh = new Mesh();
		
		processMesh(mesh);
		
		mesh.updateBound();
		
		return mesh;
	}
	
	protected Vector3f calculateNormal(Vector3f a, Vector3f b, Vector3f c) {
		return (b.subtract(a)).cross((c.subtract(a))).normalize();
	}
	
	protected void processMesh(Mesh mesh) {
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
		mesh.setBuffer(Type.TexCoord2, 4,
				BufferUtils.createFloatBuffer(
						textureTileCoordinates.toArray(new Vector4f[textureTileCoordinates.size()])));
	}
}
