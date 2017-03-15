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

package org.quadracoatl.jmeclient.states.sub;

import java.util.ArrayList;
import java.util.List;

import org.quadracoatl.environments.ClientEnvironment;
import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.common.Vector3d;
import org.quadracoatl.framework.entities.Component;
import org.quadracoatl.framework.entities.EntityContainer;
import org.quadracoatl.framework.entities.changes.ChangeType;
import org.quadracoatl.framework.entities.changes.ComponentChange;
import org.quadracoatl.framework.entities.changes.EntityChange;
import org.quadracoatl.framework.entities.changes.EntityChangeBatch;
import org.quadracoatl.framework.entities.changes.EntityChangeProcessor;
import org.quadracoatl.framework.entities.components.LightSourceComponent;
import org.quadracoatl.framework.logging.Logger;
import org.quadracoatl.framework.logging.LoggerFactory;
import org.quadracoatl.framework.resources.colors.Color;
import org.quadracoatl.framework.resources.lights.LightSource;
import org.quadracoatl.framework.resources.lights.LightSourceType;
import org.quadracoatl.jmeclient.extensions.JmeResourceManager;
import org.quadracoatl.jmeclient.utils.ShaderUtil;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.shader.VarType;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

public class LightingState extends AbstractAppState implements EntityChangeProcessor {
	private ClientEnvironment clientEnvironment = null;
	private EntityChangeBatch currentEntityChangeBatch = EntityChangeBatch.EMPTY_BATCH;
	private TIntIntMap entityIdToIndex = new TIntIntHashMap();
	private Vector4f[] lightsColors = new Vector4f[ShaderUtil.LIGHTS_MAX_COUNT];
	private int lightsCount = 0;
	private Vector3f[] lightsDirections = new Vector3f[ShaderUtil.LIGHTS_MAX_COUNT];
	private int[] lightsTypes = new int[ShaderUtil.LIGHTS_MAX_COUNT];
	private final Logger LOGGER = LoggerFactory.getLogger(this);
	private List<Material> materials = new ArrayList<>();
	
	public LightingState(ClientEnvironment clientEnvironment) {
		super();
		
		this.clientEnvironment = clientEnvironment;
		
		for (int index = 0; index < ShaderUtil.LIGHTS_MAX_COUNT; index++) {
			lightsColors[index] = new Vector4f();
			lightsDirections[index] = new Vector3f();
			lightsTypes[index] = ShaderUtil.LIGHT_TYPE_INVALID;
		}
	}
	
	@Override
	public void cleanup() {
		LOGGER.debug("Cleanup...");
		
		lightsCount = 0;
		
		for (Material material : materials) {
			material.clearParam("LightsColors");
			material.clearParam("LightsCount");
			material.clearParam("LightsDirections");
			material.clearParam("LightsTypes");
		}
		
		materials.clear();
		
		super.cleanup();
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		LOGGER.debug("Initializing...");
		
		super.initialize(stateManager, app);
		
		JmeResourceManager resourceManager = (JmeResourceManager)clientEnvironment.getResourceCache();
		
		for (Block block : clientEnvironment.getCosmos().getBlocks()) {
			Material material = resourceManager.getMaterial(block);
			
			if (material != null) {
				materials.add(material);
			}
		}
		
		EntityContainer entityContainer = clientEnvironment.getCurrentRealm().getEntityManager();
		
		for (LightSourceComponent lightComponent : entityContainer.getComponents(LightSourceComponent.class)) {
			processAddOrChange(lightComponent);
		}
		
		updateMaterialParams();
	}
	
	@Override
	public void process(EntityChangeBatch entityChangeBatch) {
		currentEntityChangeBatch = entityChangeBatch;
	}
	
	@Override
	public void update(float tpf) {
		super.update(tpf);
		
		boolean changed = false;
		
		if (currentEntityChangeBatch != null && !currentEntityChangeBatch.isEmpty()) {
			for (EntityChange entityChange : currentEntityChangeBatch) {
				for (ComponentChange componentChange : entityChange.getComponentChanges()) {
					Component component = componentChange.getComponent();
					
					if (component instanceof LightSourceComponent) {
						if (componentChange.getType() == ChangeType.REMOVED) {
							processRemoval((LightSourceComponent)component);
						} else {
							processAddOrChange((LightSourceComponent)component);
						}
						
						changed = true;
					}
				}
			}
		}
		
		if (changed) {
			updateMaterialParams();
		}
	}
	
	private final void processAddOrChange(LightSourceComponent lightSourceComponent) {
		int entityId = lightSourceComponent.getEntity().getId();
		
		LOGGER.debug("Processing add or change: ", Integer.valueOf(entityId));
		
		int index = 0;
		
		if (entityIdToIndex.containsKey(entityId)) {
			index = entityIdToIndex.get(entityId);
		} else {
			index = lightsCount;
			lightsCount = lightsCount + 1;
			entityIdToIndex.put(entityId, index);
		}
		
		LightSource lightSource = lightSourceComponent.getLightSource();
		
		updateLightDirection(index, lightSource.getDirection());
		updateLightColor(index, lightSource.getColor());
		updateLightType(index, lightSource.getType());
	}
	
	private final void processRemoval(LightSourceComponent lightComponent) {
		int entityId = lightComponent.getEntity().getId();
		
		LOGGER.debug("Processing removal: ", Integer.valueOf(entityId));
		
		if (entityIdToIndex.containsKey(entityId)) {
			int removedIndex = entityIdToIndex.remove(entityId);
			
			for (int index = removedIndex; index < lightsCount - 1; index++) {
				int nextIndex = index + 1;
				
				lightsColors[index].set(lightsColors[nextIndex]);
				lightsDirections[index].set(lightsDirections[nextIndex]);
				lightsTypes[index] = lightsTypes[nextIndex];
			}
			
			lightsCount = lightsCount - 1;
			
			lightsTypes[lightsCount] = ShaderUtil.LIGHT_TYPE_INVALID;
		}
	}
	
	private final void updateLightColor(int index, Color color) {
		lightsColors[index].set(
				(float)color.red,
				(float)color.green,
				(float)color.blue,
				(float)color.alpha);
	}
	
	private final void updateLightDirection(int index, Vector3d direction) {
		lightsDirections[index].set(
				(float)direction.x,
				(float)direction.y,
				(float)direction.z);
	}
	
	private final void updateLightType(int index, LightSourceType type) {
		lightsTypes[index] = ShaderUtil.getLightSourceTypeAsInteger(type);
	}
	
	private final void updateMaterialParams() {
		for (Material material : materials) {
			material.setParam("LightsColors", VarType.Vector4Array, lightsColors);
			material.setParam("LightsCount", VarType.Int, Integer.valueOf(lightsCount));
			material.setParam("LightsDirections", VarType.Vector3Array, lightsDirections);
			material.setParam("LightsTypes", VarType.IntArray, lightsTypes);
		}
	}
}
