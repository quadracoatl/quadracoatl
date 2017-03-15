
package org.quadracoatl.interlayer.kryonet.utils;

import java.util.ArrayList;
import java.util.List;

public final class InterfaceUtil {
	private InterfaceUtil() {
		
	}
	
	public static final Class<?>[] getAllInterfaces(Class<?> clazz) {
		List<Class<?>> allInterfaces = new ArrayList<>();
		Class<?> superClass = clazz;
		
		while (superClass != null) {
			Class<?>[] interfaces = superClass.getInterfaces();
			
			if (interfaces != null
					&& interfaces.length > 0) {
				for (int index = 0; index < interfaces.length; index++) {
					allInterfaces.add(interfaces[index]);
				}
			}
			
			superClass = superClass.getSuperclass();
		}
		
		return allInterfaces.toArray(new Class<?>[allInterfaces.size()]);
	}
}
