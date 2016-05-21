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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.quadracoatl.framework.support.noises.Noise;
import org.quadracoatl.noiseviewer.NoisePreviewPanel.NoiseType;

public class MainFrame extends JFrame {
	private JComboBox<NoiseType> drawTypeBox;
	private RSyntaxTextArea modCodeArea = null;
	private NoisePreviewPanel noisePreview;
	private JSpinner octavesSpinner = null;
	private JSpinner persistenceSpinner = null;
	private JSpinner scaleWSpinner = null;
	private JSpinner scaleXSpinner = null;
	private JSpinner scaleYSpinner = null;
	private JSpinner scaleZSpinner = null;
	private JTextField seedField = null;
	
	/**
	 * Create the frame.
	 */
	public MainFrame() {
		super();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(800, 640));
		
		JLabel drawTypeLabel = new JLabel("Visualization");
		drawTypeBox = new JComboBox<>(NoiseType.values());
		
		JLabel seedLabel = new JLabel("Seed");
		seedField = new JTextField("0");
		
		JLabel octavesLabel = new JLabel("Octaves");
		octavesSpinner = new JSpinner(new SpinnerNumberModel(1.0, 1.0, Double.MAX_VALUE, 1.0));
		octavesSpinner.addChangeListener(this::updateNoisePreview);
		
		JLabel persistenceLabel = new JLabel("Persistence");
		persistenceSpinner = new JSpinner(new SpinnerNumberModel(0.5, 0.0, Double.MAX_VALUE, 0.1));
		persistenceSpinner.addChangeListener(this::updateNoisePreview);
		
		JLabel scaleXLabel = new JLabel("Scale X");
		scaleXSpinner = new JSpinner(new SpinnerNumberModel(64.0, 1.0, Double.MAX_VALUE, 1.0));
		scaleXSpinner.addChangeListener(this::updateNoisePreview);
		
		JLabel scaleYLabel = new JLabel("Scale Y");
		scaleYSpinner = new JSpinner(new SpinnerNumberModel(64.0, 1.0, Double.MAX_VALUE, 1.0));
		scaleYSpinner.addChangeListener(this::updateNoisePreview);
		
		JLabel scaleZLabel = new JLabel("Scale Z");
		scaleZSpinner = new JSpinner(new SpinnerNumberModel(64.0, 1.0, Double.MAX_VALUE, 1.0));
		scaleZSpinner.addChangeListener(this::updateNoisePreview);
		
		JLabel scaleWLabel = new JLabel("Scale W");
		scaleWSpinner = new JSpinner(new SpinnerNumberModel(64.0, 1.0, Double.MAX_VALUE, 1.0));
		scaleWSpinner.addChangeListener(this::updateNoisePreview);
		
		GridLayout controlsLayout = new GridLayout(8, 2);
		
		JPanel controlsPanel = new JPanel();
		controlsPanel.setLayout(controlsLayout);
		controlsPanel.add(drawTypeLabel);
		controlsPanel.add(drawTypeBox);
		controlsPanel.add(seedLabel);
		controlsPanel.add(seedField);
		controlsPanel.add(octavesLabel);
		controlsPanel.add(octavesSpinner);
		controlsPanel.add(persistenceLabel);
		controlsPanel.add(persistenceSpinner);
		controlsPanel.add(scaleXLabel);
		controlsPanel.add(scaleXSpinner);
		controlsPanel.add(scaleYLabel);
		controlsPanel.add(scaleYSpinner);
		controlsPanel.add(scaleZLabel);
		controlsPanel.add(scaleZSpinner);
		controlsPanel.add(scaleWLabel);
		controlsPanel.add(scaleWSpinner);
		
		modCodeArea = new RSyntaxTextArea("function mod(value)\n\treturn value\nend\n\nreturn mod(...)");
		modCodeArea.setClearWhitespaceLinesEnabled(false);
		modCodeArea.setLineWrap(false);
		modCodeArea.setMarkOccurrences(true);
		modCodeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LUA);
		modCodeArea.setTabSize(4);
		modCodeArea.setWhitespaceVisible(true);
		
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
		noisePreview.setNoise(new Noise(
				seedField.getText(),
				((Double)octavesSpinner.getValue()).intValue(),
				((Double)persistenceSpinner.getValue()).doubleValue(),
				((Double)scaleXSpinner.getValue()).doubleValue(),
				((Double)scaleYSpinner.getValue()).doubleValue(),
				((Double)scaleZSpinner.getValue()).doubleValue(),
				((Double)scaleWSpinner.getValue()).doubleValue()));
		noisePreview.setModCode(modCodeArea.getText());
		
		noisePreview.update();
	}
	
	private void updateNoisePreview(ActionEvent event) {
		updateNoisePreview();
	}
	
	private void updateNoisePreview(ChangeEvent event) {
		updateNoisePreview();
	}
}
