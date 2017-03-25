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

package org.quadracoatl.testing;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.quadracoatl.framework.support.Charsets;

public abstract class AbstractFileHandlingTest {
	private Path temporaryDirectory = null;
	
	@Before
	public void setup() throws IOException {
		temporaryDirectory = Files.createTempDirectory("quadracoatl-unittest-" + getClass().getSimpleName() + "-");
		temporaryDirectory.toFile().deleteOnExit();
	}
	
	@After
	public void tearDown() throws IOException {
		deleteRecursive(temporaryDirectory);
	}
	
	protected Path createPath(String relativePath, String... lines) throws IOException {
		Path path = getPath(relativePath);
		
		if (relativePath.endsWith("/")) {
			Files.createDirectories(path);
		} else {
			Files.createDirectories(path.getParent());
			Files.createFile(path);
		}
		
		if (lines != null && lines.length > 0) {
			Files.write(
					path,
					Arrays.asList(lines),
					Charsets.UTF_8);
		}
		
		return path;
	}
	
	protected void deleteRecursive(Path path) throws IOException {
		try (DirectoryStream<Path> entries = Files.newDirectoryStream(path.toAbsolutePath())) {
			for (Path entry : entries) {
				if (Files.isDirectory(entry)) {
					deleteRecursive(entry);
				}
				
				Files.deleteIfExists(entry);
			}
		}
	}
	
	protected Path getPath(String relativePath) {
		Path path = Paths.get(temporaryDirectory.toString(), relativePath);
		path.toFile().deleteOnExit();
		
		Path toMarkForDeletion = path;
		
		while (!toMarkForDeletion.equals(temporaryDirectory)) {
			toMarkForDeletion.toFile().deleteOnExit();
			toMarkForDeletion = toMarkForDeletion.getParent();
		}
		
		return path;
	}
}
