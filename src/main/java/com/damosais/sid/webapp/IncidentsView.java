package com.damosais.sid.webapp;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.tepi.filtertable.FilterTable;

import com.damosais.sid.database.beans.Attack;
import com.damosais.sid.database.beans.Incident;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.services.AttackService;
import com.damosais.sid.database.services.IncidentService;
import com.damosais.sid.webapp.windows.AddAttackToIncidentWindow;
import com.damosais.sid.webapp.windows.IncidentWindow;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.CustomTable.ColumnGenerator;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

/**
 * This is screen from which users can view the different incidents and interact with them
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@SpringView(name = IncidentsView.VIEW_NAME)
public class IncidentsView extends VerticalLayout implements View, ColumnGenerator, ClickListener {
    private static final long serialVersionUID = -3062544653348114405L;
    public static final String VIEW_NAME = "IncidentsScreen";
    private static final String EDIT_BUTTON = "editButton";
    private static final String DELETE_BUTTON = "deleteButton";
    private static final String UPDATED_BY_NAME = "updatedBy.name";
    private static final String CREATED_BY_NAME = "createdBy.name";
    private BeanItemContainer<Incident> incidentsContainer;
    private BeanItemContainer<Attack> attacksContainer;
    private Button addIncident;
    private Button addAttack;
    private FilterTable incidentsTable;
    private FilterTable attacksTable;

    @Autowired
    private IncidentService incidentService;
    
    @Autowired
    private AttackService attackService;

    @Autowired
    private IncidentWindow incidentWindow;

    @Autowired
    private AddAttackToIncidentWindow addAttackWindow;
    
    /**
     * The constructor just sets the spacing and the margin
     */
    public IncidentsView() {
        setSpacing(true);
        setMargin(true);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        final Button button = event.getButton();
        final User user = ((WebApplication) getUI()).getUser();
        if (addIncident.equals(button) && user.getRoles().contains(UserRole.EDIT_DATA)) {
            incidentWindow.setAddMode(this);
            getUI().addWindow(incidentWindow);
        } else if (addAttack.equals(button) && user.getRoles().contains(UserRole.EDIT_DATA)) {
            final Incident incident = (Incident) incidentsTable.getValue();
            if (incident == null) {
                new Notification("Missing incident", "To add an attack you need to select an incident first. Please click on an incidet and try again", Type.ERROR_MESSAGE).show(getUI().getPage());
            } else {
                addAttackWindow.openAddWindow(incident, this);
                getUI().addWindow(addAttackWindow);
            }
        } else {
            // In this case we are dealing with the buttons of a row
            final Object item = button.getData();
            if (item instanceof Incident) {
                final Incident incident = (Incident) item;
                if (GraphicResources.EDIT_ICON.equals(button.getIcon())) {
                    incidentWindow.setEditMode(incident, this);
                    getUI().addWindow(incidentWindow);
                } else if (GraphicResources.DELETE_ICON.equals(button.getIcon()) && user.getRoles().contains(UserRole.EDIT_DATA)) {
                    incidentService.delete(incident);
                    refreshIncidentsTableContent();
                    refreshAttacksTableContent(null);
                }
            } else if (item instanceof Attack) {
                final Attack attack = (Attack) item;
                if (GraphicResources.DELETE_ICON.equals(button.getIcon()) && user.getRoles().contains(UserRole.EDIT_DATA)) {
                    final Incident incident = attack.getIncident();
                    incident.getAttacks().remove(attack);
                    attack.setIncident(null);
                    incidentService.save(incident);
                    attackService.save(attack);
                    refreshAttacksTableContent(incident);
                }
            }
        }
    }
    
    private void createButtons() {
        addIncident = new Button("Add Incident", this);
        addIncident.setStyleName("link");
        addIncident.setIcon(GraphicResources.ADD_ICON);
        addAttack = new Button("Add attak", this);
        addAttack.setStyleName("link");
        addAttack.setIcon(GraphicResources.ADD_ICON);
    }
    
    @Override
    public void enter(ViewChangeEvent event) {
        // We do nothing on enter
    }
    
    @Override
    public Object generateCell(CustomTable source, Object itemId, Object columnId) {
        // First we create a button and set its data with the incident
        final Button button = new Button("", this);
        button.setStyleName("link");
        // From the container we find the item with the ID itemId that is the one for which we are drawing the cell
        if (incidentsTable.equals(source)) {
            final BeanItem<Incident> currentItem = incidentsContainer.getItem(itemId);
            button.setData(currentItem.getBean());
        } else {
            final BeanItem<Attack> currentItem = attacksContainer.getItem(itemId);
            button.setData(currentItem.getBean());
        }
        // Then we check to which column the button belongs and add the corresponding action and icon
        if (EDIT_BUTTON.equals(columnId)) {
            button.setIcon(GraphicResources.EDIT_ICON);
        } else if (DELETE_BUTTON.equals(columnId)) {
            button.setIcon(GraphicResources.DELETE_ICON);
        }
        // Finally we return the button
        return button;
    }
    
    /**
     * When we start the EventsView we create the table and the buttons
     */
    @PostConstruct
    public void init() {
        // First thing we initialise the table
        initializeTables();
        // Now we create the buttons for the generic actions
        createButtons();
        // Now we add the objects to the view
        final Label incidentLabel = new Label("<center><p>In the <i>'Common Language'</i> an <b>Incident</b> is the top level of information and is defined as an <b>attacker</b> launching a set of <b>attacks</b> for a <b>motivation</b>.<br/>In this screen you can observe the existing incidents, edit, delete them, or add new ones.</p></center>", ContentMode.HTML);
        addComponent(incidentLabel);
        addComponent(addIncident);
        setComponentAlignment(addIncident, Alignment.MIDDLE_CENTER);
        addComponent(incidentsTable);
        setComponentAlignment(incidentsTable, Alignment.MIDDLE_CENTER);

        final Label attackLabel = new Label("<center><p>In the <b>Attacks</b> you can review the existing attacks and its definitions.<br/>Once you have selected an incident in the above table you will be able to assign and remove attacks that belong to it in the table below.</p></center>", ContentMode.HTML);
        addComponent(attackLabel);
        addComponent(addAttack);
        setComponentAlignment(addAttack, Alignment.TOP_CENTER);
        addComponent(attacksTable);
        setComponentAlignment(attacksTable, Alignment.TOP_CENTER);
    }
    
    private void initializeTables() {
        // We create the tables
        incidentsTable = new FilterTable();
        incidentsTable.setFilterBarVisible(true);
        attacksTable = new FilterTable();
        attacksTable.setFilterBarVisible(true);
        // We add a column with the button to edit and delete the incident
        incidentsTable.addGeneratedColumn(EDIT_BUTTON, this);
        incidentsTable.addGeneratedColumn(DELETE_BUTTON, this);
        // We add a column with the button to delete the attack
        attacksTable.addGeneratedColumn(DELETE_BUTTON, this);
        // Now we handle the containers
        incidentsContainer = new BeanItemContainer<>(Incident.class);
        incidentsContainer.addNestedContainerProperty(CREATED_BY_NAME);
        incidentsContainer.addNestedContainerProperty(UPDATED_BY_NAME);
        incidentsTable.setContainerDataSource(incidentsContainer);
        attacksContainer = new BeanItemContainer<>(Attack.class);
        attacksContainer.addNestedContainerProperty("tool.name");
        attacksContainer.addNestedContainerProperty(CREATED_BY_NAME);
        attacksContainer.addNestedContainerProperty(UPDATED_BY_NAME);
        attacksTable.setContainerDataSource(attacksContainer);
        // Now we define which columns are visible and what are going to be their names in the table header
        incidentsTable.setVisibleColumns(new Object[] { "start", "end", "attackers", "motivation", "created", CREATED_BY_NAME, "updated", UPDATED_BY_NAME, EDIT_BUTTON, DELETE_BUTTON });
        incidentsTable.setColumnHeaders(new String[] { "Start", "End", "Attackers", "Motivation", "Created", "Created by", "Last update", "Last update by", "Edit", "Delete" });
        attacksTable.setVisibleColumns(new Object[] { "start", "end", "tool.name", "vulnerability", "created", CREATED_BY_NAME, "updated", UPDATED_BY_NAME, DELETE_BUTTON });
        attacksTable.setColumnHeaders(new String[] { "Start", "End", "Tool", "Vulnerability", "Created", "Created by", "Last update", "Last update by", "Delete" });
        // We then align the buttons to the middle
        incidentsTable.setColumnAlignment(EDIT_BUTTON, CustomTable.Align.CENTER);
        incidentsTable.setColumnAlignment(DELETE_BUTTON, CustomTable.Align.CENTER);
        attacksTable.setColumnAlignment(DELETE_BUTTON, CustomTable.Align.CENTER);
        // Finally we add the selectable behaviour to the incidents table to link both tables
        incidentsTable.setSelectable(true);
        incidentsTable.setMultiSelect(false);
        incidentsTable.setImmediate(true);
        // Handle selection change.
        incidentsTable.addValueChangeListener(event -> refreshAttacksTableContent((Incident) incidentsTable.getValue()));
        // Now we refresh the content of the tables
        refreshIncidentsTableContent();
        refreshAttacksTableContent(null);
    }

    /**
     * Refreshes the table with the attacks data
     *
     * @param incident
     *            The incident currently showing
     */
    public void refreshAttacksTableContent(Incident incident) {
        // We first create the container with all the attacks and assign it to the table
        final List<Attack> attacks = new ArrayList<>();
        if (incident != null) {
            attacks.addAll(attackService.listByIncident(incident));
        }
        attacksContainer.removeAllItems();
        attacksContainer.addAll(attacks);
    }

    /**
     * Refreshes the table with the incidents data
     */
    public void refreshIncidentsTableContent() {
        // We first create the container with all the incidents and assign it to the table
        incidentsContainer.removeAllItems();
        incidentsContainer.addAll(incidentService.list());
    }
}