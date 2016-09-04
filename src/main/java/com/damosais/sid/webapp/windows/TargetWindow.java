package com.damosais.sid.webapp.windows;

import java.util.Collection;
import java.util.EnumSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.damosais.sid.database.beans.Owner;
import com.damosais.sid.database.beans.Target;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.services.TargetService;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.OwnersView;
import com.damosais.sid.webapp.WebApplication;
import com.damosais.sid.webapp.customfields.ArrayListFieldConverter;
import com.damosais.sid.webapp.customfields.CountryFieldConverter;
import com.damosais.sid.webapp.customfields.IPsField;
import com.neovisionaries.i18n.CountryCode;
import com.vaadin.data.Validator.InvalidValueException;
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
 * This class handles the window where targets are created or edited
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class TargetWindow extends Window {
    private static final Logger LOGGER = Logger.getLogger(TargetWindow.class);
    private static final long serialVersionUID = 8434835262821274799L;
    private final VerticalLayout content;

    @Autowired
    private TargetService targetService;
    
    /**
     * Creates a new window to add or edit targets
     */
    public TargetWindow() {
        setModal(true);
        setSizeUndefined();
        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);
    }
    
    /**
     * Creates the form to add or edit targets
     *
     * @param target
     *            The target being added or edited
     * @param ownersView
     *            the view which called
     * @param newItem
     *            indicates if we are creating or editing
     */
    @SuppressWarnings("rawtypes")
    private void createTargetForm(Target target, OwnersView ownersView, boolean newItem) {
        // 1st) We initialise the form and set the binder
        final FormLayout form = new FormLayout();
        final BeanFieldGroup<Target> binder = new BeanFieldGroup<>(Target.class);
        binder.setItemDataSource(target);
        binder.setBuffered(true);

        // 2nd) We set the site name
        final TextField nameField = binder.buildAndBind("Site Name", "siteName", TextField.class);
        nameField.addValidator(new NullValidator("You need to specify a site name", false));
        nameField.setNullRepresentation("");
        form.addComponent(nameField);

        // 3rd) We create the field with the IPs (which is a custom field)
        final IPsField ipsField = new IPsField();
        ipsField.addValidator(value -> {
            if (!(value instanceof Collection) || ((Collection) value).isEmpty()) {
                throw new InvalidValueException("You need to specify at least 1 IP");
            }
        });
        ipsField.setConverter(new ArrayListFieldConverter());
        binder.bind(ipsField, "ips");
        form.addComponent(ipsField);

        // 4th) We now add the country (due to the lack of a toString() that shows proper content we need this hack)
        final BeanContainer<Integer, CountryCode> countryContainer = new BeanContainer<>(CountryCode.class);
        countryContainer.setBeanIdProperty("numeric");
        countryContainer.addAll(EnumSet.allOf(CountryCode.class));
        final ComboBox countryField = new ComboBox("Country", countryContainer);
        countryField.addValidator(new NullValidator("You need to select a country", false));
        countryField.setItemCaptionPropertyId("name");
        countryField.setConverter(new CountryFieldConverter());
        binder.bind(countryField, "country");
        form.addComponent(countryField);

        // 5th) We clear the form and add the elements to it
        content.removeAllComponents();
        content.addComponent(form);

        // 6th) Now we create the save button if the user can edit data
        final User user = ((WebApplication) ownersView.getUI()).getUser();
        if (user.getRole() == UserRole.EDIT_DATA) {
            final Button saveButton = new Button("Save", event -> {
                try {
                    binder.commit();
                    final Target commitedTarget = saveTarget(binder.getItemDataSource().getBean(), newItem, user);
                    ownersView.refreshTargetsTableContent(commitedTarget.getOwner());
                    new Notification("Success", "Target saved in the database", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                    getUI().removeWindow(this);
                } catch (final Exception e) {
                    LOGGER.error("Problem saving target in database", e);
                    Throwable cause = e;
                    while (cause.getCause() != null) {
                        cause = cause.getCause();
                    }
                    new Notification("Failure", "Error saving target: " + cause.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
                }
            });
            saveButton.setStyleName("link");
            saveButton.setIcon(GraphicResources.SAVE_ICON);
            content.addComponent(saveButton);
            content.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
        }
    }
    
    private Target saveTarget(Target commitedTarget, boolean newItem, User user) {
        if (newItem) {
            commitedTarget.setCreatedBy(user);
        } else {
            commitedTarget.setUpdatedBy(user);
        }
        targetService.save(commitedTarget);
        return commitedTarget;
    }

    /**
     * Prepares the window to add a new target to an owner
     *
     * @param owner
     *            the owner to which we will add the target
     * @param ownersView
     *            The view that called
     */
    public void setAddMode(Owner owner, OwnersView ownersView) {
        setCaption("Adding new target to owner");
        final Target newTarget = new Target();
        newTarget.setOwner(owner);
        createTargetForm(newTarget, ownersView, true);
    }

    /**
     * Prepares the window to edit an existing target of an owner
     *
     * @param targetToAlter
     *            target to be edited
     * @param ownersView
     *            the view that called
     */
    public void setEditMode(Target targetToAlter, OwnersView ownersView) {
        setCaption("Editing target");
        createTargetForm(targetToAlter, ownersView, false);
    }
}