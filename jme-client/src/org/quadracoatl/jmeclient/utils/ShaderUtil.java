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

package org.quadracoatl.jmeclient.utils;

import org.quadracoatl.framework.resources.colors.ColorBlendMode;
import org.quadracoatl.framework.resources.lights.LightSourceType;

public final class ShaderUtil {
	public static final int COLOR_BLEND_MODE_ADDITION = 0;
	public static final int COLOR_BLEND_MODE_DARKEN_ONLY = 1;
	public static final int COLOR_BLEND_MODE_DIFFERENCE = 2;
	public static final int COLOR_BLEND_MODE_DIVIDE = 3;
	public static final int COLOR_BLEND_MODE_INVALID = -1;
	public static final int COLOR_BLEND_MODE_LIGHTEN_ONLY = 4;
	public static final int COLOR_BLEND_MODE_MULTIPLY = 5;
	public static final int COLOR_BLEND_MODE_NORMAL = 6;
	public static final int COLOR_BLEND_MODE_OVERLAY = 7;
	public static final int COLOR_BLEND_MODE_SCREEN = 8;
	public static final int COLOR_BLEND_MODE_SUBTRACT = 9;
	public static final int LIGHT_TYPE_AMBIENT = 0;
	public static final int LIGHT_TYPE_DIRECTIONAL = 1;
	public static final int LIGHT_TYPE_INVALID = -1;
	public static final int LIGHTS_MAX_COUNT = 16;
	
	private ShaderUtil() {
	}
	
	public static final int getColorBlendModeAsInteger(ColorBlendMode colorMode) {
		if (colorMode == null) {
			return COLOR_BLEND_MODE_INVALID;
		}
		
		switch (colorMode) {
			case ADDITION:
				return COLOR_BLEND_MODE_ADDITION;
			
			case DARKEN_ONLY:
				return COLOR_BLEND_MODE_DARKEN_ONLY;
			
			case DIFFERENCE:
				return COLOR_BLEND_MODE_DIFFERENCE;
			
			case DIVIDE:
				return COLOR_BLEND_MODE_DIVIDE;
			
			case LIGHTEN_ONLY:
				return COLOR_BLEND_MODE_LIGHTEN_ONLY;
			
			case MULTIPLY:
				return COLOR_BLEND_MODE_MULTIPLY;
			
			case NORMAL:
				return COLOR_BLEND_MODE_NORMAL;
			
			case OVERLAY:
				return COLOR_BLEND_MODE_OVERLAY;
			
			case SCREEN:
				return COLOR_BLEND_MODE_SCREEN;
			
			case SUBTRACT:
				return COLOR_BLEND_MODE_SUBTRACT;
			
			default:
				return COLOR_BLEND_MODE_INVALID;
		}
	}
	
	public static final int getLightSourceTypeAsInteger(LightSourceType lightSourceType) {
		if (lightSourceType == null) {
			return LIGHT_TYPE_INVALID;
		}
		
		switch (lightSourceType) {
			case AMBIENT:
				return LIGHT_TYPE_AMBIENT;
			
			case DIRECTIONAL:
				return LIGHT_TYPE_DIRECTIONAL;
			
			default:
				return LIGHT_TYPE_INVALID;
		}
	}
}
