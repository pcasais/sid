package com.damosais.sid.webapp.windows;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.services.UserService;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.UsersView;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This class handles the window where the users are created or edited
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class UserWindow extends Window {
    private static final Logger LOGGER = Logger.getLogger(UserWindow.class);
    private static final long serialVersionUID = -1023199213750240331L;

    private final VerticalLayout content;
    
    @Autowired
    private UserService userService;
    
    /**
     * Creates a new window to add or edit tools
     */
    public UserWindow() {
        setModal(true);
        setSizeUndefined();
        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);
    }
    
    /**
     * Creates the form for the user
     *
     * @param user
     *            The user to add or edit
     * @param usersView
     *            The view that make the call
     * @param newItem
     *            indicates if we are creating or editing
     */
    private void createUserForm(User user, UsersView usersView, boolean newItem, boolean showRoles) {
        // 1st) We create the form and assign the binder
        final FormLayout form = new FormLayout();
        final BeanFieldGroup<User> binder = new BeanFieldGroup<>(User.class);
        binder.setItemDataSource(user);
        binder.setBuffered(true);

        // 2nd) We add a field for the name
        final TextField nameField = binder.buildAndBind("Name", "name", TextField.class);
        nameField.addValidator(new NullValidator("You need to provide a name", false));
        nameField.setNullRepresentation("");
        nameField.setWidth(100, Unit.PERCENTAGE);
        form.addComponent(nameField);
        
        // 3rd) We create the password field
        final PasswordField passwordField = new PasswordField(newItem ? "Password" : "Change password");
        if (newItem) {
            passwordField.addValidator(new StringLengthValidator("You need to define a password between 8 and 64 characters", 8, 64, false));
        } else {
            passwordField.addValueChangeListener(event -> {
                final Object value = event.getProperty().getValue();
                if (value != null && StringUtils.isNotBlank(value.toString())) {
                    passwordField.addValidator(new StringLengthValidator("You need to define a password between 8 and 64 characters", 8, 64, false));
                } else {
                    passwordField.removeAllValidators();
                }
            });
        }
        form.addComponent(passwordField);
        
        // 3rd) We add the selector for the type
        if (showRoles) {
            final ComboBox rolesField = new ComboBox("Role", Arrays.asList(UserRole.values()));
            rolesField.addValidator(new NullValidator("You need to select a role", false));
            rolesField.setWidth(100, Unit.PERCENTAGE);
            binder.bind(rolesField, "role");
            form.addComponent(rolesField);
        }
        
        // 4th) We add the checkbox to enable disable
        final CheckBox suspendedField = binder.buildAndBind("Suspended", "suspended", CheckBox.class);
        form.addComponent(suspendedField);

        // 5th) Now we create the save button if the user can edit data
        final Button saveButton = new Button("Save", event -> {
            try {
                binder.commit();
                saveTool(binder.getItemDataSource().getBean(), newItem, passwordField.getValue());
                usersView.refreshTableContent();
                new Notification("Success", "Tool saved in the database", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                getUI().removeWindow(this);
            } catch (final Exception e) {
                LOGGER.error("Problem saving tool in database", e);
                Throwable cause = e;
                while (cause.getCause() != null) {
                    cause = cause.getCause();
                }
                new Notification("Failure", "Error saving tool: " + cause.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
            }
        });
        saveButton.setStyleName("link");
        saveButton.setIcon(GraphicResources.SAVE_ICON);

        // 6th) We now clear the window and add the components
        content.removeAllComponents();
        content.addComponent(form);
        content.addComponent(saveButton);
        content.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
    }
    
    private void saveTool(User userCommited, boolean newItem, String password) throws GeneralSecurityException {
        if (newItem) {
            userCommited.setSalt(userService.generateSalt());
            userCommited.setPassword(userService.getEncryptedPassword(password, userCommited.getSalt()));
        } else if (StringUtils.isNotEmpty(password)) {
            userCommited.setPassword(userService.getEncryptedPassword(password, userCommited.getSalt()));
        }
        userService.save(userCommited);
    }

    /**
     * Prepares the window to add a new user
     *
     * @param usersView
     *            The view that called
     * @param showRoles
     *            Decides if the combo box to change roles should be shown
     */
    public void setAddMode(UsersView usersView, boolean showRoles) {
        setCaption("Adding new User");
        createUserForm(new User(), usersView, true, showRoles);
    }

    /**
     * Prepares the window to edit an existing user
     *
     * @param userToAlter
     *            user to be edited
     * @param usersView
     *            the view that called
     * @param showRoles
     *            Decides if the combo box to change roles should be shown
     */
    public void setEditMode(User userToAlter, UsersView usersView, boolean showRoles) {
        setCaption("Editing User");
        createUserForm(userToAlter, usersView, false, showRoles);
    }
}