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

import org.quadracoatl.JmeChunkManager;
import org.quadracoatl.environments.ClientEnvironment;
import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.utils.ThreadedChunkMesher;
import org.quadracoatl.utils.ThreadedChunkMesher.MeshedChunk;
import org.quadracoatl.utils.Vector3i;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;

public class GameState extends AbstractAppState {
	private static final int SURROUNDING_CHUNKS = 1;
	
	private Application app = null;
	private ThreadedChunkMesher chunkMesher = null;
	private BlockingQueue<Vector3i> chunkQueue = new ArrayBlockingQueue<>(256);
	private Map<Chunk, Node> chunksToNodes = new HashMap<>();
	private ClientEnvironment clientEnvironment = null;
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
		
		chunkMesher = new ThreadedChunkMesher(clientEnvironment.getCurrentRealm());
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		
		app.getStateManager().detach(inputState);
		app.getViewPort().detachScene(rootNode);
		
		chunkMesher.stop();
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
		
		chunkMesher.start();
	}
	
	@Override
	public void render(RenderManager rm) {
		if (chunkMesher.hasMeshedChunks()) {
			while (chunkMesher.hasMeshedChunks()) {
				MeshedChunk meshedChunk = chunkMesher.nextMeshedChunk();
				
				if (chunksToNodes.containsKey(meshedChunk.getChunk())) {
					rootNode.detachChild(chunksToNodes.remove(meshedChunk.getChunk()));
				}
				
				chunksToNodes.put(meshedChunk.getChunk(), meshedChunk.getNode());
				rootNode.attachChild(meshedChunk.getNode());
			}
			
			rootNode.updateGeometricState();
		}
	}
	
	@Override
	public void update(float tpf) {
		Vector3f cameraPosition = app.getViewPort().getCamera().getLocation();
		Vector3i currentCameraPosition = clientEnvironment.getCurrentRealm().getIndex(
				(int)cameraPosition.getX(),
				(int)cameraPosition.getY(),
				(int)cameraPosition.getZ());
		
		if (!currentCameraPosition.equals(lastCameraPosition)) {
			lastCameraPosition.set(currentCameraPosition);
			
			for (int x = lastCameraPosition.x - SURROUNDING_CHUNKS; x <= lastCameraPosition.x + SURROUNDING_CHUNKS; x++) {
				for (int z = lastCameraPosition.z - SURROUNDING_CHUNKS; z <= lastCameraPosition.z + SURROUNDING_CHUNKS; z++) {
					for (int y = lastCameraPosition.y - SURROUNDING_CHUNKS; y <= lastCameraPosition.y + SURROUNDING_CHUNKS; y++) {
						chunkQueue.add(new Vector3i(x, y, z));
					}
				}
			}
		}
	}
	
	private void removeChunk(Chunk chunk) {
		Node node = chunksToNodes.remove(chunk);
		
		loadedChunks.remove(new Vector3i(
				chunk.getIndexX(),
				chunk.getIndexY(),
				chunk.getIndexZ()));
		
		if (node != null) {
			app.enqueue(() -> {
				rootNode.detachChild(node);
				rootNode.updateGeometricState();
			});
		}
	}
	
	private void spawnChunks() {
		while (spawnThreadActive && app != null) {
			try {
				Vector3i chunkIndex = chunkQueue.poll(1, TimeUnit.SECONDS);
				
				if (chunkIndex != null) {
					if (!loadedChunks.contains(chunkIndex)) {
						System.out.println("Spawning: " + chunkIndex.toString());
						
						loadedChunks.add(chunkIndex);
					} else {
						System.out.println("Remeshing: " + chunkIndex.toString());
					}
					
					chunkMesher.meshChunk(clientEnvironment.getChunk(
							chunkIndex.getX(),
							chunkIndex.getY(),
							chunkIndex.getZ()));
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
	}
}
