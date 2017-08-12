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

package org.quadracoatl.framework.resources.textures;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.quadracoatl.framework.block.Side;
import org.quadracoatl.framework.common.MathUtil;
import org.quadracoatl.framework.common.regions.Region2d;
import org.quadracoatl.framework.common.vectors.Vector2i;
import org.quadracoatl.framework.common.vectors.Vector3i;
import org.quadracoatl.framework.resources.ResourceManager;
import org.quadracoatl.framework.resources.ResourceType;

public class Texture {
	private transient BufferedImage cachedImage = null;
	private transient List<String> readonlyTextures = null;
	private transient Map<Side, Region2d> sideCoordinates = new HashMap<>();
	private List<String> textures = new ArrayList<>();
	private TextureType textureType = TextureType.SINGLE;
	
	public Texture() {
		super();
	}
	
	public Texture(TextureType textureType, String... textures) {
		this();
		
		setTextures(textureType, textures);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		Texture other = (Texture)obj;
		
		if (textureType != other.textureType) {
			return false;
		}
		
		if (textures == null) {
			if (other.textures != null) {
				return false;
			}
		} else if (!textures.equals(other.textures)) {
			return false;
		}
		
		return true;
	}
	
	public InputStream getAsResource(ResourceManager resourceManager) {
		if (textures.isEmpty()) {
			return null;
		}
		
		try {
			if (cachedImage == null) {
				cachedImage = createImage(resourceManager);
			}
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			ImageIO.write(cachedImage, "png", os);
			
			return new ByteArrayInputStream(os.toByteArray());
		} catch (IOException e) {
			// Should not happen.
		}
		
		return null;
	}
	
	public Region2d getRelativeCoordinates(Side side) {
		return sideCoordinates.get(side);
	}
	
	public List<String> getTextures() {
		if (readonlyTextures == null) {
			readonlyTextures = Collections.unmodifiableList(textures);
		}
		
		return readonlyTextures;
	}
	
	public TextureType getTextureType() {
		return textureType;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((textureType == null) ? 0 : textureType.hashCode());
		result = prime * result + ((textures == null) ? 0 : textures.hashCode());
		return result;
	}
	
	public void setTextures(TextureType textureType, Collection<String> textures) {
		this.textureType = textureType;
		this.textures.clear();
		
		if (textures != null && textures.size() > 0) {
			this.textures.addAll(textures);
		}
		
		cachedImage = null;
		sideCoordinates.clear();
	}
	
	public void setTextures(TextureType textureType, String... textures) {
		if (textures != null && textures.length > 0) {
			setTextures(textureType, Arrays.asList(textures));
		} else {
			setTextures(textureType, (Collection<String>)null);
		}
	}
	
	private BufferedImage createImage(ResourceManager resourceManager) throws IOException {
		switch (textureType) {
			case CONDENSED_CUBE:
				sideCoordinates.put(Side.FRONT, new Region2d(0.0d, 0.5d, 1.0d / 3.0d, 1.0d));
				sideCoordinates.put(Side.BACK, new Region2d(0.0d, 0.0d, 1.0d / 3.0d, 0.5d));
				sideCoordinates.put(Side.LEFT, new Region2d(1.0d / 3.0d, 0.5d, 1.0d / 3.0d * 2.0d, 1.0d));
				sideCoordinates.put(Side.RIGHT, new Region2d(1.0d / 3.0d, 0.0d, 1.0d / 3.0d * 2.0d, 0.5d));
				sideCoordinates.put(Side.TOP, new Region2d(1.0d / 3.0d * 2.0d, 0.5d, 1.0d, 1.0d));
				sideCoordinates.put(Side.BOTTOM, new Region2d(1.0d / 3.0d * 2.0d, 0.0d, 1.0d, 0.5d));
				
				return ImageIO.read(resourceManager.getResourceStream(ResourceType.TEXTURE, textures.get(0)));
			
			case SINGLE:
				Region2d singleCoordinate = new Region2d(0.0d, 0.0d, 1.0d, 1.0d);
				sideCoordinates.put(Side.FRONT, singleCoordinate);
				sideCoordinates.put(Side.BACK, singleCoordinate);
				sideCoordinates.put(Side.LEFT, singleCoordinate);
				sideCoordinates.put(Side.RIGHT, singleCoordinate);
				sideCoordinates.put(Side.TOP, singleCoordinate);
				sideCoordinates.put(Side.BOTTOM, singleCoordinate);
				
				return ImageIO.read(resourceManager.getResourceStream(ResourceType.TEXTURE, textures.get(0)));
			
			case TILES:
				List<Image> images = new ArrayList<>();
				List<Region2d> coordinates = new ArrayList<>();
				
				for (String texture : textures) {
					BufferedImage image = ImageIO.read(resourceManager.getResourceStream(
							ResourceType.TEXTURE,
							texture));
					Region2d coordinate = new Region2d(
							0.0d,
							0.0d,
							image.getWidth(),
							image.getHeight());
					
					images.add(image);
					coordinates.add(coordinate);
				}
				
				Vector3i columnWidths = new Vector3i(
						(int)Math.max(coordinates.get(0).end.x, coordinates.get(1).end.x),
						(int)Math.max(coordinates.get(2).end.x, coordinates.get(3).end.x),
						(int)Math.max(coordinates.get(4).end.x, coordinates.get(5).end.x));
				Vector2i rowHeights = new Vector2i(
						(int)MathUtil.max(coordinates.get(0).end.y, coordinates.get(2).end.y, coordinates.get(4).end.y),
						(int)MathUtil.max(coordinates.get(1).end.y, coordinates.get(3).end.y, coordinates.get(5).end.y));
				
				Vector2i packedSize = new Vector2i(
						columnWidths.x + columnWidths.y + columnWidths.z,
						rowHeights.x + rowHeights.y);
				
				// 0 is already at the correct location.
				coordinates.get(2).setStart(columnWidths.x, 0.0d);
				coordinates.get(4).setStart(columnWidths.x + columnWidths.y, 0.0d);
				
				coordinates.get(1).setStart(0.0d, rowHeights.x);
				coordinates.get(3).setStart(columnWidths.x, rowHeights.x);
				coordinates.get(5).setStart(columnWidths.x + columnWidths.y, rowHeights.x);
				
				BufferedImage packedImage = new BufferedImage(
						packedSize.x,
						packedSize.y,
						BufferedImage.TYPE_INT_ARGB);
				
				Graphics graphics = packedImage.getGraphics();
				
				for (int index = 0; index < textures.size(); index++) {
					Region2d coordinate = coordinates.get(index);
					Image image = images.get(index);
					
					graphics.drawImage(
							image,
							(int)coordinate.start.x,
							(int)coordinate.start.y,
							null);
				}
				
				graphics.dispose();
				
				for (Region2d coordinate : coordinates) {
					coordinate.start.x = coordinate.start.x / packedSize.x;
					coordinate.end.x = coordinate.start.x + coordinate.end.x / packedSize.x;
					
					coordinate.start.y = (packedSize.y - (coordinate.start.y + coordinate.end.y)) / packedSize.y;
					coordinate.end.y = coordinate.start.y + coordinate.end.y / packedSize.y;
				}
				
				sideCoordinates.put(Side.FRONT, coordinates.get(0));
				sideCoordinates.put(Side.BACK, coordinates.get(1));
				sideCoordinates.put(Side.LEFT, coordinates.get(2));
				sideCoordinates.put(Side.RIGHT, coordinates.get(3));
				sideCoordinates.put(Side.TOP, coordinates.get(4));
				sideCoordinates.put(Side.BOTTOM, coordinates.get(5));
				
				return packedImage;
			
			case UNWRAPPED_CUBE:
				sideCoordinates.put(Side.FRONT, new Region2d(0.25d, 1.0d / 3.0d, 0.5d, 1.0d / 3.0d * 2.0d));
				sideCoordinates.put(Side.BACK, new Region2d(0.75d, 1.0d / 3.0d, 1.0d, 1.0d / 3.0d * 2.0d));
				sideCoordinates.put(Side.TOP, new Region2d(0.25d, 1.0d / 3.0d * 2.0d, 0.5d, 1.0d));
				sideCoordinates.put(Side.BOTTOM, new Region2d(0.25d, 0.0d, 0.5d, 1.0d / 3.0d));
				sideCoordinates.put(Side.LEFT, new Region2d(0.0d, 1.0d / 3.0d, 0.25d, 1.0d / 3.0d * 2.0d));
				sideCoordinates.put(Side.RIGHT, new Region2d(0.5d, 1.0d / 3.0d, 0.75d, 1.0d / 3.0d * 2.0d));
				
				return ImageIO.read(resourceManager.getResourceStream(ResourceType.TEXTURE, textures.get(0)));
			
		}
		
		return null;
	}
}
