package com.damosais.sid.webapp;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.tepi.filtertable.FilterTable;

import com.damosais.sid.database.beans.CVEDefinition;
import com.damosais.sid.database.beans.Vulnerability;
import com.damosais.sid.database.services.CVEDefinitionService;
import com.damosais.sid.database.services.VulnerabilityService;
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
import com.vaadin.ui.VerticalLayout;

/**
 * This is the screen from which users can see the different vulnerabilities and interact with them
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@SpringView(name = VulnerabilitiesView.VIEW_NAME)
public class VulnerabilitiesView extends VerticalLayout implements View, ClickListener, ColumnGenerator {
    private static final long serialVersionUID = 4990531322527027247L;
    public static final String VIEW_NAME = "VulnerabilitiesScreen";
    private static final String EDIT_BUTTON = "editButton";
    private static final String DELETE_BUTTON = "deleteButton";
    private BeanItemContainer<Vulnerability> vulnerabilityContainer;
    private BeanItemContainer<CVEDefinition> cveContainer;
    private Button addVulnerability;
    private Button addCVE;
    private Button updateCVEs;
    private FilterTable vulnerabilityTable;
    private FilterTable cveTable;
    
    @Autowired
    private VulnerabilityService vulnerabilityService;

    @Autowired
    private CVEDefinitionService cveDefinitionService;
    
    @Autowired
    private VulnerabilityWindow vulnerabilityWindow;

    @Autowired
    private CVEWindow cveWindow;
    
    /**
     * The constructor just sets the spacing and the margins
     */
    public VulnerabilitiesView() {
        setSpacing(true);
        setMargin(true);
    }
    
    @Override
    public void buttonClick(ClickEvent event) {
        final Button button = event.getButton();
        if (addVulnerability.equals(button)) {
            vulnerabilityWindow.setAddMode(this);
            getUI().addWindow(vulnerabilityWindow);
        } else if (addCVE.equals(button)) {
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
                } else if (GraphicResources.DELETE_ICON.equals(button.getIcon())) {
                    vulnerabilityService.delete(vulnerability);
                    refreshVulnerabilitiesTableContent();
                }
            } else if (item instanceof CVEDefinition) {
                final CVEDefinition definition = (CVEDefinition) item;
                if (GraphicResources.EDIT_ICON.equals(button.getIcon())) {
                    cveWindow.setEditMode(definition, this);
                    getUI().addWindow(cveWindow);
                } else if (GraphicResources.DELETE_ICON.equals(button.getIcon())) {
                    cveDefinitionService.delete(definition);
                    refreshCVEsTableContent();
                }
            }
        }
    }

    private void createButtons() {
        addVulnerability = new Button("Add vulnerability", this);
        addVulnerability.setStyleName("link");
        addVulnerability.setIcon(GraphicResources.ADD_ICON);
        addCVE = new Button("Add CVE", this);
        addCVE.setStyleName("link");
        addCVE.setIcon(GraphicResources.ADD_ICON);
        updateCVEs = new Button("Update CVEs from file", this);
        updateCVEs.setStyleName("link");
        updateCVEs.setIcon(GraphicResources.UPLOAD_ICON);
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
    }
    
    private void initializeTables() {
        // We create the tables
        vulnerabilityTable = new FilterTable();
        vulnerabilityTable.setFilterBarVisible(true);
        cveTable = new FilterTable();
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
        vulnerabilityTable.setContainerDataSource(vulnerabilityContainer);
        cveContainer = new BeanItemContainer<>(CVEDefinition.class);
        cveTable.setContainerDataSource(cveContainer);
        // Now we define which columns are visible and what are going to be their names in the table header
        vulnerabilityTable.setVisibleColumns(new Object[] { "definition.name", "type", "notes", EDIT_BUTTON, DELETE_BUTTON });
        vulnerabilityTable.setColumnHeaders(new String[] { "CVE Name", "Type", "Notes", "Edit", "Delete" });
        cveTable.setVisibleColumns(new Object[] { "name", "published", "cveDesc", "severity", "cvssBaseScore", EDIT_BUTTON, DELETE_BUTTON });
        cveTable.setColumnHeaders(new String[] { "name", "Published", "Description", "Severity", "CVSS Base Score", "Edit", "Delete" });
        // We then align the buttons to the middle
        vulnerabilityTable.setColumnAlignment(EDIT_BUTTON, CustomTable.Align.CENTER);
        vulnerabilityTable.setColumnAlignment(DELETE_BUTTON, CustomTable.Align.CENTER);
        cveTable.setColumnAlignment(EDIT_BUTTON, CustomTable.Align.CENTER);
        cveTable.setColumnAlignment(DELETE_BUTTON, CustomTable.Align.CENTER);
        // Now we refresh the content of the tables
        refreshVulnerabilitiesTableContent();
        refreshCVEsTableContent();
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
}