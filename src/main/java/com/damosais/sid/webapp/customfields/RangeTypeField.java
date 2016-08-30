package com.damosais.sid.webapp.customfields;

import com.damosais.sid.database.beans.RangeType;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.GridLayout;

/**
 * This class is the field to populate the range type fields
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public class RangeTypeField extends CustomField<RangeType> {
    private static final long serialVersionUID = -110565337778593173L;
    private final GridLayout content;
    private RangeType rangeType;
    private final CheckBox local;
    private final CheckBox localNetwork;
    private final CheckBox network;
    private final CheckBox userInit;
    
    /**
     * The constructor initialises the layout
     */
    public RangeTypeField() {
        setCaption("Range Type");
        rangeType = new RangeType();
        content = new GridLayout(2, 2);
        content.setSpacing(true);
        content.setMargin(false);
        // In 0-0 we have the Availability check box
        local = new CheckBox("Local");
        local.addValueChangeListener(event -> {
            rangeType.setLocal((Boolean) event.getProperty().getValue());
        });
        local.setImmediate(true);
        // In 0-1 we have the confidentiality check box
        content.addComponent(local, 0, 0);
        localNetwork = new CheckBox("Local Network");
        localNetwork.addValueChangeListener(event -> {
            rangeType.setLocalNetwork((Boolean) event.getProperty().getValue());
        });
        localNetwork.setImmediate(true);
        content.addComponent(localNetwork, 0, 1);
        // In 1-0 we have the Availability check box
        network = new CheckBox("Network");
        network.addValueChangeListener(event -> {
            rangeType.setNetwork((Boolean) event.getProperty().getValue());
        });
        network.setImmediate(true);
        // In 1-1 we have the confidentiality check box
        content.addComponent(network, 1, 0);
        userInit = new CheckBox("User Init");
        userInit.addValueChangeListener(event -> {
            rangeType.setUserInit((Boolean) event.getProperty().getValue());
        });
        userInit.setImmediate(true);
        content.addComponent(userInit, 1, 1);
    }
    
    @Override
    public Class<? extends RangeType> getType() {
        return RangeType.class;
    }

    @Override
    protected Component initContent() {
        return content;
    }

    @Override
    public void setInternalValue(RangeType rangeType) {
        if (rangeType == null) {
            this.rangeType = new RangeType();
        } else {
            this.rangeType = rangeType;
        }
        local.setValue(this.rangeType.getLocal());
        localNetwork.setValue(this.rangeType.getLocalNetwork());
        network.setValue(this.rangeType.getNetwork());
        userInit.setValue(this.rangeType.getUserInit());
        super.setInternalValue(this.rangeType);
    }
}