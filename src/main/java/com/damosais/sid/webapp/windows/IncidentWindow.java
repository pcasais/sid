package com.damosais.sid.webapp.windows;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.damosais.sid.database.beans.Attacker;
import com.damosais.sid.database.beans.Incident;
import com.damosais.sid.database.beans.Motivation;
import com.damosais.sid.database.services.AttackerService;
import com.damosais.sid.database.services.IncidentService;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.IncidentsView;
import com.damosais.sid.webapp.customfields.ListToSetConverter;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This class represents the window used to add or edit incidents
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class IncidentWindow extends Window {
    private static final Logger LOGGER = Logger.getLogger(AttackWindow.class);
    private static final long serialVersionUID = -2509114793374353015L;
    private final VerticalLayout content;
    
    @Autowired
    private IncidentService incidentService;

    @Autowired
    private AttackerService attackerService;

    /**
     * Creates a new window to add or edit incidents
     */
    public IncidentWindow() {
        setModal(true);
        setSizeUndefined();
        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);
    }
    
    /**
     * Creates the form to add or edit incidents
     *
     * @param incident
     *            The incident being edited or created
     * @param incidentsView
     *            The view which called
     */
    private void createIncidentForm(Incident incident, IncidentsView incidentsView) {
        // 1st) We create the form and assign the binder
        final FormLayout form = new FormLayout();
        final BeanFieldGroup<Incident> binder = new BeanFieldGroup<>(Incident.class);
        binder.setItemDataSource(incident);
        binder.setBuffered(true);

        // 2nd) We add the start date
        form.addComponent(binder.buildAndBind("Start", "start"));
        
        // 3rd) we add the end date
        form.addComponent(binder.buildAndBind("End", "end"));

        // 4th) Then we add the name of the incident
        final TextField nameField = new TextField("Name");
        nameField.setNullRepresentation("");
        nameField.setNullSettingAllowed(true);
        binder.bind(nameField, "name");
        form.addComponent(nameField);

        // 5th) Now we add a list with the Attackers so they can be selected
        final ListSelect attackersField = new ListSelect("Attackers", attackerService.list());
        attackersField.setMultiSelect(true);
        attackersField.setRows(5);
        attackersField.setConverter(new ListToSetConverter<Attacker>());
        binder.bind(attackersField, "attackers");
        form.addComponent(attackersField);
        
        // 6th) We add the ComboBox with all the motivations
        final ComboBox motivationField = new ComboBox("Motivation", Arrays.asList(Motivation.values()));
        binder.bind(motivationField, "motivation");
        form.addComponent(motivationField);

        // 7th) We create the save button
        final Button saveButton = new Button("Save", event -> {
            try {
                binder.commit();
                final Incident commitedIncident = binder.getItemDataSource().getBean();
                incidentService.save(commitedIncident);
                incidentsView.refreshIncidentsTableContent();
                new Notification("Success", "Incident saved in the database", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                getUI().removeWindow(this);
            } catch (final CommitException e) {
                LOGGER.error("Problem saving incident in database", e);
                new Notification("Failure", "Error saving incident: " + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
            }
        });
        saveButton.setStyleName("link");
        saveButton.setIcon(GraphicResources.SAVE_ICON);

        // 8th) We now clear the window and add the components
        content.removeAllComponents();
        content.addComponent(form);
        content.addComponent(saveButton);
        content.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
    }
    
    /**
     * Prepares the window to add a new incident
     *
     * @param incidentsView
     *            The view that called
     */
    public void setAddMode(IncidentsView incidentsView) {
        setCaption("Adding new incident");
        createIncidentForm(new Incident(), incidentsView);
    }

    /**
     * Prepares the window to edit an existing incident
     *
     * @param incidentToAlter
     *            incident to be edited
     * @param incidentsView
     *            the view that called
     */
    public void setEditMode(Incident incidentToAlter, IncidentsView incidentsView) {
        setCaption("Editing incident");
        createIncidentForm(incidentToAlter, incidentsView);
    }
}