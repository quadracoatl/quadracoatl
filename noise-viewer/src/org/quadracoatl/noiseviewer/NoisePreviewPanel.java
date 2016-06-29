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

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.quadracoatl.scripting.lua.LuaEnvironment;
import org.quadracoatl.scripting.lua.libs.EngineLib;
import org.quadracoatl.scripting.lua.libs.SupportLib;

public class NoisePreviewPanel extends JPanel {
	private BufferedImage image = null;
	
	private LuaValue luaCode = null;
	private LuaEnvironment luaEnvironment = null;
	
	private NoiseType noiseType = NoiseType.TWO_DIMENSIONS;
	
	public NoisePreviewPanel() {
		super();
		
		luaEnvironment = new LuaEnvironment();
		luaEnvironment.load(new EngineLib());
		luaEnvironment.load(new SupportLib());
	}
	
	public void setModCode(String code) {
		luaCode = luaEnvironment.getEnvironment().load(code).call();
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
	
	private int transform(int min, int max, double value) {
		return (int)Math.round((value + 1) / 2 * (max - min) + min);
	}
	
	private void updateImage() {
		if (luaCode == null || !luaCode.isfunction()) {
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
		
		long totalTime = 0l;
		int totalPoints = 0;
		
		switch (noiseType) {
			case ONE_DIMENSTION:
				totalPoints = getWidth();
				
				graphics.setColor(Color.WHITE);
				graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
				
				for (int x = 0; x < getWidth(); x++) {
					double value = 0.0d;
					
					try {
						long start = System.nanoTime();
						
						Varargs luaValue = luaCode.invoke(new LuaValue[] {
								LuaNumber.valueOf(x),
								LuaNumber.NIL,
								LuaNumber.NIL,
								LuaNumber.NIL
						});
						value = ((LuaNumber)luaValue).todouble();
						
						totalTime = totalTime + (System.nanoTime() - start);
					} catch (LuaError e) {
						e.printStackTrace();
					}
					
					Color color = null;
					
					if (value < -1.0) {
						color = Color.GREEN;
					} else if (value > 1.0) {
						value = 1.0 / getHeight();
						color = Color.RED;
					} else {
						value = Math.max(-1.0, Math.min(1.0, value));
						color = Color.BLACK;
					}
					
					graphics.setColor(color);
					graphics.drawLine(x, getHeight(), x, transform(getHeight(), 0, value));
				}
				break;
			
			case TWO_DIMENSIONS:
				totalPoints = getWidth() * getHeight();
				
				for (int x = 0; x < getWidth(); x++) {
					for (int y = 0; y < getHeight(); y++) {
						double value = 0.0d;
						
						try {
							long start = System.nanoTime();
							
							Varargs luaValue = luaCode.invoke(new LuaValue[] {
									LuaNumber.valueOf(x),
									LuaNumber.valueOf(y),
									LuaNumber.NIL,
									LuaNumber.NIL
							});
							value = ((LuaNumber)luaValue).todouble();
							
							totalTime = totalTime + (System.nanoTime() - start);
						} catch (LuaError e) {
							e.printStackTrace();
						}
						
						Color color = null;
						
						if (value < -1.0) {
							color = Color.GREEN;
						} else if (value > 1.0) {
							color = Color.RED;
						} else {
							value = Math.max(-1.0, Math.min(1.0, value));
							color = new Color(
									transform(0, 255, value),
									transform(0, 255, value),
									transform(0, 255, value));
						}
						
						graphics.setColor(color);
						graphics.drawLine(x, y, x + 1, y + 1);
					}
				}
				break;
		}
		
		double totalTimeMilliseconds = (totalTime / 1000l) / 1000d;
		double pointTime = totalTime / totalPoints / 1000l / 1000d;
		
		graphics.setColor(new Color(0xaa000000, true));
		graphics.fillRect(getWidth() - 192, getHeight() - 68, 184, 64);
		
		graphics.setColor(Color.WHITE);
		graphics.drawString("Total time: " + Double.toString(totalTimeMilliseconds) + "ms", getWidth() - 172, getHeight() - 48);
		graphics.drawString("Total points: " + Long.toString(totalPoints), getWidth() - 172, getHeight() - 32);
		graphics.drawString("Point time: " + Double.toString(pointTime) + "ms", getWidth() - 172, getHeight() - 16);
		
		SwingUtilities.invokeLater(this::repaint);
	}
	
	private void updateImageAsync() {
		if (luaCode == null || !luaCode.isfunction()) {
			return;
		}
		
		new Thread(this::updateImage).start();
	}
	
	public enum NoiseType {
		ONE_DIMENSTION, TWO_DIMENSIONS
	}
}
