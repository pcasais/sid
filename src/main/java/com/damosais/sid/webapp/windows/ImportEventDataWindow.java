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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.damosais.sid.database.beans.Attack;
import com.damosais.sid.database.beans.Attacker;
import com.damosais.sid.database.beans.FileMappings;
import com.damosais.sid.database.beans.Incident;
import com.damosais.sid.database.beans.Owner;
import com.damosais.sid.database.beans.Target;
import com.damosais.sid.database.beans.Tool;
import com.damosais.sid.database.beans.UnauthorizedResult;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.services.AttackService;
import com.damosais.sid.database.services.AttackerService;
import com.damosais.sid.database.services.EventService;
import com.damosais.sid.database.services.FileMappigsService;
import com.damosais.sid.database.services.IncidentService;
import com.damosais.sid.database.services.OwnerService;
import com.damosais.sid.database.services.TargetService;
import com.damosais.sid.database.services.ToolService;
import com.damosais.sid.database.services.UnauthorizedResultService;
import com.damosais.sid.parsers.ExcelEventDataReader;
import com.damosais.sid.webapp.GraphicResources;
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
 * This class represents the window where the user can import
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class ImportEventDataWindow extends Window implements Receiver, Upload.FailedListener, Upload.SucceededListener, ClickListener {
    private static final long serialVersionUID = 6012590570788192463L;
    private static final Logger LOGGER = Logger.getLogger(ImportEventDataWindow.class);
    private static final String SUCCESS = "Success";
    private static final String FAILURE = "Failure";
    public static final String DATE_FIELD = "Date";
    public static final String ACTION_FIELD = "Action";
    public static final String OWNER_NAME_FIELD = "Owner name";
    public static final String OWNER_COUNTRY_FIELD = "Owner country";
    public static final String OWNER_SECTOR_FIELD = "Owner sector";
    public static final String SITE_NAME_FIELD = "Site name";
    public static final String IPS_FIELD = "Site IPs";
    public static final String SITE_COUNTRY_FIELD = "Site country";
    public static final String UNAUTHORISED_TYPE_FIELD = "Unauthorised type";
    public static final String ADMIN_ACCESS_FIELD = "Admin access";
    public static final String USER_ACCESS_FIELD = "User access";
    public static final String REGISTERS_FIELD = "Registers affected";
    public static final String DOWNTIME_FIELD = "Downtime";
    public static final String AVERAGE_TRAFFIC_FIELD = "Average traffic";
    public static final String PEAK_TRAFFIC_FIELD = "Peak traffic";
    public static final String ECONOMIC_IMPACT_FIELD = "Economic impact";
    public static final String TOOL_NAME_FIELD = "Tool name";
    public static final String TOOL_TYPE_FIELD = "Tool type";
    public static final String VULNERABILITY_NAME_FIELD = "Vulnerability name";
    public static final String VULNERABILITY_TYPE_FIELD = "Vulnerability type";
    public static final String VULNERABILITY_NOTES_FIELD = "Vulnerability notes";
    public static final String VULNERABILITY_CVE_FIELD = "Vulnerability CVE";
    public static final String INCIDENT_NAME_FIELD = "Incident name";
    public static final String ATTACKER_NAME_FIELD = "Attacker name";
    public static final String ATTACKER_COUNTRY_FIELD = "Attacker country";
    public static final String ATTACKER_TYPE_FIELD = "Attacker type";
    public static final String MOTIVATION_FIELD = "Motivation";
    private final ExcelEventDataReader excelEventDataReader;
    private final VerticalLayout content;
    private final Upload fileUpload;
    private final ComboBox sheetsField;
    private Map<String, ComboBox> columnMappings;
    private final Button importButton;
    private BufferedOutputStream tempBuffer;
    private File tempFile;
    private String fileName;
    private FileMappings fileMappings;

    @Autowired
    private FileMappigsService fileMappingsService;

    @Autowired
    private OwnerService ownerService;
    
    @Autowired
    private TargetService targetService;
    
    @Autowired
    private ToolService toolService;
    
    @Autowired
    private UnauthorizedResultService unauthorizedResultService;
    
    @Autowired
    private EventService eventService;

    @Autowired
    private AttackService attackService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private AttackerService attackerService;
    
    /**
     * The constructor creates a window with all the fields
     */
    public ImportEventDataWindow() {
        excelEventDataReader = new ExcelEventDataReader();
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
                final List<String> columns = excelEventDataReader.readColumns(sheetName);
                for (final ComboBox selector : columnMappings.values()) {
                    selector.removeAllItems();
                    selector.addItems(columns);
                }
                // After selecting the sheet we check if we had mappings and try to apply them
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
        // First we read the selected mappings
        final String sheetName = (String) sheetsField.getValue();
        final Map<String, String> mappingValues = new HashMap<>();
        mappingValues.put((String) columnMappings.get(DATE_FIELD).getValue(), DATE_FIELD);
        mappingValues.put((String) columnMappings.get(ACTION_FIELD).getValue(), ACTION_FIELD);
        mappingValues.put((String) columnMappings.get(OWNER_NAME_FIELD).getValue(), OWNER_NAME_FIELD);
        mappingValues.put((String) columnMappings.get(OWNER_COUNTRY_FIELD).getValue(), OWNER_COUNTRY_FIELD);
        mappingValues.put((String) columnMappings.get(OWNER_SECTOR_FIELD).getValue(), OWNER_SECTOR_FIELD);
        mappingValues.put((String) columnMappings.get(SITE_NAME_FIELD).getValue(), SITE_NAME_FIELD);
        mappingValues.put((String) columnMappings.get(IPS_FIELD).getValue(), IPS_FIELD);
        mappingValues.put((String) columnMappings.get(SITE_COUNTRY_FIELD).getValue(), SITE_COUNTRY_FIELD);
        mappingValues.put((String) columnMappings.get(UNAUTHORISED_TYPE_FIELD).getValue(), UNAUTHORISED_TYPE_FIELD);
        mappingValues.put((String) columnMappings.get(ADMIN_ACCESS_FIELD).getValue(), ADMIN_ACCESS_FIELD);
        mappingValues.put((String) columnMappings.get(USER_ACCESS_FIELD).getValue(), USER_ACCESS_FIELD);
        mappingValues.put((String) columnMappings.get(REGISTERS_FIELD).getValue(), REGISTERS_FIELD);
        mappingValues.put((String) columnMappings.get(DOWNTIME_FIELD).getValue(), DOWNTIME_FIELD);
        mappingValues.put((String) columnMappings.get(AVERAGE_TRAFFIC_FIELD).getValue(), AVERAGE_TRAFFIC_FIELD);
        mappingValues.put((String) columnMappings.get(PEAK_TRAFFIC_FIELD).getValue(), PEAK_TRAFFIC_FIELD);
        mappingValues.put((String) columnMappings.get(ECONOMIC_IMPACT_FIELD).getValue(), ECONOMIC_IMPACT_FIELD);
        mappingValues.put((String) columnMappings.get(TOOL_NAME_FIELD).getValue(), TOOL_NAME_FIELD);
        mappingValues.put((String) columnMappings.get(VULNERABILITY_NAME_FIELD).getValue(), VULNERABILITY_NAME_FIELD);
        mappingValues.put((String) columnMappings.get(VULNERABILITY_TYPE_FIELD).getValue(), VULNERABILITY_TYPE_FIELD);
        mappingValues.put((String) columnMappings.get(VULNERABILITY_NOTES_FIELD).getValue(), VULNERABILITY_NOTES_FIELD);
        mappingValues.put((String) columnMappings.get(VULNERABILITY_CVE_FIELD).getValue(), VULNERABILITY_CVE_FIELD);
        mappingValues.put((String) columnMappings.get(INCIDENT_NAME_FIELD).getValue(), INCIDENT_NAME_FIELD);
        mappingValues.put((String) columnMappings.get(ATTACKER_NAME_FIELD).getValue(), ATTACKER_NAME_FIELD);
        mappingValues.put((String) columnMappings.get(ATTACKER_COUNTRY_FIELD).getValue(), ATTACKER_COUNTRY_FIELD);
        mappingValues.put((String) columnMappings.get(ATTACKER_TYPE_FIELD).getValue(), ATTACKER_TYPE_FIELD);
        mappingValues.put((String) columnMappings.get(MOTIVATION_FIELD).getValue(), MOTIVATION_FIELD);
        // Then we check if the values are set
        final List<String> failedMappings = new ArrayList<>();
        for (final String value : mappingValues.keySet()) {
            final String key = mappingValues.get(value);
            if (StringUtils.isBlank(value)) {
                failedMappings.add(key);
            }
        }
        if (!failedMappings.isEmpty()) {
            new Notification(FAILURE, "You need to map the following values to columns: " + failedMappings, Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
        } else {
            // First we store the mappings in the database to facilitate future requests
            final Map<String, String> fileColumnMappings = new HashMap<>();
            for (final String key : mappingValues.keySet()) {
                fileColumnMappings.put(mappingValues.get(key), key);
            }
            if (fileMappings == null) {
                fileMappings = new FileMappings();
                fileMappings.setOwner(((WebApplication) getUI()).getUser());
                fileMappings.setFileName(fileName);
                fileMappings.setSheetName((String) sheetsField.getValue());
            }
            fileMappings.setColumnMappings(fileColumnMappings);
            fileMappingsService.save(fileMappings);
            // Then we get the list of events from the file and mix it
            final List<Owner> owners = ownerService.list();
            final List<Target> targets = targetService.list();
            final List<Tool> tools = toolService.list();
            final List<Incident> incidents = incidentService.list();
            final List<Attacker> attackers = attackerService.list();
            final List<com.damosais.sid.database.beans.Event> events = excelEventDataReader.readAndProcessValues(sheetName, mappingValues, owners, targets, tools, incidents, attackers, eventService.list());
            final User user = ((WebApplication) getUI()).getUser();
            if (!events.isEmpty()) {
                for (final com.damosais.sid.database.beans.Event newEvent : events) {
                    saveEvent(newEvent, owners, targets, tools, incidents, attackers, user);
                }
                new Notification(SUCCESS, events.size() + " event" + (events.size() > 1 ? "s" : "") + " inserted in the system" + failedMappings, Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
                getUI().removeWindow(this);
            } else {
                new Notification(FAILURE, "No new events found in the file", Notification.Type.WARNING_MESSAGE).show(getUI().getPage());
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
        mappers.setSpacing(true);
        final VerticalLayout eventLayout = new VerticalLayout();
        eventLayout.setCaption("Event details");
        eventLayout.setSpacing(true);
        mappers.addComponent(eventLayout);
        final VerticalLayout attackLayout = new VerticalLayout();
        attackLayout.setCaption("Attack details");
        attackLayout.setSpacing(true);
        mappers.addComponent(attackLayout);
        final VerticalLayout incidentLayout = new VerticalLayout();
        incidentLayout.setCaption("Incident details");
        incidentLayout.setSpacing(true);
        mappers.addComponent(incidentLayout);
        content.addComponent(mappers);
        
        // 2nd) First we add the mappers for the event detail
        columnMappings = new HashMap<>();
        generateField(columns, DATE_FIELD, eventLayout);
        generateField(columns, ACTION_FIELD, eventLayout);
        generateField(columns, OWNER_NAME_FIELD, eventLayout);
        generateField(columns, OWNER_COUNTRY_FIELD, eventLayout);
        generateField(columns, OWNER_SECTOR_FIELD, eventLayout);
        generateField(columns, SITE_NAME_FIELD, eventLayout);
        generateField(columns, IPS_FIELD, eventLayout);
        generateField(columns, SITE_COUNTRY_FIELD, eventLayout);
        
        // 3rd) We then add on top the attack details
        generateField(columns, UNAUTHORISED_TYPE_FIELD, attackLayout);
        generateField(columns, ADMIN_ACCESS_FIELD, attackLayout);
        generateField(columns, USER_ACCESS_FIELD, attackLayout);
        generateField(columns, REGISTERS_FIELD, attackLayout);
        generateField(columns, DOWNTIME_FIELD, attackLayout);
        generateField(columns, AVERAGE_TRAFFIC_FIELD, attackLayout);
        generateField(columns, PEAK_TRAFFIC_FIELD, attackLayout);
        generateField(columns, ECONOMIC_IMPACT_FIELD, attackLayout);
        generateField(columns, TOOL_NAME_FIELD, attackLayout);
        generateField(columns, TOOL_TYPE_FIELD, attackLayout);
        generateField(columns, VULNERABILITY_NAME_FIELD, attackLayout);
        generateField(columns, VULNERABILITY_TYPE_FIELD, attackLayout);
        generateField(columns, VULNERABILITY_NOTES_FIELD, attackLayout);
        generateField(columns, VULNERABILITY_CVE_FIELD, attackLayout);
        
        // 4th) We finally add the incident details
        generateField(columns, INCIDENT_NAME_FIELD, incidentLayout);
        generateField(columns, ATTACKER_NAME_FIELD, incidentLayout);
        generateField(columns, ATTACKER_COUNTRY_FIELD, incidentLayout);
        generateField(columns, ATTACKER_TYPE_FIELD, incidentLayout);
        generateField(columns, MOTIVATION_FIELD, incidentLayout);
    }
    
    /**
     * This method adds a field to the given layout
     *
     * @param columns
     *            The values to show in the field
     * @param fieldName
     *            The name of the field
     * @param layout
     *            The layout where to add the field
     */
    private void generateField(List<String> columns, String fieldName, VerticalLayout layout) {
        final ComboBox fieldMapping = new ComboBox(fieldName + " column", columns);
        columnMappings.put(fieldName, fieldMapping);
        layout.addComponent(fieldMapping);
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        try {
            fileName = filename;
            tempFile = File.createTempFile(filename, ".xslx");
            tempBuffer = new BufferedOutputStream(new FileOutputStream(tempFile));
        } catch (final IOException e) {
            new Notification(FAILURE, "Error creating output stream for upload: " + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
            LOGGER.error("Error creating output stream for upload", e);
        }
        return tempBuffer;
    }
    
    private void saveEvent(com.damosais.sid.database.beans.Event event, List<Owner> owners, List<Target> targets, List<Tool> tools, List<Incident> incidents, List<Attacker> attackers, User user) {
        // 1st) We try to save the inner components of the event
        final Owner owner = event.getTarget().getOwner();
        if (!owners.contains(owner)) {
            owner.setCreated(new Date());
            owner.setCreatedBy(user);
            ownerService.save(event.getTarget().getOwner());
        }
        final Target target = event.getTarget();
        if (!targets.contains(target)) {
            target.setCreated(new Date());
            target.setCreatedBy(user);
            targetService.save(target);
        }
        // 2nd) We then save the inner components of the attack
        final Attack attack = event.getAttack();
        final Tool tool = attack.getTool();
        if (tool != null && !tools.contains(tool)) {
            tool.setCreated(new Date());
            tool.setCreatedBy(user);
            toolService.save(tool);
        }
        if (attack.getUnauthorizedResults() != null && !attack.getUnauthorizedResults().isEmpty()) {
            for (final UnauthorizedResult result : attack.getUnauthorizedResults()) {
                result.setCreated(new Date());
                result.setCreatedBy(user);
                unauthorizedResultService.save(result);
            }
        }
        
        // 3rd) And then we do the same with the ones of the incident
        final Incident incident = attack.getIncident();
        if (incident.getAttackers() != null && !incident.getAttackers().isEmpty()) {
            for (final Attacker attacker : incident.getAttackers()) {
                if (!attackers.contains(attacker)) {
                    attacker.setCreated(new Date());
                    attacker.setCreatedBy(user);
                    attackerService.save(attacker);
                }
            }
        }

        // 4th) Now we save the event, attack and incident itself
        if (!incidents.contains(incident)) {
            incident.setCreated(new Date());
            incident.setCreatedBy(user);
            incidentService.save(incident);
        }
        attack.setCreated(new Date());
        attack.setCreatedBy(user);
        attackService.save(attack);
        event.setCreated(new Date());
        event.setCreatedBy(user);
        eventService.save(event);
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
            final List<String> sheetNames = excelEventDataReader.getSheetNames(tempFile);
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