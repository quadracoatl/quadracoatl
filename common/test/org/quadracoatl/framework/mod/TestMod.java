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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestMod {
	private Path modDirectory = null;
	private Path propertiesFile = null;
	private Path testDirectory = null;
	
	@Before
	public void setup() throws IOException {
		testDirectory = Files.createTempDirectory("quadracoatl-unittest-mod-");
		testDirectory.toFile().deleteOnExit();
		
		modDirectory = Paths.get(testDirectory.toString(), "test");
		modDirectory.toFile().deleteOnExit();
		
		propertiesFile = Paths.get(modDirectory.toString(), "mod.properties");
		propertiesFile.toFile().deleteOnExit();
	}
	
	@Test
	public void testNameSanitization() throws IOException {
		Files.createDirectory(modDirectory);
		
		Files.write(propertiesFile, Arrays.asList(
				"name = Some ~ Highly $#% Invalid Name"));
		
		Mod mod = new Mod(modDirectory);
		Assert.assertEquals("Mod name is not as expected.", "Some___Highly_____Invalid_Name", mod.getName());
	}
	
	@Test
	public void testNonExistingDirectory() throws IOException {
		Mod mod = new Mod(modDirectory);
		
		Assert.assertEquals("Mod name is not as expected.", "test", mod.getName());
		Assert.assertEquals("Mod display name is not as expected.", "test", mod.getDisplayName());
		Assert.assertTrue("Mod requirements are not empty.", mod.getRequires().isEmpty());
	}
	
	@Test
	public void testNoPropertiesFile() throws IOException {
		Files.createDirectory(modDirectory);
		
		Mod mod = new Mod(modDirectory);
		
		Assert.assertEquals("Mod name is not as expected.", "test", mod.getName());
		Assert.assertEquals("Mod display name is not as expected.", "test", mod.getDisplayName());
		Assert.assertTrue("Mod requirements are not empty.", mod.getRequires().isEmpty());
	}
	
	@Test
	public void testWithPropertiesFile() throws IOException {
		Files.createDirectory(modDirectory);
		
		Files.write(propertiesFile, Arrays.asList(
				"name = aTest",
				"displayName = This is the test",
				"requires = a b c"));
		
		Mod mod = new Mod(modDirectory);
		Assert.assertEquals("Mod name is not as expected.", "aTest", mod.getName());
		Assert.assertEquals("Mod display name is not as expected.", "This is the test", mod.getDisplayName());
		Assert.assertFalse("Mod requirements are empty.", mod.getRequires().isEmpty());
		Assert.assertEquals("First requirement is not as expected.", "a", mod.getRequires().get(0));
		Assert.assertEquals("Second requirement is not as expected.", "b", mod.getRequires().get(1));
		Assert.assertEquals("Third requirement is not as expected.", "c", mod.getRequires().get(2));
	}
}
