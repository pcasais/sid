package com.damosais.sid.webapp.windows;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.damosais.sid.database.beans.AccessComplexity;
import com.damosais.sid.database.beans.AccessVector;
import com.damosais.sid.database.beans.Authentication;
import com.damosais.sid.database.beans.CVEDefinition;
import com.damosais.sid.database.beans.Impact;
import com.damosais.sid.database.beans.Severity;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.services.CVEDefinitionService;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.VulnerabilitiesView;
import com.damosais.sid.webapp.WebApplication;
import com.damosais.sid.webapp.customfields.LossTypeField;
import com.damosais.sid.webapp.customfields.RangeTypeField;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This class handles the window where the CVE definition are created or edited
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class CVEWindow extends Window {
    private static final Logger LOGGER = Logger.getLogger(CVEWindow.class);
    private static final long serialVersionUID = 5533574448540129417L;
    private final VerticalLayout content;

    @Autowired
    private CVEDefinitionService cveDefinitionService;
    
    private final Validator scoreValidator = value -> {
        if (value == null) {
            throw new InvalidValueException("Value cannot be null");
        }
        try {
            final Double numericValue = Double.parseDouble(value.toString());
            if (numericValue > 10.0d || numericValue < 0.0) {
                throw new InvalidValueException("The number must be between 0.0 and 10.0");
            }
        } catch (final NumberFormatException nfe) {
            throw new InvalidValueException("Invalid number '" + value + "'");
        }
    };

    /**
     * Creates a new window to add or edit CVE definitions
     */
    public CVEWindow() {
        setModal(true);
        setSizeUndefined();
        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);
    }

    /**
     * Creates the form for the CVE definition
     *
     * @param definition
     *            The definition to add or edit
     * @param vulnerabilitiesView
     *            The view that make the call
     * @param newItem
     *            indicates if we are creating or editing
     */
    private void createDefinitionForm(CVEDefinition definition, VulnerabilitiesView vulnerabilitiesView, boolean newItem) {
        // 1st) We create the form and assign the binder
        final HorizontalLayout formsLayout = new HorizontalLayout();
        formsLayout.setSpacing(true);
        formsLayout.setMargin(true);
        final FormLayout formLeft = new FormLayout();
        final FormLayout formRight = new FormLayout();
        final BeanFieldGroup<CVEDefinition> binder = new BeanFieldGroup<>(CVEDefinition.class);
        binder.setItemDataSource(definition);
        binder.setBuffered(true);
        
        // 2nd) We add the name (CVE id)
        final TextField nameField = binder.buildAndBind("Name", "name", TextField.class);
        nameField.addValidator(new NullValidator("You need to provide a name", false));
        nameField.setNullRepresentation("");
        nameField.setWidth(100, Unit.PERCENTAGE);
        formLeft.addComponent(nameField);

        // 3rd) We add the dates in which was published and modified
        final PopupDateField publishedField = binder.buildAndBind("Published", "published", PopupDateField.class);
        publishedField.addValidator(new NullValidator("You need to select a date", false));
        publishedField.setDateFormat("yyyy-MM-dd");
        publishedField.setResolution(Resolution.DAY);
        publishedField.setWidth(100, Unit.PERCENTAGE);
        formLeft.addComponent(publishedField);
        final PopupDateField modifiedField = binder.buildAndBind("Modified", "modified", PopupDateField.class);
        modifiedField.setDateFormat("yyyy-MM-dd");
        modifiedField.setResolution(Resolution.DAY);
        modifiedField.setWidth(100, Unit.PERCENTAGE);
        formLeft.addComponent(modifiedField);

        // 4th) We add the descriptions
        final TextArea cveDescription = binder.buildAndBind("CVE description", "cveDesc", TextArea.class);
        cveDescription.addValidator(new NullValidator("You need to provide a description", false));
        cveDescription.setNullRepresentation("");
        cveDescription.setWidth(100, Unit.PERCENTAGE);
        formLeft.addComponent(cveDescription);
        final TextArea nvdDescription = binder.buildAndBind("NVD description", "nvdDesc", TextArea.class);
        nvdDescription.setNullRepresentation("");
        nvdDescription.setWidth(100, Unit.PERCENTAGE);
        formLeft.addComponent(nvdDescription);
        
        // 5th) We add the combo box for the severity followed by the CVSS Scores
        final ComboBox severityField = new ComboBox("Severity", Arrays.asList(Severity.values()));
        severityField.addValidator(new NullValidator("You need to select a severity", false));
        severityField.setWidth(100, Unit.PERCENTAGE);
        binder.bind(severityField, "severity");
        formRight.addComponent(severityField);
        final TextField cvssBaseScoreField = binder.buildAndBind("CVSS Base score", "cvssBaseScore", TextField.class);
        cvssBaseScoreField.setNullRepresentation("");
        cvssBaseScoreField.setWidth(100, Unit.PERCENTAGE);
        cvssBaseScoreField.addValidator(scoreValidator);
        formRight.addComponent(cvssBaseScoreField);
        final TextField cvssExploitScoreField = binder.buildAndBind("CVSS Exploit score", "cvssExploitSubscore", TextField.class);
        cvssExploitScoreField.setNullRepresentation("");
        cvssExploitScoreField.setWidth(100, Unit.PERCENTAGE);
        cvssExploitScoreField.addValidator(scoreValidator);
        formRight.addComponent(cvssExploitScoreField);
        final TextField cvssImpactScoreField = binder.buildAndBind("CVSS Impact score", "cvssImpactSubscore", TextField.class);
        cvssImpactScoreField.setNullRepresentation("");
        cvssImpactScoreField.setWidth(100, Unit.PERCENTAGE);
        cvssImpactScoreField.addValidator(scoreValidator);
        formRight.addComponent(cvssImpactScoreField);

        // 6th) We now add the three combo boxes to choose the AccessVector, AcessComplexity and Authentication
        final ComboBox accessVectorField = new ComboBox("Access Vector", Arrays.asList(AccessVector.values()));
        accessVectorField.addValidator(new NullValidator("You need to select an access vector", false));
        accessVectorField.setWidth(100, Unit.PERCENTAGE);
        binder.bind(accessVectorField, "accessVector");
        formRight.addComponent(accessVectorField);
        final ComboBox accessComplexityField = new ComboBox("Access Complexity", Arrays.asList(AccessComplexity.values()));
        accessComplexityField.addValidator(new NullValidator("You need to select an access complexity", false));
        accessComplexityField.setWidth(100, Unit.PERCENTAGE);
        binder.bind(accessComplexityField, "accessComplexity");
        formRight.addComponent(accessComplexityField);
        final ComboBox authenticationField = new ComboBox("Authentication", Arrays.asList(Authentication.values()));
        authenticationField.addValidator(new NullValidator("You need to select an authentication", false));
        authenticationField.setWidth(100, Unit.PERCENTAGE);
        binder.bind(authenticationField, "authentication");
        formRight.addComponent(authenticationField);
        
        // 7th) We add the three impacts now
        final ComboBox confImpactField = new ComboBox("Confidentiality Impact", Arrays.asList(Impact.values()));
        confImpactField.addValidator(new NullValidator("You need to select a confidentiality impact", false));
        confImpactField.setWidth(100, Unit.PERCENTAGE);
        binder.bind(confImpactField, "confImpact");
        formRight.addComponent(confImpactField);
        final ComboBox integImpactField = new ComboBox("Integrity Impact", Arrays.asList(Impact.values()));
        integImpactField.addValidator(new NullValidator("You need to select an integrity impact", false));
        integImpactField.setWidth(100, Unit.PERCENTAGE);
        binder.bind(integImpactField, "integImpact");
        formRight.addComponent(integImpactField);
        final ComboBox availImpactField = new ComboBox("Availability Impact", Arrays.asList(Impact.values()));
        availImpactField.addValidator(new NullValidator("You need to select an availability impact", false));
        availImpactField.setWidth(100, Unit.PERCENTAGE);
        binder.bind(availImpactField, "availImpact");
        formRight.addComponent(availImpactField);

        // 8th) We now add the loss and range types
        final LossTypeField lossTypeField = new LossTypeField();
        lossTypeField.setWidth(100, Unit.PERCENTAGE);
        binder.bind(lossTypeField, "lossType");
        formLeft.addComponent(lossTypeField);
        final RangeTypeField rangeTypeField = new RangeTypeField();
        rangeTypeField.setWidth(100, Unit.PERCENTAGE);
        binder.bind(rangeTypeField, "rangeType");
        formRight.addComponent(rangeTypeField);
        
        // 9th) We now clear the window and add the components
        content.removeAllComponents();
        formsLayout.addComponent(formLeft);
        formsLayout.addComponent(formRight);
        content.addComponent(formsLayout);

        // 10th) We create the save button
        final User user = ((WebApplication) vulnerabilitiesView.getUI()).getUser();
        if (user.getRole() == UserRole.EDIT_DATA) {
            final Button saveButton = new Button("Save", event -> {
                try {
                    binder.commit();
                    saveDefinition(binder.getItemDataSource().getBean(), newItem, user);
                    vulnerabilitiesView.refreshCVEsTableContent();
                    new Notification("Success", "CVE definition saved in the database", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                    getUI().removeWindow(this);
                } catch (final Exception e) {
                    LOGGER.error("Problem saving CVE definition in database", e);
                    Throwable cause = e;
                    while (cause.getCause() != null) {
                        cause = cause.getCause();
                    }
                    new Notification("Failure", "Error saving CVE definition: " + cause.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
                }
            });
            saveButton.setStyleName("link");
            saveButton.setIcon(GraphicResources.SAVE_ICON);
            content.addComponent(saveButton);
            content.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
        }
    }

    private void saveDefinition(CVEDefinition commitedDefinition, boolean newItem, User user) {
        if (commitedDefinition.getLossType().getDefinition() == null) {
            commitedDefinition.getLossType().setDefinition(commitedDefinition);
        }
        if (commitedDefinition.getRangeType().getDefinition() == null) {
            commitedDefinition.getRangeType().setDefinition(commitedDefinition);
        }
        if (newItem) {
            commitedDefinition.setCreatedBy(user);
        } else {
            commitedDefinition.setUpdatedBy(user);
        }
        cveDefinitionService.save(commitedDefinition);
    }
    
    /**
     * Prepares the window to add a new CVE definition
     *
     * @param vulnerabilitiesView
     *            The view that called
     */
    public void setAddMode(VulnerabilitiesView vulnerabilitiesView) {
        setCaption("Adding new CVE definition");
        createDefinitionForm(new CVEDefinition(), vulnerabilitiesView, true);
    }
    
    /**
     * Prepares the window to edit an existing CVE definition
     *
     * @param definitionToAlter
     *            CVE definition to be edited
     * @param vulnerabilitiesView
     *            the view that called
     */
    public void setEditMode(CVEDefinition definitionToAlter, VulnerabilitiesView vulnerabilitiesView) {
        setCaption("Editing CVE definition");
        createDefinitionForm(definitionToAlter, vulnerabilitiesView, false);
    }
}