package com.damosais.sid.parsers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.damosais.sid.database.beans.CountryVariableValue;
import com.damosais.sid.database.beans.SocioeconomicVariable;
import com.damosais.sid.webapp.windows.ImportSocioeconomicDataWindow;
import com.neovisionaries.i18n.CountryCode;

/**
 * This class models the reader of an Excel file containing socio-economic data
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public class ExcelSocioEconomicDataReader extends ExcelReader {
    private static final Logger LOGGER = Logger.getLogger(ExcelSocioEconomicDataReader.class);
    private static final String THE_OBJECT_IN_ROW = "The object in row ";
    
    /**
     * This method process a row of data from the file
     *
     * @param rowNumber
     *            The row number being parsed
     * @param row
     *            The row we are processing
     * @param columnMap
     *            The mapping of the
     * @return A list with the variables read from this row
     */
    private List<CountryVariableValue> processDataRow(int rowNumber, Row row, Map<Integer, String> columnMap) {
        final List<CountryVariableValue> values = new ArrayList<>();
        // 1st) We get the content of the row
        final Map<String, Object> rowContents = readRowContent(row, columnMap);

        // 2nd) Now we get the country and date first
        final Object countryRaw = rowContents.get(ImportSocioeconomicDataWindow.COUNTRY);
        CountryCode country = null;
        boolean error = false;
        if (countryRaw == null || !(countryRaw instanceof String) || StringUtils.isBlank((String) countryRaw) || (country = CountryCode.getByCode((String) countryRaw, false)) == null) {
            LOGGER.error(THE_OBJECT_IN_ROW + rowNumber + " has an invalid country. Skipping variables");
            error = true;
        }
        final Object dateRaw = rowContents.get(ImportSocioeconomicDataWindow.DATE);
        Date date = null;
        if (dateRaw != null && dateRaw instanceof Date) {
            date = (Date) dateRaw;
        } else if (dateRaw != null && dateRaw instanceof String && StringUtils.isNotBlank((String) dateRaw)) {
            // If the date is in string format then we try to parse it using three different date formats
            try {
                date = fullDate.parse((String) dateRaw);
            } catch (final ParseException e) {
                LOGGER.debug(THE_OBJECT_IN_ROW + rowNumber + " has a date that is not full format");
            }
            if (date == null) {
                try {
                    date = yearAndMonth.parse((String) dateRaw);
                } catch (final ParseException e) {
                    LOGGER.debug(THE_OBJECT_IN_ROW + rowNumber + " has a date that is not in year and month format");
                }
            }
            if (date == null) {
                try {
                    date = justYear.parse((String) dateRaw);
                } catch (final ParseException e) {
                    LOGGER.debug(THE_OBJECT_IN_ROW + rowNumber + " has a date that is not in year format");
                }
            }
            if (date == null) {
                LOGGER.error(THE_OBJECT_IN_ROW + rowNumber + " has a date that is not in one of the valid formats (" + FULL_DATE_FORMAT + ", " + YEAR_AND_MONTH_FORMAT + " or " + YEAR_FORMAT + "). Skipping variables");
                error = true;
            }
        } else {
            LOGGER.error(THE_OBJECT_IN_ROW + rowNumber + " has an invalid date. Skipping variables");
            error = true;
        }

        // Now is time to start creating the variables
        if (!error) {
            for (final String variableName : rowContents.keySet()) {
                // Fist we check that the variable and its value are defined
                final SocioeconomicVariable variable = SocioeconomicVariable.getByName(variableName);
                final Object valueRaw = rowContents.get(variableName);
                Double valueParsed = null;
                if (variable != null && valueRaw instanceof Double) {
                    valueParsed = (Double) valueRaw;
                } else if (variable != null && valueRaw instanceof String) {
                    try {
                        valueParsed = Double.parseDouble((String) valueRaw);
                    } catch (final NumberFormatException nfe) {
                        LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + " has an invalid value for the variable " + variableName);
                    }
                } else if (variable != null) {
                    LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + " has an invalid value for the variable " + variableName);
                }
                // If we managed to read both values then we create the object and add it to the list
                if (variable != null && valueParsed != null) {
                    final CountryVariableValue value = new CountryVariableValue();
                    value.setCountry(country);
                    value.setDate(date);
                    value.setVariable(variable);
                    value.setValue(valueParsed);
                    values.add(value);
                }
            }
        }
        return values;
    }
    
    /**
     * Reads the values from the excel file
     *
     * @param sheetName
     *            The name of the sheet
     * @param mappingValues
     *            A map with the socioeconomic variables and the column where they are mapped and the date and country column mappings
     * @return
     */
    public List<CountryVariableValue> readValues(String sheetName, Map<String, String> mappingValues) {
        final List<CountryVariableValue> values = new ArrayList<>();
        final XSSFSheet sheet = workbook.getSheet(sheetName);
        final Map<Integer, String> columnMap = new HashMap<>();
        final Iterator<Row> rowIterator = sheet.iterator();
        int rowNumber = 0;
        while (rowIterator.hasNext()) {
            if (rowNumber == 0) {
                // If is the header row then we map the position of every column name
                columnMap.putAll(processHeaderRow(rowIterator.next(), mappingValues));
            } else {
                values.addAll(processDataRow(rowNumber, rowIterator.next(), columnMap));
            }
            rowNumber++;
        }
        return values;
    }
}