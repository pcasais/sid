package com.damosais.sid.webapp.windows;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.damosais.sid.database.beans.Tool;
import com.damosais.sid.database.beans.ToolType;
import com.damosais.sid.database.services.ToolService;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.ToolsView;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This class handles the window where the Tools are created or edited
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class ToolWindow extends Window {
    private static final Logger LOGGER = Logger.getLogger(ToolWindow.class);
    private static final long serialVersionUID = 8053468344747983876L;
    
    private final VerticalLayout content;

    @Autowired
    private ToolService toolService;

    /**
     * Creates a new window to add or edit tools
     */
    public ToolWindow() {
        setModal(true);
        setSizeUndefined();
        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);
    }

    /**
     * Creates the form for the tool
     *
     * @param tool
     *            The tool to add or edit
     * @param toolsView
     *            The view that make the call
     */
    private void createDefinitionForm(Tool tool, ToolsView toolsView) {
        // 1st) We create the form and assign the binder
        final FormLayout form = new FormLayout();
        final BeanFieldGroup<Tool> binder = new BeanFieldGroup<>(Tool.class);
        binder.setItemDataSource(tool);
        binder.setBuffered(true);
        
        // 2nd) We add a field for the name
        final TextField nameField = new TextField("Name");
        nameField.setNullRepresentation("");
        nameField.setWidth(100, Unit.PERCENTAGE);
        binder.bind(nameField, "name");
        form.addComponent(nameField);

        // 3rd) We add the selector for the type
        final ComboBox toolTypeField = new ComboBox("Type", Arrays.asList(ToolType.values()));
        toolTypeField.setWidth(100, Unit.PERCENTAGE);
        binder.bind(toolTypeField, "type");
        form.addComponent(toolTypeField);
        
        // 9th) We create the save button
        final Button saveButton = new Button("Save", event -> {
            try {
                binder.commit();
                final Tool toolCommited = binder.getItemDataSource().getBean();
                toolService.save(toolCommited);
                toolsView.refreshTableContent();
                new Notification("Success", "Tool saved in the database", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                getUI().removeWindow(this);
            } catch (final CommitException e) {
                LOGGER.error("Problem saving tool in database", e);
                new Notification("Failure", "Error saving tool: " + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
            }
        });
        saveButton.setStyleName("link");
        saveButton.setIcon(GraphicResources.SAVE_ICON);
        
        // 7th) We now clear the window and add the components
        content.removeAllComponents();
        content.addComponent(form);
        content.addComponent(saveButton);
        content.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
    }

    /**
     * Prepares the window to add a new tool
     *
     * @param toolsView
     *            The view that called
     */
    public void setAddMode(ToolsView toolsView) {
        setCaption("Adding new Tool");
        createDefinitionForm(new Tool(), toolsView);
    }
    
    /**
     * Prepares the window to edit an existing tool
     *
     * @param toolToAlter
     *            tool to be edited
     * @param toolsView
     *            the view that called
     */
    public void setEditMode(Tool toolToAlter, ToolsView toolsView) {
        setCaption("Editing Tool");
        createDefinitionForm(toolToAlter, toolsView);
    }
}