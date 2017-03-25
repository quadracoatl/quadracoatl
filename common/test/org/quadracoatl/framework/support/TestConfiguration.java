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

package org.quadracoatl.framework.support;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.quadracoatl.testing.AbstractFileHandlingTest;

public class TestConfiguration extends AbstractFileHandlingTest {
	private Configuration configuration = null;
	
	@Override
	@Before
	public void setup() throws IOException {
		super.setup();
		
		configuration = new Configuration(createPath("test.properties",
				"flag=true",
				"decimal-positive=0.56",
				"decimal-negative=-1.56",
				"number-byte=0b11111111",
				"number-hex=0xf",
				"number-negative=-4",
				"number-octal=0o200",
				"number-positive=3",
				"string=This is a string.",
				"utf8=äöü"));
	}
	
	@Test
	public void testGetBoolean() {
		Assert.assertFalse(configuration.getBoolean("non-existent"));
		Assert.assertTrue(configuration.getBoolean("non-existent", true));
		
		Assert.assertTrue(configuration.getBoolean("flag"));
		
		Assert.assertFalse(configuration.getBoolean("string"));
	}
	
	@Test
	public void testGetDouble() {
		Assert.assertEquals(0d, configuration.getDouble("non-existent"), 0.001d);
		Assert.assertEquals(1.21d, configuration.getDouble("non-existent", 1.21d), 0.001d);
		
		Assert.assertEquals(0.56d, configuration.getDouble("decimal-positive"), 0.001d);
		Assert.assertEquals(-1.56d, configuration.getDouble("decimal-negative"), 0.001d);
		
		Assert.assertEquals(1d, configuration.getDouble("string", 1d), 0.001d);
	}
	
	@Test
	public void testGetFloat() {
		Assert.assertEquals(0f, configuration.getFloat("non-existent"), 0.001f);
		Assert.assertEquals(1.21f, configuration.getFloat("non-existent", 1.21f), 0.001f);
		
		Assert.assertEquals(0.56f, configuration.getFloat("decimal-positive"), 0.001f);
		Assert.assertEquals(-1.56f, configuration.getFloat("decimal-negative"), 0.001f);
		
		Assert.assertEquals(1f, configuration.getFloat("string", 1f), 0.001f);
	}
	
	@Test
	public void testGetInteger() {
		Assert.assertEquals(0, configuration.getInteger("non-existent"));
		Assert.assertEquals(1, configuration.getInteger("non-existent", 1));
		
		Assert.assertEquals(3, configuration.getInteger("number-positive"));
		Assert.assertEquals(-4, configuration.getInteger("number-negative"));
		Assert.assertEquals(15, configuration.getInteger("number-hex"));
		Assert.assertEquals(255, configuration.getInteger("number-byte"));
		Assert.assertEquals(128, configuration.getInteger("number-octal"));
		
		Assert.assertEquals(1, configuration.getInteger("flag", 1));
	}
	
	@Test
	public void testGetLong() {
		Assert.assertEquals(0, configuration.getLong("non-existent"));
		Assert.assertEquals(1, configuration.getLong("non-existent", 1));
		
		Assert.assertEquals(3, configuration.getLong("number-positive"));
		Assert.assertEquals(-4, configuration.getLong("number-negative"));
		Assert.assertEquals(15, configuration.getLong("number-hex"));
		Assert.assertEquals(255, configuration.getLong("number-byte"));
		Assert.assertEquals(128, configuration.getLong("number-octal"));
		
		Assert.assertEquals(1, configuration.getLong("flag", 1));
	}
	
	@Test
	public void testGetString() {
		Assert.assertEquals("", configuration.getString("non-existent"));
		Assert.assertEquals("default", configuration.getString("non-existent", "default"));
		
		Assert.assertEquals("This is a string.", configuration.getString("string"));
	}
	
	@Test
	public void testLoadAdditional() throws IOException {
		configuration.load(createPath("additional.properties",
				"additional=The additional value.",
				"string=The new value."));
		
		Assert.assertEquals("The new value.", configuration.getString("string"));
		Assert.assertEquals("The additional value.", configuration.getString("additional"));
	}
	
	@Test
	public void testUtf8() {
		Assert.assertEquals("äöü", configuration.getString("utf8"));
	}
}
