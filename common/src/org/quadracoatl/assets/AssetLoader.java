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

package org.quadracoatl.assets;

import java.io.InputStream;

/**
 * {@link AssetLoader} is a simple utility which assists with and allows loading
 * of embedded assets in Quadracoatl.
 */
public final class AssetLoader {
	/**
	 * Constant containing the fully qualified name of the package containing
	 * the assets.
	 */
	private static final String ASSET_PACKAGE = AssetLoader.class.getPackage().getName().replace(".", "/");
	
	/**
	 * The {@link ClassLoader} that is used for accessing the assets.
	 */
	private static ClassLoader assetClassLoader = AssetLoader.class.getClassLoader();
	
	/**
	 * No instance required.
	 */
	private AssetLoader() {
		// Static utility.
	}
	
	/**
	 * Gets the asset with the given name.
	 * 
	 * @param name The fully qualified name of the asset.
	 * @return The {@link InputStream} of the asset, {@code null} if it could
	 *         not be found.
	 * @see ClassLoader#getResourceAsStream(String)
	 */
	public static InputStream getAsset(String name) {
		return getAssetClassLoader().getResourceAsStream(name);
	}
	
	/**
	 * Gets the {@link ClassLoader} used for loading the assets.
	 * 
	 * @return The {@link ClassLoader} used for loading the assets.
	 */
	public static final ClassLoader getAssetClassLoader() {
		return assetClassLoader;
	}
	
	/**
	 * Gets the "debug cube" texture, an condensed cube texture which shows
	 * information about orientation and axis.
	 * 
	 * @return the "debug cube" texture.
	 */
	public static InputStream getDebugCondensedCubeTexture() {
		return getAsset(ASSET_PACKAGE + "/textures/debug-condensed-cube.png");
	}
	
	/**
	 * Gets the "debug" texture.
	 * 
	 * @return the "debug" texture.
	 */
	public static InputStream getDebugTexture() {
		return getAsset(ASSET_PACKAGE + "/textures/debug.png");
	}
	
	/**
	 * Gets the "debug cube" texture, an unwrapped cube texture which shows
	 * information about orientation and axis.
	 * 
	 * @return the "debug cube" texture.
	 */
	public static InputStream getDebugUnwrappedCubeTexture() {
		return getAsset(ASSET_PACKAGE + "/textures/debug-unwrapped-cube.png");
	}
	
	/**
	 * Gets the "missing" texture, a simple image which signifies that the
	 * texture is missing.
	 * 
	 * @return the "missing" texture.
	 */
	public static InputStream getMissingTexture() {
		return getAsset(ASSET_PACKAGE + "/textures/missing.png");
	}
	
	/**
	 * Sets the {@link ClassLoader} used for loading the assets.
	 * <p>
	 * If there is a custom ClassLoader-Hierarchy in place, you can use this to
	 * set the correct {@link ClassLoader} which can access the assets.
	 * 
	 * @param assetClassLoader The {@link ClassLoader} used for loading the
	 *        assets.
	 */
	public static final void setAssetClassLoader(ClassLoader assetClassLoader) {
		AssetLoader.assetClassLoader = assetClassLoader;
	}
}
