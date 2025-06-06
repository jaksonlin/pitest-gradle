package com.github.jaksonlin.testcraft.presentation.components.premium;

import com.github.jaksonlin.testcraft.infrastructure.license.PremiumManager;

import javax.swing.*;


public class PremiumSettingsComponent {
    private JPanel mainPanel;
    private JTextField licenseKeyField;
    private JButton activateButton;

    public PremiumSettingsComponent() {
        mainPanel = new JPanel();
        licenseKeyField = new JTextField();
        activateButton = new JButton("Activate Premium");

        activateButton.addActionListener(e -> {
            String licenseKey = licenseKeyField.getText();
            PremiumManager.getInstance().activatePremium(licenseKey);
            // TODO: Implement UI update logic
            // updateUI();
        });

        // Add components to panel
        mainPanel.add(licenseKeyField);
        mainPanel.add(activateButton);
    }

    public JPanel getPanel() {
        return mainPanel;
    }
}