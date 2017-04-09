package com.damosais.sid.webapp.customfields;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.vaadin.data.util.converter.StringToDateConverter;

public class YearMonthDate extends StringToDateConverter {
    private static final long serialVersionUID = -6097760577992594909L;
    
    @Override
    public DateFormat getFormat(Locale locale) {
        return new SimpleDateFormat("yyyy-MMM");
    }
}