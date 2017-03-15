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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.quadracoatl.framework.entities.changes.ChangeType;
import org.quadracoatl.framework.logging.LogUtil;
import org.quadracoatl.framework.logging.Logger;
import org.quadracoatl.framework.logging.LoggerFactory;

public class Entity {
	protected Set<Component> components = new HashSet<>();
	protected Map<Class<? extends Component>, Component> componentsByClass = new HashMap<>();
	protected int id = 0;
	protected transient EntityContainer parentContainer = null;
	private final transient Logger LOGGER = LoggerFactory.getLogger(this);
	private transient Set<? extends Component> readonlyComponents = null;
	
	public Entity(int id) {
		this();
		
		this.id = id;
	}
	
	public <COMPONENT extends Component> Entity(int id, Iterable<COMPONENT> components) {
		this(id);
		
		addComponents(components);
	}
	
	private Entity() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public <COMPONENT extends Component> void addComponent(COMPONENT component) {
		if (component == null) {
			return;
		}
		
		removeComponent(component);
		
		components.add(component);
		componentsByClass.put(component.getClass(), component);
		
		component.attach(this);
		
		if (parentContainer != null) {
			parentContainer.getChanges().change(ChangeType.ADDED, component);
		}
		
		Class<?> superComponentClass = component.getClass().getSuperclass();
		
		while (superComponentClass != null
				&& superComponentClass != Object.class) {
			Component existingComponent = componentsByClass.get(superComponentClass);
			
			if (existingComponent == null
					|| existingComponent.getClass() != superComponentClass) {
				componentsByClass.put((Class<? extends Component>)superComponentClass, component);
			}
			
			superComponentClass = superComponentClass.getSuperclass();
		}
	}
	
	public <COMPONENT extends Component> void addComponents(Iterable<COMPONENT> components) {
		if (components == null) {
			return;
		}
		
		for (Component component : components) {
			addComponent(component);
		}
	}
	
	public void attach(EntityContainer parentContainer) {
		if (parentContainer != null) {
			detach();
		}
		
		LOGGER.debug("Attaching to ", LogUtil.getIdentity(parentContainer), ".");
		
		this.parentContainer = parentContainer;
	}
	
	public void detach() {
		if (parentContainer != null) {
			LOGGER.debug("Detaching from ", LogUtil.getIdentity(parentContainer), ".");
		}
		
		parentContainer = null;
	}
	
	public <COMPONENT extends Component> COMPONENT getComponent(Class<COMPONENT> componentClass) {
		return componentClass.cast(componentsByClass.get(componentClass));
	}
	
	public Set<? extends Component> getComponents() {
		if (readonlyComponents == null) {
			readonlyComponents = Collections.unmodifiableSet(components);
		}
		
		return readonlyComponents;
	}
	
	public int getId() {
		return id;
	}
	
	public EntityContainer getParentContainer() {
		return parentContainer;
	}
	
	public <COMPONENT extends Component> boolean hasComponent(Class<COMPONENT> componentClass) {
		return componentsByClass.containsKey(componentClass);
	}
	
	public <COMPONENT extends Component> void removeComponent(Class<COMPONENT> componentClass) {
		if (componentClass == null) {
			return;
		}
		
		Component component = componentsByClass.remove(componentClass);
		
		if (component != null
				&& component.getClass() == componentClass) {
			if (parentContainer != null) {
				parentContainer.getChanges().change(ChangeType.REMOVED, component);
			}
			
			components.remove(component);
			
			component.detach();
			
			Class<?> superComponentClass = componentClass.getSuperclass();
			
			while (superComponentClass != null
					&& superComponentClass != Object.class) {
				if (componentsByClass.get(superComponentClass) == component) {
					componentsByClass.remove(superComponentClass);
				}
				
				superComponentClass = superComponentClass.getSuperclass();
			}
		}
	}
	
	public <COMPONENT extends Component> void removeComponent(COMPONENT component) {
		if (component == null) {
			return;
		}
		
		removeComponent(component.getClass());
	}
	
	public <COMPONENT extends Component> void removeComponents(Iterable<COMPONENT> components) {
		if (components == null) {
			return;
		}
		
		for (Component component : components) {
			removeComponent(component);
		}
	}
	
	public void replaceComponents(Iterable<? extends Component> components) {
		for (Component component : new ArrayList<>(this.components)) {
			removeComponent(component);
		}
		
		addComponents(components);
	}
}
