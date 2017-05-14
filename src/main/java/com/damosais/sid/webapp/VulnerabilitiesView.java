package com.damosais.sid.webapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.tepi.filtertable.FilterTable;
import org.xml.sax.SAXException;

import com.damosais.sid.database.beans.CVEDefinition;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.beans.Vulnerability;
import com.damosais.sid.database.services.CVEDefinitionService;
import com.damosais.sid.database.services.VulnerabilityService;
import com.damosais.sid.parsers.CVENVDParser;
import com.damosais.sid.webapp.windows.CVEWindow;
import com.damosais.sid.webapp.windows.VulnerabilityWindow;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.CustomTable.ColumnGenerator;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;

/**
 * This is the screen from which users can see the different vulnerabilities and interact with them
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@SpringView(name = VulnerabilitiesView.VIEW_NAME)
public class VulnerabilitiesView extends VerticalLayout implements View, ClickListener, ColumnGenerator, Receiver, Upload.StartedListener, Upload.FailedListener, Upload.SucceededListener {
    private static final Logger LOGGER = Logger.getLogger(VulnerabilitiesView.class);
    private static final long serialVersionUID = 4990531322527027247L;
    public static final String VIEW_NAME = "VulnerabilitiesScreen";
    private static final String EDIT_BUTTON = "editButton";
    private static final String DELETE_BUTTON = "deleteButton";
    private static final String UPDATED_BY_NAME = "updatedBy.name";
    private static final String CREATED_BY_NAME = "createdBy.name";
    private static final String FAILURE = "Failure";
    private BeanItemContainer<Vulnerability> vulnerabilityContainer;
    private BeanItemContainer<CVEDefinition> cveContainer;
    private Button addVulnerability;
    private Button addCVE;
    private Upload updateCVEs;
    private FilterTable vulnerabilityTable;
    private FilterTable cveTable;
    private File tempXML;
    private BufferedWriter bufferedWriter;
    private CVENVDParser cveNvdParser;

    @Autowired
    private VulnerabilityService vulnerabilityService;
    
    @Autowired
    private CVEDefinitionService cveDefinitionService;

    @Autowired
    private VulnerabilityWindow vulnerabilityWindow;
    
    @Autowired
    private CVEWindow cveWindow;

    /**
     * The constructor just sets the spacing and the margins and initialises the parser
     */
    public VulnerabilitiesView() {
        setSpacing(true);
        setMargin(true);
        setSizeFull();
        try {
            cveNvdParser = new CVENVDParser();
        } catch (final SAXException e) {
            new Notification(FAILURE, "Error creating CVE NVD parser: " + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
            LOGGER.error("Problem creating CVE NVD parser", e);
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        final Button button = event.getButton();
        final User user = ((WebApplication) getUI()).getUser();
        if (addVulnerability.equals(button) && user.getRole() == UserRole.EDIT_DATA) {
            vulnerabilityWindow.setAddMode(this);
            getUI().addWindow(vulnerabilityWindow);
        } else if (addCVE.equals(button) && user.getRole() == UserRole.EDIT_DATA) {
            cveWindow.setAddMode(this);
            getUI().addWindow(cveWindow);
        } else {
            // In this case we are dealing with the buttons of a row
            final Object item = button.getData();
            if (item instanceof Vulnerability) {
                final Vulnerability vulnerability = (Vulnerability) item;
                if (GraphicResources.EDIT_ICON.equals(button.getIcon())) {
                    vulnerabilityWindow.setEditMode(vulnerability, this);
                    getUI().addWindow(vulnerabilityWindow);
                } else if (GraphicResources.DELETE_ICON.equals(button.getIcon()) && user.getRole() == UserRole.EDIT_DATA) {
                    vulnerabilityService.delete(vulnerability);
                    refreshVulnerabilitiesTableContent();
                }
            } else if (item instanceof CVEDefinition) {
                final CVEDefinition definition = (CVEDefinition) item;
                if (GraphicResources.EDIT_ICON.equals(button.getIcon())) {
                    cveWindow.setEditMode(definition, this);
                    getUI().addWindow(cveWindow);
                } else if (GraphicResources.DELETE_ICON.equals(button.getIcon()) && user.getRole() == UserRole.EDIT_DATA) {
                    cveDefinitionService.delete(definition);
                    refreshCVEsTableContent();
                }
            }
        }
    }
    
    private void closeTempXmlWriter() {
        if (bufferedWriter != null) {
            try {
                bufferedWriter.close();
            } catch (final IOException e) {
                LOGGER.error("Problem closing temp file", e);
                new Notification(FAILURE, "Error closing temp file: " + tempXML.getName(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
            }
            bufferedWriter = null;
        }
    }
    
    private void createButtons() {
        addVulnerability = new Button("Add vulnerability", this);
        addVulnerability.setStyleName("link");
        addVulnerability.setIcon(GraphicResources.ADD_ICON);
        addCVE = new Button("Add CVE", this);
        addCVE.setStyleName("link");
        addCVE.setIcon(GraphicResources.ADD_ICON);
        updateCVEs = new Upload(null, this);
        updateCVEs.addStartedListener(this);
        updateCVEs.addFailedListener(this);
        updateCVEs.addSucceededListener(this);
        updateCVEs.setButtonCaption("Update CVEs from file");
        updateCVEs.setImmediate(true);
    }
    
    @Override
    public void enter(ViewChangeEvent event) {
        // Nothing to do when entering the view
    }

    // This method generates the cells for the different buttons
    @Override
    public Object generateCell(CustomTable source, Object itemId, Object columnId) {
        // First we create a button and set its data with the Computer
        final Button button = new Button("", this);
        button.setStyleName("link");
        // From the container we find the item with the ID itemId that is the one for which we are drawing the cell
        if (vulnerabilityTable.equals(source)) {
            final BeanItem<Vulnerability> currentItem = vulnerabilityContainer.getItem(itemId);
            button.setData(currentItem.getBean());
        } else {
            final BeanItem<CVEDefinition> currentItem = cveContainer.getItem(itemId);
            button.setData(currentItem.getBean());
        }
        // Then we check to which column the button belongs and add the corresponding action and icon
        if (EDIT_BUTTON.equals(columnId)) {
            button.setIcon(GraphicResources.EDIT_ICON);
        } else if (DELETE_BUTTON.equals(columnId)) {
            button.setIcon(GraphicResources.DELETE_ICON);
        }
        // Finally we return the button
        return button;
    }

    /**
     * When we start the EventsView we create the table and the buttons
     */
    @PostConstruct
    public void init() {
        // 1st) We initialise the tables
        initializeTables();
        // 2nd) We create the buttons for the generic actions
        createButtons();
        // 3rd) We add the objects to the view
        final Label vulnerabilityLabel = new Label("<center><p>In computer science a <b>Vulnerability</b> is a weakness that allows an attacker to reduce a system's information assurance.<br/>In this screen you can review, edit, delete and add vulnerabilities and also review, edit, delete or add CVE definitions associated.</p></center>", ContentMode.HTML);
        addComponent(vulnerabilityLabel);
        addComponent(addVulnerability);
        setComponentAlignment(addVulnerability, Alignment.TOP_CENTER);
        addComponent(vulnerabilityTable);
        setComponentAlignment(vulnerabilityTable, Alignment.TOP_CENTER);
        setExpandRatio(vulnerabilityTable, 0.7f);
        
        final Label cveLabel = new Label("<center><p>A <b>CVE</b> is a common identifier used by the US-CERT to categorise vulnerabilities. A CVE definition contains an ID and standardised details of a vulnerability.<br/> Below you can see the current known CVE definitions, you can edit, add, delete or upload definitions</p></center>", ContentMode.HTML);
        addComponent(cveLabel);
        final HorizontalLayout cveButtons = new HorizontalLayout();
        cveButtons.setSpacing(true);
        cveButtons.addComponent(addCVE);
        cveButtons.addComponent(updateCVEs);
        addComponent(cveButtons);
        setComponentAlignment(cveButtons, Alignment.MIDDLE_CENTER);
        addComponent(cveTable);
        setComponentAlignment(cveTable, Alignment.MIDDLE_CENTER);
        setExpandRatio(cveTable, 0.3f);
    }

    private void initializeTables() {
        // We create the tables
        vulnerabilityTable = new FilterTable();
        vulnerabilityTable.setSortEnabled(true);
        vulnerabilityTable.setSizeFull();
        vulnerabilityTable.setFilterBarVisible(true);
        cveTable = new FilterTable();
        cveTable.setSortEnabled(true);
        cveTable.setSizeFull();
        cveTable.setFilterBarVisible(true);
        // We add a column with the button to edit the details
        vulnerabilityTable.addGeneratedColumn(EDIT_BUTTON, this);
        cveTable.addGeneratedColumn(EDIT_BUTTON, this);
        // We add a column with the button to delete the element
        vulnerabilityTable.addGeneratedColumn(DELETE_BUTTON, this);
        cveTable.addGeneratedColumn(DELETE_BUTTON, this);
        // Now we handle the containers
        vulnerabilityContainer = new BeanItemContainer<>(Vulnerability.class);
        vulnerabilityContainer.addNestedContainerProperty("definition.name");
        vulnerabilityContainer.addNestedContainerProperty(CREATED_BY_NAME);
        vulnerabilityContainer.addNestedContainerProperty(UPDATED_BY_NAME);
        vulnerabilityTable.setContainerDataSource(vulnerabilityContainer);
        cveContainer = new BeanItemContainer<>(CVEDefinition.class);
        cveContainer.addNestedContainerProperty(CREATED_BY_NAME);
        cveContainer.addNestedContainerProperty(UPDATED_BY_NAME);
        cveTable.setContainerDataSource(cveContainer);
        // Now we define which columns are visible and what are going to be their names in the table header
        vulnerabilityTable.setVisibleColumns(new Object[] { "definition.name", "type", "notes", "created", CREATED_BY_NAME, "updated", UPDATED_BY_NAME, EDIT_BUTTON, DELETE_BUTTON });
        vulnerabilityTable.setColumnHeaders(new String[] { "CVE Name", "Type", "Notes", "Created", "Created by", "Last update", "Last update by", "Edit", "Delete" });
        cveTable.setVisibleColumns(new Object[] { "name", "published", "cveDesc", "severity", "cvssBaseScore", "created", CREATED_BY_NAME, "updated", UPDATED_BY_NAME, EDIT_BUTTON, DELETE_BUTTON });
        cveTable.setColumnHeaders(new String[] { "Name", "Published", "Description", "Severity", "CVSS Base Score", "Created", "Created by", "Last update", "Last update by", "Edit", "Delete" });
        // We then align the buttons to the middle
        vulnerabilityTable.setColumnAlignment(EDIT_BUTTON, CustomTable.Align.CENTER);
        vulnerabilityTable.setColumnAlignment(DELETE_BUTTON, CustomTable.Align.CENTER);
        cveTable.setColumnAlignment(EDIT_BUTTON, CustomTable.Align.CENTER);
        cveTable.setColumnAlignment(DELETE_BUTTON, CustomTable.Align.CENTER);
        // We then collapse the columns that have less value
        vulnerabilityTable.setColumnCollapsingAllowed(true);
        vulnerabilityTable.setColumnCollapsed("created", true);
        vulnerabilityTable.setColumnCollapsed("createdBy.name", true);
        vulnerabilityTable.setColumnCollapsed("updated", true);
        vulnerabilityTable.setColumnCollapsed("updatedBy.name", true);
        cveTable.setColumnCollapsingAllowed(true);
        cveTable.setColumnCollapsed("created", true);
        cveTable.setColumnCollapsed("createdBy.name", true);
        cveTable.setColumnCollapsed("updated", true);
        cveTable.setColumnCollapsed("updatedBy.name", true);
        // Now we refresh the content of the tables
        refreshVulnerabilitiesTableContent();
        refreshCVEsTableContent();
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        return new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                bufferedWriter.write(b);
            }
        };
    }

    /**
     * Refreshes the table with the targets data
     *
     * @param owner
     *            The owner currently showing
     */
    public void refreshCVEsTableContent() {
        cveContainer.removeAllItems();
        cveContainer.addAll(cveDefinitionService.list());
    }

    /**
     * Refreshes the table with the vulnerabilities data
     */
    public void refreshVulnerabilitiesTableContent() {
        // We first create the container with all the owners and assign it to the table
        vulnerabilityContainer.removeAllItems();
        vulnerabilityContainer.addAll(vulnerabilityService.list());
    }

    @Override
    public void uploadFailed(FailedEvent event) {
        closeTempXmlWriter();
        if (tempXML.exists() && !tempXML.delete()) {
            new Notification(FAILURE, "Error deleting temp file: " + tempXML.getName(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
        }
        new Notification(FAILURE, "Error uploading file: " + event.getReason().getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
    }

    @Override
    public void uploadStarted(StartedEvent event) {
        try {
            tempXML = File.createTempFile("tempUpload", ".xml");
            bufferedWriter = new BufferedWriter(new FileWriter(tempXML));
        } catch (final IOException e) {
            new Notification(FAILURE, "Error creating temporary file for upload: " + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
            LOGGER.error("Error creating temporary file for upload", e);
        }
    }

    @Override
    public void uploadSucceeded(SucceededEvent event) {
        closeTempXmlWriter();
        try {
            final List<CVEDefinition> parsedDefinitions = cveNvdParser.parse(tempXML);
            final List<String> errors = cveDefinitionService.update(parsedDefinitions, ((WebApplication) getUI()).getUser());
            if (errors != null && !errors.isEmpty()) {
                new Notification(FAILURE, "The following errors were found when saving the CVE definitions: " + errors, Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
            } else {
                new Notification("Success", parsedDefinitions.size() + " CVE definitions parsed and uploaded", Notification.Type.TRAY_NOTIFICATION).show(getUI().getPage());
            }
            refreshCVEsTableContent();
        } catch (final SAXException e) {
            new Notification(FAILURE, "Error parsing uploaded file: " + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
            LOGGER.error("Error creating temporary file for upload", e);
        } finally {
            if (tempXML.exists() && !tempXML.delete()) {
                new Notification(FAILURE, "Error deleting temp file: " + tempXML.getName(), Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
            }
        }
    }
}