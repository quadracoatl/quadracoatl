package org.quadracoatl.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.realm.Realm;
import org.quadracoatl.meshers.GreedyMesher;
import org.quadracoatl.meshers.Mesher;

import com.jme3.scene.Node;

public class ThreadedChunkMesher {
	private static final int SLEEP_TIME = 500;
	private List<Chunk> chunksToMesh = Collections.synchronizedList(new ArrayList<>(32));
	private List<MeshedChunk> meshedChunks = Collections.synchronizedList(new ArrayList<>(32));
	private Mesher mesher = null;
	private volatile boolean running = false;
	private Thread thread = new Thread(this::run);
	
	public ThreadedChunkMesher(Realm realm) {
		super();
		
		mesher = new GreedyMesher(realm);
	}
	
	public boolean hasMeshedChunks() {
		return !meshedChunks.isEmpty();
	}
	
	public void meshChunk(Chunk chunk) {
		chunksToMesh.add(chunk);
	}
	
	public MeshedChunk nextMeshedChunk() {
		if (!meshedChunks.isEmpty()) {
			return meshedChunks.remove(0);
		}
		
		return null;
	}
	
	public void start() {
		if (!running) {
			running = true;
			thread.start();
		}
	}
	
	public void stop() {
		running = false;
	}
	
	private void run() {
		while (running) {
			if (!chunksToMesh.isEmpty()) {
				Chunk chunk = chunksToMesh.remove(0);
				Node node = mesher.createGeometry(chunk);
				
				meshedChunks.add(new MeshedChunk(chunk, node));
			} else {
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					running = false;
					return;
				}
			}
		}
	}
	
	public static final class MeshedChunk {
		private Chunk chunk = null;
		private Node node = null;
		
		public MeshedChunk(Chunk chunk, Node node) {
			super();
			this.chunk = chunk;
			this.node = node;
		}
		
		public Chunk getChunk() {
			return chunk;
		}
		
		public Node getNode() {
			return node;
		}
		
		public void setChunk(Chunk chunk) {
			this.chunk = chunk;
		}
		
		public void setNode(Node node) {
			this.node = node;
		}
		
	}
}
