package com.damosais.sid.webapp.windows;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tepi.filtertable.FilterTable;

import com.damosais.sid.database.beans.Attack;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.services.EventService;
import com.damosais.sid.webapp.AttacksView;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.WebApplication;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This class allows the user to select an existing event to add to an existing attack
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class AddEventToAttackWindow extends Window {
    private static final Logger LOGGER = Logger.getLogger(AddEventToAttackWindow.class);
    private static final long serialVersionUID = 886191629031920874L;
    private final VerticalLayout content;
    
    @Autowired
    private EventService eventService;
    
    /**
     * Creates a new window to add events to an existing attack
     */
    public AddEventToAttackWindow() {
        setModal(true);
        setSizeUndefined();
        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);
        setCaption("Adding event to attack");
    }
    
    /**
     * Prepares the window to add an existing event to an attack
     *
     * @param attackToAlter
     *            the attack to which we will add the event
     * @param attacksView
     *            the view that called
     */
    public void openAddWindow(Attack attackToAlter, AttacksView attacksView) {
        // 1st) We initialise the form and add the combo box (we only add events that are not assigned)
        final VerticalLayout form = new VerticalLayout();
        form.addComponent(new Label("If you can't see any events in the table is because they have all been assigned to other attacks"));
        final List<com.damosais.sid.database.beans.Event> eventsFiltered = eventService.list().stream().filter(event -> event.getAttack() == null).collect(Collectors.toList());
        
        final FilterTable eventsTable = new FilterTable();
        eventsTable.setFilterBarVisible(true);
        // Now we add the container
        final BeanItemContainer<com.damosais.sid.database.beans.Event> eventContainer = new BeanItemContainer<>(com.damosais.sid.database.beans.Event.class);
        eventContainer.addNestedContainerProperty("target.siteName");
        eventContainer.addNestedContainerProperty("target.country.name");
        eventContainer.addNestedContainerProperty("target.owner.name");
        eventsTable.setContainerDataSource(eventContainer);
        // Now we define which columns are visible and what are going to be their names in the table header
        eventsTable.setVisibleColumns(new Object[] { "date", "action", "target.siteName", "target.country.name", "target.owner.name" });
        eventsTable.setColumnHeaders(new String[] { "Date", "Action", "Target Site", "Target Country", "Target Owner" });
        // Finally we add the selectable behaviour
        eventsTable.setSelectable(true);
        eventsTable.setMultiSelect(false);
        eventsTable.setImmediate(true);
        eventContainer.addAll(eventsFiltered);
        form.addComponent(eventsTable);
        
        // 2nd) Now we create the button to save
        final User user = ((WebApplication) attacksView.getUI()).getUser();
        final Button saveButton = new Button("Save", event -> {
            try {
                final com.damosais.sid.database.beans.Event selectedEvent = (com.damosais.sid.database.beans.Event) eventsTable.getValue();
                if (selectedEvent != null) {
                    selectedEvent.setAttack(attackToAlter);
                }
                selectedEvent.setUpdatedBy(user);
                eventService.save(selectedEvent);
                attacksView.refreshEventsTableContent(attackToAlter);
                new Notification("Success", "Event added to attack in the database", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                getUI().removeWindow(this);
            } catch (final Exception e) {
                LOGGER.error("Problem saving event in database", e);
                Throwable cause = e;
                while (cause.getCause() != null) {
                    cause = cause.getCause();
                }
                new Notification("Failure", "Error saving event: " + cause.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
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