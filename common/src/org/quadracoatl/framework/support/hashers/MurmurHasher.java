/*
 * All code is released to the public domain. For business purposes, Murmurhash
 * is under the MIT license. 
 */

package org.quadracoatl.framework.support.hashers;

import org.quadracoatl.framework.support.Charsets;

/**
 * This is a port of MurmurHash, a non-cryptographic hash function suitable for
 * general hash-based lookup.
 * 
 * @see <a href="https://sites.google.com/site/murmurhash/">Website</a>
 */
public final class MurmurHasher {
	
	private MurmurHasher() {
		// There is no instance needed.
	}
	
	/**
	 * MurmurHashing https://sites.google.com/site/murmurhash/
	 * 
	 * @param data
	 * @param seed
	 * @return
	 */
	public static final long hash(byte[] data, long seed) {
		long m = 0xc6a4a7935bd1e995L;
		int r = 47;
		
		long hash = seed ^ (data.length * m);
		
		for (int index = 0; index < data.length - 7; index = index + 8) {
			long k = (long)data[index + 0] & 0xff;
			k = k + (((long)data[index + 1] & 0xff) << 8);
			k = k + (((long)data[index + 2] & 0xff) << 16);
			k = k + (((long)data[index + 3] & 0xff) << 24);
			k = k + (((long)data[index + 4] & 0xff) << 32);
			k = k + (((long)data[index + 5] & 0xff) << 40);
			k = k + (((long)data[index + 6] & 0xff) << 48);
			k = k + (((long)data[index + 7] & 0xff) << 56);
			
			k = k * m;
			k = k ^ (k >>> r);
			k = k * m;
			
			hash = hash ^ k;
			hash = hash * m;
		}
		
		switch (data.length % 8) {
			case 7:
				hash = hash ^ (((long)data[(data.length & ~7) + 6] & 0xff) << 48);
			case 6:
				hash = hash ^ (((long)data[(data.length & ~7) + 5] & 0xff) << 40);
			case 5:
				hash = hash ^ (((long)data[(data.length & ~7) + 4] & 0xff) << 32);
			case 4:
				hash = hash ^ (((long)data[(data.length & ~7) + 3] & 0xff) << 24);
			case 3:
				hash = hash ^ (((long)data[(data.length & ~7) + 2] & 0xff) << 16);
			case 2:
				hash = hash ^ (((long)data[(data.length & ~7) + 1] & 0xff) << 8);
			case 1:
				hash = hash ^ ((long)(data[data.length & ~7]) & 0xff);
				hash = hash * m;
				
		}
		
		hash = hash ^ (hash >>> r);
		hash = hash * m;
		hash = hash ^ (hash >>> r);
		
		return hash;
	}
	
	public static final long hash(String string) {
		return hash(string, 0);
	}
	
	public static final long hash(String string, long seed) {
		return hash(string.getBytes(Charsets.UTF_8), seed);
	}
	
}
