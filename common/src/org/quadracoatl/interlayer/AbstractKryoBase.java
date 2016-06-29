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

package org.quadracoatl.interlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quadracoatl.framework.block.Block;
import org.quadracoatl.framework.chunk.Chunk;
import org.quadracoatl.framework.chunk.SpawnBehavior;
import org.quadracoatl.framework.cosmos.Cosmos;
import org.quadracoatl.framework.realm.Realm;
import org.quadracoatl.framework.realm.Sky;
import org.quadracoatl.framework.resources.Resource;
import org.quadracoatl.framework.resources.ResourceType;
import org.quadracoatl.framework.resources.textures.CubeTexture;
import org.quadracoatl.interlayer.serializers.TransferrableFieldSerializer;
import org.quadracoatl.utils.Vector3i;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;

public abstract class AbstractKryoBase {
	protected static final int DEFAULT_BUFFER_SIZE = (int)Math.pow(2, 21);
	
	protected AbstractKryoBase() {
		super();
	}
	
	protected void registerClass(Kryo kryo, Class<?> type) {
		kryo.register(type, new TransferrableFieldSerializer<>(kryo, type));
	}
	
	protected void registerClasses(Kryo kryo) {
		ObjectSpace.registerClasses(kryo);
		
		kryo.register(byte[].class);
		kryo.register(int[].class);
		kryo.register(int[][].class);
		kryo.register(int[][][].class);
		kryo.register(ArrayList.class);
		kryo.register(HashMap.class);
		kryo.register(List.class);
		kryo.register(Map.class);
		
		kryo.register(Vector3i.class);
		
		kryo.register(Interlayer.class);
		
		kryo.register(ResourceType.class);
		kryo.register(SpawnBehavior.class);
		
		registerClass(kryo, Block.class);
		registerClass(kryo, Chunk.class);
		registerClass(kryo, Cosmos.class);
		registerClass(kryo, CubeTexture.class);
		registerClass(kryo, Realm.class);
		registerClass(kryo, Resource.class);
		registerClass(kryo, Sky.class);
	}
}
