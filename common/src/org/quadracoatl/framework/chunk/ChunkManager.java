
package org.quadracoatl.framework.chunk;

import java.util.List;

import org.quadracoatl.framework.realm.Realm;

public interface ChunkManager {
	public void add(Chunk chunk);
	
	public void addAll(Iterable<Chunk> chunks);
	
	public void attachTo(Realm realm);
	
	public void detach();
	
	public Chunk get(int x, int y, int z);
	
	public List<Chunk> getChunks();
	
	public void remove(Chunk chunk);
	
	public void remove(int x, int y, int z);
}
