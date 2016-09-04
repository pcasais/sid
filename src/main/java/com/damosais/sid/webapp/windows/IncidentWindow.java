package com.damosais.sid.webapp.windows;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tepi.filtertable.FilterTable;

import com.damosais.sid.database.beans.Attacker;
import com.damosais.sid.database.beans.Incident;
import com.damosais.sid.database.beans.Motivation;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.services.AttackerService;
import com.damosais.sid.database.services.IncidentService;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.IncidentsView;
import com.damosais.sid.webapp.WebApplication;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItemContainer;
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
     * @param newItem
     *            indicates if we are creating or editing
     */
    private void createIncidentForm(Incident incident, IncidentsView incidentsView, boolean newItem) {
        // 1st) We create the form and assign the binder
        final FormLayout form = new FormLayout();
        final BeanFieldGroup<Incident> binder = new BeanFieldGroup<>(Incident.class);
        binder.setItemDataSource(incident);
        binder.setBuffered(true);

        // 2nd) Then we add the name of the incident
        final TextField nameField = binder.buildAndBind("Name", "name", TextField.class);
        nameField.addValidator(new NullValidator("You need to provide a name", false));
        nameField.setNullRepresentation("");
        nameField.setNullSettingAllowed(true);
        form.addComponent(nameField);

        // 5th) Now we add a table with the Attackers so they can be selected
        final FilterTable attackersTable = new FilterTable();
        attackersTable.addValidator(new NullValidator("You need to select the attackers", false));
        attackersTable.setFilterBarVisible(true);
        final BeanItemContainer<Attacker> attackerContainer = new BeanItemContainer<>(Attacker.class, attackerService.list());
        attackerContainer.addNestedContainerProperty("country.name");
        attackersTable.setContainerDataSource(attackerContainer);
        // Now we define which columns are visible and what are going to be their names in the table header
        attackersTable.setVisibleColumns(new Object[] { "name", "country.name", "type" });
        attackersTable.setColumnHeaders(new String[] { "Name", "Country", "Type" });
        // Finally we add the selectable behaviour
        attackersTable.setNullSelectionAllowed(false);
        attackersTable.setSelectable(true);
        attackersTable.setMultiSelect(true);
        attackersTable.setImmediate(true);
        form.addComponent(attackersTable);
        if (incident.getAttackers() != null && !incident.getAttackers().isEmpty()) {
            for (final Attacker attacker : incident.getAttackers()) {
                attackersTable.select(attacker);
            }
        }
        
        // 6th) We add the ComboBox with all the motivations
        final ComboBox motivationField = new ComboBox("Motivation", Arrays.asList(Motivation.values()));
        motivationField.addValidator(new NullValidator("You need to select a motivation", false));
        binder.bind(motivationField, "motivation");
        form.addComponent(motivationField);

        // 7th) We now clear the window and add the components
        content.removeAllComponents();
        content.addComponent(form);

        // 8th) Now we create the save button if the user can edit data
        final User user = ((WebApplication) incidentsView.getUI()).getUser();
        if (user.getRole() == UserRole.EDIT_DATA) {
            final Button saveButton = new Button("Save", event -> {
                try {
                    binder.commit();
                    saveIncident(binder.getItemDataSource().getBean(), attackersTable.getValue(), newItem, user);
                    incidentsView.refreshIncidentsTableContent();
                    new Notification("Success", "Incident saved in the database", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                    getUI().removeWindow(this);
                } catch (final CommitException e) {
                    LOGGER.error("Problem saving incident in database", e);
                    Throwable cause = e;
                    while (cause.getCause() != null) {
                        cause = cause.getCause();
                    }
                    new Notification("Failure", "Error saving incident: " + cause.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
                }
            });
            saveButton.setStyleName("link");
            saveButton.setIcon(GraphicResources.SAVE_ICON);
            content.addComponent(saveButton);
            content.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void saveIncident(Incident commitedIncident, Object object, boolean newItem, User user) {
        if (commitedIncident.getAttackers() == null) {
            commitedIncident.setAttackers(new HashSet<>());
        } else {
            commitedIncident.getAttackers().clear();
        }
        if (object instanceof Set) {
            final Set<Attacker> attackers = (Set<Attacker>) object;
            commitedIncident.getAttackers().addAll(attackers);
        } else if (object instanceof Attacker) {
            commitedIncident.getAttackers().add((Attacker) object);
        }
        if (newItem) {
            commitedIncident.setCreatedBy(user);
        } else {
            commitedIncident.setUpdatedBy(user);
        }
        incidentService.save(commitedIncident);
    }
    
    /**
     * Prepares the window to add a new incident
     *
     * @param incidentsView
     *            The view that called
     */
    public void setAddMode(IncidentsView incidentsView) {
        setCaption("Adding new incident");
        createIncidentForm(new Incident(), incidentsView, true);
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
        createIncidentForm(incidentToAlter, incidentsView, false);
    }
}