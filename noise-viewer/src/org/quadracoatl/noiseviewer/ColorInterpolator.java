package org.quadracoatl.noiseviewer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorInterpolator {
	private List<Color> colors = new ArrayList<>();
	
	public ColorInterpolator(Color... colors) {
		super();
		
		if (colors != null) {
			this.colors.addAll(Arrays.asList(colors));
		}
	}
	
	public static final int cubic(int a, int b, int m, int n, double offset) {
		int p = (n - m) - (a - b);
		int q = (a - b) - p;
		int r = m - a;
		
		return (int)(p * Math.pow(offset, 3) + q * Math.pow(offset, 2) + (r * offset) + b);
	}
	
	private static final double transform(double value, double min, double max, double newMin, double newMax) {
		return (value - min) / (max - min) * (newMax - newMin) + newMin;
	}
	
	public Color getColor(double min, double max, double value) {
		if (colors.isEmpty()) {
			return Color.BLACK;
		}
		
		if (value <= min) {
			return Color.BLACK;
		} else if (value >= max) {
			return Color.WHITE;
		}
		
		double transformedValue = transform(value, min, max, 0, colors.size() - 1);
		
		int firstColorIndex = Math.max(0, (int)Math.floor(transformedValue) - 1);
		int secondColorIndex = (int)Math.floor(transformedValue);
		int thirdColorIndex = (int)Math.ceil(transformedValue);
		int fourthColorIndex = Math.min(colors.size() - 1, (int)Math.ceil(transformedValue) + 1);
		
		Color firstColor = colors.get(firstColorIndex);
		Color secondColor = colors.get(secondColorIndex);
		Color thirdColor = colors.get(thirdColorIndex);
		Color fourthColor = colors.get(fourthColorIndex);
		
		double betweenColorsValue = transformedValue - secondColorIndex;
		
		double red = cubic(firstColor.getRed(), secondColor.getRed(), thirdColor.getRed(), fourthColor.getRed(), betweenColorsValue);
		double green = cubic(firstColor.getGreen(), secondColor.getGreen(), thirdColor.getGreen(), fourthColor.getGreen(), betweenColorsValue);
		double blue = cubic(firstColor.getBlue(), secondColor.getBlue(), thirdColor.getBlue(), fourthColor.getBlue(), betweenColorsValue);
		
		return new Color(
				Math.min(255, Math.max(0, (int)red)),
				Math.min(255, Math.max(0, (int)green)),
				Math.min(255, Math.max(0, (int)blue)));
	}
}
