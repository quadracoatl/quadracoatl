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

package org.quadracoatl.framework.support;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class Arguments {
	private OptionParser optionParser = new OptionParser();
	private OptionSet optionSet = null;
	
	public Arguments() {
		super();
	}
	
	public void addInteger(String name) {
		addInteger(name, 0);
	}
	
	public void addInteger(String name, int defaultValue) {
		optionParser.accepts(name)
				.withRequiredArg()
				.ofType(Integer.class)
				.defaultsTo(Integer.valueOf(defaultValue));
	}
	
	public void addString(String name) {
		addString(name, null);
	}
	
	public void addString(String name, String defaultValue) {
		optionParser.accepts(name)
				.withRequiredArg()
				.ofType(String.class)
				.defaultsTo(defaultValue);
	}
	
	public int getInteger(String name) {
		return ((Integer)optionSet.valueOf(name)).intValue();
	}
	
	public String getString(String name) {
		return (String)optionSet.valueOf(name);
	}
	
	public void process(String... arguments) {
		optionSet = optionParser.parse(arguments);
	}
}
