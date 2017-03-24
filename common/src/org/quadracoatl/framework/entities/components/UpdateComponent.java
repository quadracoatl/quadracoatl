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

package org.quadracoatl.framework.entities.components;

import org.quadracoatl.framework.entities.AbstractComponent;
import org.quadracoatl.framework.entities.Component;
import org.quadracoatl.interlayer.Transient;

public @Transient class UpdateComponent extends AbstractComponent {
	public UpdateComponent() {
		super();
	}
	
	public void update(double currentTime, long elapsedNanoSecondsSinceLastUpdate) {
	}
	
	@Override
	public void updateWith(Component component) {
		// Nothing to do for the time being.
	}
}
