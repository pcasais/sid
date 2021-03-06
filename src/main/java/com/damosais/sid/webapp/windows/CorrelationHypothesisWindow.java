package com.damosais.sid.webapp.windows;

import java.util.Arrays;
import java.util.EnumSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.damosais.sid.database.beans.CorrelationHypothesis;
import com.damosais.sid.database.beans.SocioeconomicVariable;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.services.ConflictService;
import com.damosais.sid.database.services.CorrelationHypothesisService;
import com.damosais.sid.webapp.CorrelationsView;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.WebApplication;
import com.damosais.sid.webapp.customfields.CountryFieldConverter;
import com.damosais.sid.webapp.customfields.CountrySelector;
import com.damosais.sid.webapp.customfields.SectorField;
import com.neovisionaries.i18n.CountryCode;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This class represents the window used to add correlations
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class CorrelationHypothesisWindow extends Window {
    private static final Logger LOGGER = Logger.getLogger(ConflictWindow.class);
    private static final long serialVersionUID = -849902087781592670L;
    private static final String BORDER_STYLE = "layout-with-border";
    private static final String CONFLICT_MODE = "Conflict";
    private static final String MANUAL_MODE = "Manual values";
    private final VerticalLayout content;
    
    @Autowired
    private CorrelationHypothesisService correlationService;
    
    @Autowired
    private ConflictService conflictService;

    /**
     * Creates a new window to add or edit correlations
     */
    public CorrelationHypothesisWindow() {
        setModal(true);
        setSizeUndefined();
        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);
    }
    
    /**
     * Creates the form to add or edit correlations
     *
     * @param correlationHypothesis
     *            The correlation being edited or created
     * @param correlationsView
     *            The view which called
     * @param newItem
     *            indicates if we are creating or editing
     */
    private void createCorrelationForm(CorrelationHypothesis correlationHypothesis, CorrelationsView correlationsView, boolean newItem) {
        // 1st) We create the form and assign the binder
        final FormLayout form = new FormLayout();
        form.setSpacing(true);
        final BeanFieldGroup<CorrelationHypothesis> binder = new BeanFieldGroup<>(CorrelationHypothesis.class);
        binder.setItemDataSource(correlationHypothesis);
        binder.setBuffered(true);

        // 2nd) We add first the mode to select and the sector
        final VerticalLayout modeLayout = new VerticalLayout();
        modeLayout.setCaption("Mode & sector");
        modeLayout.addStyleName(BORDER_STYLE);
        modeLayout.setMargin(true);
        modeLayout.setSpacing(true);
        final OptionGroup modeField = new OptionGroup("Creation mode", Arrays.asList(CONFLICT_MODE, MANUAL_MODE));
        modeLayout.addComponent(modeField);
        final SectorField sectorField = new SectorField();
        sectorField.addValidator(new NullValidator("You need to select a sector", false));
        binder.bind(sectorField, "sector");
        modeLayout.addComponent(sectorField);
        final ListSelect socioEconomicVariables = new ListSelect("Variables", Arrays.asList(SocioeconomicVariable.values()));
        socioEconomicVariables.setMultiSelect(true);
        socioEconomicVariables.addValidator(new NullValidator("You need to select at least one socioeconomic variable", false));
        binder.bind(socioEconomicVariables, "variables");
        modeLayout.addComponent(socioEconomicVariables);
        form.addComponent(modeLayout);

        // 3rd) Now we add the fields for both modes
        final HorizontalLayout detailsContainer = new HorizontalLayout();
        detailsContainer.setCaption("Details");
        detailsContainer.addStyleName(BORDER_STYLE);
        detailsContainer.setMargin(true);
        detailsContainer.setSpacing(true);
        // 3.1) In the conflict mode we just choose the conflict
        final VerticalLayout conflictModeLayout = new VerticalLayout();
        conflictModeLayout.setCaption("Conflict mode values");
        conflictModeLayout.addStyleName(BORDER_STYLE);
        final ComboBox conflictField = new ComboBox("Conflict", conflictService.list());
        binder.bind(conflictField, "conflict");
        conflictModeLayout.addComponent(conflictField);
        detailsContainer.addComponent(conflictModeLayout);
        // 3.2) In the other side we need to add the dates and countries
        final VerticalLayout manualModeLayout = new VerticalLayout();
        manualModeLayout.setCaption("Manual mode values");
        manualModeLayout.addStyleName(BORDER_STYLE);
        manualModeLayout.setMargin(true);
        manualModeLayout.setSpacing(true);
        final HorizontalLayout hlDates = new HorizontalLayout();
        hlDates.setMargin(true);
        hlDates.setSpacing(true);
        final DateField startField = binder.buildAndBind("Start", "startDate", DateField.class);
        startField.setResolution(Resolution.MONTH);
        hlDates.addComponent(startField);
        final DateField endField = binder.buildAndBind("End", "endDate", DateField.class);
        endField.setResolution(Resolution.MONTH);
        hlDates.addComponent(endField);
        manualModeLayout.addComponent(hlDates);
        final BeanContainer<Integer, CountryCode> countryContainer = new BeanContainer<>(CountryCode.class);
        countryContainer.setBeanIdProperty("numeric");
        countryContainer.addAll(EnumSet.allOf(CountryCode.class));
        final ComboBox targetCountry = new ComboBox("Target Country", countryContainer);
        targetCountry.setItemCaptionPropertyId("name");
        targetCountry.setConverter(new CountryFieldConverter());
        binder.bind(targetCountry, "targetCountry");
        manualModeLayout.addComponent(targetCountry);
        final CountrySelector sourceCountries = new CountrySelector("Source Countries", true);
        binder.bind(sourceCountries, "sourceCountries");
        manualModeLayout.addComponent(sourceCountries);
        detailsContainer.addComponent(manualModeLayout);
        form.addComponent(detailsContainer);

        // 4th) We now clear the window and add the components
        content.removeAllComponents();
        content.addComponent(form);

        // 5th) We then add the behaviour to the mode selector
        modeField.addValueChangeListener(event -> {
            final String modeSelected = (String) event.getProperty().getValue();
            if (CONFLICT_MODE.equalsIgnoreCase(modeSelected)) {
                // 5.1) If we are in the conflict mode then we make the conflict mandatory and disable the rest
                conflictField.setEnabled(true);
                conflictField.addValidator(new NullValidator("You need to select a conflict", false));
                startField.setEnabled(false);
                startField.setValue(null);
                startField.removeAllValidators();
                endField.setEnabled(false);
                endField.setValue(null);
                endField.removeAllValidators();
                targetCountry.setEnabled(false);
                targetCountry.setValue(null);
                sourceCountries.setEnabled(false);
                sourceCountries.removeAllValidators();
                sourceCountries.setValue(null);
            }
            if (MANUAL_MODE.equalsIgnoreCase(modeSelected)) {
                // 5.2) In this case we disable the conflict and enable the rest
                conflictField.setEnabled(false);
                conflictField.removeAllValidators();
                conflictField.setValue(null);
                startField.setEnabled(true);
                startField.addValidator(new NullValidator("You need to select a start date", false));
                endField.setEnabled(true);
                endField.addValidator(new NullValidator("You need to select an end date", false));
                targetCountry.setEnabled(true);
                sourceCountries.setEnabled(true);
                sourceCountries.setNullable(false);
            }
        });

        // 6th) Now depending on the whether the conflic is set or not we choose the option
        modeField.setValue(correlationHypothesis.getConflict() != null ? CONFLICT_MODE : MANUAL_MODE);

        // 7th) Now we create the save button if the user can edit data
        final User user = ((WebApplication) correlationsView.getUI()).getUser();
        if (user.getRole() == UserRole.EDIT_DATA) {
            final Button saveButton = new Button("Save", event -> {
                try {
                    // 7.1) We commit the form and retrieve the bean
                    binder.commit();
                    final CorrelationHypothesis hypothesisToSave = binder.getItemDataSource().getBean();

                    // 7.2) We save the hypothesis and refresh the window
                    saveCorrelation(hypothesisToSave, newItem, user);
                    correlationsView.refreshTableContent();
                    new Notification("Success", "Correlation saved in the database", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                    getUI().removeWindow(this);
                } catch (final Exception e) {
                    LOGGER.error("Problem saving correlation in database", e);
                    Throwable cause = e;
                    while (cause.getCause() != null && cause.getMessage() != null) {
                        cause = cause.getCause();
                    }
                    new Notification("Failure", "Error saving correlation: " + cause.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
                }
            });
            // 8th) We add the save buttons
            saveButton.setStyleName("link");
            saveButton.setIcon(GraphicResources.SAVE_ICON);
            content.addComponent(saveButton);
            content.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
        }
    }
    
    private void saveCorrelation(CorrelationHypothesis correlation, boolean newItem, User user) {
        if (newItem) {
            correlation.setCreatedBy(user);
        } else {
            correlation.setUpdatedBy(user);
        }
        correlationService.save(correlation);
    }
    
    /**
     * Prepares the window to add a new correlation
     *
     * @param correlationsView
     *            The view that called
     */
    public void setAddMode(CorrelationsView correlationsView) {
        setCaption("Adding new correlation hypothesis");
        createCorrelationForm(new CorrelationHypothesis(), correlationsView, true);
    }

    /**
     * Prepares the window to edit an existing correlation
     *
     * @param correlationToAlter
     *            correlation to be edited
     * @param correlationsView
     *            the view that called
     */
    public void setEditMode(CorrelationHypothesis correlationToAlter, CorrelationsView correlationsView) {
        setCaption("Editing correlation hypothesis");
        createCorrelationForm(correlationToAlter, correlationsView, false);
    }

}