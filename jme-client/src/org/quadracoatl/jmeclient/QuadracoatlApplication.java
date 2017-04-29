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

package org.quadracoatl.jmeclient;

import org.quadracoatl.environments.ClientEnvironment;
import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.block.BlockType;
import org.quadracoatl.framework.logging.Logger;
import org.quadracoatl.framework.logging.LoggerFactory;
import org.quadracoatl.interlayer.Interlayer;
import org.quadracoatl.interlayer.InterlayerException;
import org.quadracoatl.jmeclient.extensions.JmeResourceManager;
import org.quadracoatl.jmeclient.materials.BlockMaterial;
import org.quadracoatl.jmeclient.states.GameState;

import com.jme3.app.LostFocusBehavior;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

public class QuadracoatlApplication extends SimpleApplication {
	private String baseDir = null;
	private ClientEnvironment clientEnvironment = null;
	private Interlayer interlayer = null;
	private final Logger LOGGER = LoggerFactory.getLogger(this);
	
	public QuadracoatlApplication(String baseDir, Interlayer interlayer) {
		super();
		
		this.baseDir = baseDir;
		this.interlayer = interlayer;
		
		setDisplayFps(true);
		setDisplayStatView(true);
		setLostFocusBehavior(LostFocusBehavior.Disabled);
		setShowSettings(false);
	}
	
	@Override
	public void destroy() {
		super.destroy();
		
		stop();
	}
	
	@Override
	public void simpleInitApp() {
		flyCam.setMoveSpeed(25);
		
		getCamera().setFrustumPerspective(
				90.0f,
				cam.getWidth() / (float)cam.getHeight(),
				0.01f,
				1000.0f);
		getCamera().setLocation(new Vector3f(0, 0, 0));
		getCamera().lookAt(new Vector3f(64, 0, 0), new Vector3f(0, 1, 0));
		
		viewPort.setBackgroundColor(ColorRGBA.Black);
		
		JmeResourceManager resourceManager = new JmeResourceManager(baseDir + "/cache");
		
		try {
			interlayer.start();
		} catch (InterlayerException e) {
			LOGGER.fatal("Failed to start interlayer, exiting.", e);
			
			// TODO Not the best way to exit.
			System.exit(1);
		}
		
		clientEnvironment = new ClientEnvironment(interlayer, resourceManager);
		clientEnvironment.fetchAllResources();
		clientEnvironment.fetchAllBlocks();
		
		for (Block block : clientEnvironment.getCosmos().getBlocks()) {
			if (!Block.isNullBlock(block)) {
				if (block.getBlockType() != BlockType.NONE
						&& (block.getTexture() != null || block.getColor() != null)) {
					Material material = new BlockMaterial(
							block,
							assetManager,
							resourceManager);
					
					material.setName(block.getName());
					
					resourceManager.setMaterial(block, material);
				}
			}
		}
		
		clientEnvironment.fetchAllEntities();
		
		clientEnvironment.start();
		
		stateManager.attach(new GameState(clientEnvironment));
	}
	
	@Override
	public void stop() {
		super.stop();
		
		clientEnvironment.stop();
		interlayer.stop();
	}
}
