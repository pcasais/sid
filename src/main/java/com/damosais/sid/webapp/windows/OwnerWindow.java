package com.damosais.sid.webapp.windows;

import java.util.EnumSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.damosais.sid.database.beans.Owner;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.services.OwnerService;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.OwnersView;
import com.damosais.sid.webapp.WebApplication;
import com.damosais.sid.webapp.customfields.CountryFieldConverter;
import com.damosais.sid.webapp.customfields.SectorField;
import com.neovisionaries.i18n.CountryCode;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This is the window to edit or add owners (victims) to the application
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class OwnerWindow extends Window {
    private static final Logger LOGGER = Logger.getLogger(OwnerWindow.class);
    private static final long serialVersionUID = 8434835262821274799L;
    private final VerticalLayout content;

    @Autowired
    private OwnerService ownerService;

    /**
     * Creates a new window to add or edit owners (victims)
     */
    public OwnerWindow() {
        setModal(true);
        setSizeUndefined();
        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);
    }

    /**
     * Creates the form to add or edit owners (victims)
     *
     * @param owner
     *            the owner (victim) being created or edited
     * @param ownersView
     *            the view which called
     * @param newItem
     *            indicates if we are creating or editing
     */
    private void createOwnerForm(Owner owner, OwnersView ownersView, boolean newItem) {
        // 1st) We clear the form and create the new binder
        final FormLayout form = new FormLayout();
        final BeanFieldGroup<Owner> binder = new BeanFieldGroup<>(Owner.class);
        binder.setItemDataSource(owner);
        binder.setBuffered(true);

        // 2nd) We add the name field
        final TextField nameField = binder.buildAndBind("Name", "name", TextField.class);
        nameField.setNullRepresentation("");
        nameField.addValidator(new NullValidator("You need to specify a name", false));
        form.addComponent(nameField);

        // 3rd) We add the country (due to the lack of a toString() that shows proper content we need this hack)
        final BeanContainer<Integer, CountryCode> countryContainer = new BeanContainer<>(CountryCode.class);
        countryContainer.setBeanIdProperty("numeric");
        countryContainer.addAll(EnumSet.allOf(CountryCode.class));
        final ComboBox countryField = new ComboBox("Country", countryContainer);
        countryField.addValidator(new NullValidator("You need to select a country", false));
        countryField.setItemCaptionPropertyId("name");
        countryField.setConverter(new CountryFieldConverter());
        binder.bind(countryField, "country");
        form.addComponent(countryField);

        // 4th) We then add the sector which is a custom field with a tree
        final SectorField sectorField = new SectorField();
        sectorField.addValidator(new NullValidator("You need to select a sector", false));
        binder.bind(sectorField, "sector");
        form.addComponent(sectorField);

        // 5th) We clear the form and add the elements to it
        content.removeAllComponents();
        content.addComponent(form);

        // 6th) Now we create the save button if the user can edit data
        final User user = ((WebApplication) ownersView.getUI()).getUser();
        if (user.getRoles().contains(UserRole.EDIT_DATA)) {
            final Button saveButton = new Button("Save", event -> {
                try {
                    binder.commit();
                    saveOwner(binder.getItemDataSource().getBean(), newItem, user);
                    ownersView.refreshOwnersTableContent();
                    new Notification("Success", "Owner (victim) saved in the database", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                    getUI().removeWindow(this);
                } catch (final Exception e) {
                    LOGGER.error("Problem saving owner (victim) in database", e);
                    Throwable cause = e;
                    while (cause.getCause() != null) {
                        cause = cause.getCause();
                    }
                    new Notification("Failure", "Error saving owner (victim): " + cause.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
                }
            });
            saveButton.setStyleName("link");
            saveButton.setIcon(GraphicResources.SAVE_ICON);
            
            content.addComponent(saveButton);
            content.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
        }
    }

    private void saveOwner(Owner commitedOwner, boolean newItem, User user) {
        if (newItem) {
            commitedOwner.setCreatedBy(user);
        } else {
            commitedOwner.setUpdatedBy(user);
        }
        ownerService.save(commitedOwner);
    }

    /**
     * Prepares the window to add a new victim (owner)
     *
     * @param ownersView
     *            The view that called
     */
    public void setAddMode(OwnersView ownersView) {
        setCaption("Adding new victim");
        final Owner newOwner = new Owner();
        createOwnerForm(newOwner, ownersView, true);
    }
    
    /**
     * Prepares the window to edit an existing victim (owner)
     *
     * @param ownerToAlter
     *            owner to be edited
     * @param ownersView
     *            the view that called
     */
    public void setEditMode(Owner ownerToAlter, OwnersView ownersView) {
        setCaption("Editing victim");
        createOwnerForm(ownerToAlter, ownersView, false);
    }
}