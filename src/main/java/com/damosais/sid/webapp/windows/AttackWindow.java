package com.damosais.sid.webapp.windows;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.damosais.sid.database.beans.Attack;
import com.damosais.sid.database.beans.ConfidencialityLevel;
import com.damosais.sid.database.beans.UnathourizedResultType;
import com.damosais.sid.database.beans.UnauthorizedResult;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.services.AttackService;
import com.damosais.sid.database.services.ToolService;
import com.damosais.sid.database.services.UnauthorizedResultService;
import com.damosais.sid.database.services.VulnerabilityService;
import com.damosais.sid.webapp.AttacksView;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.WebApplication;
import com.damosais.sid.webapp.customfields.StringLongConverter;
import com.damosais.sid.webapp.customfields.StringToDoubleConverter;
import com.damosais.sid.webapp.customfields.StringToLongTimeConverter;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This class handles the window where attacks are created or edited
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class AttackWindow extends Window {
    private static final Logger LOGGER = Logger.getLogger(AttackWindow.class);
    private static final long serialVersionUID = -452236927715765818L;
    private final VerticalLayout content;
    private BeanFieldGroup<UnauthorizedResult> unauthorizedResultBinder;
    
    @Autowired
    private UnauthorizedResultService unauthorizedResultService;
    
    @Autowired
    private AttackService attackService;

    @Autowired
    private ToolService toolService;

    @Autowired
    private VulnerabilityService vulnerabilityService;
    
    /**
     * Creates a new window to add or edit attacks
     */
    public AttackWindow() {
        setModal(true);
        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);
        setSizeUndefined();
        center();
    }
    
    /**
     * Creates the form to add or edit attacks
     *
     * @param attack
     *            The attack being edited or created
     * @param attacksView
     *            The view which called
     * @param newItem
     *            indicates if we are creating or editing
     */
    private void createAttackForm(Attack attack, AttacksView attacksView, boolean newItem) {
        // 1st) We create the form and assign the binder
        final FormLayout form = new FormLayout();
        final BeanFieldGroup<Attack> binder = new BeanFieldGroup<>(Attack.class);
        binder.setItemDataSource(attack);
        binder.setBuffered(true);
        
        // 2nd) Then we add the ComboBox with all the tools
        final ComboBox toolField = new ComboBox("Tool", toolService.list());
        binder.bind(toolField, "tool");
        form.addComponent(toolField);
        
        // 3rd) We add the ComboBox with all the vulnerabilities
        final ComboBox vulnerabilityField = new ComboBox("Vulnerability", vulnerabilityService.list());
        binder.bind(vulnerabilityField, "vulnerability");
        form.addComponent(vulnerabilityField);
        
        // 4th) We now create the table with the impact
        final FormLayout impactForm = createUnauthorizedResultForm(attack.getUnauthorizedResults());

        // 5th) We now clear the window and add the components
        content.removeAllComponents();
        content.addComponent(form);
        content.addComponent(impactForm);
        
        // 6th) Now we create the save button if the user can edit data
        final User user = ((WebApplication) attacksView.getUI()).getUser();
        if (user.getRole() == UserRole.EDIT_DATA) {
            final Button saveButton = new Button("Save", event -> {
                try {
                    binder.commit();
                    unauthorizedResultBinder.commit();
                    binder.getItemDataSource().getBean().setUnauthorizedResults(unauthorizedResultBinder.getItemDataSource().getBean());
                    saveAttack(binder.getItemDataSource().getBean(), newItem, user);
                    attacksView.refreshAttacksTableContent();
                    new Notification("Success", "Attack saved in the database", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                    getUI().removeWindow(this);
                } catch (final CommitException e) {
                    LOGGER.error("Problem saving attack in database", e);
                    Throwable cause = e;
                    while (cause.getCause() != null) {
                        cause = cause.getCause();
                    }
                    new Notification("Failure", "Error saving attack: " + cause.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
                }
            });
            saveButton.setStyleName("link");
            saveButton.setIcon(GraphicResources.SAVE_ICON);
            content.addComponent(saveButton);
            content.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
        }
    }
    
    /**
     * This method creates the form where the unauthorized results will be defined
     *
     * @param unauthorizedResult
     *            The results to implement
     * @return The form layout where the data will be displayed
     */
    private FormLayout createUnauthorizedResultForm(UnauthorizedResult unauthorizedResult) {
        // 1st) We build the form
        final FormLayout form = new FormLayout();
        form.setCaption("Unauthorized Result");
        unauthorizedResultBinder = new BeanFieldGroup<>(UnauthorizedResult.class);
        unauthorizedResultBinder.setItemDataSource(unauthorizedResult != null ? unauthorizedResult : new UnauthorizedResult());
        unauthorizedResultBinder.setBuffered(true);
        
        // 2nd) We build the form by adding the fields
        final ComboBox type = new ComboBox("Type", Arrays.asList(UnathourizedResultType.values()));
        unauthorizedResultBinder.bind(type, "type");
        form.addComponent(type);
        form.addComponent(unauthorizedResultBinder.buildAndBind("Admin Access Granted", "adminAccess"));
        form.addComponent(unauthorizedResultBinder.buildAndBind("User Access Granted", "userAccess"));
        final TextField numRegisters = new TextField("Registers hacked #");
        numRegisters.setConverter(new StringLongConverter());
        unauthorizedResultBinder.bind(numRegisters, "numRegisters");
        form.addComponent(numRegisters);
        final ComboBox confidencialityLevel = new ComboBox("Confidenciality Level", Arrays.asList(ConfidencialityLevel.values()));
        unauthorizedResultBinder.bind(confidencialityLevel, "confidetialityLevel");
        form.addComponent(confidencialityLevel);
        final TextField downTime = new TextField("Downtime");
        downTime.setConverter(new StringToLongTimeConverter());
        unauthorizedResultBinder.bind(downTime, "downTime");
        form.addComponent(downTime);
        final TextField averageTraffic = new TextField("Avg. Traffic (GBps)");
        averageTraffic.setConverter(new StringToDoubleConverter());
        unauthorizedResultBinder.bind(averageTraffic, "averageTraffic");
        form.addComponent(averageTraffic);
        final TextField peakTraffic = new TextField("Peak Traffic");
        peakTraffic.setConverter(new StringToDoubleConverter());
        unauthorizedResultBinder.bind(peakTraffic, "peakTraffic");
        form.addComponent(peakTraffic);
        final TextField economicImpact = new TextField("Economic Impact ($)");
        economicImpact.setConverter(new StringToDoubleConverter());
        unauthorizedResultBinder.bind(economicImpact, "economicImpact");
        form.addComponent(economicImpact);

        return form;
    }
    
    private void saveAttack(Attack commitedAttack, boolean newItem, User user) {
        if (newItem) {
            commitedAttack.setCreatedBy(user);
        } else {
            commitedAttack.setUpdatedBy(user);
        }
        if (commitedAttack.getUnauthorizedResults().getId() == null) {
            commitedAttack.getUnauthorizedResults().setCreatedBy(user);
        } else {
            commitedAttack.getUnauthorizedResults().setUpdatedBy(user);
        }
        unauthorizedResultService.save(commitedAttack.getUnauthorizedResults());
        attackService.save(commitedAttack);
    }

    /**
     * Prepares the window to add a new attack
     *
     * @param attacksView
     *            The view that called
     */
    public void setAddMode(AttacksView attacksView) {
        setCaption("Adding new attack");
        createAttackForm(new Attack(), attacksView, true);
    }

    /**
     * Prepares the window to edit an existing attack
     *
     * @param attackToAlter
     *            attack to be edited
     * @param attacksView
     *            the view that called
     */
    public void setEditMode(Attack attackToAlter, AttacksView attacksView) {
        setCaption("Editing attack");
        createAttackForm(attackToAlter, attacksView, false);
    }
}