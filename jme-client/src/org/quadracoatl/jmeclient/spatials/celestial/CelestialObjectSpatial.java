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

import org.quadracoatl.framework.common.Vector3d;
import org.quadracoatl.framework.realm.components.CelestialObjectComponent;
import org.quadracoatl.framework.resources.colors.Color;
import org.quadracoatl.framework.resources.colors.ColorBlendMode;
import org.quadracoatl.jmeclient.controls.CameraAttachingControl;
import org.quadracoatl.jmeclient.extensions.JmeResourceManager;
import org.quadracoatl.jmeclient.utils.ShaderUtil;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

public class CelestialObjectSpatial extends AbstractSkySpatial {
	private Geometry geometry = null;
	private Material material = null;
	private Quad quad = null;
	private Texture texture = null;
	
	public CelestialObjectSpatial(
			CelestialObjectComponent celestialBodyComponent,
			JmeResourceManager resourceManager,
			AssetManager assetManager) {
		super(
				celestialBodyComponent.getName(),
				(float)celestialBodyComponent.getOrder(),
				resourceManager,
				assetManager);
		
		setCullHint(Spatial.CullHint.Never);
		setQueueBucket(Bucket.Sky);
		
		texture = new Texture2D();
		texture.setMagFilter(Texture.MagFilter.Nearest);
		texture.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
		texture.setAnisotropicFilter(0);
		texture.setWrap(Texture.WrapMode.EdgeClamp);
		
		material = new Material(assetManager, "/assets/shaders/sky/CelestialObject.j3md");
		material.setVector4("Color", new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));
		material.setTexture("ColorMap", texture);
		material.setInt("ColorBlendMode", ShaderUtil.getColorBlendModeAsInteger(ColorBlendMode.NORMAL));
		material.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		material.getAdditionalRenderState().setDepthTest(false);
		
		quad = new Quad(1, 1);
		
		geometry = new Geometry();
		geometry.setMaterial(material);
		geometry.setMesh(quad);
		
		geometry.setLocalTranslation(
				10.0f,
				-(quad.getWidth() / 2),
				-(quad.getHeight() / 2));
		geometry.lookAt(new Vector3f(
				0,
				-(quad.getWidth() / 2),
				-(quad.getHeight() / 2)),
				Vector3f.UNIT_Y);
		
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
		quad.updateGeometry((float)size.getX(), (float)size.getY());
		
		geometry.setLocalTranslation(
				10.0f,
				-(quad.getWidth() / 2),
				-(quad.getHeight() / 2));
		geometry.lookAt(new Vector3f(
				0,
				-(quad.getWidth() / 2),
				-(quad.getHeight() / 2)),
				Vector3f.UNIT_Y);
	}
	
	@Override
	public void setTexture(org.quadracoatl.framework.resources.textures.Texture texture) {
		if (texture != null && !texture.getTextures().isEmpty()) {
			this.texture.setImage(resourceManager.loadImage(texture.getAsResource(resourceManager)));
		} else {
			this.texture.setImage(assetManager.loadTexture("/assets/images/white-pixel.png").getImage());
		}
	}
}
