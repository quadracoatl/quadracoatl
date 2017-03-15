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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.quadracoatl.environments.ServerEnvironment;
import org.quadracoatl.framework.common.Client;
import org.quadracoatl.interlayer.Interlayer;
import org.quadracoatl.interlayer.InterlayerClient;
import org.quadracoatl.interlayer.InterlayerException;
import org.quadracoatl.interlayer.kryonet.utils.InterfaceUtil;

import com.esotericsoftware.kryo.Kryo;

public class CloningInterlayer extends AbstractKryoBase implements Interlayer {
	private Client client = null;
	private Kryo kryo = null;
	private ServerEnvironment serverEnvironment = null;
	
	public CloningInterlayer(ServerEnvironment serverEnvironment) {
		super();
		
		this.serverEnvironment = serverEnvironment;
		
		kryo = new Kryo();
		setup(kryo);
		
		client = new Client();
		client.setInterlayerClient(new WrappingInterlayerClient(kryo));
		
		this.serverEnvironment.registerClient(client);
	}
	
	private static final Object wrapObject(Kryo kryo, Object object) {
		if (object == null) {
			return null;
		}
		
		return Proxy.newProxyInstance(
				object.getClass().getClassLoader(),
				InterfaceUtil.getAllInterfaces(object.getClass()),
				new CloningInvocationHandler(kryo, object));
	}
	
	@Override
	public <PART> PART getPart(int partId, Class<PART> partClass) {
		return client.getInterlayerClient().getPart(partId, partClass);
	}
	
	@Override
	public boolean isStarted() {
		return serverEnvironment.isStarted();
	}
	
	@Override
	public <PART> void putPart(int partId, PART part) {
		client.getInterlayerClient().putPart(partId, wrapObject(kryo, part));
	}
	
	@Override
	public void start() throws InterlayerException {
		serverEnvironment.start();
	}
	
	@Override
	public void stop() {
		serverEnvironment.stop();
	}
	
	private static final class CloningInvocationHandler implements InvocationHandler {
		private Kryo kryo = null;
		private Object wrappedObject = null;
		
		public CloningInvocationHandler(Kryo kryo, Object wrappedObject) {
			super();
			
			this.kryo = kryo;
			this.wrappedObject = wrappedObject;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object[] clonedArgs = null;
			
			synchronized (kryo) {
				if (args != null) {
					clonedArgs = new Object[args.length];
					for (int index = 0; index < args.length; index++) {
						clonedArgs[index] = kryo.copy(args[index]);
					}
				}
				
				return kryo.copy(method.invoke(wrappedObject, clonedArgs));
			}
		}
	}
	
	private static final class WrappingInterlayerClient extends InterlayerClient {
		private Kryo kryo = null;
		
		public WrappingInterlayerClient(Kryo kryo) {
			super();
			
			this.kryo = kryo;
		}
		
		@Override
		public <PART> void putPart(int partId, PART part) {
			super.putPart(partId, wrapObject(kryo, part));
		}
	}
}
