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

package org.quadracoatl.framework.block;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the sides of, for example, a Cube.
 */
public enum Side {
	/**
	 * The back, the side facing -Z.
	 */
	BACK,
	
	/**
	 * The bottom, the side facing -Y.
	 */
	BOTTOM,
	
	/**
	 * The front, the side facing +Z.
	 */
	FRONT,
	
	/**
	 * The left side, the side facing -X.
	 */
	LEFT,
	
	/**
	 * The right side, the side facing +X.
	 */
	RIGHT,
	
	/**
	 * The top, the side facing +Y.
	 */
	TOP;
	
	/**
	 * An {@link EnumSet} containing all sides.
	 * <p>
	 * A cached instance of the {@link EnumSet} return by
	 * {@link EnumSet#allOf(Class)}.
	 */
	public static final EnumSet<Side> ALL = EnumSet.allOf(Side.class);
	
	/**
	 * An {@link EnumSet} containing no sides.
	 * <p>
	 * A cached instance of the {@link EnumSet} return by
	 * {@link EnumSet#noneOf(Class)}.
	 */
	public static final EnumSet<Side> NONE = EnumSet.noneOf(Side.class);
	
	/**
	 * Creates an {@link EnumSet} from the given values.
	 * <p>
	 * Contrary to {@link EnumSet#of(Enum, Enum...)} (and similar functions),
	 * the parameters to this call can be {@code null} or none at all.
	 * 
	 * @param sides The {@link Side}s to add, can be {@code null} or empty and
	 *        can contain {@code null}..
	 * @return The {@link EnumSet} containing all {@link Side}s.
	 */
	public static final EnumSet<Side> from(Side... sides) {
		if (sides != null && sides.length > 0) {
			Set<Side> set = new HashSet<Side>();
			
			for (Side side : sides) {
				if (side != null) {
					set.add(side);
				}
			}
			
			if (!set.isEmpty()) {
				return EnumSet.copyOf(set);
			}
		}
		
		return NONE;
	}
}
