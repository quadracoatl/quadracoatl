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

package org.quadracoatl.framework.entities;

import org.junit.Assert;
import org.junit.Test;
import org.quadracoatl.framework.entities.testcomponents.ExtendedExtendedTestComponent;
import org.quadracoatl.framework.entities.testcomponents.ExtendedTestComponent;
import org.quadracoatl.framework.entities.testcomponents.TestComponent;

public class EntityTest {
	@Test
	public void testBasicAddFetchRemove() {
		Entity entity = new Entity(0);
		entity.addComponent(new TestComponent("Start"));
		
		Assert.assertNotNull("Fetching TestComponent by class failed.", entity.getComponent(TestComponent.class));
		Assert.assertEquals("TestComponent does not have the expected value.", "Start", entity.getComponent(TestComponent.class).getValue());
		
		entity.getComponent(TestComponent.class).setValue("End");
		
		Assert.assertEquals("TestComponent does not have the expected value.", "End", entity.getComponent(TestComponent.class).getValue());
		
		entity.removeComponent(TestComponent.class);
		
		Assert.assertNull("TestComponent is still attached to the Entity.", entity.getComponent(TestComponent.class));
	}
	
	@Test
	public void testExtendedAddFetchRemove() {
		Entity entity = new Entity(0);
		
		Component firstComponent = new ExtendedExtendedTestComponent("First");
		entity.addComponent(firstComponent);
		
		Assert.assertNotNull("Fetching ExtendedExtendedTestComponent by class failed.", entity.getComponent(ExtendedExtendedTestComponent.class));
		Assert.assertNotNull("Fetching ExtendedExtendedTestComponent by super-class failed.", entity.getComponent(ExtendedTestComponent.class));
		Assert.assertNotNull("Fetching ExtendedExtendedTestComponent by super-super-class failed.", entity.getComponent(ExtendedTestComponent.class));
		
		Component secondComponent = new TestComponent("Second");
		entity.addComponent(secondComponent);
		
		Assert.assertEquals("Fetching ExtendedExtendedTestComponent by class failed.", firstComponent, entity.getComponent(ExtendedExtendedTestComponent.class));
		Assert.assertEquals("Fetching ExtendedExtendedTestComponent by super-class failed.", firstComponent, entity.getComponent(ExtendedTestComponent.class));
		Assert.assertEquals("Fetching TestComponent by class failed.", secondComponent, entity.getComponent(TestComponent.class));
		
		Component thirdComponent = new ExtendedTestComponent("Third");
		entity.addComponent(thirdComponent);
		
		Assert.assertEquals("Fetching ExtendedExtendedTestComponent by class failed.", firstComponent, entity.getComponent(ExtendedExtendedTestComponent.class));
		Assert.assertEquals("Fetching ExtendedTestComponent by class failed.", thirdComponent, entity.getComponent(ExtendedTestComponent.class));
		Assert.assertEquals("Fetching TestComponent by class failed.", secondComponent, entity.getComponent(TestComponent.class));
	}
}
