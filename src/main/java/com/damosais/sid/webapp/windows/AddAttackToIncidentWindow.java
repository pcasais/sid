package com.damosais.sid.webapp.windows;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tepi.filtertable.FilterTable;

import com.damosais.sid.database.beans.Attack;
import com.damosais.sid.database.beans.Incident;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.services.AttackService;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.IncidentsView;
import com.damosais.sid.webapp.WebApplication;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This class allows the user to select an existing attack to add to an existing incident
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class AddAttackToIncidentWindow extends Window {
    private static final Logger LOGGER = Logger.getLogger(AddEventToAttackWindow.class);
    private static final long serialVersionUID = -507586143880266424L;
    private final VerticalLayout content;

    @Autowired
    private AttackService attackService;

    /**
     * Creates a new window to add attacks to an existing incident
     */
    public AddAttackToIncidentWindow() {
        setModal(true);
        setSizeUndefined();
        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);
        setCaption("Adding attack to incident");
    }

    /**
     * Prepares the window to add an existing attack to an incident
     *
     * @param incidetToAlter
     *            the incident to which we will add the attack
     * @param incidentsView
     *            the view that called
     */
    public void openAddWindow(Incident incidetToAlter, IncidentsView incidentsView) {
        // 1st) We initialise the form and add the combo box (we only add events that are not assigned)
        final VerticalLayout form = new VerticalLayout();
        form.addComponent(new Label("If there are no attacks in the table below is because they have already all been assigned to an incident"));
        final List<Attack> attacksFiltered = attackService.list().stream().filter(attack -> attack.getIncident() == null).collect(Collectors.toList());

        final FilterTable attacksTable = new FilterTable();
        attacksTable.setFilterBarVisible(true);
        final BeanItemContainer<Attack> attacksContainer = new BeanItemContainer<>(Attack.class);
        attacksContainer.addNestedContainerProperty("tool.name");
        attacksTable.setContainerDataSource(attacksContainer);
        // Now we define which columns are visible and what are going to be their names in the table header
        attacksTable.setVisibleColumns(new Object[] { "start", "end", "tool.name", "vulnerability" });
        attacksTable.setColumnHeaders(new String[] { "Start", "End", "Tool", "Vulnerability" });
        // Finally we add the selectable behaviour
        attacksTable.setSelectable(true);
        attacksTable.setMultiSelect(false);
        attacksTable.setImmediate(true);
        attacksContainer.addAll(attacksFiltered);
        form.addComponent(attacksTable);
        
        // 2nd) Now we create the button to save
        final User user = ((WebApplication) incidentsView.getUI()).getUser();
        final Button saveButton = new Button("Save", event -> {
            try {
                final Attack selectedAttack = (Attack) attacksTable.getValue();
                if (selectedAttack != null) {
                    selectedAttack.setIncident(incidetToAlter);
                }
                selectedAttack.setUpdatedBy(user);
                attackService.save(selectedAttack);
                incidentsView.refreshAttacksTableContent(incidetToAlter);
                new Notification("Success", "Attack added to incident in the database", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                getUI().removeWindow(this);
            } catch (final Exception e) {
                LOGGER.error("Problem saving attack in database", e);
                Throwable cause = e;
                while (cause.getCause() != null) {
                    cause = cause.getCause();
                }
                new Notification("Failure", "Error saving attack: " + cause.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
            }
        });
        saveButton.setStyleName("link");
        saveButton.setIcon(GraphicResources.SAVE_ICON);
        
        // 3rd) Finally we clear the window and add the components
        content.removeAllComponents();
        content.addComponent(form);
        content.addComponent(saveButton);
        content.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
    }
}