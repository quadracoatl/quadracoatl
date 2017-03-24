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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.quadracoatl.framework.logging.Logger;
import org.quadracoatl.framework.logging.LoggerFactory;

public class Configuration {
	protected static final String BINARY_PREFIX = "0b";
	protected static final String HEX_PREFIX = "0x";
	protected static final String OCTAL_PREFIX = "0o";
	protected Path defaultFile = null;
	
	protected Properties properties = null;
	
	private final Logger LOGGER = LoggerFactory.getLogger(this);
	
	public Configuration() {
		super();
		
		properties = new Properties();
	}
	
	public Configuration(Path defaultFile) {
		this();
		
		this.defaultFile = defaultFile;
		
		load();
	}
	
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}
	
	public boolean getBoolean(String key, boolean defaultValue) {
		String value = properties.getProperty(key);
		
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		
		return Boolean.parseBoolean(value);
	}
	
	public double getDouble(String key) {
		return getDouble(key, 0.0d);
	}
	
	public double getDouble(String key, double defaultValue) {
		String value = properties.getProperty(key);
		
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			LOGGER.info("Property \"", key, "\" with value \"", value, "\" is not a double.", e);
			
			return defaultValue;
		}
	}
	
	public float getFloat(String key) {
		return getFloat(key, 0.0f);
	}
	
	public float getFloat(String key, float defaultValue) {
		String value = properties.getProperty(key);
		
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException e) {
			LOGGER.info("Property \"", key, "\" with value \"", value, "\" is not a float.", e);
			
			return defaultValue;
		}
	}
	
	public int getInteger(String key) {
		return getInteger(key, 0);
	}
	
	public int getInteger(String key, int defaultValue) {
		String value = properties.getProperty(key);
		
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		
		try {
			if (value.startsWith(HEX_PREFIX)) {
				return Integer.parseInt(value.substring(HEX_PREFIX.length()), 16);
			} else {
				return Integer.parseInt(value);
			}
		} catch (NumberFormatException e) {
			LOGGER.info("Property \"", key, "\" with value \"", value, "\" is not an integer.", e);
			
			return defaultValue;
		}
	}
	
	public List<String> getList(String key, String separator) {
		String value = properties.getProperty(key);
		
		if (value == null || value.length() == 0) {
			return Collections.emptyList();
		}
		
		return Arrays.asList(value.split(separator));
	}
	
	public long getLong(String key) {
		return getLong(key, 0);
	}
	
	public long getLong(String key, int defaultValue) {
		String value = properties.getProperty(key);
		
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		
		try {
			if (value.startsWith(BINARY_PREFIX)) {
				return Long.parseLong(value.substring(HEX_PREFIX.length()), 2);
			} else if (value.startsWith(HEX_PREFIX)) {
				return Long.parseLong(value.substring(HEX_PREFIX.length()), 16);
			} else if (value.startsWith(OCTAL_PREFIX)) {
				return Long.parseLong(value.substring(HEX_PREFIX.length()), 8);
			} else {
				return Long.parseLong(value);
			}
		} catch (NumberFormatException e) {
			LOGGER.info("Property \"", key, "\" with value \"", value, "\" is not a long.", e);
			
			return defaultValue;
		}
	}
	
	public String getString(String key) {
		return getString(key, "");
	}
	
	public String getString(String key, String defaultValue) {
		String value = properties.getProperty(key);
		
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		
		return value;
	}
	
	public void load() {
		load(defaultFile);
	}
	
	public void load(Path propertiesFile) {
		try (FileInputStream stream = new FileInputStream(propertiesFile.toFile())) {
			properties.load(new InputStreamReader(stream, Charsets.UTF_8));
		} catch (IOException e) {
			LOGGER.error("Could not read property file from \"", propertiesFile.toAbsolutePath(), "\".", e);
		}
	}
	
	public void store() {
		store(defaultFile);
	}
	
	public void store(Path propertiesFile) {
		try (FileOutputStream stream = new FileOutputStream(propertiesFile.toFile())) {
			properties.store(new OutputStreamWriter(stream, Charsets.UTF_8), null);
		} catch (IOException e) {
			LOGGER.error("Could not write property file to \"", propertiesFile.toAbsolutePath(), "\".", e);
		}
	}
}
