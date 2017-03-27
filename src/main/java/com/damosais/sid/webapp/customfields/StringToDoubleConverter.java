package com.damosais.sid.webapp.customfields;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/**
 * This class allows the conversion between a String presentation and a Double model
 * 
 * @author Pablo Casais
 */
public class StringToDoubleConverter implements Converter<String, Double> {
    private static final long serialVersionUID = -8181466202656286531L;
    
    @Override
    public Double convertToModel(String value, Class<? extends Double> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
        return value != null ? Double.parseDouble(value) : 0d;
    }

    @Override
    public String convertToPresentation(Double value, Class<? extends String> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
        return value != null ? value.toString() : "0.0";
    }

    @Override
    public Class<Double> getModelType() {
        return Double.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}