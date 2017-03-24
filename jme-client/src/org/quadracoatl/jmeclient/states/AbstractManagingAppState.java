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

package org.quadracoatl.jmeclient.states;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;

public class AbstractManagingAppState extends AbstractAppState {
	private Application app = null;
	private AppStateManager stateManager = null;
	private List<AppState> states = new ArrayList<>();
	
	public AbstractManagingAppState() {
		super();
	}
	
	public void addAppState(AppState state) {
		states.add(state);
		
		if (stateManager != null) {
			stateManager.attach(state);
		}
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		
		for (AppState state : states) {
			stateManager.detach(state);
		}
		
		app = null;
		stateManager = null;
	}
	
	public Application getApp() {
		return app;
	}
	
	public AppStateManager getStateManager() {
		return stateManager;
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		this.stateManager = stateManager;
		this.app = app;
		
		for (AppState state : states) {
			stateManager.attach(state);
		}
	}
	
	public void removeAppState(AppState state) {
		if (stateManager != null) {
			stateManager.detach(state);
		}
		
		states.remove(state);
	}
}