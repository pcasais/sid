package com.damosais.sid.parsers;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.damosais.sid.database.beans.CountryVariableValue;
import com.damosais.sid.database.beans.SocioeconomicVariable;
import com.neovisionaries.i18n.CountryCode;

/**
 * This class models the reader of an Excel file containing socio-economic data
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public class ExcelSocioEconomicDataReader {
    private static final Logger LOGGER = Logger.getLogger(ExcelSocioEconomicDataReader.class);
    private XSSFWorkbook workbook;

    /**
     * Returns the name of the sheets
     *
     * @param file
     *            The file we are reading
     * @throws IOException
     *             If there is a problem reading the file
     * @return the name of the sheets
     */
    public List<String> getSheetNames(File file) throws IOException {
        try {
            workbook = new XSSFWorkbook(file);
            final List<String> sheetNames = new ArrayList<>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheetNames.add(workbook.getSheetName(i));
            }
            return sheetNames;
        } catch (final InvalidFormatException e) {
            throw new IOException("Invalid format exception when reading file", e);
        }
    }
    
    /**
     * This method process a row of data from the file
     *
     * @param rowNumber
     *            The row number being parsed
     * @param variablesMapping
     *            The map with the column names and the socioeconomic variables
     * @param countryColumn
     *            The column that contains the country code
     * @param dateColumn
     *            The column that contains the date
     * @param columnMap
     *            The map with the column numbers and names
     * @param rowIterator
     *            The iterator for the rows
     * @param justYear
     *            The format to parse dates that just have the year
     * @param yearAndMonth
     *            The format to parse dates that have year and month
     * @param fullDate
     *            The format to parse dates that are defined fully
     * @return A list with the variables read from this row
     */
    private List<CountryVariableValue> processDataRow(int rowNumber, Map<String, SocioeconomicVariable> variablesMapping, int countryColumn, int dateColumn, final Map<Integer, String> columnMap, final Iterator<Row> rowIterator, final DateFormat justYear, final DateFormat yearAndMonth, final DateFormat fullDate) {
        final List<CountryVariableValue> values = new ArrayList<>();
        final Iterator<Cell> cellIterator = rowIterator.next().cellIterator();
        int columnNumber = 0;
        String countryCode = null;
        String dateValue = null;
        final Map<SocioeconomicVariable, Double> rowValues = new HashMap<>();
        // 1st) We loop through the columns reading the values
        while (cellIterator.hasNext()) {
            final Cell cell = cellIterator.next();
            if (columnNumber == countryColumn) {
                countryCode = readCellValue(cell);
            } else if (columnNumber == dateColumn) {
                dateValue = readCellValue(cell);
            } else if (columnMap.containsKey(columnNumber) && variablesMapping.containsKey(columnMap.get(columnNumber))) {
                final SocioeconomicVariable varible = variablesMapping.get(columnMap.get(columnNumber));
                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    rowValues.put(varible, cell.getNumericCellValue());
                }
            }
            columnNumber++;
        }
        // 2nd) We only process rows that have a value set for country and date
        if (StringUtils.isNotBlank(countryCode) && StringUtils.isNotBlank(dateValue)) {
            final CountryCode country = CountryCode.getByCode(countryCode, false);
            Date date = null;
            // 2.1) For the date we attempt three different parsings
            try {
                date = fullDate.parse(dateValue);
            } catch (final ParseException e) {
                LOGGER.debug("The object in row " + rowNumber + " has a date that is not full format");
            }
            if (date == null) {
                try {
                    date = yearAndMonth.parse(dateValue);
                } catch (final ParseException e) {
                    LOGGER.debug("The object in row " + rowNumber + " has a date that is not in year and month format");
                }
            }
            if (date == null) {
                try {
                    date = justYear.parse(dateValue);
                } catch (final ParseException e) {
                    LOGGER.debug("The object in row " + rowNumber + " has a date that is not in year format");
                }
            }
            // 3rd) We then check that the date and country are valid and has some data in it
            if (country != null && date != null && !rowValues.isEmpty()) {
                for (final SocioeconomicVariable variable : rowValues.keySet()) {
                    final CountryVariableValue value = new CountryVariableValue();
                    value.setCountry(country);
                    value.setDate(date);
                    value.setVariable(variable);
                    value.setValue(rowValues.get(variable));
                    values.add(value);
                }
            } else {
                LOGGER.debug("The object in row " + rowNumber + " doesn't have a valid country/date/data");
            }
        }
        return values;
    }
    
    /**
     * This method processes the header row and returns an array with the location of the country and date columns
     *
     * @param rowIterator
     *            The iterator to loop through the rows
     * @param columnMap
     *            The map where to store all the columns
     * @param countryColumnName
     *            The name of the column containing the country code
     * @param dateColumnName
     *            The name of the column containing the date
     * @return An array of size 2 being [countryCodeColumn, dateColumn] or -1 for any value not found
     */
    private int[] processHeaderRow(Iterator<Row> rowIterator, Map<Integer, String> columnMap, String countryColumnName, String dateColumnName) {
        final int[] result = new int[2];
        result[0] = -1;
        result[1] = -1;
        final Iterator<Cell> cellIterator = rowIterator.next().cellIterator();
        int columnNumber = 0;
        while (cellIterator.hasNext()) {
            final String cellValue = readCellValue(cellIterator.next());
            if (cellValue != null) {
                if (countryColumnName.equalsIgnoreCase(cellValue)) {
                    result[0] = columnNumber;
                } else if (dateColumnName.equalsIgnoreCase(cellValue)) {
                    result[1] = columnNumber;
                } else {
                    columnMap.put(columnNumber, cellValue);
                }
            }
            columnNumber++;
        }
        return result;
    }
    
    /**
     * Reads the value of a cell
     *
     * @param cell
     *            The cell to be checked
     * @return The string value or null if it can't be read as a string
     */
    private String readCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case Cell.CELL_TYPE_NUMERIC:
                return Double.toString(cell.getNumericCellValue());
            default:
                return null;
        }
    }

    /**
     * Reads the columns headers so the user can map them to their values
     *
     * @param sheetName
     *            The name of the sheet
     * @return A list with the column names
     */
    public List<String> readColumns(String sheetName) {
        final List<String> columnNames = new ArrayList<>();
        final XSSFSheet sheet = workbook.getSheet(sheetName);
        final Iterator<Row> rowIterator = sheet.iterator();
        if (rowIterator.hasNext()) {
            final Iterator<Cell> cellIterator = rowIterator.next().cellIterator();
            while (cellIterator.hasNext()) {
                final String cellValue = readCellValue(cellIterator.next());
                if (cellValue != null) {
                    columnNames.add(cellValue);
                }
            }
        }
        return columnNames;
    }
    
    /**
     * Reads the values from the excel file
     *
     * @param sheetName
     *            The name of the sheet
     * @param countryColumnName
     *            The column from which we will read the country values
     * @param dateColumnName
     *            The column from which we will read the date
     * @param variablesMapping
     *            A map with the socioeconomic variables and the column where they are mapped
     * @return
     */
    public List<CountryVariableValue> readValues(String sheetName, String countryColumnName, String dateColumnName, Map<String, SocioeconomicVariable> variablesMapping) {
        final List<CountryVariableValue> values = new ArrayList<>();
        final XSSFSheet sheet = workbook.getSheet(sheetName);
        int countryColumn = -1;
        int dateColumn = -1;
        final Map<Integer, String> columnMap = new HashMap<>();
        final Iterator<Row> rowIterator = sheet.iterator();
        int rowNumber = 0;
        final DateFormat justYear = new SimpleDateFormat("yyyy");
        final DateFormat yearAndMonth = new SimpleDateFormat("yyyy-MM");
        final DateFormat fullDate = new SimpleDateFormat("yyyy-MM-dd");
        while (rowIterator.hasNext()) {
            if (rowNumber == 0) {
                // If is the header row then we map the position of every column name
                final int[] headers = processHeaderRow(rowIterator, columnMap, countryColumnName, dateColumnName);
                countryColumn = headers[0];
                dateColumn = headers[1];
            } else {
                // In this case we are reading a row so we try to get every value that has been mapped
                values.addAll(processDataRow(rowNumber, variablesMapping, countryColumn, dateColumn, columnMap, rowIterator, justYear, yearAndMonth, fullDate));
            }
            rowNumber++;
        }
        return values;
    }
}