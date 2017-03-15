/*
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

package org.quadracoatl.support;

import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.quadracoatl.framework.support.Configuration;

public class TestConfiguration {
	private Configuration configuration = null;
	
	public TestConfiguration() {
		super();
	}
	
	@Before
	public void setUp() {
		configuration = new Configuration();
		configuration.load(Paths.get("./test-data/configuration-test/base.properties"));
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
		Assert.assertEquals(0d, configuration.getDouble("non-existent"), 0.01d);
		Assert.assertEquals(1d, configuration.getDouble("non-existent", 1d), 0.01d);
		
		Assert.assertEquals(0.56d, configuration.getDouble("decimal"), 0.01d);
		
		Assert.assertEquals(1d, configuration.getDouble("string", 1d), 0.01d);
	}
	
	@Test
	public void testGetFloat() {
		Assert.assertEquals(0f, configuration.getFloat("non-existent"), 0.01f);
		Assert.assertEquals(1f, configuration.getFloat("non-existent", 1f), 0.01f);
		
		Assert.assertEquals(0.56f, configuration.getFloat("decimal"), 0.01f);
		
		Assert.assertEquals(1f, configuration.getFloat("string", 1f), 0.01f);
	}
	
	@Test
	public void testGetInteger() {
		Assert.assertEquals(0, configuration.getInteger("non-existent"));
		Assert.assertEquals(1, configuration.getInteger("non-existent", 1));
		
		Assert.assertEquals(5, configuration.getInteger("number"));
		Assert.assertEquals(255, configuration.getInteger("hex"));
		
		Assert.assertEquals(1, configuration.getInteger("flag", 1));
	}
	
	@Test
	public void testGetLong() {
		Assert.assertEquals(0, configuration.getLong("non-existent"));
		Assert.assertEquals(1, configuration.getLong("non-existent", 1));
		
		Assert.assertEquals(5, configuration.getLong("number"));
		Assert.assertEquals(255, configuration.getLong("hex"));
		
		Assert.assertEquals(1, configuration.getLong("flag", 1));
	}
	
	@Test
	public void testGetString() {
		Assert.assertEquals("", configuration.getString("non-existent"));
		Assert.assertEquals("default", configuration.getString("non-existent", "default"));
		
		Assert.assertEquals("This is a string.", configuration.getString("string"));
	}
	
	@Test
	public void testLoadAdditional() {
		configuration.load(Paths.get("./test-data/configuration-test/addition.properties"));
		
		Assert.assertEquals("addition", configuration.getString("string"));
		Assert.assertEquals("additional", configuration.getString("additional"));
	}
	
	@Test
	public void testUtf8() {
		Assert.assertEquals("äöü", configuration.getString("utf8"));
	}
	
}
