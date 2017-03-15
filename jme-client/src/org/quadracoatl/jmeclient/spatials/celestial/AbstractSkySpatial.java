
package org.quadracoatl.jmeclient.spatials.celestial;

import org.quadracoatl.framework.common.Vector3d;
import org.quadracoatl.framework.resources.colors.Color;
import org.quadracoatl.framework.resources.colors.ColorBlendMode;
import org.quadracoatl.framework.resources.textures.Texture;
import org.quadracoatl.jmeclient.extensions.JmeResourceManager;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

public abstract class AbstractSkySpatial extends Node {
	protected AssetManager assetManager = null;
	protected float order = 1.0f;
	protected JmeResourceManager resourceManager = null;
	
	protected AbstractSkySpatial(
			String name,
			float order,
			JmeResourceManager resourceManager,
			AssetManager assetManager) {
		super(name);
		
		this.order = order;
		this.resourceManager = resourceManager;
		this.assetManager = assetManager;
	}
	
	public float getOrder() {
		return order;
	}
	
	public abstract void setColor(Color color, ColorBlendMode colorMode);
	
	public void setOrder(float order) {
		this.order = order;
	}
	
	public abstract void setSize(Vector3d size);
	
	public abstract void setTexture(Texture texture);
}
