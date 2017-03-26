package com.damosais.sid.parsers;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * This class implements the common methods used when parsing from an Excel file
 *
 * @author Pablo Casais
 */
public abstract class ExcelReader {
    protected static final String YEAR_FORMAT = "yyyy";
    protected static final String YEAR_AND_MONTH_FORMAT = "yyyy-MM";
    protected static final String FULL_DATE_FORMAT = "yyyy-MM-dd";
    protected XSSFWorkbook workbook;
    protected final DateFormat justYear = new SimpleDateFormat(YEAR_FORMAT);
    protected final DateFormat yearAndMonth = new SimpleDateFormat(YEAR_AND_MONTH_FORMAT);
    protected final DateFormat fullDate = new SimpleDateFormat(FULL_DATE_FORMAT);

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
     * Checks if the given object is a non empty string
     *
     * @param object
     *            The object to check
     * @return True if the object is a non empty string, false otherwise
     */
    public boolean isObjectANonEmptyString(Object object) {
        return object != null && object instanceof String && StringUtils.isNotBlank((String) object);
    }

    /**
     * This method process the header row and returns a map with the position of the important columns
     *
     * @param row
     *            The row containing the headers
     * @param mappingValues
     *            the mappings done by the user between the column names and the data
     * @return A map with the column number as key and the data it represents as value
     */
    public Map<Integer, String> processHeaderRow(Row row, Map<String, String> mappingValues) {
        final Map<Integer, String> columnIndexes = new HashMap<>();
        final Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            final Cell cell = cellIterator.next();
            final Object cellValue = readCellValue(cell);
            if (cellValue instanceof String && StringUtils.isNotBlank((String) cellValue) && mappingValues.containsKey(cellValue)) {
                columnIndexes.put(cell.getColumnIndex(), mappingValues.get(cellValue));
            }
        }
        return columnIndexes;
    }
    
    /**
     * Reads the value of a cell
     *
     * @param cell
     *            The cell to be checked
     * @return The string value or null if it can't be read as a string
     */
    private Object readCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                return cell.getNumericCellValue();
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
                final Object cellValue = readCellValue(cellIterator.next());
                if (cellValue != null && cellValue instanceof String) {
                    columnNames.add((String) cellValue);
                }
            }
        }
        return columnNames;
    }
    
    /**
     * This method returns a map with the name of the date and the value as value
     *
     * @param row
     *            The row to be processed
     * @param columnMap
     *            The map with the name of the data
     * @return a map with the name of the date and the value as value
     */
    public Map<String, Object> readRowContent(Row row, Map<Integer, String> columnMap) {
        final Iterator<Cell> cellIterator = row.cellIterator();
        final Map<String, Object> valueMap = new HashMap<>();
        while (cellIterator.hasNext() && valueMap.size() < columnMap.size()) {
            final Cell cell = cellIterator.next();
            final Object cellValue = readCellValue(cell);
            if (cellValue != null && columnMap.containsKey(cell.getColumnIndex())) {
                valueMap.put(columnMap.get(cell.getColumnIndex()), cellValue);
            }
        }
        return valueMap;
    }
}