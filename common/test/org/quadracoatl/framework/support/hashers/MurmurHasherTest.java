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

package org.quadracoatl.framework.support.hashers;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link MurmurHasher}.
 */
public class MurmurHasherTest {
	/**
	 * Tests the {@link MurmurHasher#hash(byte[], long)} method.
	 */
	@Test
	public void testHashByteArrayLong() {
		Assert.assertEquals(0, MurmurHasher.hash((byte[])null, 0));
		Assert.assertEquals(1, MurmurHasher.hash((byte[])null, 1));
		
		Assert.assertEquals(0, MurmurHasher.hash(new byte[] {}, 0));
		Assert.assertEquals(-4132994306676857636l, MurmurHasher.hash(new byte[] {}, 1));
		
		Assert.assertEquals(4478902098989184874l, MurmurHasher.hash(new byte[] { 0, 0, 0 }, 0));
		Assert.assertEquals(-5596927997962420379l, MurmurHasher.hash(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, 0));
		
		Assert.assertEquals(-2684172583407358623l, MurmurHasher.hash(new byte[] { 0, 0, 0 }, 1));
		Assert.assertEquals(9191354494066247061l, MurmurHasher.hash(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, 1));
	}
	
	/**
	 * Tests the {@link MurmurHasher#hash(String)} method.
	 */
	@Test
	public void testHashString() {
		Assert.assertEquals(0, MurmurHasher.hash(null));
		Assert.assertEquals(0, MurmurHasher.hash(""));
		
		Assert.assertEquals(-7148968302806999301l, MurmurHasher.hash("abc"));
	}
	
	/**
	 * Tests the {@link MurmurHasher#hash(String, long)} method.
	 */
	@Test
	public void testHashStringLong() {
		Assert.assertEquals(0, MurmurHasher.hash((String)null, 0));
		Assert.assertEquals(0, MurmurHasher.hash("", 0));
		Assert.assertEquals(-4132994306676857636l, MurmurHasher.hash("", 1));
		
		Assert.assertEquals(-7148968302806999301l, MurmurHasher.hash("abc", 0));
		Assert.assertEquals(-5424825208994788271l, MurmurHasher.hash("abc", 1));
	}
}
