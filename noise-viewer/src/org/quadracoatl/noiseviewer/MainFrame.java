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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
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
		setSize(new Dimension(900, 600));
		
		JLabel drawTypeLabel = new JLabel("Visualization");
		drawTypeBox = new JComboBox<>(NoiseType.values());
		
		GridLayout controlsLayout = new GridLayout(1, 2);
		
		JPanel controlsPanel = new JPanel();
		controlsPanel.setLayout(controlsLayout);
		controlsPanel.add(drawTypeLabel);
		controlsPanel.add(drawTypeBox);
		
		modCodeArea = new RSyntaxTextArea(getDefaultCode());
		modCodeArea.setAutoIndentEnabled(true);
		modCodeArea.setBracketMatchingEnabled(true);
		modCodeArea.setClearWhitespaceLinesEnabled(false);
		modCodeArea.setCloseCurlyBraces(true);
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
		controlsContainerPanel.add(new RTextScrollPane(modCodeArea, true), BorderLayout.CENTER);
		controlsContainerPanel.add(refreshButton, BorderLayout.SOUTH);
		
		noisePreview = new NoisePreviewPanel();
		noisePreview.setMinimumSize(new Dimension(0, 0));
		noisePreview.setPreferredSize(new Dimension(460, 640));
		
		JSplitPane contentPanel = new JSplitPane();
		contentPanel.setDividerLocation(480);
		contentPanel.setLeftComponent(controlsContainerPanel);
		contentPanel.setRightComponent(noisePreview);
		
		setContentPane(contentPanel);
		
		updateNoisePreview();
	}
	
	private static String getDefaultCode() {
		StringBuilder content = new StringBuilder();
		
		try (InputStream stream = Main.class.getResourceAsStream("/org/quadracoatl/noiseviewer/default-code.lua")) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				content.append(line);
				content.append("\n");
			}
		} catch (IOException e) {
			// We will ignore this exception for now...
		}
		
		return content.toString();
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
