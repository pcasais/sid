package com.damosais.sid.webapp.windows;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tepi.filtertable.FilterTable;

import com.damosais.sid.database.beans.Action;
import com.damosais.sid.database.beans.Target;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.services.EventService;
import com.damosais.sid.database.services.TargetService;
import com.damosais.sid.webapp.EventsView;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.WebApplication;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This class represents the window used to add or edit events
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class EventWindow extends Window {
    private static final Logger LOGGER = Logger.getLogger(EventWindow.class);
    private static final long serialVersionUID = 8434835262821274799L;
    private final VerticalLayout content;
    
    @Autowired
    private TargetService targetService;

    @Autowired
    private EventService eventService;
    
    /**
     * Creates a new window to add or edit events
     */
    public EventWindow() {
        setModal(true);
        setSizeUndefined();
        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);
    }
    
    /**
     * Creates the form to add or edit an event
     *
     * @param eventForForm
     *            The event to add or edit
     * @param eventsView
     *            The view which called
     * @param newItem
     *            indicates if we are creating or editing
     */
    private void createEventForm(com.damosais.sid.database.beans.Event eventForForm, EventsView eventsView, boolean newItem) {
        // 1st) We clear the form and set the new binder to the new object
        final FormLayout form = new FormLayout();
        final BeanFieldGroup<com.damosais.sid.database.beans.Event> binder = new BeanFieldGroup<>(com.damosais.sid.database.beans.Event.class);
        binder.setItemDataSource(eventForForm);
        binder.setBuffered(true);
        
        // 2nd) We create the date field
        final PopupDateField dateField = binder.buildAndBind("Date", "date", PopupDateField.class);
        dateField.addValidator(new NullValidator("You need to select a date", false));
        dateField.setDateFormat("yyyy-MM-dd HH:mm:ss");
        dateField.setResolution(Resolution.SECOND);
        dateField.setWidth(100, Unit.PERCENTAGE);
        form.addComponent(dateField);
        
        // 3rd) We create the combo box with all the actions
        final ComboBox actionField = new ComboBox("Action", Arrays.asList(Action.values()));
        actionField.addValidator(new NullValidator("You need to select an action", false));
        actionField.setWidth(100, Unit.PERCENTAGE);
        binder.bind(actionField, "action");
        form.addComponent(actionField);

        // 4th) We create the table with the targets available
        final FilterTable targetsTable = new FilterTable();
        targetsTable.addValidator(new NullValidator("You need to select a target", false));
        targetsTable.setFilterBarVisible(true);
        final BeanItemContainer<Target> targetsContainer = new BeanItemContainer<>(Target.class, targetService.list());
        targetsContainer.addNestedContainerProperty("country.name");
        targetsTable.setContainerDataSource(targetsContainer);
        // Now we define which columns are visible and what are going to be their names in the table header
        targetsTable.setVisibleColumns(new Object[] { "siteName", "ips", "country.name" });
        targetsTable.setColumnHeaders(new String[] { "Site name", "IPs", "Country" });
        // Finally we add the selectable behaviour
        targetsTable.setNullSelectionAllowed(false);
        targetsTable.setSelectable(true);
        targetsTable.setMultiSelect(false);
        targetsTable.setImmediate(true);
        form.addComponent(targetsTable);
        targetsTable.select(eventForForm.getTarget());
        
        // 5th) We clear the form and add the elements to it
        content.removeAllComponents();
        content.addComponent(form);
        
        // 6th) Now we create the save button if the user can edit data
        final User user = ((WebApplication) eventsView.getUI()).getUser();
        if (user.getRole() == UserRole.EDIT_DATA) {
            final Button saveButton = new Button("Save", event -> {
                try {
                    binder.commit();
                    saveEvent(binder.getItemDataSource().getBean(), (Target) targetsTable.getValue(), newItem, user);
                    eventsView.refreshTableContent();
                    new Notification("Success", "Event saved in the database", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
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
            content.addComponent(saveButton);
            content.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
        }
    }

    private void saveEvent(com.damosais.sid.database.beans.Event commitedEvent, Target target, boolean newItem, User user) {
        commitedEvent.setTarget(target);
        if (newItem) {
            commitedEvent.setCreatedBy(user);
        } else {
            commitedEvent.setUpdatedBy(user);
        }
        eventService.save(commitedEvent);
    }

    /**
     * Prepares the window to add a new event
     *
     * @param eventsView
     *            The view that called
     */
    public void setAddMode(EventsView eventsView) {
        setCaption("Adding new event");
        final com.damosais.sid.database.beans.Event newEvent = new com.damosais.sid.database.beans.Event();
        createEventForm(newEvent, eventsView, true);
    }

    /**
     * Prepares the window to edit an existing event
     *
     * @param eventToAlter
     *            event to be edited
     * @param eventsView
     *            the view that called
     */
    public void setEditMode(com.damosais.sid.database.beans.Event eventToAlter, EventsView eventsView) {
        setCaption("Editing event");
        createEventForm(eventToAlter, eventsView, false);
    }
}