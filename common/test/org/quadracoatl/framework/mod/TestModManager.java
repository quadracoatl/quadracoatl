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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.quadracoatl.testing.AbstractFileHandlingTest;

public class TestModManager extends AbstractFileHandlingTest {
	@Test
	public void testMissingRequirement() throws IOException {
		ModManager modManager = new ModManager();
		
		modManager.add(createMod("a", "missing"));
		
		Assert.assertTrue("Mod list is not empty, even though it should be.", modManager.getModsInLoadOrder().isEmpty());
	}
	
	@Test
	public void testMultipleOnSameLayer() throws IOException {
		ModManager modManager = new ModManager();
		
		modManager.add(createMod("a", ""));
		modManager.add(createMod("b", ""));
		modManager.add(createMod("c", ""));
		modManager.add(createMod("d", ""));
		
		List<Mod> mods = modManager.getModsInLoadOrder();
		assertMods(mods, 0, 4, "a", "b", "c", "d");
	}
	
	@Test
	public void testNoRequirements() throws IOException {
		ModManager modManager = new ModManager();
		
		modManager.add(createMod("a0", ""));
		modManager.add(createMod("a1", ""));
		modManager.add(createMod("a2", ""));
		
		modManager.add(createMod("b0", "a0"));
		modManager.add(createMod("b1", "a1"));
		modManager.add(createMod("b2", "a2"));
		
		modManager.add(createMod("c0", "b0"));
		modManager.add(createMod("c1", "b1"));
		modManager.add(createMod("c2", "b2"));
		
		modManager.add(createMod("d0", "c0"));
		modManager.add(createMod("d1", "c1"));
		modManager.add(createMod("d2", "c2"));
		
		List<Mod> mods = modManager.getModsInLoadOrder();
		assertMods(mods, 0, 3, "a0", "a1", "a2");
		assertMods(mods, 3, 6, "b0", "b1", "b2");
		assertMods(mods, 6, 9, "c0", "c1", "c2");
		assertMods(mods, 9, 12, "d0", "d1", "d2");
	}
	
	@Test
	public void testSimpleChain() throws IOException {
		ModManager modManager = new ModManager();
		
		modManager.add(createMod("a", ""));
		modManager.add(createMod("b", "a"));
		modManager.add(createMod("c", "b"));
		modManager.add(createMod("d", "c"));
		
		List<Mod> mods = modManager.getModsInLoadOrder();
		Assert.assertEquals("Mod \"a\" is not at position zero.", "a", mods.get(0).getName());
		Assert.assertEquals("Mod \"b\" is not at position zero.", "b", mods.get(1).getName());
		Assert.assertEquals("Mod \"c\" is not at position zero.", "c", mods.get(2).getName());
		Assert.assertEquals("Mod \"d\" is not at position zero.", "d", mods.get(3).getName());
	}
	
	private void assertMods(List<Mod> mods, int startIndex, int endIndex, String... modNames) {
		List<String> notContainedMods = new ArrayList<>(Arrays.asList(modNames));
		
		for (int index = startIndex; index < endIndex; index++) {
			Mod currentMod = mods.get(index);
			Iterator<String> notContainedModsIterator = notContainedMods.iterator();
			
			while (notContainedModsIterator.hasNext()) {
				if (currentMod.getName().equals(notContainedModsIterator.next())) {
					notContainedModsIterator.remove();
					break;
				}
			}
		}
		
		if (!notContainedMods.isEmpty()) {
			Assert.fail("Mod \"" + notContainedMods.get(0) + "\" was not found.");
		}
	}
	
	private Mod createMod(String dirName, String... modRequirements) throws IOException {
		return new Mod(createPath("./mods/" + dirName + "/mod.properties",
				"name = " + dirName,
				"requires = " + String.join(" ", modRequirements)).getParent());
	}
}
