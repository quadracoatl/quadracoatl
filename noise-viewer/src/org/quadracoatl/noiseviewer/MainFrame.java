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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.quadracoatl.noiseviewer.NoisePreviewPanel.NoiseType;

public class MainFrame extends JFrame {
	private JComboBox<NoiseType> drawTypeBox;
	private RSyntaxTextArea modCodeArea = null;
	private NoisePreviewPanel noisePreview;
	
	/**
	 * Create the frame.
	 */
	public MainFrame() {
		super();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(800, 600));
		
		JLabel drawTypeLabel = new JLabel("Visualization");
		drawTypeBox = new JComboBox<>(NoiseType.values());
		
		GridLayout controlsLayout = new GridLayout(1, 2);
		
		JPanel controlsPanel = new JPanel();
		controlsPanel.setLayout(controlsLayout);
		controlsPanel.add(drawTypeLabel);
		controlsPanel.add(drawTypeBox);
		
		modCodeArea = new RSyntaxTextArea("local noise = engine.support.createNoise({\n"
				+ "\tnoiseType = NoiseType.OPEN_SIMPLEX,\n"
				+ "\tseed = \"viewer\",\n"
				+ "\toctaves = 7,\n"
				+ "\tpersistence = 0.7,\n"
				+ "\tscaleX = 128,\n"
				+ "\tscaleY = 128,\n"
				+ "\tscaleZ = 128,\n"
				+ "\tscaleW = 128,\n"
				+ "})\n"
				+ "\n"
				+ "return function(x, y, z, w)\n"
				+ "\tif y == nil then\n"
				+ "\t\treturn noise:get(x)\n"
				+ "\telseif z == nil then\n"
				+ "\t\treturn noise:get(x, y)\n"
				+ "\telseif w == nil then\n"
				+ "\t\treturn noise:get(x, y, z)\n"
				+ "\telse\n"
				+ "\t\treturn noise:get(x, y, z, w)\n"
				+ "\tend\n"
				+ "end\n");
		modCodeArea.setClearWhitespaceLinesEnabled(false);
		modCodeArea.setLineWrap(false);
		modCodeArea.setMarkOccurrences(true);
		modCodeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LUA);
		modCodeArea.setTabSize(4);
		modCodeArea.setWhitespaceVisible(true);
		modCodeArea.addKeyListener(new UpdatingKeyListener());
		
		JButton refreshButton = new JButton("Refresh");
		refreshButton.addActionListener(this::updateNoisePreview);
		
		BorderLayout controlsContainerLayout = new BorderLayout();
		
		JPanel controlsContainerPanel = new JPanel();
		controlsContainerPanel.setLayout(controlsContainerLayout);
		controlsContainerPanel.setMinimumSize(new Dimension(0, 0));
		controlsContainerPanel.setPreferredSize(new Dimension(240, 640));
		controlsContainerPanel.add(controlsPanel, BorderLayout.NORTH);
		controlsContainerPanel.add(modCodeArea, BorderLayout.CENTER);
		controlsContainerPanel.add(refreshButton, BorderLayout.SOUTH);
		
		noisePreview = new NoisePreviewPanel();
		noisePreview.setMinimumSize(new Dimension(0, 0));
		noisePreview.setPreferredSize(new Dimension(460, 640));
		
		JSplitPane contentPanel = new JSplitPane();
		contentPanel.setDividerLocation(240);
		contentPanel.setLeftComponent(controlsContainerPanel);
		contentPanel.setRightComponent(noisePreview);
		
		setContentPane(contentPanel);
		
		updateNoisePreview();
	}
	
	private void updateNoisePreview() {
		noisePreview.setNoiseType((NoiseType)drawTypeBox.getSelectedItem());
		noisePreview.setModCode(modCodeArea.getText());
		
		noisePreview.update();
	}
	
	private void updateNoisePreview(ActionEvent event) {
		updateNoisePreview();
	}
	
	private final class UpdatingKeyListener implements KeyListener {
		
		@Override
		public void keyPressed(KeyEvent e) {
			
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_F5
					|| e.getKeyCode() == KeyEvent.VK_F9
					|| (e.getKeyCode() == KeyEvent.VK_ENTER && (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK)) {
				updateNoisePreview();
			}
		}
		
		@Override
		public void keyTyped(KeyEvent e) {
			
		}
		
	}
}
