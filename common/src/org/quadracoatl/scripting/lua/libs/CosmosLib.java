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

import java.util.HashMap;
import java.util.Map;

import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.block.BlockType;
import org.quadracoatl.framework.block.type_parameters.DisplacedCubeParameters;
import org.quadracoatl.framework.common.MathUtil;
import org.quadracoatl.framework.common.regions.Region3d;
import org.quadracoatl.framework.common.vectors.Vector3d;
import org.quadracoatl.framework.cosmos.Cosmos;
import org.quadracoatl.framework.realm.Realm;
import org.quadracoatl.framework.resources.colors.ColorBlendMode;
import org.quadracoatl.framework.resources.lights.LightSourceType;
import org.quadracoatl.framework.resources.textures.TextureType;
import org.quadracoatl.scripting.lua.LuaOneWay;
import org.quadracoatl.scripting.lua.LuaUtil;
import org.quadracoatl.scripting.lua.wrappers.LuaTimeProvider;
import org.quadracoatl.scripting.lua.wrappers.RealmWrapper;

public class CosmosLib extends TwoArgFunction {
	private Cosmos cosmos = null;
	private Map<String, RealmWrapper> realmNameToRealmWrapper = new HashMap<>();
	
	public CosmosLib(Cosmos cosmos) {
		super();
		
		this.cosmos = cosmos;
	}
	
	@Override
	public LuaValue call(LuaValue value, LuaValue env) {
		LuaUtil.loadEnum(env, BlockType.class);
		LuaUtil.loadEnum(env, ColorBlendMode.class);
		LuaUtil.loadEnum(env, LightSourceType.class);
		LuaUtil.loadEnum(env, TextureType.class);
		
		LuaTable cosmos = new LuaTable();
		cosmos.set("getBlockId", new getBlockId());
		cosmos.set("getBlockName", new getBlockName());
		cosmos.set("getRealm", new getRealm());
		cosmos.set("getSeed", new getSeed());
		cosmos.set("registerBlock", new registerBlock());
		cosmos.set("registerRealm", new registerRealm());
		cosmos.set("seed", new seed());
		
		env.set("cosmos", cosmos);
		
		return cosmos;
	}
	
	private final Region3d asRegion3d(LuaValue parentValue, String name) {
		if (parentValue != null && !parentValue.isnil()) {
			LuaValue regionValue = parentValue.get(name);
			
			if (regionValue != null && !regionValue.isnil()) {
				LuaValue start = regionValue.get("start");
				LuaValue end = regionValue.get("end");
				
				if (start == null || start.isnil()) {
					start = regionValue.get(0);
				}
				
				if (end == null || start.isnil()) {
					end = regionValue.get(1);
				}
				
				return new Region3d(
						LuaUtil.asDouble(start, "x", LuaUtil.asDouble(start, "0", 0.0d)),
						LuaUtil.asDouble(start, "y", LuaUtil.asDouble(start, "1", 0.0d)),
						LuaUtil.asDouble(start, "z", LuaUtil.asDouble(start, "2", 0.0d)),
						LuaUtil.asDouble(end, "x", LuaUtil.asDouble(end, "0", 0.0d)),
						LuaUtil.asDouble(end, "y", LuaUtil.asDouble(end, "1", 0.0d)),
						LuaUtil.asDouble(end, "z", LuaUtil.asDouble(end, "2", 0.0d)));
			}
		}
		
		return null;
	}
	
	private final Object createTypeParameters(LuaValue blockDefinition, BlockType blockType) {
		if (blockType == BlockType.DISPLACED_CUBE) {
			LuaValue luaTypeParameters = blockDefinition.get("typeParameters");
			
			if (!luaTypeParameters.isnil()) {
				double displacementStrength = LuaUtil.asDouble(luaTypeParameters, "displacementStrength", 0.0d);
				double displacementStrengthX = LuaUtil.asDouble(luaTypeParameters, "displacementStrengthX", displacementStrength);
				double displacementStrengthY = LuaUtil.asDouble(luaTypeParameters, "displacementStrengthY", displacementStrengthX);
				double displacementStrengthZ = LuaUtil.asDouble(luaTypeParameters, "displacementStrengthZ", displacementStrengthY);
				
				return new DisplacedCubeParameters(new Vector3d(
						displacementStrengthX,
						displacementStrengthY,
						displacementStrengthZ));
			} else {
				return new DisplacedCubeParameters(new Vector3d(0.5d, 0.5d, 0.5d));
			}
		}
		
		return null;
	}
	
	private final class getBlockId extends OneArgFunction {
		public getBlockId() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue input) {
			if (input.isint()) {
				int id = input.toint();
				
				if (id >= 0 && id < cosmos.getBlocks().size()) {
					return LuaValue.valueOf(id);
				} else {
					return LuaValue.valueOf(Block.NONEXISTENT_BLOCK.getId());
				}
			} else if (input.isstring()) {
				String name = input.tojstring();
				
				Block block = cosmos.getBlock(name);
				
				if (block != null) {
					return LuaString.valueOf(block.getId());
				} else {
					return LuaValue.valueOf(Block.NONEXISTENT_BLOCK.getId());
				}
			}
			
			return LuaValue.valueOf(Block.NONEXISTENT_BLOCK.getId());
		}
		
	}
	
	private final class getBlockName extends OneArgFunction {
		public getBlockName() {
			super();
		}
		
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
	
	private final class getRealm extends OneArgFunction {
		public getRealm() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue realmName) {
			if (realmName.isstring()) {
				RealmWrapper realmWrapper = realmNameToRealmWrapper.get(realmName.tojstring());
				
				if (realmWrapper == null) {
					Realm realm = cosmos.getRealm(name);
					
					if (realm == null) {
						return null;
					}
					
					realmWrapper = new RealmWrapper(realm);
					realmNameToRealmWrapper.put(realm.getName(), realmWrapper);
				}
				
				return realmWrapper;
			}
			
			return null;
		}
	}
	
	private final class getSeed extends ZeroArgFunction {
		public getSeed() {
			super();
		}
		
		@Override
		public LuaValue call() {
			return LuaNumber.valueOf(cosmos.getSeed());
		}
		
	}
	
	private final class registerBlock extends OneArgFunction {
		public registerBlock() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue blockDefinition) {
			int id = cosmos.getBlocks().size();
			String name = LuaUtil.asString(blockDefinition, "name", "block");
			BlockType blockType = LuaUtil.asEnum(blockDefinition, "type", BlockType.NONE, BlockType.class);
			Object typeParameters = createTypeParameters(blockDefinition, blockType);
			
			Block block = new Block(id, name, blockType);
			block.setColor(LuaOneWay.createColor(blockDefinition.get("color")));
			block.setTexture(LuaOneWay.createTexture(blockDefinition));
			block.setTypeParameters(typeParameters);
			
			cosmos.registerBlock(block);
			
			return LuaValue.valueOf(id);
		}
		
	}
	
	private final class registerRealm extends OneArgFunction {
		public registerRealm() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue realmDefinition) {
			Realm realm = new Realm(
					LuaUtil.asString(realmDefinition, "name", "default"),
					LuaUtil.asInt(realmDefinition, "chunkWidth", 64),
					LuaUtil.asInt(realmDefinition, "chunkHeight", 64),
					LuaUtil.asInt(realmDefinition, "chunkWidth", 64),
					LuaUtil.asDouble(realmDefinition, "blockWidth", 1.0d),
					LuaUtil.asDouble(realmDefinition, "blockHeight", 1.0d),
					LuaUtil.asDouble(realmDefinition, "blockDepth", 1.0d));
			
			realm.setBounds(asRegion3d(realmDefinition, "bounds"));
			
			LuaValue timeProviderFunction = realmDefinition.get("onTimeUpdate");
			if (timeProviderFunction.isfunction()) {
				realm.setTimeProvider(new LuaTimeProvider(timeProviderFunction));
			}
			
			cosmos.registerRealm(realm);
			
			RealmWrapper realmWrapper = new RealmWrapper(realm);
			realmNameToRealmWrapper.put(realm.getName(), realmWrapper);
			
			return realmWrapper;
		}
	}
	
	private final class seed extends OneArgFunction {
		public seed() {
			super();
		}
		
		@Override
		public LuaValue call(LuaValue seed) {
			return LuaNumber.valueOf(MathUtil.cantorPairing(cosmos.getSeed(), LuaUtil.asSeed(seed)));
		}
	}
}
