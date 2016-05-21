/*
 * Copyright 2016, Robert 'Bobby' Zenz
 * 
 * This file is part of Quadracoatl/Noise Viewer.
 * 
 * Quadracoatl is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Quadracoatl/Noise Viewer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Quadracoatl/Noise Viewer.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package org.quadracoatl.noiseviewer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.luajc.LuaJC;
import org.quadracoatl.framework.support.noises.Noise;

public class NoisePreviewPanel extends JPanel {
	private BufferedImage image = null;
	
	private LuaValue luaCode = null;
	private Globals luaGlobals = null;
	
	private Noise noise = null;
	private NoiseType noiseType = NoiseType.TWO_DIMENSIONS;
	
	public NoisePreviewPanel() {
		super();
		
		luaGlobals = new Globals();
		LuaC.install(luaGlobals);
		LuaJC.install(luaGlobals);
		
		luaGlobals.load(new PackageLib());
		
		luaGlobals.load(new Bit32Lib());
		luaGlobals.load(new StringLib());
		luaGlobals.load(new TableLib());
		
		luaGlobals.load(new JseBaseLib());
		luaGlobals.load(new JseMathLib());
	}
	
	public void setModCode(String code) {
		luaCode = luaGlobals.load(code);
	}
	
	public void setNoise(Noise noise) {
		this.noise = noise;
	}
	
	public void setNoiseType(NoiseType noiseType) {
		this.noiseType = noiseType;
	}
	
	public void update() {
		updateImageAsync();
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		graphics.drawImage(image, 0, 0, null);
	}
	
	private void updateImage() {
		if (noise == null || luaCode == null) {
			return;
		}
		
		if (image == null
				|| image.getWidth() != getWidth()
				|| image.getHeight() != getHeight()) {
			image = new BufferedImage(
					Math.max(1, getWidth()),
					Math.max(1, getHeight()),
					BufferedImage.TYPE_INT_ARGB);
		}
		
		Graphics graphics = image.getGraphics();
		
		switch (noiseType) {
			case ONE_DIMENSTION:
				graphics.setColor(Color.WHITE);
				graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
				
				for (int x = 0; x < getWidth(); x++) {
					double value = noise.get(x);
					
					try {
						Varargs luaValue = luaCode.call(LuaNumber.valueOf(value));
						value = ((LuaNumber)luaValue).todouble();
					} catch (LuaError e) {
						e.printStackTrace();
					}
					
					value = (value + 1.0) / 2.0;
					
					Color color = null;
					
					if (value < 0.0) {
						color = Color.GREEN;
					} else if (value > 1.0) {
						value = 1.0 / getHeight();
						color = Color.RED;
					} else {
						value = Math.max(0.0, Math.min(1.0, value));
						color = Color.BLACK;
					}
					
					graphics.setColor(color);
					graphics.drawLine(x, getHeight(), x, (int)(value * getHeight()));
				}
				break;
			
			case TWO_DIMENSIONS:
				for (int x = 0; x < getWidth(); x++) {
					for (int y = 0; y < getHeight(); y++) {
						double value = noise.get(x, y);
						value = (value + 1.0) / 2.0;
						
						try {
							Varargs luaValue = luaCode.call(LuaNumber.valueOf(value));
							value = ((LuaNumber)luaValue).todouble();
						} catch (LuaError e) {
							e.printStackTrace();
						}
						
						Color color = null;
						
						if (value < 0.0) {
							color = Color.GREEN;
						} else if (value > 1.0) {
							color = Color.RED;
						} else {
							value = Math.max(0.0, Math.min(1.0, value));
							color = new Color((float)value, (float)value, (float)value);
						}
						
						graphics.setColor(color);
						graphics.drawLine(x, y, x + 1, y + 1);
					}
				}
				break;
		}
		
		SwingUtilities.invokeLater(this::repaint);
	}
	
	private void updateImageAsync() {
		if (noise == null || luaCode == null) {
			return;
		}
		
		new Thread(this::updateImage).start();
	}
	
	public enum NoiseType {
		ONE_DIMENSTION, TWO_DIMENSIONS
	}
}
