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

package org.quadracoatl.states;

import java.util.HashMap;
import java.util.Map;

import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.cosmos.Cosmos;

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

public class GameInputState extends AbstractAppState {
	private Map<String, ActionListener> actions = new HashMap<>();
	private Application app = null;
	private Cosmos cosmos = null;
	
	public GameInputState(Cosmos cosmos) {
		super();
		
		this.cosmos = cosmos;
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		
		removeAction("wireframe-mode");
		
		this.app = null;
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
	}
	
	private void attachAction(String name, Trigger trigger, ActionListener listener) {
		app.getInputManager().addMapping(name, trigger);
		app.getInputManager().addListener(listener, name);
	}
	
	private void removeAction(String name) {
		app.getInputManager().deleteMapping(name);
		app.getInputManager().removeListener(actions.get(name));
		
		actions.remove(name);
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
					Material material = (Material)block.getAdditionalState().get("material");
					RenderState renderState = material.getAdditionalRenderState();
					renderState.setWireframe(!renderState.isWireframe());
				}
			}
		}
	}
}
