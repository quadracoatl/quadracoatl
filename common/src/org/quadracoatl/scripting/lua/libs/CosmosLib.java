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

package org.quadracoatl.scripting.lua.libs;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.chunk.ChunkDataProvider;
import org.quadracoatl.framework.cosmos.Cosmos;
import org.quadracoatl.framework.realm.Realm;
import org.quadracoatl.framework.realm.Sky;
import org.quadracoatl.framework.resources.textures.CubeTexture;
import org.quadracoatl.scripting.lua.LuaUtil;
import org.quadracoatl.utils.Vector3d;

public class CosmosLib extends TwoArgFunction {
	private Cosmos cosmos = null;
	
	public CosmosLib(Cosmos cosmos) {
		super();
		
		this.cosmos = cosmos;
	}
	
	@Override
	public LuaValue call(LuaValue value, LuaValue env) {
		LuaTable cosmos = new LuaTable();
		cosmos.set("addSky", new addSky());
		cosmos.set("getBlockId", new getBlockId());
		cosmos.set("getBlockName", new getBlockName());
		cosmos.set("onChunkCreation", new onChunkCreation());
		cosmos.set("registerBlock", new registerBlock());
		cosmos.set("registerRealm", new registerRealm());
		
		LuaValue engine = env.get("engine");
		engine.set("cosmos", cosmos);
		
		return cosmos;
	}
	
	private final String asString(LuaValue luaValue) {
		return asString(luaValue, null);
	}
	
	private final String asString(LuaValue luaValue, String defaultValue) {
		if (luaValue == null) {
			return defaultValue;
		}
		
		return luaValue.tojstring();
	}
	
	private final CubeTexture createTexture(LuaValue texture) {
		if (!texture.isnil()) {
			if (texture.isstring()) {
				return new CubeTexture(texture.tojstring());
			} else if (texture.istable()) {
				if (!texture.get("top").isnil()) {
					return new CubeTexture(
							texture.get("top").tojstring(),
							texture.get("bottom").tojstring(),
							texture.get("front").tojstring(),
							texture.get("back").tojstring(),
							texture.get("left").tojstring(),
							texture.get("right").tojstring());
				} else {
					return new CubeTexture(
							texture.get(1).tojstring(),
							texture.get(2).tojstring(),
							texture.get(3).tojstring(),
							texture.get(4).tojstring(),
							texture.get(5).tojstring(),
							texture.get(6).tojstring());
				}
			}
		}
		
		return null;
	}
	
	private final class addSky extends TwoArgFunction {
		
		@Override
		public LuaValue call(LuaValue realmName, LuaValue skyDefinition) {
			String name = asString(skyDefinition.get("name"));
			
			Sky sky = new Sky(name);
			sky.setTexture(createTexture(skyDefinition.get("texture")));
			
			cosmos.getRealm(realmName.tojstring()).addSky(sky);
			
			return null;
		}
		
	}
	
	private final class getBlockId extends OneArgFunction {
		
		@Override
		public LuaValue call(LuaValue input) {
			if (input.isint()) {
				int id = input.toint();
				
				if (id >= 0 && id < cosmos.getBlocks().size()) {
					return LuaValue.valueOf(id);
				} else {
					return LuaValue.ZERO;
				}
			} else if (input.isstring()) {
				String name = input.tojstring();
				
				Block block = cosmos.getBlock(name);
				
				if (block != null) {
					return LuaString.valueOf(block.getId());
				} else {
					return LuaValue.ZERO;
				}
			}
			
			return LuaValue.ZERO;
		}
		
	}
	
	private final class getBlockName extends OneArgFunction {
		
		@Override
		public LuaValue call(LuaValue input) {
			if (input.isint()) {
				int id = input.toint();
				
				Block block = cosmos.getBlock(id);
				
				if (block != null) {
					return LuaValue.valueOf(block.getId());
				} else {
					return LuaValue.NIL;
				}
			} else if (input.isstring()) {
				String name = input.tojstring();
				
				Block block = cosmos.getBlock(name);
				
				if (block != null) {
					return LuaValue.valueOf(block.getId());
				} else {
					return LuaValue.NIL;
				}
			}
			
			return LuaValue.NIL;
		}
		
	}
	
	private final class onChunkCreation extends TwoArgFunction {
		
		@Override
		public LuaValue call(LuaValue realmName, LuaValue handler) {
			cosmos.getRealm(realmName.tojstring()).addChunkProvider(new ChunkDataProvider() {
				
				@Override
				public boolean provideChunkData(Realm realm, Chunk chunk) {
					Vector3d chunkLocation = realm.getChunkStart(
							chunk.getIndexX(),
							chunk.getIndexY(),
							chunk.getIndexZ());
					
					handler.invoke(new LuaValue[] {
							CoerceJavaToLua.coerce(realm),
							LuaValue.valueOf(chunkLocation.x),
							LuaValue.valueOf(chunkLocation.y),
							LuaValue.valueOf(chunkLocation.z),
							LuaValue.valueOf(chunkLocation.x + realm.getChunkWidth() * realm.getBlockWidth() - 1),
							LuaValue.valueOf(chunkLocation.y + realm.getChunkHeight() * realm.getBlockHeight() - 1),
							LuaValue.valueOf(chunkLocation.z + realm.getChunkDepth() * realm.getBlockDepth() - 1)
					});
					
					return false;
				}
			});
			return null;
		}
		
	}
	
	private final class registerBlock extends OneArgFunction {
		
		@Override
		public LuaValue call(LuaValue blockDefinition) {
			int id = cosmos.getBlocks().size();
			String name = asString(blockDefinition.get("name"));
			
			Block block = new Block(id, name);
			block.setTexture(createTexture(blockDefinition.get("texture")));
			
			cosmos.registerBlock(block);
			
			return LuaValue.valueOf(id);
		}
		
	}
	
	private final class registerRealm extends OneArgFunction {
		
		@Override
		public LuaValue call(LuaValue realmDefinition) {
			Realm realm = new Realm(
					LuaUtil.asString(realmDefinition, "name", "default"),
					LuaUtil.asInt(realmDefinition, "chunkWidth", 64),
					LuaUtil.asInt(realmDefinition, "chunkHeight", 64),
					LuaUtil.asInt(realmDefinition, "chunkWidth", 64),
					LuaUtil.asDouble(realmDefinition, "blockWidth", 1.0d),
					LuaUtil.asDouble(realmDefinition, "blockHeight", 1.0d),
					LuaUtil.asDouble(realmDefinition, "blockWidth", 1.0d));
			
			LuaValue skies = realmDefinition.get("skies");
			
			if (!skies.isnil()) {
				if (skies instanceof LuaTable) {
					for (LuaValue skyNameValue : ((LuaTable)skies).keys()) {
						Sky sky = new Sky(skyNameValue.tojstring());
						sky.setTexture(createTexture(skies.get(skyNameValue)));
						
						realm.addSky(sky);
					}
				}
			}
			
			cosmos.registerRealm(realm);
			
			return null;
		}
		
	}
}
