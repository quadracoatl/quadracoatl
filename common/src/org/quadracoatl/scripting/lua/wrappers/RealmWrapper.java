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

package org.quadracoatl.scripting.lua.wrappers;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.chunk.ChunkDataProvider;
import org.quadracoatl.framework.common.vectors.Vector3d;
import org.quadracoatl.framework.entities.Entity;
import org.quadracoatl.framework.entities.components.ColorComponent;
import org.quadracoatl.framework.entities.components.LightSourceComponent;
import org.quadracoatl.framework.entities.components.RotationComponent;
import org.quadracoatl.framework.entities.components.SizeComponent;
import org.quadracoatl.framework.entities.components.TextureComponent;
import org.quadracoatl.framework.realm.Realm;
import org.quadracoatl.framework.realm.components.CelestialObjectComponent;
import org.quadracoatl.framework.realm.components.SkyComponent;
import org.quadracoatl.framework.resources.colors.Color;
import org.quadracoatl.framework.resources.colors.ColorBlendMode;
import org.quadracoatl.framework.resources.lights.LightSource;
import org.quadracoatl.scripting.lua.LuaOneWay;
import org.quadracoatl.scripting.lua.LuaUtil;
import org.quadracoatl.scripting.lua.components.LuaUpdateComponent;
import org.quadracoatl.scripting.lua.wrappers.entities.EntityWrapper;

public class RealmWrapper extends LuaTable {
	private Realm realm = null;
	
	public RealmWrapper(Realm realm) {
		super();
		
		this.realm = realm;
		
		set("addCelestialObject", new addCelestialObject());
		set("addLightSource", new addLightSource());
		set("addSky", new addSky());
		set("onChunkCreation", new onChunkCreation());
	}
	
	private final class addCelestialObject extends TwoArgFunction {
		public addCelestialObject() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue dummy, LuaValue celestialObjectDefinition) {
			String name = LuaUtil.asString(celestialObjectDefinition, "name", "object");
			Color color = LuaOneWay.createColor(celestialObjectDefinition.get("color"));
			ColorBlendMode colorBlendMode = LuaUtil.asEnum(celestialObjectDefinition, "colorBlendMode", null, ColorBlendMode.class);
			double order = LuaUtil.asDouble(celestialObjectDefinition, "order", 1.0d);
			
			LuaValue updateFunction = celestialObjectDefinition.get("onUpdate");
			LuaUpdateComponent updateComponent = null;
			if (updateFunction.isfunction()) {
				updateComponent = new LuaUpdateComponent(updateFunction);
			}
			
			LightSource lightSource = LuaOneWay.createLightSource(celestialObjectDefinition);
			LightSourceComponent lightSourceComponent = null;
			if (lightSource != null) {
				lightSourceComponent = new LightSourceComponent(lightSource);
			}
			
			Entity entity = realm.getEntityManager().register(
					new ColorComponent(color, colorBlendMode),
					new RotationComponent(LuaOneWay.createVector3d(celestialObjectDefinition.get("rotation"))),
					new SizeComponent(LuaOneWay.createVector3d(celestialObjectDefinition.get("size"))),
					new CelestialObjectComponent(name, order),
					new TextureComponent(LuaOneWay.createTexture(celestialObjectDefinition)),
					updateComponent,
					lightSourceComponent);
			
			EntityWrapper entityWrapper = null;
			
			if (updateComponent != null) {
				entityWrapper = updateComponent.getEntityWrapper();
			} else {
				entityWrapper = new EntityWrapper(entity);
			}
			
			LuaValue creationFunction = celestialObjectDefinition.get("onCreation");
			if (creationFunction.isfunction()) {
				creationFunction.call(entityWrapper);
			}
			
			return entityWrapper;
		}
	}
	
	private final class addLightSource extends TwoArgFunction {
		public addLightSource() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue dummy, LuaValue lightDefinition) {
			LuaValue updateFunction = lightDefinition.get("onUpdate");
			LuaUpdateComponent updateComponent = null;
			if (updateFunction.isfunction()) {
				updateComponent = new LuaUpdateComponent(updateFunction);
			}
			
			LightSource lightSource = LuaOneWay.createLightSource(lightDefinition);
			LightSourceComponent lightSourceComponent = null;
			if (lightSource != null) {
				lightSourceComponent = new LightSourceComponent(lightSource);
			}
			
			Entity entity = realm.getEntityManager().register(
					new RotationComponent(LuaOneWay.createVector3d(lightDefinition.get("rotation"))),
					lightSourceComponent,
					updateComponent);
			
			EntityWrapper entityWrapper = null;
			
			if (updateComponent != null) {
				entityWrapper = updateComponent.getEntityWrapper();
			} else {
				entityWrapper = new EntityWrapper(entity);
			}
			
			LuaValue creationFunction = lightDefinition.get("onCreation");
			if (creationFunction.isfunction()) {
				creationFunction.call(entityWrapper);
			}
			
			return entityWrapper;
		}
	}
	
	private final class addSky extends TwoArgFunction {
		public addSky() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue dummy, LuaValue skyDefinition) {
			String name = LuaUtil.asString(skyDefinition, "name", "sky");
			Color color = LuaOneWay.createColor(skyDefinition.get("color"));
			ColorBlendMode colorBlendMode = LuaUtil.asEnum(skyDefinition, "colorBlendMode", null, ColorBlendMode.class);
			double order = LuaUtil.asDouble(skyDefinition, "order", 1.0d);
			
			LuaValue updateFunction = skyDefinition.get("onUpdate");
			LuaUpdateComponent updateComponent = null;
			if (updateFunction.isfunction()) {
				updateComponent = new LuaUpdateComponent(updateFunction);
			}
			
			Entity entity = realm.getEntityManager().register(
					new ColorComponent(color, colorBlendMode),
					new RotationComponent(LuaOneWay.createVector3d(skyDefinition.get("rotation"))),
					new SizeComponent(LuaOneWay.createVector3d(skyDefinition.get("size"))),
					new SkyComponent(name, order),
					new TextureComponent(LuaOneWay.createTexture(skyDefinition)),
					updateComponent);
			
			EntityWrapper entityWrapper = null;
			
			if (updateComponent != null) {
				entityWrapper = updateComponent.getEntityWrapper();
			} else {
				entityWrapper = new EntityWrapper(entity);
			}
			
			LuaValue creationFunction = skyDefinition.get("onCreation");
			if (creationFunction.isfunction()) {
				creationFunction.call(entityWrapper);
			}
			
			return entityWrapper;
		}
	}
	
	private final class onChunkCreation extends TwoArgFunction {
		public onChunkCreation() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue dummy, LuaValue handler) {
			realm.addChunkProvider(new ChunkDataProvider() {
				@Override
				public boolean provideChunkData(Realm realm, Chunk chunk) {
					Vector3d chunkLocation = realm.getChunkStart(
							chunk.getIndexX(),
							chunk.getIndexY(),
							chunk.getIndexZ());
					
					handler.invoke(new LuaValue[] {
							CoerceJavaToLua.coerce(realm),
							CoerceJavaToLua.coerce(chunk),
							LuaValue.valueOf(chunkLocation.x),
							LuaValue.valueOf(chunkLocation.y),
							LuaValue.valueOf(chunkLocation.z),
							LuaValue.valueOf(chunkLocation.x + realm.getChunkWidth() * realm.getBlockWidth() - realm.getBlockWidth()),
							LuaValue.valueOf(chunkLocation.y + realm.getChunkHeight() * realm.getBlockHeight() - realm.getBlockHeight()),
							LuaValue.valueOf(chunkLocation.z + realm.getChunkDepth() * realm.getBlockDepth() - realm.getBlockDepth())
					});
					
					return false;
				}
			});
			
			return null;
		}
	}
}
