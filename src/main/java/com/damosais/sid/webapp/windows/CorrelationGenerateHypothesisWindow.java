package com.damosais.sid.webapp.windows;

import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.damosais.sid.database.beans.Conflict;
import com.damosais.sid.database.beans.CorrelationHypothesis;
import com.damosais.sid.database.beans.SocioeconomicVariable;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.dao.EventDAO;
import com.damosais.sid.database.services.ConflictService;
import com.damosais.sid.database.services.CorrelationHypothesisService;
import com.damosais.sid.webapp.CorrelationsView;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.WebApplication;
import com.damosais.sid.webapp.customfields.CountrySelector;
import com.damosais.sid.webapp.customfields.SectorField;
import com.neovisionaries.i18n.CountryCode;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This class represents the window used to generate multiple correlations
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class CorrelationGenerateHypothesisWindow extends Window {
    private static final Logger LOGGER = Logger.getLogger(CorrelationGenerateHypothesisWindow.class);
    private static final long serialVersionUID = 7461456382653780762L;
    private static final String BORDER_STYLE = "layout-with-border";
    private static final String CONFLICT_MODE = "Conflict";
    private static final String MANUAL_MODE = "Manual values";
    private final VerticalLayout content;
    private OptionGroup modeField;
    private SectorField sectorField;
    private ListSelect socioEconomicVariables;
    private TextField minEvents;
    private ComboBox conflictField;
    private DateField startField;
    private DateField endField;
    private CountrySelector sourceCountries;
    
    @Autowired
    private CorrelationHypothesisService correlationService;

    @Autowired
    private EventDAO eventDAO;

    @Autowired
    private ConflictService conflictService;

    /**
     * Creates a new window to add or edit correlations
     */
    public CorrelationGenerateHypothesisWindow() {
        setModal(true);
        setCaption("Generating new correlation hypothesis");
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
    @SuppressWarnings("unchecked")
    public void createCorrelationForm(CorrelationsView correlationsView) {
        // 1st) We clear the form
        content.removeAllComponents();
        
        // 2nd) We add first the mode to select and the sector
        final VerticalLayout modeLayout = new VerticalLayout();
        modeLayout.setCaption("Mode & sector");
        modeLayout.addStyleName(BORDER_STYLE);
        modeLayout.setMargin(true);
        modeLayout.setSpacing(true);
        modeField = new OptionGroup("Creation mode", Arrays.asList(CONFLICT_MODE, MANUAL_MODE));
        modeField.setImmediate(true);
        modeLayout.addComponent(modeField);
        sectorField = new SectorField();
        sectorField.setImmediate(true);
        sectorField.addValidator(new NullValidator("You need to select a sector", false));
        modeLayout.addComponent(sectorField);
        socioEconomicVariables = new ListSelect("Variables", Arrays.asList(SocioeconomicVariable.values()));
        socioEconomicVariables.setImmediate(true);
        socioEconomicVariables.setMultiSelect(true);
        socioEconomicVariables.addValidator(new NullValidator("You need to select at least one socioeconomic variable", false));
        modeLayout.addComponent(socioEconomicVariables);
        minEvents = new TextField("Min Events", "0");
        minEvents.setImmediate(true);
        modeLayout.addComponent(minEvents);
        content.addComponent(modeLayout);
        
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
        conflictField = new ComboBox("Conflict", conflictService.list());
        conflictField.setImmediate(true);
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
        startField = new DateField("Start");
        startField.setImmediate(true);
        startField.setResolution(Resolution.MONTH);
        hlDates.addComponent(startField);
        endField = new DateField("End");
        endField.setImmediate(true);
        endField.setResolution(Resolution.MONTH);
        hlDates.addComponent(endField);
        manualModeLayout.addComponent(hlDates);
        final BeanContainer<Integer, CountryCode> countryContainer = new BeanContainer<>(CountryCode.class);
        countryContainer.setBeanIdProperty("numeric");
        countryContainer.addAll(EnumSet.allOf(CountryCode.class));
        sourceCountries = new CountrySelector("Source Countries", true);
        sourceCountries.setImmediate(true);
        manualModeLayout.addComponent(sourceCountries);
        detailsContainer.addComponent(manualModeLayout);
        content.addComponent(detailsContainer);
        
        // 4th) We then add the behaviour to the mode selector
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
                sourceCountries.setEnabled(true);
                sourceCountries.setNullable(false);
            }
        });
        
        // 5th) We set the mode to manual by default
        modeField.setValue(MANUAL_MODE);
        
        // 6th) Now we create the save button if the user can edit data
        final User user = ((WebApplication) correlationsView.getUI()).getUser();
        if (user.getRole() == UserRole.EDIT_DATA) {
            final Button saveButton = new Button("Save", event -> {
                // 6.1) First we calculate the dates and retrieve the minimum events
                final Date startDate = startField.getValue();
                final Date endDate = endField.getValue();
                final Date lastDay = java.sql.Date.valueOf(new java.sql.Date(endDate.getTime()).toLocalDate().with(TemporalAdjusters.lastDayOfMonth()));
                final int requiredEvents = Integer.parseInt(minEvents.getValue());
                
                int saved = 0;
                final List<CorrelationHypothesis> correlationHypothesis = correlationService.list();
                // We then create a hypothesis per target country
                for (final CountryCode country : CountryCode.values()) {
                    final List<com.damosais.sid.database.beans.Event> securityEvents = eventDAO.findByDateBetweenAndTargetCountry(startDate, lastDay, country);
                    if (securityEvents.size() >= requiredEvents) {
                        try {
                            final CorrelationHypothesis hypothesis = new CorrelationHypothesis();
                            hypothesis.setSector(sectorField.getValue());
                            hypothesis.setVariables((Set<SocioeconomicVariable>) socioEconomicVariables.getValue());
                            if (CONFLICT_MODE.equalsIgnoreCase((String) modeField.getValue())) {
                                hypothesis.setConflict((Conflict) conflictField.getValue());
                            } else {
                                hypothesis.setStartDate(startDate);
                                hypothesis.setEndDate(endDate);
                                hypothesis.setSourceCountries(sourceCountries.getValue());
                                hypothesis.setTargetCountry(country);
                            }
                            hypothesis.setCreatedBy(user);
                            
                            // 7.2) We save the hypothesis and refresh the window
                            if (!correlationHypothesis.contains(hypothesis)) {
                                correlationService.save(hypothesis);
                                correlationHypothesis.add(hypothesis);
                                saved++;
                            }
                        } catch (final Exception e) {
                            LOGGER.error("Problem saving correlation in database", e);
                        }
                    }
                }
                correlationsView.refreshTableContent();
                if (saved > 0) {
                    new Notification("Success", saved + " correlation saved in the database", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                } else {
                    new Notification("Failure", "No correlation saved in the database", Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
                }
                getUI().removeWindow(this);
            });
            // 8th) We add the save buttons
            saveButton.setStyleName("link");
            saveButton.setIcon(GraphicResources.SAVE_ICON);
            content.addComponent(saveButton);
            content.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
        }
    }
}