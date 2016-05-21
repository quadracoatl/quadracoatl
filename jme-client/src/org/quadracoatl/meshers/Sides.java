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

package org.quadracoatl.meshers;

import java.util.EnumSet;

public enum Sides {
	BACK, BOTTOM, FRONT, LEFT, NO_SIDE, RIGHT, TOP;
	
	public static final EnumSet<Sides> ALL = EnumSet.allOf(Sides.class);
	public static final EnumSet<Sides> NONE = EnumSet.noneOf(Sides.class);
	
	public static boolean hasSide(EnumSet<Sides> sides) {
		return !sides.isEmpty()
				&& (sides.size() != 1
						|| !sides.contains(Sides.NO_SIDE));
	}
}
