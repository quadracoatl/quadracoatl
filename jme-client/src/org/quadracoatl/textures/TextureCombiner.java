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

package org.quadracoatl.textures;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class TextureCombiner {
	
	public TextureCombiner() {
		
	}
	
	public static BufferedImage createMap(
			InputStream top,
			InputStream bottom,
			InputStream left,
			InputStream front,
			InputStream right,
			InputStream back) {
		
		BufferedImage image = new BufferedImage(128 * 6, 128, BufferedImage.TYPE_INT_ARGB);
		
		Graphics graphics = image.getGraphics();
		
		try {
			graphics.drawImage(ImageIO.read(top), 0, 0, null);
			graphics.drawImage(ImageIO.read(bottom), 128, 0, null);
			graphics.drawImage(ImageIO.read(left), 256, 0, null);
			graphics.drawImage(ImageIO.read(front), 384, 0, null);
			graphics.drawImage(ImageIO.read(right), 512, 0, null);
			graphics.drawImage(ImageIO.read(back), 640, 0, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		graphics.dispose();
		
		return image;
	}
}
