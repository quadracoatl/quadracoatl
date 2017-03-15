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

package org.quadracoatl.jmeclient.states.sub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.cosmos.Cosmos;
import org.quadracoatl.jmeclient.extensions.JmeResourceManager;
import org.quadracoatl.jmeclient.spatials.debug.AxisSpatial;
import org.quadracoatl.jmeclient.spatials.debug.GridsSpatial;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.scene.Spatial.CullHint;

public class GameInputState extends AbstractAppState {
	private Map<String, ActionListener> actions = new HashMap<>();
	private Application app = null;
	private AxisSpatial axisSpatial = null;
	private Cosmos cosmos = null;
	private GridsSpatial gridsSpatial = null;
	private JmeResourceManager resourceManager = null;
	
	public GameInputState(JmeResourceManager resourceManager, Cosmos cosmos) {
		super();
		
		this.resourceManager = resourceManager;
		this.cosmos = cosmos;
	}
	
	@Override
	public void cleanup() {
		List<String> actionNames = new ArrayList<>(actions.keySet());
		
		for (String actionName : actionNames) {
			removeAction(actionName);
		}
		
		this.app = null;
		
		super.cleanup();
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		this.app = app;
		
		attachAction(
				"wireframe-mode",
				new KeyTrigger(KeyInput.KEY_TAB),
				this::toggleWireframeMode);
		
		attachAction(
				"mouse-lock",
				new KeyTrigger(KeyInput.KEY_F1),
				this::toggleMouseLock);
		
		attachAction(
				"show-axis",
				new KeyTrigger(KeyInput.KEY_F2),
				this::toggleAxis);
		
		attachAction(
				"show-grids",
				new KeyTrigger(KeyInput.KEY_F3),
				this::toggleGrids);
	}
	
	private void attachAction(String name, Trigger trigger, ActionListener listener) {
		app.getInputManager().addMapping(name, trigger);
		app.getInputManager().addListener(listener, name);
		
		actions.put(name, listener);
	}
	
	private void removeAction(String name) {
		app.getInputManager().deleteMapping(name);
		app.getInputManager().removeListener(actions.get(name));
		
		actions.remove(name);
	}
	
	private void toggleAxis(String name, boolean isPressed, float tpf) {
		if (!isPressed) {
			if (axisSpatial == null) {
				axisSpatial = new AxisSpatial(app.getAssetManager());
				app.getViewPort().attachScene(axisSpatial);
			} else {
				if (axisSpatial.getCullHint() == CullHint.Never) {
					axisSpatial.setCullHint(CullHint.Always);
				} else {
					axisSpatial.setCullHint(CullHint.Never);
				}
			}
		}
	}
	
	private void toggleGrids(String name, boolean isPressed, float tpf) {
		if (!isPressed) {
			if (gridsSpatial == null) {
				gridsSpatial = new GridsSpatial(app.getAssetManager());
				app.getViewPort().attachScene(gridsSpatial);
			} else {
				if (gridsSpatial.getCullHint() == CullHint.Never) {
					gridsSpatial.setCullHint(CullHint.Always);
				} else {
					gridsSpatial.setCullHint(CullHint.Never);
				}
			}
		}
	}
	
	private void toggleMouseLock(String name, boolean isPressed, float tpf) {
		if (!isPressed) {
			boolean mouseLocked = !app.getInputManager().isCursorVisible();
			
			app.getInputManager().setCursorVisible(mouseLocked);
			((SimpleApplication)app).getFlyByCamera().setEnabled(!mouseLocked);
		}
	}
	
	private void toggleWireframeMode(String name, boolean isPressed, float tpf) {
		if (!isPressed) {
			for (Block block : cosmos.getBlocks()) {
				if (!Block.isNullBlock(block)) {
					Material material = resourceManager.getMaterial(block);
					
					if (material != null) {
						RenderState renderState = material.getAdditionalRenderState();
						renderState.setWireframe(!renderState.isWireframe());
					}
				}
			}
		}
	}
}
