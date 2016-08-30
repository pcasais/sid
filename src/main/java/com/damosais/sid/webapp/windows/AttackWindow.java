package com.damosais.sid.webapp.windows;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.damosais.sid.database.beans.Attack;
import com.damosais.sid.database.services.AttackService;
import com.damosais.sid.database.services.ToolService;
import com.damosais.sid.database.services.VulnerabilityService;
import com.damosais.sid.webapp.AttacksView;
import com.damosais.sid.webapp.GraphicResources;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
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
        setSizeUndefined();
        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);
    }

    /**
     * Creates the form to add or edit attacks
     *
     * @param attack
     *            The attack being edited or created
     * @param attacksView
     *            The view which called
     */
    private void createAttackForm(Attack attack, AttacksView attacksView) {
        // 1st) We create the form and assign the binder
        final FormLayout form = new FormLayout();
        final BeanFieldGroup<Attack> binder = new BeanFieldGroup<>(Attack.class);
        binder.setItemDataSource(attack);
        binder.setBuffered(true);
        
        // 2nd) We add the start date
        final DateField startDateField = (DateField) binder.buildAndBind("Start", "start");
        startDateField.setDateFormat("yyyy-MM-dd HH:mm:ss");
        startDateField.setResolution(Resolution.SECOND);
        form.addComponent(startDateField);

        // 3rd) we add the end date
        final DateField endDateField = (DateField) binder.buildAndBind("End", "end");
        endDateField.setDateFormat("yyyy-MM-dd HH:mm:ss");
        endDateField.setResolution(Resolution.SECOND);
        form.addComponent(endDateField);
        
        // 4th) Then we add the ComboBox with all the tools
        final ComboBox toolField = new ComboBox("Tool", toolService.list());
        binder.bind(toolField, "tool");
        form.addComponent(toolField);
        
        // 5th) We add the ComboBox with all the vulnerabilities
        final ComboBox vulnerabilityField = new ComboBox("Vulnerability", vulnerabilityService.list());
        binder.bind(vulnerabilityField, "vulnerability");
        form.addComponent(vulnerabilityField);
        
        // 6th) We create the save button
        final Button saveButton = new Button("Save", event -> {
            try {
                binder.commit();
                final Attack commitedAttack = binder.getItemDataSource().getBean();
                attackService.save(commitedAttack);
                attacksView.refreshAttacksTableContent();
                new Notification("Success", "Attack saved in the database", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                getUI().removeWindow(this);
            } catch (final CommitException e) {
                LOGGER.error("Problem saving attack in database", e);
                new Notification("Failure", "Error saving attack: " + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
            }
        });
        saveButton.setStyleName("link");
        saveButton.setIcon(GraphicResources.SAVE_ICON);
        
        // 7th) We now clear the window and add the components
        content.removeAllComponents();
        content.addComponent(form);
        content.addComponent(saveButton);
        content.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
    }

    /**
     * Prepares the window to add a new attack
     *
     * @param attacksView
     *            The view that called
     */
    public void setAddMode(AttacksView attacksView) {
        setCaption("Adding new attack");
        createAttackForm(new Attack(), attacksView);
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
        createAttackForm(attackToAlter, attacksView);
    }
}