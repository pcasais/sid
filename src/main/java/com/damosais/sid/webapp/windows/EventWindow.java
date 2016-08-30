package com.damosais.sid.webapp.windows;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.damosais.sid.database.beans.Action;
import com.damosais.sid.database.services.EventService;
import com.damosais.sid.database.services.TargetService;
import com.damosais.sid.webapp.EventsView;
import com.damosais.sid.webapp.GraphicResources;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
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
     */
    private void createEventForm(com.damosais.sid.database.beans.Event eventForForm, EventsView eventsView) {
        // 1st) We clear the form and set the new binder to the new object
        final FormLayout form = new FormLayout();
        final BeanFieldGroup<com.damosais.sid.database.beans.Event> binder = new BeanFieldGroup<>(com.damosais.sid.database.beans.Event.class);
        binder.setItemDataSource(eventForForm);
        binder.setBuffered(true);

        // 2nd) We create the date field
        final PopupDateField dateField = new PopupDateField("Date", eventForForm.getDate());
        dateField.setDateFormat("yyyy-MM-dd HH:mm:ss");
        dateField.setWidth(100, Unit.PERCENTAGE);
        binder.bind(dateField, "date");
        form.addComponent(dateField);

        // 3rd) We create the combo box with all the actions
        final ComboBox actionField = new ComboBox("Action", Arrays.asList(Action.values()));
        actionField.setWidth(100, Unit.PERCENTAGE);
        binder.bind(actionField, "action");
        form.addComponent(actionField);
        
        // 4th) We create the combo box with the targets available
        final ComboBox targetField = new ComboBox("Target", targetService.list());
        targetField.setWidth(100, Unit.PERCENTAGE);
        binder.bind(targetField, "target");
        form.addComponent(targetField);

        // 5th) We create the save button
        final Button saveButton = new Button("Save", event -> {
            try {
                binder.commit();
                final com.damosais.sid.database.beans.Event commitedEvent = binder.getItemDataSource().getBean();
                eventService.save(commitedEvent);
                eventsView.refreshTableContent();
                new Notification("Success", "Event saved in the database", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                getUI().removeWindow(this);
            } catch (final CommitException e) {
                LOGGER.error("Problem saving event in database", e);
                new Notification("Failure", "Error saving event: " + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
            }
        });
        saveButton.setStyleName("link");
        saveButton.setIcon(GraphicResources.SAVE_ICON);
        
        // 6th) We clear the content of the window and add the form and the save buttons
        content.removeAllComponents();
        content.addComponent(form);
        content.addComponent(saveButton);
        content.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
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
        createEventForm(newEvent, eventsView);
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
        createEventForm(eventToAlter, eventsView);
    }
}