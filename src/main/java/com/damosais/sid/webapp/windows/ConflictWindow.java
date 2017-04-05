package com.damosais.sid.webapp.windows;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.damosais.sid.database.beans.Conflict;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.services.ConflictService;
import com.damosais.sid.webapp.ConflictsView;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.WebApplication;
import com.damosais.sid.webapp.customfields.CountrySelector;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This class represents the window used to add or edit conflicts
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class ConflictWindow extends Window {
    private static final Logger LOGGER = Logger.getLogger(ConflictWindow.class);
    private static final long serialVersionUID = 564490309079766L;
    private final VerticalLayout content;

    @Autowired
    private ConflictService conflictService;
    
    /**
     * Creates a new window to add or edit conflicts
     */
    public ConflictWindow() {
        setModal(true);
        setSizeUndefined();
        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);
    }

    /**
     * Creates the form to add or edit conflicts
     *
     * @param conflict
     *            The conflict being edited or created
     * @param conflictsView
     *            The view which called
     * @param newItem
     *            indicates if we are creating or editing
     */
    private void createConflictForm(Conflict conflict, ConflictsView conflictsView, boolean newItem) {
        // 1st) We create the form and assign the binder
        final FormLayout form = new FormLayout();
        final BeanFieldGroup<Conflict> binder = new BeanFieldGroup<>(Conflict.class);
        binder.setItemDataSource(conflict);
        binder.setBuffered(true);
        
        // 2nd) We add the fields that are only one selection
        final DateField startField = binder.buildAndBind("Start", "start", DateField.class);
        startField.setResolution(Resolution.DAY);
        form.addComponent(startField);
        final DateField endField = binder.buildAndBind("End", "end", DateField.class);
        endField.setResolution(Resolution.DAY);
        form.addComponent(endField);
        final TextField nameField = binder.buildAndBind("Name", "name", TextField.class);
        nameField.addValidator(new NullValidator("You need to provide a name", false));
        nameField.setNullRepresentation("");
        form.addComponent(nameField);
        
        // 3rd) We now add the country (due to the lack of a toString() that shows proper content we need this hack)
        final CountrySelector location = new CountrySelector("Location", false);
        binder.bind(location, "location");
        form.addComponent(location);
        final CountrySelector partiesInvolved = new CountrySelector("Parties involved", false);
        binder.bind(partiesInvolved, "partiesInvolved");
        form.addComponent(partiesInvolved);
        
        // 4th) We now clear the window and add the components
        content.removeAllComponents();
        content.addComponent(form);

        // 5th) Now we create the save button if the user can edit data
        final User user = ((WebApplication) conflictsView.getUI()).getUser();
        if (user.getRole() == UserRole.EDIT_DATA) {
            final Button saveButton = new Button("Save", event -> {
                try {
                    binder.commit();
                    saveConflict(binder.getItemDataSource().getBean(), newItem, user);
                    conflictsView.refreshTableContent();
                    new Notification("Success", "Conflict saved in the database", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                    getUI().removeWindow(this);
                } catch (final Exception e) {
                    LOGGER.error("Problem saving conflict in database", e);
                    Throwable cause = e;
                    while (cause.getCause() != null) {
                        cause = cause.getCause();
                    }
                    new Notification("Failure", "Error saving conflict: " + cause.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
                }
            });
            saveButton.setStyleName("link");
            saveButton.setIcon(GraphicResources.SAVE_ICON);
            content.addComponent(saveButton);
            content.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
        }
    }

    private void saveConflict(Conflict conflict, boolean newItem, User user) {
        if (newItem) {
            conflict.setCreatedBy(user);
        } else {
            conflict.setUpdatedBy(user);
        }
        conflictService.save(conflict);
    }

    /**
     * Prepares the window to add a new conflict
     *
     * @param conflictsView
     *            The view that called
     */
    public void setAddMode(ConflictsView conflictsView) {
        setCaption("Adding new conflict");
        createConflictForm(new Conflict(), conflictsView, true);
    }
    
    /**
     * Prepares the window to edit an existing conflict
     *
     * @param conflictToAlter
     *            conflict to be edited
     * @param conflictsView
     *            the view that called
     */
    public void setEditMode(Conflict conflictToAlter, ConflictsView conflictsView) {
        setCaption("Editing conflict");
        createConflictForm(conflictToAlter, conflictsView, false);
    }
}