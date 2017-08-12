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

package org.quadracoatl.interlayer.kryonet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.block.BlockType;
import org.quadracoatl.framework.block.Side;
import org.quadracoatl.framework.block.type_parameters.DisplacedCubeParameters;
import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.common.regions.Region2d;
import org.quadracoatl.framework.common.regions.Region2i;
import org.quadracoatl.framework.common.regions.Region3d;
import org.quadracoatl.framework.common.regions.Region3i;
import org.quadracoatl.framework.common.vectors.Vector2d;
import org.quadracoatl.framework.common.vectors.Vector2i;
import org.quadracoatl.framework.common.vectors.Vector3d;
import org.quadracoatl.framework.common.vectors.Vector3i;
import org.quadracoatl.framework.cosmos.Cosmos;
import org.quadracoatl.framework.entities.AbstractComponent;
import org.quadracoatl.framework.entities.Component;
import org.quadracoatl.framework.entities.Entity;
import org.quadracoatl.framework.entities.changes.ChangeType;
import org.quadracoatl.framework.entities.changes.ComponentChange;
import org.quadracoatl.framework.entities.changes.EntityChange;
import org.quadracoatl.framework.entities.changes.EntityChangeBatch;
import org.quadracoatl.framework.entities.components.ColorComponent;
import org.quadracoatl.framework.entities.components.LightSourceComponent;
import org.quadracoatl.framework.entities.components.LocationComponent;
import org.quadracoatl.framework.entities.components.RotationComponent;
import org.quadracoatl.framework.entities.components.SizeComponent;
import org.quadracoatl.framework.entities.components.TextureComponent;
import org.quadracoatl.framework.entities.components.UpdateComponent;
import org.quadracoatl.framework.realm.Realm;
import org.quadracoatl.framework.realm.components.CelestialObjectComponent;
import org.quadracoatl.framework.realm.components.SkyComponent;
import org.quadracoatl.framework.resources.Resource;
import org.quadracoatl.framework.resources.ResourceType;
import org.quadracoatl.framework.resources.colors.Color;
import org.quadracoatl.framework.resources.colors.ColorBlendMode;
import org.quadracoatl.framework.resources.lights.LightSource;
import org.quadracoatl.framework.resources.lights.LightSourceType;
import org.quadracoatl.framework.resources.textures.Texture;
import org.quadracoatl.framework.resources.textures.TextureType;
import org.quadracoatl.interlayer.kryonet.serializerfactories.ConfiguringSerializerFactory;
import org.quadracoatl.interlayer.kryonet.serializers.UnmodifiableCollectionsSerializer;
import org.quadracoatl.interlayer.parts.CosmosPart;
import org.quadracoatl.interlayer.parts.EntityEventReceiver;
import org.quadracoatl.interlayer.parts.direct.DirectCosmosPart;
import org.quadracoatl.interlayer.parts.direct.DirectEntityEventReceiver;
import org.quadracoatl.scripting.lua.components.LuaUpdateComponent;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsEmptyListSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsEmptyMapSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsEmptySetSerializer;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;

public abstract class AbstractKryoBase {
	/** The default buffer size for the transfers, 4 megabyte. */
	protected static final int DEFAULT_BUFFER_SIZE = (int)Math.pow(2, 22);
	
	protected AbstractKryoBase() {
		super();
	}
	
	protected void registerArrays(Kryo kryo) {
		kryo.register(byte[].class);
		kryo.register(short[].class);
		kryo.register(int[].class);
		kryo.register(long[].class);
		kryo.register(float[].class);
		kryo.register(double[].class);
		
		kryo.register(byte[][].class);
		kryo.register(short[][].class);
		kryo.register(int[][].class);
		kryo.register(long[][].class);
		kryo.register(float[][].class);
		kryo.register(double[][].class);
		
		kryo.register(byte[][][].class);
		kryo.register(short[][][].class);
		kryo.register(int[][][].class);
		kryo.register(long[][][].class);
		kryo.register(float[][][].class);
		kryo.register(double[][][].class);
		
		kryo.register(String[].class);
		kryo.register(String[][].class);
		kryo.register(String[][][].class);
	}
	
	protected void registerCollections(Kryo kryo) {
		kryo.register(ArrayList.class);
		kryo.register(HashMap.class);
		kryo.register(HashSet.class);
		
		kryo.register(Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer());
		kryo.register(Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer());
		kryo.register(Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer());
		
		UnmodifiableCollectionsSerializer.registerSerializers(kryo);
	}
	
	protected void registerFrameworkClasses(Kryo kryo) {
		kryo.register(Block.class);
		kryo.register(BlockType.class);
		kryo.register(Side.class);
		
		kryo.register(DisplacedCubeParameters.class);
		
		kryo.register(Chunk.class);
		
		kryo.register(Region2d.class);
		kryo.register(Region2i.class);
		kryo.register(Region3d.class);
		kryo.register(Region3i.class);
		kryo.register(Vector2d.class);
		kryo.register(Vector2i.class);
		kryo.register(Vector3d.class);
		kryo.register(Vector3i.class);
		
		kryo.register(Cosmos.class);
		
		kryo.register(Component.class);
		kryo.register(Entity.class);
		
		kryo.register(ChangeType.class);
		kryo.register(ComponentChange.class);
		kryo.register(EntityChange.class);
		kryo.register(EntityChangeBatch.class);
		
		kryo.register(AbstractComponent.class);
		kryo.register(ColorComponent.class);
		kryo.register(LightSourceComponent.class);
		kryo.register(LocationComponent.class);
		kryo.register(RotationComponent.class);
		kryo.register(SizeComponent.class);
		kryo.register(TextureComponent.class);
		kryo.register(UpdateComponent.class);
		
		kryo.register(LuaUpdateComponent.class);
		
		kryo.register(Realm.class);
		
		kryo.register(CelestialObjectComponent.class);
		kryo.register(SkyComponent.class);
		
		kryo.register(Resource.class);
		kryo.register(ResourceType.class);
		
		kryo.register(Color.class);
		kryo.register(ColorBlendMode.class);
		kryo.register(LightSource.class);
		kryo.register(LightSourceType.class);
		
		kryo.register(Texture.class);
		kryo.register(TextureType.class);
		
		kryo.register(CosmosPart.class);
		kryo.register(EntityEventReceiver.class);
		
		kryo.register(DirectCosmosPart.class);
		kryo.register(DirectEntityEventReceiver.class);
	}
	
	protected void registerJvmClasses(Kryo kryo) {
		kryo.register(Class.class);
	}
	
	protected void setup(Kryo kryo) {
		kryo.setRegistrationRequired(true);
		
		ObjectSpace.registerClasses(kryo);
		
		kryo.setDefaultSerializer(new ConfiguringSerializerFactory());
		
		registerArrays(kryo);
		registerCollections(kryo);
		registerJvmClasses(kryo);
		
		registerFrameworkClasses(kryo);
	}
}
