/*
 * Copyright 2017, Robert 'Bobby' Zenz
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

package org.quadracoatl.jmeclient.states.sub;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.quadracoatl.environments.ClientEnvironment;
import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.chunk.managers.SimpleChunkManager;
import org.quadracoatl.framework.common.Camera;
import org.quadracoatl.framework.common.vectors.Vector3i;
import org.quadracoatl.framework.common.vectors.Vector3iStack;
import org.quadracoatl.framework.common.vectors.VectorUtil;
import org.quadracoatl.framework.realm.Realm;
import org.quadracoatl.framework.support.TraceableThread;
import org.quadracoatl.jmeclient.extensions.JmeChunkManager;
import org.quadracoatl.jmeclient.extensions.JmeResourceManager;
import org.quadracoatl.jmeclient.meshers.DisplacingSimpleMesher;
import org.quadracoatl.jmeclient.meshers.GreedyMesher;
import org.quadracoatl.jmeclient.meshers.Mesher;
import org.quadracoatl.jmeclient.spatials.ChunkSpatial;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

public class ChunkManagingState extends AbstractAppState {
	private volatile boolean active = false;
	private Application app = null;
	private Camera camera = null;
	private Node chunksNode = new Node("chunks");
	private TLongObjectMap<ChunkSpatial> chunksToNodes = new TLongObjectHashMap<>();
	private ClientEnvironment clientEnvironment = null;
	private BitmapText debugText = null;
	private final Predicate<Vector3i> IS_INSIDE_VIEWPYRAMID = this::isInsideViewPyramid;
	private final Predicate<Vector3i> IS_INSIDE_VIEWSPHERE = this::isInsideViewSphere;
	private Vector3iStack loading = new Vector3iStack();
	private List<Mesher> meshers = new ArrayList<>();
	private Vector3iStack meshing = new Vector3iStack();
	private Vector3iStack ready = new Vector3iStack();
	private Realm realm = null;
	private Vector3iStack remeshing = new Vector3iStack();
	private Vector3iStack remove = new Vector3iStack();
	private int SLEEP_TIME = 64;
	
	public ChunkManagingState(ClientEnvironment clientEnvironment) {
		super();
		
		this.clientEnvironment = clientEnvironment;
		
		camera = new Camera();
		camera.setViewDistance(128);
		camera.setFov(90);
		
		// TODO The realm might change later on.
		realm = clientEnvironment.getCurrentRealm();
		initMeshers(realm);
		
		clientEnvironment.getCurrentRealm().swapChunkManager(new JmeChunkManager(this::removeChunk));
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		
		active = false;
		
		clientEnvironment.getCurrentRealm().swapChunkManager(new SimpleChunkManager());
		
		app.getViewPort().detachScene(chunksNode);
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		this.app = app;
		
		chunksNode.updateGeometricState();
		
		app.getViewPort().attachScene(chunksNode);
		
		active = true;
		
		new TraceableThread(this::loadChunks, "chunk-loader").start();
		new TraceableThread(this::meshChunks, "chunk-mesher").start();
		
		BitmapFont font = app.getAssetManager().loadFont("Interface/Fonts/Console.fnt");
		
		debugText = new BitmapText(font);
		debugText.setSize(font.getCharSet().getRenderedSize());
		debugText.setColor(ColorRGBA.White);
		debugText.setText("");
		debugText.setLocalTranslation(0.0f, 0.0f, 1.0f);
		debugText.updateGeometricState();
		
		((SimpleApplication)app).getGuiNode().attachChild(debugText);
	}
	
	@Override
	public void update(float tpf) {
		super.update(tpf);
		
		updateCamera();
		queueChunks();
		updateChunksNode();
		
		chunksNode.updateLogicalState(tpf);
		
		debugText.setText(
				"Chunk Management\n"
						+ " Loading: " + Integer.toString(loading.size()) + "\n"
						+ " Meshing: " + Integer.toString(meshing.size()) + "\n"
						+ " Ready: " + Integer.toString(ready.size()) + "\n"
						+ " Added: " + Integer.toString(chunksNode.getQuantity()) + "\n"
						+ " Remeshing: " + Integer.toString(remeshing.size()));
		debugText.setLocalTranslation(
				12.0f,
				app.getCamera().getHeight() - 12.0f,
				1.0f);
	}
	
	private void initMeshers(Realm realm) {
		meshers.add(new GreedyMesher(realm));
		meshers.add(new DisplacingSimpleMesher(realm));
	}
	
	private boolean isInsideViewPyramid(Vector3i position) {
		return camera.isInsideViewPyramid(
				position.x * realm.getChunkWidth() * realm.getBlockWidth(),
				position.y * realm.getChunkHeight() * realm.getBlockHeight(),
				position.z * realm.getChunkDepth() * realm.getBlockDepth());
	}
	
	private boolean isInsideViewSphere(Vector3i position) {
		return camera.isInsideViewSphere(
				position.x * realm.getChunkWidth() * realm.getBlockWidth(),
				position.y * realm.getChunkHeight() * realm.getBlockHeight(),
				position.z * realm.getChunkDepth() * realm.getBlockDepth());
	}
	
	private void loadChunk(Vector3i chunkIndex) {
		clientEnvironment.getChunk(chunkIndex.x, chunkIndex.y, chunkIndex.z);
		
		remeshing.push(chunkIndex.x - 1, chunkIndex.y, chunkIndex.z);
		remeshing.push(chunkIndex.x + 1, chunkIndex.y, chunkIndex.z);
		remeshing.push(chunkIndex.x, chunkIndex.y - 1, chunkIndex.z);
		remeshing.push(chunkIndex.x, chunkIndex.y + 1, chunkIndex.z);
		remeshing.push(chunkIndex.x, chunkIndex.y, chunkIndex.z - 1);
		remeshing.push(chunkIndex.x, chunkIndex.y, chunkIndex.z + 1);
	}
	
	private void loadChunks() {
		while (active) {
			Vector3i forLoading = popFromStack(loading);
			
			if (forLoading != null) {
				loadChunk(forLoading);
				
				meshing.push(forLoading);
			} else {
				sleep();
			}
		}
	}
	
	private void meshChunk(Vector3i chunkIndex) {
		Chunk chunk = clientEnvironment.getChunk(chunkIndex.x, chunkIndex.y, chunkIndex.z);
		
		if (chunk != null) {
			ChunkSpatial chunkSpatial = new ChunkSpatial(chunk);
			chunkSpatial.createMesh(meshers, (JmeResourceManager)clientEnvironment.getResourceCache());
			
			synchronized (chunksToNodes) {
				chunksToNodes.put(VectorUtil.pack(chunkIndex), chunkSpatial);
			}
		}
	}
	
	private void meshChunks() {
		while (active) {
			if (remeshing.size() > 36 || meshing.isEmpty()) {
				while (!remeshing.isEmpty()) {
					remeshChunk(remeshing.pop());
				}
			}
			
			Vector3i forMeshing = popFromStack(meshing);
			
			if (forMeshing != null) {
				meshChunk(forMeshing);
				
				ready.push(forMeshing);
			} else {
				sleep();
			}
		}
	}
	
	private Vector3i popFromStack(Vector3iStack pStack) {
		if (pStack.isEmpty()) {
			return null;
		}
		
		Vector3i value = pStack.pop(IS_INSIDE_VIEWPYRAMID);
		
		if (value == null) {
			value = pStack.pop(IS_INSIDE_VIEWSPHERE);
		}
		
		if (value == null) {
			value = pStack.pop();
		}
		
		return value;
	}
	
	private void queueChunks() {
		double startX = camera.getLocation().x - camera.getViewDistance();
		double startY = camera.getLocation().y - camera.getViewDistance();
		double startZ = camera.getLocation().z - camera.getViewDistance();
		
		double endX = camera.getLocation().x + camera.getViewDistance();
		double endY = camera.getLocation().y + camera.getViewDistance();
		double endZ = camera.getLocation().z + camera.getViewDistance();
		
		double stepX = realm.getChunkWidth() * realm.getBlockWidth();
		double stepY = realm.getChunkHeight() * realm.getBlockHeight();
		double stepZ = realm.getChunkDepth() * realm.getBlockDepth();
		
		for (double x = startX; x < endX; x = x + stepX) {
			for (double y = startY; y < endY; y = y + stepY) {
				for (double z = startZ; z < endZ; z = z + stepZ) {
					if (camera.isInsideViewSphere(x, y, z)) {
						queueForLoading(x, y, z);
						queueForLoading(x - stepX, y, z);
						queueForLoading(x - stepX, y - stepY, z);
						queueForLoading(x - stepX, y - stepY, z - stepZ);
						queueForLoading(x, y - stepY, z);
						queueForLoading(x, y - stepY, z - stepZ);
						queueForLoading(x, y, z - stepZ);
					}
				}
			}
		}
	}
	
	private void queueForLoading(double x, double y, double z) {
		Vector3i chunkIndex = realm.getIndex(x, y, z);
		
		synchronized (chunksToNodes) {
			if (!meshing.contains(chunkIndex)
					&& !ready.contains(chunkIndex)
					&& !chunksToNodes.containsKey(VectorUtil.pack(chunkIndex))) {
				loading.push(chunkIndex);
			}
		}
	}
	
	private void remeshChunk(Vector3i chunkIndex) {
		ChunkSpatial chunkSpatial = null;
		
		synchronized (chunksToNodes) {
			chunkSpatial = chunksToNodes.get(VectorUtil.pack(chunkIndex));
		}
		
		if (chunkSpatial != null) {
			chunkSpatial.createMesh(meshers, (JmeResourceManager)clientEnvironment.getResourceCache());
			
			ready.push(chunkIndex);
		}
	}
	
	private void removeChunk(Chunk chunk) {
		remove.push(
				chunk.getIndexX(),
				chunk.getIndexY(),
				chunk.getIndexZ());
	}
	
	private void sleep() {
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			// Let's make sure that the other thread does also stop.
			active = false;
		}
	}
	
	private void updateCamera() {
		Vector3f newLocation = app.getCamera().getLocation();
		Vector3f newDirection = app.getCamera().getDirection();
		
		camera.setLocation(newLocation.x, newLocation.y, newLocation.z);
		camera.setDirection(newDirection.x, newDirection.y, newDirection.z);
	}
	
	private void updateChunksNode() {
		boolean updateRequired = false;
		
		Vector3i chunkIndex = ready.pop();
		
		while (chunkIndex != null) {
			ChunkSpatial chunkSpatial = null;
			
			synchronized (chunksToNodes) {
				chunkSpatial = chunksToNodes.get(VectorUtil.pack(chunkIndex));
			}
			
			if (chunkSpatial != null) {
				if (chunkSpatial.updateMesh()) {
					updateRequired = true;
				}
				
				if (chunkSpatial.getParent() == null) {
					chunksNode.attachChild(chunkSpatial);
					updateRequired = true;
				}
			}
			
			chunkIndex = ready.pop();
		}
		
		chunkIndex = remove.pop();
		
		while (chunkIndex != null) {
			ChunkSpatial chunkSpatial = null;
			
			synchronized (chunksToNodes) {
				chunkSpatial = chunksToNodes.remove(VectorUtil.pack(chunkIndex));
			}
			
			if (chunkSpatial != null && chunkSpatial.getParent() != null) {
				chunksNode.detachChild(chunkSpatial);
				updateRequired = true;
			}
			
			chunkIndex = remove.pop();
		}
		
		if (updateRequired) {
			chunksNode.updateGeometricState();
		}
	}
}
