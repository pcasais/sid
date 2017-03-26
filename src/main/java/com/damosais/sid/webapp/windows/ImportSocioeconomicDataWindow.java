package com.damosais.sid.webapp.windows;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.damosais.sid.database.beans.CountryVariableValue;
import com.damosais.sid.database.beans.FileMappings;
import com.damosais.sid.database.beans.SocioeconomicVariable;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.services.CountryVariableValueService;
import com.damosais.sid.database.services.FileMappigsService;
import com.damosais.sid.parsers.ExcelSocioEconomicDataReader;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.VulnerabilitiesView;
import com.damosais.sid.webapp.WebApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This class represents the window where the user can import socioeconomic data
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class ImportSocioeconomicDataWindow extends Window implements Receiver, Upload.FailedListener, Upload.SucceededListener, ClickListener {
    private static final long serialVersionUID = -4878340856695966947L;
    private static final Logger LOGGER = Logger.getLogger(VulnerabilitiesView.class);
    private static final String FAILURE = "Failure";
    public static final String COUNTRY = "Country";
    public static final String DATE = "Date";
    private final VerticalLayout content;
    private final Upload fileUpload;
    private final ComboBox sheetsField;
    private Map<String, ComboBox> columnMappings;
    private final Button importButton;
    private BufferedOutputStream tempBuffer;
    private File tempFile;
    private final ExcelSocioEconomicDataReader excelSocioEconomicDataReader;
    private String fileName;
    private FileMappings fileMappings;
    
    @Autowired
    private FileMappigsService fileMappingsService;
    
    @Autowired
    private CountryVariableValueService countryVariableValueService;

    /**
     * The constructor creates a window with all the fields
     */
    public ImportSocioeconomicDataWindow() {
        excelSocioEconomicDataReader = new ExcelSocioEconomicDataReader();
        setModal(true);
        setSizeUndefined();
        // 1st) We create the grid
        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setSpacing(true);
        content.setMargin(true);
        
        // 2nd) We add the uploader to select the file
        fileUpload = new Upload(null, this);
        fileUpload.addFailedListener(this);
        fileUpload.addSucceededListener(this);
        fileUpload.setButtonCaption("Upload excel file");
        fileUpload.setImmediate(true);
        content.addComponent(fileUpload);
        
        // 3nd) We add the sheet selector
        sheetsField = new ComboBox("Sheet", new ArrayList<String>());
        sheetsField.setNullSelectionAllowed(false);
        sheetsField.setImmediate(true);
        sheetsField.addValueChangeListener(changeEvent -> {
            if (changeEvent != null && changeEvent.getProperty() != null && changeEvent.getProperty().getValue() != null) {
                final String sheetName = changeEvent.getProperty().getValue().toString();
                final List<String> columns = excelSocioEconomicDataReader.readColumns(sheetName);
                for (final ComboBox selector : columnMappings.values()) {
                    selector.removeAllItems();
                    selector.addItems(columns);
                }
                fileMappings = fileMappingsService.get(((WebApplication) getUI()).getUser(), fileName, sheetName);
                if (fileMappings != null) {
                    for (final String key : fileMappings.getColumnMappings().keySet()) {
                        final ComboBox selector = columnMappings.get(key);
                        final String value = fileMappings.getColumnMappings().get(key);
                        if (selector != null && selector.containsId(value)) {
                            selector.select(value);
                        }
                    }
                }
            }
        });
        content.addComponent(sheetsField);
        
        // 4th) We then add the rest of the column mappers
        createMappingGrid(new ArrayList<>());

        // 5th) We then add the import button and set the content
        importButton = new Button("Import", this);
        importButton.setStyleName("link");
        importButton.setEnabled(false);
        content.addComponent(importButton);
        setContent(content);
    }
    
    @Override
    public void buttonClick(ClickEvent event) {
        // First we check that the mandatory mappings are defined and that we havee
        final String sheetName = (String) sheetsField.getValue();
        final Map<String, String> variableMappings = new HashMap<>();
        final String countryColumn = (String) columnMappings.get(COUNTRY).getValue();
        variableMappings.put(countryColumn, COUNTRY);
        final String dateColumn = (String) columnMappings.get(DATE).getValue();
        variableMappings.put(dateColumn, DATE);
        for (final SocioeconomicVariable variable : SocioeconomicVariable.values()) {
            final String mappedColumn = (String) columnMappings.get(variable.getName()).getValue();
            if (mappedColumn != null) {
                variableMappings.put(mappedColumn, variable.getName());
            }
        }

        // Now we verify the key mappings and
        if (countryColumn == null) {
            new Notification(FAILURE, "A mapping for the country code column is required", Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
        } else if (dateColumn == null) {
            new Notification(FAILURE, "A mapping for the year column is required", Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
        } else if (variableMappings.size() == 0) {
            new Notification(FAILURE, "You need to map at least one socioeconomic variable column", Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
        } else {
            // First we store the mappings in the database to facilitate future requests
            final Map<String, String> fileColumnMappings = new HashMap<>();
            for (final String key : variableMappings.keySet()) {
                fileColumnMappings.put(variableMappings.get(key), key);
            }
            if (fileMappings == null) {
                fileMappings = new FileMappings();
                fileMappings.setOwner(((WebApplication) getUI()).getUser());
                fileMappings.setFileName(fileName);
                fileMappings.setSheetName((String) sheetsField.getValue());
            }
            fileMappings.setColumnMappings(fileColumnMappings);
            fileMappingsService.save(fileMappings);

            // Then we read the values from the Excel
            final List<CountryVariableValue> readValues = excelSocioEconomicDataReader.readValues(sheetName, variableMappings);
            if (readValues != null && !readValues.isEmpty()) {
                new Notification("Success", readValues.size() + " value" + (readValues.size() > 1 ? "s" : "") + " read from the Excel file", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                processReadValues(readValues);
            } else {
                new Notification(FAILURE, "No values read from the Excel file", Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
            }
        }
    }
    
    private void closeTempWriter() {
        if (tempBuffer != null) {
            try {
                tempBuffer.flush();
                tempBuffer.close();
            } catch (final IOException e) {
                LOGGER.error("Problem closing temp file", e);
                new Notification(FAILURE, "Error closing temp file: " + tempFile.getName(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
            }
        }
    }

    private void createMappingGrid(List<String> columns) {
        // 1st) We create the layout with three columns
        final HorizontalLayout mappers = new HorizontalLayout();
        final VerticalLayout economicLayout = new VerticalLayout();
        mappers.addComponent(economicLayout);
        final VerticalLayout politicalLayout = new VerticalLayout();
        mappers.addComponent(politicalLayout);
        final VerticalLayout socialLayout = new VerticalLayout();
        mappers.addComponent(socialLayout);
        content.addComponent(mappers);
        
        // 2nd) We now add the country and date
        columnMappings = new HashMap<>();
        final ComboBox countryMapping = new ComboBox("Country Code column", columns);
        columnMappings.put(COUNTRY, countryMapping);
        economicLayout.addComponent(countryMapping);
        final ComboBox yearMapping = new ComboBox("Date column", columns);
        columnMappings.put(DATE, yearMapping);
        politicalLayout.addComponent(yearMapping);
        
        // 3rd) Now we add the rest of the columns to a three column grid
        for (final SocioeconomicVariable variable : SocioeconomicVariable.values()) {
            final ComboBox variableMapping = new ComboBox(variable.getName() + " column", columns);
            columnMappings.put(variable.getName(), variableMapping);
            switch (variable.getType()) {
                case ECONOMIC:
                    economicLayout.addComponent(variableMapping);
                    break;
                case POLITICAL:
                    politicalLayout.addComponent(variableMapping);
                    break;
                case SOCIAL:
                    socialLayout.addComponent(variableMapping);
                    break;
                default:
                    LOGGER.debug("Variable of type " + variable.getType() + " not added as type is unrecognised");
            }
        }
    }
    
    /**
     * This method processes the values read from the external file and inserts the correct objects in the database by updates or inserts
     *
     * @param readValues
     *            the list of CountryVariableValue read from the external source
     */
    private void processReadValues(List<CountryVariableValue> readValues) {
        // After we received some values is time to do a mix and see which ones are insert and which are updates
        // 1st) We read the existing values and map them by socioeconomic variable and date
        final Map<SocioeconomicVariable, Map<Date, CountryVariableValue>> mappedExistingValues = new HashMap<>();
        for (final CountryVariableValue existingValue : countryVariableValueService.list()) {
            Map<Date, CountryVariableValue> dateValues = mappedExistingValues.get(existingValue.getVariable());
            if (dateValues == null) {
                dateValues = new HashMap<>();
                mappedExistingValues.put(existingValue.getVariable(), dateValues);
            }
            dateValues.put(existingValue.getDate(), existingValue);
        }

        // 2nd) Now we loop through the parsed values and check if they need to become an update or an insert
        int valuesChanged = 0;
        final User user = ((WebApplication) getUI()).getUser();
        for (final CountryVariableValue readValue : readValues) {
            // 2.1) We try to find the matching value
            CountryVariableValue matching = null;
            final Map<Date, CountryVariableValue> dateValues = mappedExistingValues.get(readValue.getVariable());
            if (dateValues != null) {
                matching = dateValues.get(readValue.getDate());
            }
            if (matching != null && readValue.getValue().compareTo(matching.getValue()) != 0) {
                // 2.2) If is a match and the value is different we update the value and save it
                matching.setValue(readValue.getValue());
                matching.setUpdatedBy(user);
                countryVariableValueService.save(matching);
                valuesChanged++;
            } else if (matching == null) {
                // 2.3) If is a new value then we just put the data about its creator
                readValue.setCreatedBy(user);
                countryVariableValueService.save(readValue);
                valuesChanged++;
            }
        }

        // 3rd) We finally refresh the contents of the table
        if (valuesChanged > 0) {
            new Notification("Success", valuesChanged + " value" + (valuesChanged > 1 ? "s" : "") + " updated from the Excel file", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
        } else {
            new Notification("Warn", "No values updated from the Excel file. Either the data was the same or tehre was an error parsing it", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
        }
        getUI().removeWindow(this);
    }
    
    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        try {
            fileName = filename;
            tempFile = File.createTempFile(fileName, ".xlsx");
            tempBuffer = new BufferedOutputStream(new FileOutputStream(tempFile));
        } catch (final IOException e) {
            new Notification(FAILURE, "Error creating output stream for upload: " + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
            LOGGER.error("Error creating output stream for upload", e);
        }
        return tempBuffer;
    }
    
    @Override
    public void uploadFailed(FailedEvent event) {
        closeTempWriter();
        if (tempFile.exists() && !tempFile.delete()) {
            new Notification(FAILURE, "Error deleting temp file: " + tempFile.getName(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
        }
        new Notification(FAILURE, "Error uploading file: " + event.getReason().getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
    }

    @Override
    public void uploadSucceeded(SucceededEvent event) {
        closeTempWriter();
        // After a successful upload is time to read the file
        try {
            final List<String> sheetNames = excelSocioEconomicDataReader.getSheetNames(tempFile);
            sheetsField.removeAllItems();
            sheetsField.addItems(sheetNames);
            importButton.setEnabled(true);
            importButton.setIcon(GraphicResources.UPLOAD_ICON);
        } catch (final IOException e) {
            new Notification(FAILURE, "Error reading file: " + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
            LOGGER.error("Error reading file", e);
        }
    }
}
