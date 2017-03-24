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

package org.quadracoatl.interlayer.kryonet.serializerfactories;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.quadracoatl.framework.logging.Logger;
import org.quadracoatl.framework.logging.LoggerFactory;
import org.quadracoatl.interlayer.Transient;
import org.quadracoatl.interlayer.kryonet.serializers.DummySerializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.factories.SerializerFactory;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

public class ConfiguringSerializerFactory implements SerializerFactory {
	private final Logger LOGGER = LoggerFactory.getLogger(this);
	
	public ConfiguringSerializerFactory() {
		super();
	}
	
	@Override
	public Serializer<?> makeSerializer(Kryo kryo, Class<?> type) {
		if (type.isAnnotationPresent(Transient.class)) {
			LOGGER.info(type.getName(), " -> DummySerializer");
			return new DummySerializer();
		}
		
		LOGGER.info(type.getName(), " -> FieldSerializer");
		checkDefaultConstructor(type);
		
		FieldSerializer<?> fieldSerializer = new FieldSerializer<>(kryo, type);
		fieldSerializer.setAcceptsNull(true);
		fieldSerializer.setCopyTransient(false);
		fieldSerializer.setFieldsCanBeNull(true);
		
		return fieldSerializer;
	}
	
	private void checkDefaultConstructor(Class<?> type) {
		if (type.isInterface()) {
			return;
		}
		
		// We must check if the class is declared final or not, because for some
		// reason or another the JVM crashes hard when KryoNet tries to send
		// final class.
		//
		// Took me four days to figure out.
		if (Modifier.isFinal(type.getModifiers())) {
			LOGGER.fatal(type.getName(), " is declared final, but it is not possible to send final classes over the wire because the JVM crashes when it is tried.");
			
			throw new IllegalArgumentException(type + " is declared final and final classes can not be send.");
		}
		
		for (Constructor<?> constructor : type.getDeclaredConstructors()) {
			if (constructor.getParameterCount() == 0) {
				return;
			}
		}
		
		LOGGER.fatal("No default constructor for ", type.getName(), " found.");
		
		throw new IllegalArgumentException(type.getName() + " does not have a default constructor and can not be serialized.");
	}
}
