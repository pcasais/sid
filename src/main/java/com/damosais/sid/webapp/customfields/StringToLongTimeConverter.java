package com.damosais.sid.webapp.customfields;

import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.vaadin.data.util.converter.Converter;

/**
 * Converts a presentation Date to a model Long
 *
 * @author Pablo Casais
 */
public class StringToLongTimeConverter implements Converter<String, Long> {
    private static final long serialVersionUID = 4888189836489055926L;
    private final PeriodFormatter daysAndTime = new PeriodFormatterBuilder().printZeroAlways().appendHours().appendSeparator(":").appendMinutes().appendSeparator(":").appendSecondsWithMillis().toFormatter();

    @Override
    public Long convertToModel(String value, Class<? extends Long> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
        return value != null ? daysAndTime.parsePeriod(value).toDurationFrom(new DateTime(0)).getMillis() : 0L;
    }
    
    @Override
    public String convertToPresentation(Long value, Class<? extends String> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
        return daysAndTime.print(value != null ? new Period(value.longValue()) : new Period(0L));
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