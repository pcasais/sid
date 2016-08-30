package com.damosais.sid.webapp.windows;

import java.util.Arrays;
import java.util.EnumSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.damosais.sid.database.beans.Attacker;
import com.damosais.sid.database.beans.AttackerType;
import com.damosais.sid.database.services.AttackerService;
import com.damosais.sid.webapp.AttackersView;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.customfields.CountryFieldConverter;
import com.neovisionaries.i18n.CountryCode;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This class represents the window used to add or edit attackers
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class AttackerWindow extends Window {
    private static final Logger LOGGER = Logger.getLogger(AttackWindow.class);
    private static final long serialVersionUID = 1010720438789412977L;
    private final VerticalLayout content;

    @Autowired
    private AttackerService attackerService;
    
    /**
     * Creates a new window to add or edit attackers
     */
    public AttackerWindow() {
        setModal(true);
        setSizeUndefined();
        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);
    }

    /**
     * Creates the form to add or edit attackers
     *
     * @param attacker
     *            The attacker being edited or created
     * @param attackersView
     *            The view which called
     */
    private void createIncidentForm(Attacker attacker, AttackersView attackersView) {
        // 1st) We create the form and assign the binder
        final FormLayout form = new FormLayout();
        final BeanFieldGroup<Attacker> binder = new BeanFieldGroup<>(Attacker.class);
        binder.setItemDataSource(attacker);
        binder.setBuffered(true);
        
        // 2nd) We add the name
        final TextField nameField = new TextField("Name");
        nameField.setNullRepresentation("");
        nameField.setNullSettingAllowed(false);
        binder.bind(nameField, "name");
        form.addComponent(nameField);
        
        // 3rd) We now add the country (due to the lack of a toString() that shows proper content we need this hack)
        final BeanContainer<Integer, CountryCode> countryContainer = new BeanContainer<>(CountryCode.class);
        countryContainer.setBeanIdProperty("numeric");
        countryContainer.addAll(EnumSet.allOf(CountryCode.class));
        final ComboBox countryField = new ComboBox("Country", countryContainer);
        countryField.setItemCaptionPropertyId("name");
        countryField.setConverter(new CountryFieldConverter());
        binder.bind(countryField, "country");
        form.addComponent(countryField);
        
        // 4th) We add the type of attacker
        final ComboBox typeField = new ComboBox("Type", Arrays.asList(AttackerType.values()));
        binder.bind(typeField, "type");
        form.addComponent(typeField);
        
        // 6th) We create the save button
        final Button saveButton = new Button("Save", event -> {
            try {
                binder.commit();
                final Attacker commitedAttacker = binder.getItemDataSource().getBean();
                attackerService.save(commitedAttacker);
                attackersView.refreshTableContent();
                new Notification("Success", "Attacker saved in the database", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                getUI().removeWindow(this);
            } catch (final CommitException e) {
                LOGGER.error("Problem saving attacker in database", e);
                new Notification("Failure", "Error saving attacker: " + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
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
     * Prepares the window to add a new attacker
     *
     * @param attackersView
     *            The view that called
     */
    public void setAddMode(AttackersView attackersView) {
        setCaption("Adding new attacker");
        createIncidentForm(new Attacker(), attackersView);
    }
    
    /**
     * Prepares the window to edit an existing attacker
     *
     * @param attackerToAlter
     *            attacker to be edited
     * @param attackersView
     *            the view that called
     */
    public void setEditMode(Attacker attackerToAlter, AttackersView attackersView) {
        setCaption("Editing attacker");
        createIncidentForm(attackerToAlter, attackersView);
    }
}