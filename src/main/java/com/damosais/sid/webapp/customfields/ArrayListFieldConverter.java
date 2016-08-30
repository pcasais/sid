package com.damosais.sid.webapp.customfields;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/**
 * This is to convert from any list implementation to an arraylist
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public class ArrayListFieldConverter implements Converter<List<String>, Object> {
    private static final long serialVersionUID = 2043220672860824905L;

    @Override
    public Object convertToModel(List<String> value, Class<? extends Object> targetType, Locale locale) throws ConversionException {
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> convertToPresentation(Object value, Class<? extends List<String>> targetType, Locale locale) throws ConversionException {
        final List<String> arrayListValues = new ArrayList<>();
        if (value instanceof List<?>) {
            for (final String string : (List<String>) value) {
                arrayListValues.add(string);
            }
        }
        return arrayListValues;
    }

    @Override
    public Class<Object> getModelType() {
        return Object.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<List<String>> getPresentationType() {
        return (Class<List<String>>) new ArrayList<String>().getClass();
    }
}