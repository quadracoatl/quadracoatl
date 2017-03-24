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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link Md5Hasher}.
 */
public class Md5HasherTest {
	/**
	 * Tests the {@link Md5Hasher#hash(byte[])} method.
	 */
	@Test
	public void testHashByteArray() {
		Assert.assertEquals("00000000000000000000000000000000", Md5Hasher.hash((byte[])null));
		Assert.assertEquals("d41d8cd98f00b204e9800998ecf8427e", Md5Hasher.hash(new byte[] {}));
		Assert.assertEquals("93b885adfe0da089cdf634904fd59f71", Md5Hasher.hash(new byte[] { 0 }));
		Assert.assertEquals("693e9af84d3dfcc71e640e005bdc5e2e", Md5Hasher.hash(new byte[] { 0, 0, 0 }));
	}
	
	/**
	 * Tests the {@link Md5Hasher#hash(InputStream)} method.
	 * 
	 * @throws IOException If something went very wrong.
	 */
	@Test
	public void testHashInputStream() throws IOException {
		Assert.assertEquals("00000000000000000000000000000000", Md5Hasher.hash((InputStream)null));
		Assert.assertEquals("d41d8cd98f00b204e9800998ecf8427e", Md5Hasher.hash(new ByteArrayInputStream(new byte[] {})));
		Assert.assertEquals("93b885adfe0da089cdf634904fd59f71", Md5Hasher.hash(new ByteArrayInputStream(new byte[] { 0 })));
		Assert.assertEquals("693e9af84d3dfcc71e640e005bdc5e2e", Md5Hasher.hash(new ByteArrayInputStream(new byte[] { 0, 0, 0 })));
	}
	
	/**
	 * Tests the {@link Md5Hasher#hash(String)} method.
	 */
	@Test
	public void testHashString() {
		Assert.assertEquals("00000000000000000000000000000000", Md5Hasher.hash((String)null));
		Assert.assertEquals("d41d8cd98f00b204e9800998ecf8427e", Md5Hasher.hash(""));
		Assert.assertEquals("0cc175b9c0f1b6a831c399e269772661", Md5Hasher.hash("a"));
		Assert.assertEquals("900150983cd24fb0d6963f7d28e17f72", Md5Hasher.hash("abc"));
	}
}
