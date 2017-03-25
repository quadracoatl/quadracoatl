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

package org.quadracoatl.framework.mod;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;
import org.quadracoatl.testing.AbstractFileHandlingTest;

public class TestMod extends AbstractFileHandlingTest {
	@Test
	public void testNameSanitization() throws IOException {
		Path propertiesFile = createPath("./mods/test/mod.properties",
				"name = Some ~ Highly $#%@*%$ Invalid Name");
		
		Mod mod = new Mod(propertiesFile.getParent());
		Assert.assertEquals("Mod name is not as expected.", "Some___Highly_________Invalid_Name", mod.getName());
	}
	
	@Test
	public void testNonExistingDirectory() throws IOException {
		Mod mod = new Mod(Paths.get("./mods/test/"));
		
		Assert.assertEquals("Mod name is not as expected.", "test", mod.getName());
		Assert.assertEquals("Mod display name is not as expected.", "test", mod.getDisplayName());
		Assert.assertTrue("Mod requirements are not empty.", mod.getRequires().isEmpty());
	}
	
	@Test
	public void testNoPropertiesFile() throws IOException {
		Mod mod = new Mod(createPath("./mods/test/"));
		
		Assert.assertEquals("Mod name is not as expected.", "test", mod.getName());
		Assert.assertEquals("Mod display name is not as expected.", "test", mod.getDisplayName());
		Assert.assertTrue("Mod requirements are not empty.", mod.getRequires().isEmpty());
	}
	
	@Test
	public void testWithPropertiesFile() throws IOException {
		Path propertiesFile = createPath("./mods/test/mod.properties",
				"name = aTest",
				"displayName = This is the test",
				"requires = a b c");
		
		Mod mod = new Mod(propertiesFile.getParent());
		Assert.assertEquals("Mod name is not as expected.", "aTest", mod.getName());
		Assert.assertEquals("Mod display name is not as expected.", "This is the test", mod.getDisplayName());
		Assert.assertFalse("Mod requirements are empty.", mod.getRequires().isEmpty());
		Assert.assertEquals("First requirement is not as expected.", "a", mod.getRequires().get(0));
		Assert.assertEquals("Second requirement is not as expected.", "b", mod.getRequires().get(1));
		Assert.assertEquals("Third requirement is not as expected.", "c", mod.getRequires().get(2));
	}
}
