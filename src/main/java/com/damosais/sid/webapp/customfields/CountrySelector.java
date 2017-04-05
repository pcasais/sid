package com.damosais.sid.webapp.customfields;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.tepi.filtertable.FilterTable;

import com.neovisionaries.i18n.CountryCode;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.VerticalLayout;

public class CountrySelector extends CustomField<Set<CountryCode>> {
    private static final long serialVersionUID = -4192153915613729371L;
    private final VerticalLayout content;
    private final BeanItemContainer<CountryCode> container;
    private final FilterTable table;

    /**
     * The constructor sets the caption of the field and initialises the layout
     */
    public CountrySelector(String caption, boolean nullable) {
        setCaption(caption);
        content = new VerticalLayout();
        container = new BeanItemContainer<>(CountryCode.class, Arrays.asList(CountryCode.values()));
        table = new FilterTable();
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setNullSelectionAllowed(nullable);
        table.setFilterBarVisible(true);
        // We add a column with the button to edit the conflict details
        table.setContainerDataSource(container);
        // Now we define which columns are visible and what are going to be their names in the table header
        table.setVisibleColumns(new Object[] { "alpha2", "name" });
        table.setColumnHeaders(new String[] { "Code", "Name" });
        
        content.setSpacing(true);
        content.addComponent(table);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<CountryCode> getInternalValue() {
        final Set<CountryCode> countries = new HashSet<>();
        if (table.getValue() != null && table.getValue() instanceof Set) {
            countries.addAll((Set<CountryCode>) table.getValue());
        } else if (table.getValue() instanceof CountryCode) {
            countries.add((CountryCode) table.getValue());
        }
        return countries;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Set<CountryCode>> getType() {
        return (Class<? extends Set<CountryCode>>) new org.hibernate.collection.internal.PersistentSet().getClass();
    }
    
    @Override
    protected Component initContent() {
        return content;
    }
    
    @Override
    public void setInternalValue(Set<CountryCode> countries) {
        if (countries != null) {
            for (final CountryCode country : countries) {
                table.select(country);
            }
        }
    }
}