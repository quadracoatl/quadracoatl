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

import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.quadracoatl.framework.support.Charsets;

public final class Md5Hasher {
	
	private Md5Hasher() {
		// No instance needed.
	}
	
	public static final String hash(byte[] data) {
		return createHexString(getMd5Digest().digest(data));
	}
	
	public static final String hash(InputStream stream) {
		MessageDigest digest = getMd5Digest();
		
		new DigestInputStream(stream, digest);
		
		return createHexString(digest.digest());
	}
	
	public static final String hash(String string) {
		return hash(string.getBytes(Charsets.UTF_8));
	}
	
	private static final String createHexString(byte[] bytes) {
		StringBuilder hash = new StringBuilder(bytes.length * 2);
		
		for (byte piece : bytes) {
			hash.append(Integer.toString(piece, 16));
		}
		
		return hash.toString();
	}
	
	private static final MessageDigest getMd5Digest() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 algorithm is not available.");
		}
	}
}
