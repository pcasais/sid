package com.damosais.sid.webapp.customfields;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.InetAddressValidator;

import com.damosais.sid.webapp.GraphicResources;
import com.vaadin.data.Validator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * This class handles the way to add or edit IPs in a form
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public class IPsField extends CustomField<List<String>> {
    private static final long serialVersionUID = -6264998242809457748L;
    private final List<String> ips;
    private final VerticalLayout fields;
    private final HorizontalLayout content;
    
    /**
     * The constructor sets the caption of the field and initialises the layout
     */
    public IPsField() {
        setCaption("IPs");
        ips = new ArrayList<>();
        fields = new VerticalLayout();
        content = new HorizontalLayout();
        content.setSpacing(true);
        content.addComponent(fields);
        
        final Button addIP = new Button("", this::addItem);
        addIP.setStyleName("link");
        addIP.setIcon(GraphicResources.ADD_ICON);
        content.addComponent(addIP);
        content.setComponentAlignment(fields, Alignment.TOP_LEFT);
        content.setComponentAlignment(addIP, Alignment.BOTTOM_LEFT);
    }
    
    private void addIpItem(String ipValue, boolean recursive) {
        List<String> list = getValue();
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add("");
        if (recursive) {
            setValue(list);
        }
        final HorizontalLayout ipLine = new HorizontalLayout();
        ipLine.setSpacing(true);
        final TextField tf = new TextField();
        if (ipValue != null) {
            tf.setValue(ipValue);
        }
        tf.addValidator(value -> {
            if (!InetAddressValidator.getInstance().isValid((String) value)) {
                throw new Validator.InvalidValueException("The value '" + value + "' is not a valid IP");
            }
        });
        tf.addValueChangeListener(valueChange -> { // Java 8
            final int index = fields.getComponentIndex(ipLine);
            if (index > -1) {
                final List<String> values = getValue();
                values.set(index, tf.getValue());
                setValue(values);
            }
        });
        ipLine.addComponent(tf);
        final Button deleteButton = new Button("", event -> {
            final HorizontalLayout lineToDelete = (HorizontalLayout) event.getButton().getData();
            fields.removeComponent(lineToDelete);
            final List<String> value = getValue();
            value.remove(tf.getValue());
            setValue(value);
        });
        deleteButton.setData(ipLine);
        deleteButton.setStyleName("link");
        deleteButton.setIcon(GraphicResources.DELETE_ICON);
        ipLine.addComponent(deleteButton);
        fields.addComponent(ipLine);
    }

    void addItem(ClickEvent event) {
        addIpItem(null, true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends List<String>> getType() {
        return (Class<List<String>>) new ArrayList<String>().getClass();
    }
    
    @Override
    protected com.vaadin.ui.Component initContent() {
        return content;
    }

    @Override
    public void setInternalValue(List<String> ips) {
        if (ips != null && !this.ips.equals(ips)) {
            getValue().clear();
            fields.removeAllComponents();
            for (final String ip : ips) {
                addIpItem(ip, false);
            }
        }
        super.setInternalValue(ips);
    }
}