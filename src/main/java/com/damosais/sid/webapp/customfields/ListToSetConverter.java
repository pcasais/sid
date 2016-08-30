package com.damosais.sid.webapp.customfields;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.vaadin.data.util.converter.Converter;

/**
 * This class allows the conversion between a country code and its numeric representation
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 * @param <T>
 *            The class of the list to be converted to set
 */
public class ListToSetConverter<T> implements Converter<Object, Set<T>> {
    private static final long serialVersionUID = 2825935903701681690L;

    @SuppressWarnings("unchecked")
    @Override
    public Set<T> convertToModel(Object value, Class<? extends Set<T>> targetType, Locale locale) throws ConversionException {
        if (value instanceof Set) {
            return (Set<T>) value;
        }
        final Set<T> mySet = new HashSet<>();
        if (value != null) {
            if (value instanceof List) {
                mySet.addAll((List<T>) value);
            } else {
                mySet.add((T) value);
            }
        }
        return mySet;
    }

    @Override
    public Object convertToPresentation(Set<T> value, Class<? extends Object> targetType, Locale locale) throws ConversionException {
        return value != null ? new HashSet<>(value) : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Set<T>> getModelType() {
        return (Class<Set<T>>) new HashSet<T>().getClass();
    }

    @Override
    public Class<Object> getPresentationType() {
        return Object.class;
    }
    
}