package com.damosais.sid.webapp.customfields;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/**
 * This class allows the conversion between a String presentation and a Long value
 *
 * @author Pablo Casais
 */
public class StringLongConverter implements Converter<String, Long> {
    private static final long serialVersionUID = 6399234749935167049L;
    
    @Override
    public Long convertToModel(String value, Class<? extends Long> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
        return value != null ? Long.parseLong(value) : 0L;
    }

    @Override
    public String convertToPresentation(Long value, Class<? extends String> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
        return value != null ? value.toString() : "0";
    }

    @Override
    public Class<Long> getModelType() {
        return Long.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}