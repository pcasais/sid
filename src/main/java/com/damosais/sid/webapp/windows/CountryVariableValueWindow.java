package com.damosais.sid.webapp.windows;

import java.util.Arrays;
import java.util.EnumSet;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.damosais.sid.database.beans.CountryVariableValue;
import com.damosais.sid.database.beans.SocioeconomicVariable;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.services.CountryVariableValueService;
import com.damosais.sid.webapp.CountryStatisticsView;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.WebApplication;
import com.damosais.sid.webapp.customfields.CountryFieldConverter;
import com.neovisionaries.i18n.CountryCode;
import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This is the window to edit or add owners (victims) to the application
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class CountryVariableValueWindow extends Window {
    private static final String THE_VALUE = "The value '";
    private static final Logger LOGGER = Logger.getLogger(CountryVariableValueWindow.class);
    private static final long serialVersionUID = 7784870465344376142L;
    private final VerticalLayout content;

    @Autowired
    private CountryVariableValueService countryVariableValueService;

    /**
     * Creates a new window to add or edit country variable values
     */
    public CountryVariableValueWindow() {
        setModal(true);
        setSizeUndefined();
        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);
    }

    /**
     * Creates the form to add or edit country variable values
     *
     * @param countryVariableValue
     *            the country variable values being created or edited
     * @param countryStaticsView
     *            the view which called
     * @param newItem
     *            indicates if we are creating or editing
     */
    private void createCountryVariableValueForm(CountryVariableValue countryVariableValue, CountryStatisticsView countryStaticsView, boolean newItem) {
        // 1st) We clear the form and create the new binder
        final FormLayout form = new FormLayout();
        final BeanFieldGroup<CountryVariableValue> binder = new BeanFieldGroup<>(CountryVariableValue.class);
        binder.setItemDataSource(countryVariableValue);
        binder.setBuffered(true);

        // 2dd) We add the country (due to the lack of a toString() that shows proper content we need this hack)
        final BeanContainer<Integer, CountryCode> countryContainer = new BeanContainer<>(CountryCode.class);
        countryContainer.setBeanIdProperty("numeric");
        countryContainer.addAll(EnumSet.allOf(CountryCode.class));
        final ComboBox countryField = new ComboBox("Country", countryContainer);
        countryField.addValidator(new NullValidator("You need to select a country", false));
        countryField.setItemCaptionPropertyId("name");
        countryField.setConverter(new CountryFieldConverter());
        countryField.setNullSelectionAllowed(false);
        binder.bind(countryField, "country");
        form.addComponent(countryField);

        // 3rd) We then add the type of variable
        final ComboBox variableField = new ComboBox("Variable", Arrays.asList(SocioeconomicVariable.values()));
        variableField.addValidator(new NullValidator("You need to select a variable", false));
        binder.bind(variableField, "variable");
        form.addComponent(variableField);
        
        // 4th) We add the date
        final PopupDateField dateField = binder.buildAndBind("Date", "date", PopupDateField.class);
        dateField.addValidator(new NullValidator("You need to select a date", false));
        dateField.setDateFormat("yyyy-MM");
        dateField.setResolution(Resolution.MONTH);
        dateField.setWidth(100, Unit.PERCENTAGE);
        form.addComponent(dateField);
        
        // 5th) We add the value of the variable
        final TextField valueField = binder.buildAndBind("Value", "value", TextField.class);
        valueField.addValidator(new NullValidator("You need to provide a value", false));
        valueField.setNullRepresentation("");
        binder.bind(valueField, "value");
        form.addComponent(valueField);
        
        // 6th) We add a change value listener to the variable field so we can set the field range limits if any
        variableField.setImmediate(true);
        variableField.addValueChangeListener(event -> {
            final SocioeconomicVariable variable = (SocioeconomicVariable) event.getProperty().getValue();
            if (variable != null) {
                valueField.removeAllValidators();
                valueField.addValidator(value -> {
                    if (value == null || !NumberUtils.isNumber(value.toString())) {
                        throw new Validator.InvalidValueException(THE_VALUE + value + "' is not a number");
                    } else {
                        final Double valueNumber = Double.parseDouble(value.toString());
                        if (variable.getMin() != null && variable.getMin() > valueNumber) {
                            throw new Validator.InvalidValueException(THE_VALUE + value + "' needs to be at least " + variable.getMin());
                        }
                        if (variable.getMax() != null && valueNumber > variable.getMax()) {
                            throw new Validator.InvalidValueException(THE_VALUE + value + "' cannot be greater than " + variable.getMax());
                        }
                    }
                });
            }
        });

        // 5th) We clear the window contents and add everything in order
        content.removeAllComponents();
        content.addComponent(form);
        
        // 6th) Now we create the save button if the user can edit data
        final User user = ((WebApplication) countryStaticsView.getUI()).getUser();
        if (user.getRole() == UserRole.EDIT_DATA) {
            final Button saveButton = new Button("Save", event -> {
                try {
                    binder.commit();
                    saveCountryVariableValue(binder.getItemDataSource().getBean(), newItem, user);
                    countryStaticsView.refreshTableContent();
                    new Notification("Success", "Country variable value saved in the database", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                    getUI().removeWindow(this);
                } catch (final Exception e) {
                    LOGGER.error("Problem saving country variable value in database", e);
                    Throwable cause = e;
                    while (cause.getCause() != null) {
                        cause = cause.getCause();
                    }
                    new Notification("Failure", "Error saving country variable value: " + cause.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
                }
            });
            saveButton.setStyleName("link");
            saveButton.setIcon(GraphicResources.SAVE_ICON);
            content.addComponent(saveButton);
            content.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
        }
    }

    private void saveCountryVariableValue(CountryVariableValue commitedCountryVariableValue, boolean newItem, User user) {
        if (newItem) {
            commitedCountryVariableValue.setCreatedBy(user);
        } else {
            commitedCountryVariableValue.setUpdatedBy(user);
        }
        countryVariableValueService.save(commitedCountryVariableValue);
    }

    /**
     * Prepares the window to add a new country variable value
     *
     * @param countryStatisticsView
     *            The view that called
     */
    public void setAddMode(CountryStatisticsView countryStatisticsView) {
        setCaption("Adding new country variable value");
        final CountryVariableValue newCountryVariableValue = new CountryVariableValue();
        createCountryVariableValueForm(newCountryVariableValue, countryStatisticsView, true);
    }
    
    /**
     * Prepares the window to edit an existing country variable value
     *
     * @param countryVariableValueToAlter
     *            country variable value to be edited
     * @param countryStatisticsView
     *            the view that called
     */
    public void setEditMode(CountryVariableValue countryVariableValueToAlter, CountryStatisticsView countryStatisticsView) {
        setCaption("Editing country variable value");
        createCountryVariableValueForm(countryVariableValueToAlter, countryStatisticsView, false);
    }
}