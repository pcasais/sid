package com.damosais.sid.webapp.windows;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.damosais.sid.database.beans.CorrelationHypothesis;
import com.damosais.sid.database.beans.SocioeconomicVariable;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.services.CorrelationHypothesisService;
import com.damosais.sid.webapp.CorrelationsView;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.WebApplication;
import com.damosais.sid.webapp.customfields.CountrySelector;
import com.damosais.sid.webapp.customfields.SectorField;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
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
public class CorrelationWindow extends Window {
    private static final Logger LOGGER = Logger.getLogger(ConflictWindow.class);
    private static final long serialVersionUID = -849902087781592670L;
    private final VerticalLayout content;
    
    @Autowired
    private CorrelationHypothesisService correlationService;

    /**
     * Creates a new window to add or edit correlations
     */
    public CorrelationWindow() {
        setModal(true);
        setSizeUndefined();
        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);
        center();
    }
    
    /**
     * Creates the form to add or edit correlations
     *
     * @param correlation
     *            The correlation being edited or created
     * @param correlationsView
     *            The view which called
     * @param newItem
     *            indicates if we are creating or editing
     */
    private void createCorrelationForm(CorrelationHypothesis correlation, CorrelationsView correlationsView, boolean newItem) {
        // 1st) We create the form and assign the binder
        final FormLayout form = new FormLayout();
        final BeanFieldGroup<CorrelationHypothesis> binder = new BeanFieldGroup<>(CorrelationHypothesis.class);
        binder.setItemDataSource(correlation);
        binder.setBuffered(true);

        // 2nd) We add the fields that are only one selection
        final DateField startField = binder.buildAndBind("Start", "startDate", DateField.class);
        startField.addValidator(new NullValidator("An start date is required", false));
        startField.setResolution(Resolution.MONTH);
        form.addComponent(startField);
        final DateField endField = binder.buildAndBind("End", "endDate", DateField.class);
        endField.addValidator(new NullValidator("An end date is required", false));
        endField.setResolution(Resolution.MONTH);
        form.addComponent(endField);
        // 2.1) The sector is a combo box with null selection allowed
        final SectorField sectorField = new SectorField();
        sectorField.addValidator(new NullValidator("You need to select a sector", false));
        binder.bind(sectorField, "sector");
        form.addComponent(sectorField);
        
        // 3rd) Now we add the fields that allow multiple selections
        final CountrySelector targetCountries = new CountrySelector("Target Countries", false);
        binder.bind(targetCountries, "targetCountries");
        form.addComponent(targetCountries);
        final CountrySelector sourceCountries = new CountrySelector("Source Countries", true);
        binder.bind(sourceCountries, "sourceCountries");
        form.addComponent(sourceCountries);
        final ListSelect socioEconomicVariables = new ListSelect("Variables", Arrays.asList(SocioeconomicVariable.values()));
        socioEconomicVariables.setMultiSelect(true);
        binder.bind(socioEconomicVariables, "variables");
        form.addComponent(socioEconomicVariables);

        // 4th) We now clear the window and add the components
        content.removeAllComponents();
        content.addComponent(form);
        
        // 5th) Now we create the save button if the user can edit data
        final User user = ((WebApplication) correlationsView.getUI()).getUser();
        if (user.getRole() == UserRole.EDIT_DATA) {
            final Button saveButton = new Button("Save", event -> {
                try {
                    binder.commit();
                    saveCorrelation(binder.getItemDataSource().getBean(), newItem, user);
                    correlationsView.refreshTableContent();
                    new Notification("Success", "Correlation saved in the database", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                    getUI().removeWindow(this);
                } catch (final Exception e) {
                    LOGGER.error("Problem saving correlation in database", e);
                    Throwable cause = e;
                    while (cause.getCause() != null) {
                        cause = cause.getCause();
                    }
                    new Notification("Failure", "Error saving correlation: " + cause.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
                }
            });
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
        setCaption("Adding new correlation");
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
        setCaption("Editing correlation");
        createCorrelationForm(correlationToAlter, correlationsView, false);
    }

}