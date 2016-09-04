package com.damosais.sid.webapp;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.tepi.filtertable.FilterTable;

import com.damosais.sid.database.beans.Attack;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.services.AttackService;
import com.damosais.sid.database.services.EventService;
import com.damosais.sid.webapp.windows.AddEventToAttackWindow;
import com.damosais.sid.webapp.windows.AttackWindow;
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
 * This is screen from which users can view the different attacks and interact with them
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@SpringView(name = AttacksView.VIEW_NAME)
public class AttacksView extends VerticalLayout implements View, ColumnGenerator, ClickListener {
    private static final long serialVersionUID = 4861130650154980237L;
    public static final String VIEW_NAME = "AttacksScreen";
    private static final String EDIT_BUTTON = "editButton";
    private static final String DELETE_BUTTON = "deleteButton";
    private static final String UPDATED_BY_NAME = "updatedBy.name";
    private static final String CREATED_BY_NAME = "createdBy.name";
    private BeanItemContainer<Attack> attacksContainer;
    private BeanItemContainer<com.damosais.sid.database.beans.Event> eventsContainer;
    private Button addAttack;
    private Button addEvent;
    private FilterTable attacksTable;
    private FilterTable eventsTable;

    @Autowired
    private AttackService attackService;

    @Autowired
    private EventService eventService;
    
    @Autowired
    private AttackWindow attackWindow;
    
    @Autowired
    private AddEventToAttackWindow addEventWindow;
    
    /**
     * The constructor just sets the spacing and the margin
     */
    public AttacksView() {
        setSpacing(true);
        setMargin(true);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        final Button button = event.getButton();
        final User user = ((WebApplication) getUI()).getUser();
        if (addAttack.equals(button) && user.getRoles().contains(UserRole.EDIT_DATA)) {
            attackWindow.setAddMode(this);
            getUI().addWindow(attackWindow);
        } else if (addEvent.equals(button) && user.getRoles().contains(UserRole.EDIT_DATA)) {
            final Attack attack = (Attack) attacksTable.getValue();
            if (attack == null) {
                new Notification("Missing attack", "To add an event you need to select an attack first. Please click on an attack and try again", Type.ERROR_MESSAGE).show(getUI().getPage());
            } else {
                addEventWindow.openAddWindow(attack, this);
                getUI().addWindow(addEventWindow);
            }
        } else {
            // In this case we are dealing with the buttons of a row
            final Object item = button.getData();
            if (item instanceof Attack) {
                final Attack attack = (Attack) item;
                if (GraphicResources.EDIT_ICON.equals(button.getIcon())) {
                    attackWindow.setEditMode(attack, this);
                    getUI().addWindow(attackWindow);
                } else if (GraphicResources.DELETE_ICON.equals(button.getIcon()) && user.getRoles().contains(UserRole.EDIT_DATA)) {
                    attackService.delete(attack);
                    refreshAttacksTableContent();
                    refreshEventsTableContent(null);
                }
            } else if (item instanceof com.damosais.sid.database.beans.Event) {
                final com.damosais.sid.database.beans.Event eventToAlter = (com.damosais.sid.database.beans.Event) item;
                if (GraphicResources.DELETE_ICON.equals(button.getIcon()) && user.getRoles().contains(UserRole.EDIT_DATA)) {
                    final Attack attack = eventToAlter.getAttack();
                    attack.getEvents().remove(eventToAlter);
                    eventToAlter.setAttack(null);
                    attackService.save(attack);
                    eventService.save(eventToAlter);
                    refreshEventsTableContent(attack);
                }
            }
        }
    }
    
    private void createButtons() {
        addAttack = new Button("Add attak", this);
        addAttack.setStyleName("link");
        addAttack.setIcon(GraphicResources.ADD_ICON);
        addEvent = new Button("Add Event", this);
        addEvent.setStyleName("link");
        addEvent.setIcon(GraphicResources.ADD_ICON);
    }
    
    @Override
    public void enter(ViewChangeEvent event) {
        // We do nothing on enter
    }
    
    @Override
    public Object generateCell(CustomTable source, Object itemId, Object columnId) {
        // First we create a button and set its data with the Computer
        final Button button = new Button("", this);
        button.setStyleName("link");
        // From the container we find the item with the ID itemId that is the one for which we are drawing the cell
        if (attacksTable.equals(source)) {
            final BeanItem<Attack> currentItem = attacksContainer.getItem(itemId);
            button.setData(currentItem.getBean());
        } else {
            final BeanItem<com.damosais.sid.database.beans.Event> currentItem = eventsContainer.getItem(itemId);
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
        final Label attackLabel = new Label("<center><p>In the <i>'Common Language'</i> an <b>Attack</b> is the second level of information and is defined as the execution of an <b>tool</b> to exploit a <b>vulnerability</b> to cause a set of <b>events</b> to achieve an <b>unauthorised result</b>.<br/>In this screen you can observe the existing attacks, edit, delete them, or add new ones.</p></center>", ContentMode.HTML);
        addComponent(attackLabel);
        addComponent(addAttack);
        setComponentAlignment(addAttack, Alignment.TOP_CENTER);
        addComponent(attacksTable);
        setComponentAlignment(attacksTable, Alignment.TOP_CENTER);

        final Label eventsLabel = new Label("<center><p>In the <b>Events</b> you can review the existing events and its definitions.<br/>Once you have selected an attack in the above table you will be able to assign and remove events that belong to it in the table below.</p></center>", ContentMode.HTML);
        addComponent(eventsLabel);
        addComponent(addEvent);
        setComponentAlignment(addEvent, Alignment.MIDDLE_CENTER);
        addComponent(eventsTable);
        setComponentAlignment(eventsTable, Alignment.MIDDLE_CENTER);
    }
    
    private void initializeTables() {
        // We create the tables
        attacksTable = new FilterTable();
        attacksTable.setFilterBarVisible(true);
        eventsTable = new FilterTable();
        eventsTable.setFilterBarVisible(true);
        // We add a column with the button to edit the attack details
        attacksTable.addGeneratedColumn(EDIT_BUTTON, this);
        // We add a column with the button to delete the attack
        attacksTable.addGeneratedColumn(DELETE_BUTTON, this);
        eventsTable.addGeneratedColumn(DELETE_BUTTON, this);
        // Now we handle the containers
        attacksContainer = new BeanItemContainer<>(Attack.class);
        attacksContainer.addNestedContainerProperty("tool.name");
        attacksContainer.addNestedContainerProperty(CREATED_BY_NAME);
        attacksContainer.addNestedContainerProperty(UPDATED_BY_NAME);
        attacksTable.setContainerDataSource(attacksContainer);
        eventsContainer = new BeanItemContainer<>(com.damosais.sid.database.beans.Event.class);
        eventsContainer.addNestedContainerProperty(CREATED_BY_NAME);
        eventsContainer.addNestedContainerProperty(UPDATED_BY_NAME);
        eventsTable.setContainerDataSource(eventsContainer);
        // Now we define which columns are visible and what are going to be their names in the table header
        attacksTable.setVisibleColumns(new Object[] { "start", "end", "tool.name", "vulnerability", "created", CREATED_BY_NAME, "updated", UPDATED_BY_NAME, EDIT_BUTTON, DELETE_BUTTON });
        attacksTable.setColumnHeaders(new String[] { "Start", "End", "Tool", "Vulnerability", "Created", "Created by", "Last update", "Last update by", "Edit", "Delete" });
        eventsTable.setVisibleColumns(new Object[] { "date", "action", "target", "created", CREATED_BY_NAME, "updated", UPDATED_BY_NAME, DELETE_BUTTON });
        eventsTable.setColumnHeaders(new String[] { "Date", "Action", "Target", "Created", "Created by", "Last update", "Last update by", "Delete" });
        // We then align the buttons to the middle
        attacksTable.setColumnAlignment(EDIT_BUTTON, CustomTable.Align.CENTER);
        attacksTable.setColumnAlignment(DELETE_BUTTON, CustomTable.Align.CENTER);
        eventsTable.setColumnAlignment(DELETE_BUTTON, CustomTable.Align.CENTER);
        // Finally we add the selectable behaviour to the attacks table to link both tables
        attacksTable.setSelectable(true);
        attacksTable.setMultiSelect(false);
        attacksTable.setImmediate(true);
        // Handle selection change.
        attacksTable.addValueChangeListener(event -> refreshEventsTableContent((Attack) attacksTable.getValue()));
        // Now we refresh the content of the tables
        refreshAttacksTableContent();
        refreshEventsTableContent(null);
    }

    /**
     * Refreshes the table with the attacks data
     */
    public void refreshAttacksTableContent() {
        // We first create the container with all the owners and assign it to the table
        attacksContainer.removeAllItems();
        attacksContainer.addAll(attackService.list());
    }

    /**
     * Refreshes the table with the events data
     *
     * @param attack
     *            The attack currently showing
     */
    public void refreshEventsTableContent(Attack attack) {
        // We first create the container with all the events and assign it to the table
        final List<com.damosais.sid.database.beans.Event> events = new ArrayList<>();
        if (attack != null) {
            events.addAll(eventService.listByAttack(attack));
        }
        eventsContainer.removeAllItems();
        eventsContainer.addAll(events);
    }
}