
package org.quadracoatl.jmeclient.states.sub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quadracoatl.environments.ClientEnvironment;
import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.chunk.managers.RingChunkManager;
import org.quadracoatl.framework.common.Vector3i;
import org.quadracoatl.framework.logging.Logger;
import org.quadracoatl.framework.logging.LoggerFactory;
import org.quadracoatl.framework.realm.Realm;
import org.quadracoatl.framework.support.HashStack;
import org.quadracoatl.jmeclient.extensions.JmeChunkManager;
import org.quadracoatl.jmeclient.extensions.JmeResourceManager;
import org.quadracoatl.jmeclient.meshers.DisplacingSimpleMesher;
import org.quadracoatl.jmeclient.meshers.GreedyMesher;
import org.quadracoatl.jmeclient.meshers.Mesher;
import org.quadracoatl.jmeclient.spatials.ChunkSpatial;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class ChunkState extends AbstractAppState {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChunkState.class);
	private static final int SLEEP_TIME = 500;
	private static final int SURROUNDING_CHUNKS = 3;
	private Application app = null;
	private HashStack<Vector3i> chunkCoordinatesToSpawn = new HashStack<>();
	private Node chunksNode = new Node("chunks");
	private Map<Chunk, Node> chunksToNodes = new HashMap<>();
	private ClientEnvironment clientEnvironment = null;
	private Vector3i lastCameraPosition = new Vector3i(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
	private Set<Vector3i> loadedChunks = new HashSet<>();
	private List<Mesher> meshers = new ArrayList<>();
	private HashStack<Vector3i> remeshStack = new HashStack<>();
	private Map<Chunk, ChunkSpatial> spawnedChunksToNodes = Collections.synchronizedMap(new HashMap<>(256));
	private Map<Vector3i, Chunk> spawnedCoordinatesToChunks = Collections.synchronizedMap(new HashMap<>(256));
	private Thread spawnThread = null;
	private volatile boolean spawnThreadActive = true;
	private List<ChunkSpatial> updatedNodes = Collections.synchronizedList(new ArrayList<>(64));
	private volatile boolean updateJobEnqueued = false;
	
	public ChunkState(ClientEnvironment clientEnvironment) {
		super();
		
		this.clientEnvironment = clientEnvironment;
		
		initMeshers(clientEnvironment.getCurrentRealm());
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		
		spawnThreadActive = false;
		
		app.getViewPort().detachScene(chunksNode);
		
		clientEnvironment.getCurrentRealm().swapChunkManager(new RingChunkManager(256));
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		this.app = app;
		
		clientEnvironment.getCurrentRealm().swapChunkManager(new JmeChunkManager(this::removeChunk));
		
		chunksNode.updateGeometricState();
		
		app.getViewPort().attachScene(chunksNode);
		
		spawnThread = new Thread(this::spawnChunks);
		spawnThread.setName("chunk-spawner");
		spawnThread.start();
	}
	
	@Override
	public void update(float tpf) {
		super.update(tpf);
		
		chunksNode.updateLogicalState(tpf);
		
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
						chunkCoordinatesToSpawn.push(new Vector3i(x, y, z));
					}
				}
			}
		}
	}
	
	private void enqueueForRemeshing(Vector3i coordinate) {
		LOGGER.info("Enqueuing chunk for remeshing: ", coordinate);
		
		remeshStack.push(coordinate);
	}
	
	private void enqueueNeighboursForRemeshing(Vector3i coordinate) {
		LOGGER.info("Enqueuing neighbouring chunks for remeshing: ", coordinate);
		
		for (int x = coordinate.x - 1; x <= coordinate.x + 1; x++) {
			for (int z = coordinate.z - 1; z <= coordinate.z + 1; z++) {
				for (int y = coordinate.y - 1; y <= coordinate.y + 1; y++) {
					enqueueForRemeshing(new Vector3i(x, y, z));
				}
			}
		}
	}
	
	private void enqueueUpdateJobIfNeeded() {
		if (!updateJobEnqueued && app != null) {
			updateJobEnqueued = true;
			
			app.enqueue(this::updateNodes);
		}
	}
	
	private void fetchChunk(Vector3i coordinate) {
		LOGGER.info("Fetching chunk: ", coordinate);
		
		Chunk chunk = clientEnvironment.getChunk(
				coordinate.getX(),
				coordinate.getY(),
				coordinate.getZ());
		
		LOGGER.info("Creating mesh for chunk: ", coordinate);
		
		ChunkSpatial chunkSpatial = new ChunkSpatial(chunk);
		chunkSpatial.createMesh(meshers, (JmeResourceManager)clientEnvironment.getResourceCache());
		
		spawnedCoordinatesToChunks.put(coordinate, chunk);
		spawnedChunksToNodes.put(chunk, chunkSpatial);
		
		updatedNodes.add(chunkSpatial);
	}
	
	private void initMeshers(Realm realm) {
		meshers.add(new GreedyMesher(realm));
		meshers.add(new DisplacingSimpleMesher(realm));
	}
	
	private void remeshChunks() {
		Vector3i coordinateToRemesh = remeshStack.pop();
		Chunk remeshingChunk = spawnedCoordinatesToChunks.get(coordinateToRemesh);
		ChunkSpatial remeshingChunkSpatial = spawnedChunksToNodes.get(remeshingChunk);
		
		if (remeshingChunkSpatial != null) {
			LOGGER.info("Remeshing chunk: ", coordinateToRemesh);
			
			remeshingChunkSpatial.createMesh(meshers, (JmeResourceManager)clientEnvironment.getResourceCache());
			updatedNodes.add(remeshingChunkSpatial);
		}
	}
	
	private void removeChunk(Chunk chunk) {
		Vector3i chunkIndex = new Vector3i(
				chunk.getIndexX(),
				chunk.getIndexY(),
				chunk.getIndexZ());
		
		LOGGER.info("Removing chunk at ", chunkIndex, ".");
		
		Node node = chunksToNodes.remove(chunk);
		
		loadedChunks.remove(chunkIndex);
		
		if (node != null) {
			app.enqueue(() -> {
				chunksNode.detachChild(node);
				chunksNode.updateGeometricState();
			});
		}
	}
	
	private void spawnChunks() {
		while (spawnThreadActive && app != null) {
			try {
				while (!chunkCoordinatesToSpawn.isEmpty() && remeshStack.size() <= 36) {
					Vector3i chunkCoordinate = chunkCoordinatesToSpawn.pop();
					
					if (!spawnedCoordinatesToChunks.containsKey(chunkCoordinate)) {
						fetchChunk(chunkCoordinate);
						
						enqueueNeighboursForRemeshing(chunkCoordinate);
						enqueueUpdateJobIfNeeded();
					}
				}
				
				if (!remeshStack.isEmpty()) {
					remeshChunks();
					enqueueUpdateJobIfNeeded();
				} else {
					Thread.sleep(SLEEP_TIME);
				}
			} catch (InterruptedException e) {
				LOGGER.error(e);
				return;
			}
		}
	}
	
	private void updateNodes() {
		while (!updatedNodes.isEmpty()) {
			ChunkSpatial node = updatedNodes.remove(0);
			
			node.updateMesh();
			
			if (node.getParent() == null) {
				LOGGER.info("Attaching node: " + node.getName());
				chunksNode.attachChild(node);
			}
		}
		
		chunksNode.updateGeometricState();
		updateJobEnqueued = false;
	}
}
