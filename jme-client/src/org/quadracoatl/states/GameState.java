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

package org.quadracoatl.states;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.quadracoatl.JmeChunkManager;
import org.quadracoatl.environments.ClientEnvironment;
import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.meshers.GreedyMesher;
import org.quadracoatl.meshers.Mesher;
import org.quadracoatl.utils.Vector3i;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class GameState extends AbstractAppState {
	private Application app = null;
	private BlockingQueue<Vector3i> chunkQueue = new ArrayBlockingQueue<>(256);
	private Map<Chunk, Node> chunksToNodes = new HashMap<>();
	private ClientEnvironment clientEnvironment = null;
	private volatile AtomicInteger enqueuedBlocks = new AtomicInteger(0);
	private GameInputState inputState = null;
	private Vector3i lastCameraPosition = new Vector3i(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
	private Set<Vector3i> loadedChunks = new HashSet<>();
	private Node rootNode = new Node("Realm");
	private Thread spawnThread = null;
	private volatile boolean spawnThreadActive = true;
	
	public GameState(ClientEnvironment clientEnvironment) {
		super();
		
		this.clientEnvironment = clientEnvironment;
		
		inputState = new GameInputState(clientEnvironment.getCosmos());
		
		clientEnvironment.getCurrentRealm().swapChunkManager(new JmeChunkManager(this::removeChunk));
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		
		app.getStateManager().detach(inputState);
		app.getViewPort().detachScene(rootNode);
		
		spawnThreadActive = false;
		
		app = null;
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		this.app = app;
		
		app.getStateManager().attach(inputState);
		
		app.getViewPort().attachScene(rootNode);
		rootNode.updateGeometricState();
		
		spawnThread = new Thread(this::spawnChunks);
		spawnThread.start();
	}
	
	@Override
	public void update(float tpf) {
		super.update(tpf);
		
		Vector3f cameraPosition = app.getViewPort().getCamera().getLocation();
		Vector3i currentCameraPosition = clientEnvironment.getCurrentRealm().getIndex(
				(int)cameraPosition.getX(),
				(int)cameraPosition.getY(),
				(int)cameraPosition.getZ());
		
		if (!currentCameraPosition.equals(lastCameraPosition)) {
			System.out.println("Camera position changed: " + currentCameraPosition);
			lastCameraPosition.set(currentCameraPosition);
			
			for (int x = lastCameraPosition.x - 1; x <= lastCameraPosition.x + 1; x++) {
				for (int z = lastCameraPosition.z - 1; z <= lastCameraPosition.z + 1; z++) {
					for (int y = lastCameraPosition.y - 1; y <= lastCameraPosition.y + 1; y++) {
						Vector3i chunkLocation = new Vector3i(x, y, z);
						
						if (!loadedChunks.contains(chunkLocation)) {
							chunkQueue.add(chunkLocation);
						}
					}
				}
			}
		}
	}
	
	private void removeChunk(Chunk chunk) {
		Node node = chunksToNodes.remove(chunk);
		
		loadedChunks.remove(chunk.getIndex());
		
		if (node != null) {
			app.enqueue(() -> {
				rootNode.detachChild(node);
				rootNode.updateGeometricState();
			});
		}
	}
	
	private void spawnChunks() {
		Mesher mesher = new GreedyMesher(
				clientEnvironment.getCurrentRealm().getChunkWidth(),
				clientEnvironment.getCurrentRealm().getChunkHeight(),
				clientEnvironment.getCurrentRealm().getChunkDepth(),
				clientEnvironment.getCurrentRealm().getBlockWidth(),
				clientEnvironment.getCurrentRealm().getBlockHeight(),
				clientEnvironment.getCurrentRealm().getBlockDepth());
		
		while (spawnThreadActive && app != null) {
			try {
				if (enqueuedBlocks.get() < 3) {
					Vector3i chunkLocation = chunkQueue.poll(1, TimeUnit.SECONDS);
					
					if (chunkLocation != null
							&& !loadedChunks.contains(chunkLocation)) {
						
						System.out.println("Spawning: " + chunkLocation.toString());
						
						loadedChunks.add(chunkLocation);
						Chunk chunk = clientEnvironment.getChunk(
								chunkLocation.getX(),
								chunkLocation.getY(),
								chunkLocation.getZ());
						
						if (chunk != null) {
							Node node = mesher.createGeometry(chunk, clientEnvironment.getCosmos().getBlocks());
							
							chunksToNodes.put(chunk, node);
							
							app.enqueue(() -> {
								rootNode.attachChild(node);
								rootNode.updateGeometricState();
								
								enqueuedBlocks.addAndGet(-1);
							});
							
							enqueuedBlocks.addAndGet(1);
						}
					}
				} else {
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
	}
}
