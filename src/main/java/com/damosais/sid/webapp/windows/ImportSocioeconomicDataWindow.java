package com.damosais.sid.webapp.windows;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.damosais.sid.database.beans.CountryVariableValue;
import com.damosais.sid.database.beans.SocioeconomicVariable;
import com.damosais.sid.parsers.ExcelSocioEconomicDataReader;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.VulnerabilitiesView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This class represents the window where the user can import
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class ImportSocioeconomicDataWindow extends Window implements Receiver, Upload.StartedListener, Upload.FailedListener, Upload.SucceededListener, ClickListener {
    private static final long serialVersionUID = -4878340856695966947L;
    private static final Logger LOGGER = Logger.getLogger(VulnerabilitiesView.class);
    private static final String FAILURE = "Failure";
    private static final String COUNTRY = "Country";
    private static final String DATE = "Date";
    private final VerticalLayout content;
    private final Upload fileUpload;
    private final ComboBox sheetsField;
    private Map<String, ComboBox> columnMappings;
    private final Button importButton;
    private BufferedOutputStream tempBuffer;
    private File tempFile;
    private final ExcelSocioEconomicDataReader excelSocioEconomicDataReader;

    /**
     * The constructor just creates a blank window
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
        fileUpload.addStartedListener(this);
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
                final List<String> columns = excelSocioEconomicDataReader.readColumns(changeEvent.getProperty().getValue().toString());
                for (final ComboBox selector : columnMappings.values()) {
                    selector.removeAllItems();
                    selector.addItems(columns);
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
        // First we read the selected mappings
        final String sheetName = (String) sheetsField.getValue();
        final String countryColumn = (String) columnMappings.get(COUNTRY).getValue();
        final String dateColumn = (String) columnMappings.get(DATE).getValue();
        final Map<String, SocioeconomicVariable> variableMappings = new HashMap<>();
        for (final SocioeconomicVariable variable : SocioeconomicVariable.values()) {
            final String mappedColumn = (String) columnMappings.get(variable.getName()).getValue();
            if (mappedColumn != null) {
                variableMappings.put(mappedColumn, variable);
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
            // If we have mappings then we read the values from the Excel
            final List<CountryVariableValue> readValues = excelSocioEconomicDataReader.readValues(sheetName, countryColumn, dateColumn, variableMappings);
            if (readValues != null && !readValues.isEmpty()) {
                new Notification("Success", readValues.size() + " value" + (readValues.size() > 1 ? "s" : "") + " read from the Excel file", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                getUI().removeWindow(this);
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
    
    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        try {
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
    public void uploadStarted(StartedEvent event) {
        try {
            tempFile = File.createTempFile("tempUpload", ".xlsx");
        } catch (final IOException e) {
            new Notification(FAILURE, "Error creating temporary file for upload: " + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
            LOGGER.error("Error creating temporary file for upload", e);
        }
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
