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

package org.quadracoatl.jmeclient.spatials.celestial;

import java.util.List;

import org.quadracoatl.framework.common.vectors.Vector3d;
import org.quadracoatl.framework.realm.components.SkyComponent;
import org.quadracoatl.framework.resources.colors.Color;
import org.quadracoatl.framework.resources.colors.ColorBlendMode;
import org.quadracoatl.jmeclient.controls.CameraAttachingControl;
import org.quadracoatl.jmeclient.extensions.JmeResourceManager;
import org.quadracoatl.jmeclient.utils.ShaderUtil;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingSphere;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.TextureCubeMap;
import com.jme3.texture.image.ColorSpace;

public class SkySpatial extends AbstractSkySpatial {
	private Geometry geometry = null;
	private Material material = null;
	private Sphere sphere = null;
	private TextureCubeMap texture = null;
	
	public SkySpatial(
			SkyComponent skyComponent,
			JmeResourceManager resourceManager,
			AssetManager assetManager) {
		super(
				skyComponent.getName(),
				(float)skyComponent.getOrder(),
				resourceManager,
				assetManager);
		
		setCullHint(Spatial.CullHint.Never);
		setQueueBucket(Bucket.Sky);
		
		texture = new TextureCubeMap();
		texture.setMagFilter(Texture.MagFilter.Nearest);
		texture.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
		texture.setAnisotropicFilter(0);
		texture.setWrap(Texture.WrapMode.EdgeClamp);
		
		material = new Material(assetManager, "/assets/shaders/sky/Sky.j3md");
		material.setVector4("Color", new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));
		material.setInt("ColorBlendMode", ShaderUtil.getColorBlendModeAsInteger(ColorBlendMode.NORMAL));
		material.setVector3("NormalScale", Vector3f.UNIT_XYZ);
		material.setTexture("Texture", texture);
		material.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		
		sphere = new Sphere(8, 8, 10, false, true);
		
		geometry = new Geometry();
		geometry.setMaterial(material);
		geometry.setMesh(sphere);
		geometry.setModelBound(new BoundingSphere(Float.POSITIVE_INFINITY, Vector3f.ZERO));
		
		attachChild(geometry);
		
		addControl(new CameraAttachingControl());
	}
	
	@Override
	public void setColor(Color color, ColorBlendMode colorBlendMode) {
		Vector4f materialColor = (Vector4f)material.getParam("Color").getValue();
		
		if (color != null) {
			materialColor.setX((float)color.red);
			materialColor.setY((float)color.green);
			materialColor.setZ((float)color.blue);
			materialColor.setW((float)color.alpha);
			
			material.setInt("ColorBlendMode", ShaderUtil.getColorBlendModeAsInteger(colorBlendMode));
		} else {
			materialColor.setX(0.0f);
			materialColor.setY(0.0f);
			materialColor.setZ(0.0f);
			materialColor.setW(0.0f);
			
			material.setInt("ColorBlendMode", ShaderUtil.COLOR_BLEND_MODE_INVALID);
		}
		
		material.setVector4("Color", materialColor);
	}
	
	@Override
	public void setSize(Vector3d size) {
		// TODO Can we do that? And if yes, what should it do?
	}
	
	@Override
	public void setTexture(org.quadracoatl.framework.resources.textures.Texture texture) {
		Texture topTexture = null;
		Texture bottomTexture = null;
		Texture frontTexture = null;
		Texture backTexture = null;
		Texture leftTexture = null;
		Texture rightTexture = null;
		
		if (texture != null && !texture.getTextures().isEmpty()) {
			List<String> textures = texture.getTextures();
			
			if (texture.getTextures().size() > 1) {
				frontTexture = resourceManager.loadTexture(textures.get(0));
				backTexture = resourceManager.loadTexture(textures.get(1));
				leftTexture = resourceManager.loadTexture(textures.get(2));
				rightTexture = resourceManager.loadTexture(textures.get(3));
				topTexture = resourceManager.loadTexture(textures.get(4));
				bottomTexture = resourceManager.loadTexture(textures.get(5));
			} else {
				frontTexture = resourceManager.loadTexture(textures.get(0));
				backTexture = frontTexture;
				leftTexture = frontTexture;
				rightTexture = frontTexture;
				topTexture = frontTexture;
				bottomTexture = frontTexture;
			}
		} else {
			frontTexture = assetManager.loadTexture("/assets/images/white-pixel.png");
			backTexture = frontTexture;
			leftTexture = frontTexture;
			rightTexture = frontTexture;
			topTexture = frontTexture;
			bottomTexture = frontTexture;
		}
		
		Image image = new Image(
				Format.ABGR8,
				topTexture.getImage().getWidth(),
				topTexture.getImage().getHeight(),
				null,
				ColorSpace.sRGB);
		
		image.addData(leftTexture.getImage().getData(0));
		image.addData(rightTexture.getImage().getData(0));
		image.addData(bottomTexture.getImage().getData(0));
		image.addData(topTexture.getImage().getData(0));
		image.addData(frontTexture.getImage().getData(0));
		image.addData(backTexture.getImage().getData(0));
		
		this.texture.setImage(image);
	}
}
