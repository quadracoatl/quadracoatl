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

import org.quadracoatl.framework.entities.Component;
import org.quadracoatl.framework.entities.Entity;
import org.quadracoatl.framework.entities.changes.ChangeType;
import org.quadracoatl.framework.logging.LogUtil;
import org.quadracoatl.framework.logging.Logger;
import org.quadracoatl.framework.logging.LoggerFactory;

public abstract class AbstractComponent implements Component {
	protected transient Entity entity = null;
	protected final transient Logger LOGGER = LoggerFactory.getLogger(this);
	
	protected AbstractComponent() {
		super();
	}
	
	@Override
	public void attach(Entity entity) {
		if (this.entity != null) {
			detach();
		}
		
		LOGGER.debug("Attaching to #", Integer.valueOf(entity.getId()), " ", LogUtil.getSimpleIdentity(entity), ".");
		
		this.entity = entity;
	}
	
	@Override
	public void detach() {
		if (entity != null) {
			LOGGER.debug("Detaching from #", Integer.valueOf(entity.getId()), " ", LogUtil.getSimpleIdentity(entity), ".");
		}
		
		entity = null;
	}
	
	@Override
	public Entity getEntity() {
		return entity;
	}
	
	@Override
	public void markAsChanged() {
		if (entity != null && entity.getParentContainer() != null) {
			entity.getParentContainer().getChanges().change(ChangeType.CHANGED, this);
		}
	}
}
