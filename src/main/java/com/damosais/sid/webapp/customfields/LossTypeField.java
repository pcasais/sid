package com.damosais.sid.webapp.customfields;

import com.damosais.sid.database.beans.LossType;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.GridLayout;

/**
 * This class represents the field for the loss types
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public class LossTypeField extends CustomField<LossType> {
    private static final long serialVersionUID = 4220906076016154077L;
    private final GridLayout content;
    private LossType lossType;
    private final CheckBox availability;
    private final CheckBox confidenciality;
    private final CheckBox integrity;
    private final CheckBox adminSecurityProtection;
    private final CheckBox userSecurityProtection;
    private final CheckBox otherSecurityProtection;

    /**
     * The constructor initialises the components
     */
    public LossTypeField() {
        setCaption("Loss Type");
        lossType = new LossType();
        content = new GridLayout(2, 3);
        content.setSpacing(true);
        content.setMargin(false);
        // In 0-0 we have the Availability check box
        availability = new CheckBox("Availability");
        availability.addValueChangeListener(event -> {
            lossType.setAvailability((Boolean) event.getProperty().getValue());
        });
        availability.setImmediate(true);
        // In 0-1 we have the confidentiality check box
        content.addComponent(availability, 0, 0);
        confidenciality = new CheckBox("Confidentiality");
        confidenciality.addValueChangeListener(event -> {
            lossType.setConfidentiality((Boolean) event.getProperty().getValue());
        });
        confidenciality.setImmediate(true);
        content.addComponent(confidenciality, 0, 1);
        // In 0-2 we have the integrity check box
        integrity = new CheckBox("Integrity");
        integrity.addValueChangeListener(event -> {
            lossType.setIntegrity((Boolean) event.getProperty().getValue());
        });
        integrity.setImmediate(true);
        content.addComponent(integrity, 0, 2);
        // In 0-0 we have the Availability check box
        adminSecurityProtection = new CheckBox("Admin Security");
        adminSecurityProtection.addValueChangeListener(event -> {
            lossType.setAdminSecurityProtection((Boolean) event.getProperty().getValue());
        });
        adminSecurityProtection.setImmediate(true);
        // In 0-1 we have the confidentiality check box
        content.addComponent(adminSecurityProtection, 1, 0);
        userSecurityProtection = new CheckBox("User Security");
        userSecurityProtection.addValueChangeListener(event -> {
            lossType.setUserSecurityProtection((Boolean) event.getProperty().getValue());
        });
        userSecurityProtection.setImmediate(true);
        content.addComponent(userSecurityProtection, 1, 1);
        // In 0-2 we have the integrity check box
        otherSecurityProtection = new CheckBox("Other Security");
        otherSecurityProtection.addValueChangeListener(event -> {
            lossType.setOtherSecurityProtection((Boolean) event.getProperty().getValue());
        });
        otherSecurityProtection.setImmediate(true);
        content.addComponent(otherSecurityProtection, 1, 2);
    }

    @Override
    public Class<? extends LossType> getType() {
        return LossType.class;
    }
    
    @Override
    protected Component initContent() {
        return content;
    }
    
    @Override
    public void setInternalValue(LossType lossType) {
        if (lossType == null) {
            this.lossType = new LossType();
        } else {
            this.lossType = lossType;
        }
        availability.setValue(this.lossType.getAvailability());
        confidenciality.setValue(this.lossType.getConfidentiality());
        integrity.setValue(this.lossType.getIntegrity());
        adminSecurityProtection.setValue(this.lossType.getAdminSecurityProtection());
        userSecurityProtection.setValue(this.lossType.getUserSecurityProtection());
        otherSecurityProtection.setValue(this.lossType.getOtherSecurityProtection());
        super.setInternalValue(this.lossType);
    }
}