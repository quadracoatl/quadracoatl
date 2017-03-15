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

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.quadracoatl.framework.support.Charsets;

/**
 * The {@link Md5Hasher} is a static utility which allows to create MD5 hashsums
 * for various types of data.
 */
public final class Md5Hasher {
	/** The constant that is used for {@code null} input. */
	public static final String NULL_HASH = "00000000000000000000000000000000";
	
	/**
	 * No instance is needed, this is a static utility.
	 */
	private Md5Hasher() {
		// No instance needed.
	}
	
	/**
	 * Hashes the given data and returns the hash as hex string. If the input is
	 * {@code null} the {@link #NULL_HASH} is returned.
	 * 
	 * @param data The data to be hashed.
	 * @return The hash as hex string or if the input is {@code null}
	 *         {@link #NULL_HASH}.
	 */
	public static final String hash(byte[] data) {
		if (data == null) {
			return NULL_HASH;
		}
		
		return createHexString(getMd5Digest().digest(data));
	}
	
	/**
	 * Hashes the given data from the {@link InputStream} and returns the hash
	 * as hex string. If the input is {@code null} the {@link #NULL_HASH} is
	 * returned.
	 * 
	 * @param stream The stream with the data to be hashed.
	 * @return The hash as hex string or if the input is {@code null}
	 *         {@link #NULL_HASH}.
	 * @throws IOException If reading from the given {@link InputStream} failed.
	 */
	public static final String hash(InputStream stream) throws IOException {
		if (stream == null) {
			return NULL_HASH;
		}
		
		MessageDigest digest = getMd5Digest();
		
		try (DigestInputStream digestInputStream = new DigestInputStream(stream, digest)) {
			while (digestInputStream.read() != -1) {
				// Nothing to do here, the read is all we need.
			}
		}
		
		return createHexString(digest.digest());
	}
	
	/**
	 * Hashes the given {@link String} and returns the hash as hex string. If
	 * the input is {@code null} the {@link #NULL_HASH} is returned.
	 * <p>
	 * The given {@link String} is converted to UTF-8 bytes.
	 * 
	 * @param string The string to be hashed.
	 * @return The hash as hex string or if the input is {@code null}
	 *         {@link #NULL_HASH}.
	 */
	public static final String hash(String string) {
		if (string == null) {
			return NULL_HASH;
		}
		
		return hash(string.getBytes(Charsets.UTF_8));
	}
	
	/**
	 * Converts the given data to a hex string representation.
	 * 
	 * @param bytes The data which should be converted, may not be {@code null}.
	 * @return The hex string representation of the given data.
	 */
	private static final String createHexString(byte[] bytes) {
		StringBuilder hash = new StringBuilder(bytes.length * 2);
		
		for (byte piece : bytes) {
			if ((piece & 0xff) < 0xf) {
				hash.append("0");
			}
			
			hash.append(Integer.toString(piece & 0xff, 16));
		}
		
		return hash.toString();
	}
	
	/**
	 * Gets the MD5 {@link MessageDigest} which can be used for hashing.
	 * 
	 * @return the MD5 {@link MessageDigest} which can be used for hashing.
	 * @throws RuntimeException If for some reason the {@link MessageDigest}
	 *         could not be fetched.
	 */
	private static final MessageDigest getMd5Digest() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 algorithm is not available.");
		}
	}
}
