package com.github.jaksonlin.testcraft.presentation.components.configuration;

import com.github.jaksonlin.testcraft.infrastructure.services.system.I18nService;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class AnnotationSettingsComponent {
    private final JPanel mainPanel;
    private final EditorTextField schemaEditor;
    private final JBTextField packageTextField = new JBTextField();
    private final JCheckBox autoImportCheckBox;
    private final JCheckBox enableValidationCheckBox;
    private static final int EDITOR_HEIGHT = 300;

    private static final String EXAMPLE_JSON = "{\n" +
            "    \"fields\": [\n" +
            "        {\n" +
            "            \"name\": \"author\",\n" +
            "            \"type\": \"STRING\",\n" +
            "            \"required\": true,\n" +
            "            \"valueProvider\": {\n" +
            "                \"type\": \"FIRST_CREATOR_AUTHOR\"\n" +
            "            },\n" +
            "            \"validation\": {\n" +
            "                \"allowEmpty\": false\n" +
            "            }\n" +
            "        },\n" +
            "        {\n" +
            "            \"name\": \"title\",\n" +
            "            \"type\": \"STRING\",\n" +
            "            \"required\": true,\n" +
            "            \"valueProvider\": {\n" +
            "                \"type\": \"METHOD_NAME_BASED\"\n" +
            "            },\n" +
            "            \"validation\": {\n" +
            "                \"allowEmpty\": false\n" +
            "            }\n" +
            "        },\n" +
            "        {\n" +
            "            \"name\": \"testPoints\",\n" +
            "            \"type\": \"STRING_LIST\",\n" +
            "            \"required\": false,\n" +
            "            \"defaultValue\": {\n" +
            "                \"type\": \"StringListValue\",\n" +
            "                \"value\": [\"Functionality\"]\n" +
            "            },\n" +
            "            \"validation\": {\n" +
            "                \"validValues\": [\"Functionality\", \"Performance\", \"Security\"],\n" +
            "                \"allowCustomValues\": true,\n" +
            "                \"mode\": \"CONTAINS\",\n" +
            "                \"allowEmpty\": true\n" +
            "            }\n" +
            "        },\n" +
            "        {\n" +
            "            \"name\": \"status\",\n" +
            "            \"type\": \"STRING\",\n" +
            "            \"required\": false,\n" +
            "            \"defaultValue\": {\n" +
            "                \"type\": \"StringValue\",\n" +
            "                \"value\": \"TODO\"\n" +
            "            },\n" +
            "            \"validation\": {\n" +
            "                \"validValues\": [\"TODO\", \"IN_PROGRESS\", \"DONE\", \"BLOCKED\"],\n" +
            "                \"allowCustomValues\": false,\n" +
            "                \"mode\": \"EXACT\",\n" +
            "                \"allowEmpty\": false\n" +
            "            }\n" +
            "        }\n" +
            "    ]\n" +
            "}";

    public AnnotationSettingsComponent() {
        // Create main panel with a border layout to ensure full width usage
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Create content panel with GridBagLayout
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 1.0;

        // Import Settings section
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.0;
        JLabel importSettingsLabel = new JBLabel("<html><b>" + I18nService.getInstance().message("settings.annotation.import.title") + "</b></html>");
        contentPanel.add(importSettingsLabel, c);

        // Package input
        addLabelAndField(contentPanel, I18nService.getInstance().message("settings.annotation.package.label"), packageTextField, 1,
                I18nService.getInstance().message("settings.annotation.package.tooltip"));

        // Checkboxes
        c.gridy = 2;
        c.gridwidth = 2;
        autoImportCheckBox = new JCheckBox(I18nService.getInstance().message("settings.annotation.autoImport"));
        autoImportCheckBox.setToolTipText(I18nService.getInstance().message("settings.annotation.autoImport.tooltip"));
        contentPanel.add(autoImportCheckBox, c);

        c.gridy = 3;
        enableValidationCheckBox = new JCheckBox(I18nService.getInstance().message("settings.annotation.enableValidation"));
        enableValidationCheckBox.setToolTipText(I18nService.getInstance().message("settings.annotation.enableValidation.tooltip"));
        contentPanel.add(enableValidationCheckBox, c);

        // Schema Configuration section
        c.gridy = 4;
        c.insets = new Insets(15, 5, 5, 5);
        JLabel schemaLabel = new JBLabel("<html><b>" + I18nService.getInstance().message("settings.annotation.schema.title") + "</b></html>");
        contentPanel.add(schemaLabel, c);

        // Schema editor
        c.gridy = 5;
        c.weighty = 1.0; // Make editor expand vertically
        c.insets = new Insets(5, 5, 5, 5);
        schemaEditor = createSchemaEditor();
        
        // Wrap editor in a panel to ensure it expands properly
        JPanel editorPanel = new JPanel(new BorderLayout());
        editorPanel.add(new JBLabel(I18nService.getInstance().message("settings.annotation.schema.label")), BorderLayout.NORTH);
        editorPanel.add(schemaEditor, BorderLayout.CENTER);
        contentPanel.add(editorPanel, c);

        // Schema help text
        c.gridy = 6;
        c.weighty = 1.0; // Give more weight to help section
        c.insets = new Insets(10, 5, 5, 5);
        
        // Create a scrollable panel for the help text
        JPanel helpPanel = new JPanel(new BorderLayout());
        helpPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JLabel helpText = new JBLabel("<html>" +
                "<h3>" + I18nService.getInstance().message("settings.annotation.schema.help.title") + "</h3>" +
                "<p>" + I18nService.getInstance().message("settings.annotation.schema.help.intro") + "</p>" +
                
                "<h4>" + I18nService.getInstance().message("settings.annotation.schema.help.structure.title") + "</h4>" +
                "<ul>" +
                "<li>" + I18nService.getInstance().message("settings.annotation.schema.help.structure.1") + "</li>" +
                "<li>" + I18nService.getInstance().message("settings.annotation.schema.help.structure.2") + "</li>" +
                "<li>" + I18nService.getInstance().message("settings.annotation.schema.help.structure.3") + "</li>" +
                "</ul>" +
                
                "<h4>" + I18nService.getInstance().message("settings.annotation.schema.help.validation.title") + "</h4>" +
                "<ul>" +
                "<li>" + I18nService.getInstance().message("settings.annotation.schema.help.validation.1") + "</li>" +
                "<li>" + I18nService.getInstance().message("settings.annotation.schema.help.validation.2") + "</li>" +
                "<li>" + I18nService.getInstance().message("settings.annotation.schema.help.validation.3") + "</li>" +
                "<li>" + I18nService.getInstance().message("settings.annotation.schema.help.validation.4") + "</li>" +
                "<li>" + I18nService.getInstance().message("settings.annotation.schema.help.validation.5") + "</li>" +
                "</ul>" +
                
                "<h4>" + I18nService.getInstance().message("settings.annotation.schema.help.valueProvider.title") + "</h4>" +
                "<ul>" +
                "<li>" + I18nService.getInstance().message("settings.annotation.schema.help.valueProvider.1") + "</li>" +
                "</ul>" +
                "</html>");
        
        helpPanel.add(helpText, BorderLayout.CENTER);
        contentPanel.add(helpPanel, c);

        // Add content panel to main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private EditorTextField createSchemaEditor() {
        EditorTextField editor = new EditorTextField("", null, PlainTextFileType.INSTANCE) {
            @Override
            protected @NotNull EditorEx createEditor() {
                EditorEx editor = super.createEditor();
                editor.setVerticalScrollbarVisible(true);
                editor.setHorizontalScrollbarVisible(false);
                
                EditorSettings settings = editor.getSettings();
                settings.setFoldingOutlineShown(true);
                settings.setLineNumbersShown(true);
                settings.setLineMarkerAreaShown(true);
                settings.setIndentGuidesShown(true);
                settings.setUseSoftWraps(true);
                settings.setAdditionalLinesCount(3);
                settings.setAdditionalColumnsCount(3);
                settings.setRightMarginShown(true);

                // Set caret to the start
                editor.getCaretModel().moveToOffset(0);
                editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                
                return editor;
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, EDITOR_HEIGHT);
            }

            @Override
            public Dimension getMinimumSize() {
                return new Dimension(100, EDITOR_HEIGHT);
            }
        };
        editor.setOneLineMode(false);
        return editor;
    }

    private void addLabelAndField(JPanel panel, String labelText, JComponent field, int gridy, String tooltip) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;

        // Label
        c.gridx = 0;
        c.gridy = gridy;
        c.gridwidth = 1;
        c.weightx = 0.0;
        JLabel label = new JBLabel(labelText);
        label.setPreferredSize(new Dimension(150, label.getPreferredSize().height));
        panel.add(label, c);

        // Field
        c.gridx = 1;
        c.weightx = 1.0;
        field.setToolTipText(tooltip);
        panel.add(field, c);
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return schemaEditor;
    }

    @NotNull
    public String getSchemaText() {
        return schemaEditor.getText();
    }

    public void setSchemaText(@NotNull String text) {
        schemaEditor.setText(text);
    }

    @NotNull
    public String getPackageText() {
        return packageTextField.getText();
    }

    public void setPackageText(@NotNull String text) {
        packageTextField.setText(text);
    }

    public boolean isAutoImport() {
        return autoImportCheckBox.isSelected();
    }

    public void setAutoImport(boolean selected) {
        autoImportCheckBox.setSelected(selected);
    }

    public boolean isEnableValidation() {
        return enableValidationCheckBox.isSelected();
    }

    public void setEnableValidation(boolean selected) {
        enableValidationCheckBox.setSelected(selected);
    }
} 