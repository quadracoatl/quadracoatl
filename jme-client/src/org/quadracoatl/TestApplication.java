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

package org.quadracoatl;

import org.quadracoatl.environments.ClientEnvironment;
import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.realm.Sky;
import org.quadracoatl.interlayer.Interlayer;
import org.quadracoatl.states.GameState;
import org.quadracoatl.textures.BlockMaterial;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.util.SkyFactory;

public class TestApplication extends SimpleApplication {
	private String baseDir = null;
	private ClientEnvironment clientEnvironment = null;
	private Interlayer connection = null;
	
	public TestApplication(String baseDir, Interlayer connection) {
		super();
		
		this.baseDir = baseDir;
		this.connection = connection;
	}
	
	@Override
	public void simpleInitApp() {
		flyCam.setMoveSpeed(25);
		
		getCamera().setFrustumPerspective(
				90.0f,
				cam.getWidth() / (float)cam.getHeight(),
				0.001f,
				1000.0f);
		getCamera().setLocation(new Vector3f(0, 0, 0));
		getCamera().lookAt(new Vector3f(64, 0, 0), new Vector3f(0, 1, 0));
		
		viewPort.setBackgroundColor(ColorRGBA.Black);
		
		JmeResourceManager resourceManager = new JmeResourceManager(baseDir + "/cache");
		
		clientEnvironment = new ClientEnvironment(connection, resourceManager);
		clientEnvironment.fetchAllResources();
		clientEnvironment.fetchAllBlocks();
		
		for (Sky sky : clientEnvironment.getCosmos().getRealm("default").getSkies()) {
			rootNode.attachChild(SkyFactory.createSky(
					assetManager,
					resourceManager.loadTexture(sky.getTexture().getLeft()),
					resourceManager.loadTexture(sky.getTexture().getRight()),
					resourceManager.loadTexture(sky.getTexture().getFront()),
					resourceManager.loadTexture(sky.getTexture().getBack()),
					resourceManager.loadTexture(sky.getTexture().getTop()),
					resourceManager.loadTexture(sky.getTexture().getBottom()),
					Vector3f.UNIT_XYZ));
		}
		
		for (Block block : clientEnvironment.getCosmos().getBlocks()) {
			if (!Block.isNullBlock(block)) {
				Material material = new BlockMaterial(
						assetManager,
						resourceManager,
						block.getTexture());
				
				material.setName(block.getName());
				
				block.getAdditionalState().put("material", material);
			}
		}
		
		stateManager.attach(new GameState(clientEnvironment));
	}
}
