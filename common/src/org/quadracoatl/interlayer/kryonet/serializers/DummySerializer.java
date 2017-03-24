
package org.quadracoatl.interlayer.kryonet.serializers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.quadracoatl.interlayer.kryonet.utils.InterfaceUtil;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class DummySerializer extends Serializer<Object> {
	public DummySerializer() {
		super(true, false);
	}
	
	@Override
	public Object copy(Kryo kryo, Object original) {
		if (original == null) {
			return null;
		}
		
		return createDummy(original.getClass());
	}
	
	@Override
	public Object read(Kryo kryo, Input input, Class<Object> type) {
		return createDummy(type);
	}
	
	@Override
	public void write(Kryo kryo, Output output, Object object) {
		
	}
	
	protected Object createDummy(Class<? extends Object> type) {
		return Proxy.newProxyInstance(
				type.getClassLoader(),
				InterfaceUtil.getAllInterfaces(type),
				DummyInvocationHandler.INSTANCE);
	}
	
	protected static final class DummyInvocationHandler implements InvocationHandler {
		public static final DummyInvocationHandler INSTANCE = new DummyInvocationHandler();
		
		public DummyInvocationHandler() {
			super();
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			switch (method.getName()) {
				case "equals":
					return Boolean.FALSE;
				
				case "hashCode":
					return Integer.valueOf(hashCode());
			}
			
			return null;
		}
	}
}
