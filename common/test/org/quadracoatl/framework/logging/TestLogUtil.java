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

package org.quadracoatl.framework.logging;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

public class TestLogUtil {
	@Test
	public void testgetSimpleIdentity() {
		Assert.assertEquals("getIdentity(null) did not return \"null\".", "null", LogUtil.getSimpleIdentity(null));
		
		Assert.assertEquals("getIdentity(Object.class) did not return \"Object\".", "Object", LogUtil.getSimpleIdentity(Object.class));
		Assert.assertEquals("getIdentity(HashMap.class) did not return \"HashMap\".", "HashMap", LogUtil.getSimpleIdentity(HashMap.class));
		
		Assert.assertTrue(
				"getIdentity(new HashMap<Object, Object>) did not return a nice identity string.",
				LogUtil.getSimpleIdentity(new HashMap<Object, Object>()).matches("HashMap@[0-9a-f]+"));
	}
}
