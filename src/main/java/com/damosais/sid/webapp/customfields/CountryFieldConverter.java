package com.damosais.sid.webapp.customfields;

import java.util.Locale;

import com.neovisionaries.i18n.CountryCode;
import com.vaadin.data.util.converter.Converter;

/**
 * This class allows the conversion between a country code and its numeric representation
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public class CountryFieldConverter implements Converter<Object, CountryCode> {
    private static final long serialVersionUID = 2825935903701681690L;
    
    @Override
    public CountryCode convertToModel(Object value, Class<? extends CountryCode> countryType, Locale locale) throws ConversionException {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return CountryCode.getByCode((Integer) value);
        } else {
            return null;
        }
    }
    
    @Override
    public Object convertToPresentation(CountryCode value, Class<? extends Object> countryType, Locale locale) throws ConversionException {
        return value != null ? value.getNumeric() : null;
    }
    
    @Override
    public Class<CountryCode> getModelType() {
        return CountryCode.class;
    }
    
    @Override
    public Class<Object> getPresentationType() {
        return Object.class;
    }
}