package com.damosais.sid.webapp;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.tepi.filtertable.FilterTable;

import com.damosais.sid.database.beans.CorrelationHypothesis;
import com.damosais.sid.database.beans.CorrelationResult;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.services.CorrelationHypothesisService;
import com.damosais.sid.webapp.customfields.YearMonthDate;
import com.damosais.sid.webapp.windows.CorrelationHypothesisWindow;
import com.damosais.sid.webapp.windows.CorrelationResultsWindow;
import com.damosais.sid.webapp.windows.CorrelationSearchWindow;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.CustomTable.CellStyleGenerator;
import com.vaadin.ui.CustomTable.ColumnGenerator;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * This is the screen from which users can see the different military conflicts and interact with them
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@SpringView(name = CorrelationsView.VIEW_NAME)
public class CorrelationsView extends VerticalLayout implements View, ClickListener, ColumnGenerator, CellStyleGenerator {
    private static final long serialVersionUID = -4489257378797953375L;
    public static final String VIEW_NAME = "CorrelationsScreen";
    private static final String EDIT_BUTTON = "editButton";
    private static final String DETAILS_BUTTON = "detailsButton";
    private static final String RUN_BUTTON = "runButton";
    private static final String DELETE_BUTTON = "deleteButton";
    private BeanItemContainer<CorrelationHypothesis> container;
    private Button addCorrelation;
    private Button findCorrelations;
    private FilterTable table;

    @Autowired
    private CorrelationHypothesisService correlationService;
    
    @Autowired
    private CorrelationHypothesisWindow correlationWindow;

    @Autowired
    private CorrelationResultsWindow correlationResultsWindow;
    
    @Autowired
    private CorrelationSearchWindow correlationSearchWindow;
    
    /**
     * The constructor just enables the spacing and margins on the layout
     */
    public CorrelationsView() {
        setSpacing(true);
        setMargin(true);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        final Button button = event.getButton();
        final User user = ((WebApplication) getUI()).getUser();
        if (addCorrelation.equals(button) && user.getRole() == UserRole.EDIT_DATA) {
            correlationWindow.setAddMode(this);
            getUI().addWindow(correlationWindow);
        } else if (findCorrelations.equals(button) && user.getRole() == UserRole.EDIT_DATA) {
            correlationSearchWindow.generateForm(this);
            getUI().addWindow(correlationSearchWindow);
        } else {
            // In this case we are dealing with the buttons of a row
            final CorrelationHypothesis correlationToAlter = (CorrelationHypothesis) button.getData();
            if (GraphicResources.EDIT_ICON.equals(button.getIcon())) {
                correlationWindow.setEditMode(correlationToAlter, this);
                getUI().addWindow(correlationWindow);
            } else if (GraphicResources.DELETE_ICON.equals(button.getIcon()) && user.getRole() == UserRole.EDIT_DATA) {
                correlationService.delete(correlationToAlter);
                refreshTableContent();
            } else if (GraphicResources.RUN_ICON.equals(button.getIcon()) && user.getRole() == UserRole.EDIT_DATA) {
                correlationService.calculateHyphotesisSimpleCorrelations(correlationToAlter, this);
                refreshTableContent();
            } else if (GraphicResources.INFO_ICON.equals(button.getIcon())) {
                correlationResultsWindow.addValues(correlationToAlter);
                getUI().addWindow(correlationResultsWindow);
            }
        }
    }

    private void createButtons() {
        final HorizontalLayout hl = new HorizontalLayout();
        addCorrelation = new Button("Add correlation", this);
        addCorrelation.setStyleName("link");
        addCorrelation.setIcon(GraphicResources.ADD_ICON);
        hl.addComponent(addCorrelation);
        hl.setComponentAlignment(addCorrelation, Alignment.MIDDLE_CENTER);
        findCorrelations = new Button("Find correlations automatically", this);
        findCorrelations.setStyleName("link");
        findCorrelations.setIcon(GraphicResources.SEARCH_ICON);
        hl.addComponent(findCorrelations);
        hl.setComponentAlignment(findCorrelations, Alignment.MIDDLE_CENTER);
        addComponent(hl);
        setComponentAlignment(hl, Alignment.TOP_CENTER);
    }
    
    @Override
    public void enter(ViewChangeEvent event) {
        // We do nothing on enter
    }
    
    // This method generates the cells for the different buttons
    @Override
    public Object generateCell(CustomTable source, Object itemId, Object columnId) {
        // First we create a button and set its data with the correlation
        final Button button = new Button("", this);
        button.setStyleName("link");
        // From the container we find the item with the ID itemId that is the one for which we are drawing the cell
        final BeanItem<CorrelationHypothesis> currentItem = container.getItem(itemId);
        // Finally we add to the button as data the conflict of this item
        button.setData(currentItem.getBean());
        // Then we check to which column the button belongs and add the corresponding action and icon
        if (EDIT_BUTTON.equals(columnId)) {
            button.setIcon(GraphicResources.EDIT_ICON);
        } else if (DETAILS_BUTTON.equals(columnId)) {
            button.setIcon(GraphicResources.INFO_ICON);
        } else if (RUN_BUTTON.equals(columnId)) {
            button.setIcon(GraphicResources.RUN_ICON);
        } else if (DELETE_BUTTON.equals(columnId)) {
            button.setIcon(GraphicResources.DELETE_ICON);
        }
        // Finally we return the button
        return button;
    }
    
    @Override
    public String getStyle(CustomTable source, Object itemId, Object propertyId) {
        final CorrelationHypothesis hypothesis = (CorrelationHypothesis) itemId;
        if (hypothesis.getResults() != null && !hypothesis.getResults().isEmpty()) {
            double maxCorrelationCoefficient = 0;
            double pValue = 1;
            for (final CorrelationResult result : hypothesis.getResults()) {
                final double correlationFactor = result.getCorrelationCoefficient();
                final double resultPValue = result.getpValue();
                if (Math.abs(correlationFactor) > Math.abs(maxCorrelationCoefficient) && (resultPValue < 0.05d || resultPValue < pValue)) {
                    maxCorrelationCoefficient = correlationFactor;
                    pValue = resultPValue;
                }
            }
            if (Math.abs(maxCorrelationCoefficient) > 0.75d && pValue < 0.05d) {
                return "green";
            } else if (Math.abs(maxCorrelationCoefficient) > 0.5) {
                return "orange";
            } else {
                return "red";
            }
        }
        return null;
    }

    /**
     * When we start the ConflictsView we create the table and the buttons
     */
    @PostConstruct
    public void init() {
        // 1st) thing we initialise the table
        initializeTable();
        // Now we create the buttons for the generic actions
        createButtons();
        // Now we add the table to the view
        addComponent(table);
        setComponentAlignment(table, Alignment.TOP_CENTER);
    }
    
    /**
     * This method generates the table for first time, only to be called when initialising the table
     */
    private void initializeTable() {
        // We create a table and set the source of data as the container
        table = new FilterTable();
        table.setFilterBarVisible(true);
        table.setSortEnabled(true);
        // We add the columns to edit the correlation, see the details, run it and delete it
        table.addGeneratedColumn(EDIT_BUTTON, this);
        table.addGeneratedColumn(DETAILS_BUTTON, this);
        table.addGeneratedColumn(RUN_BUTTON, this);
        table.addGeneratedColumn(DELETE_BUTTON, this);
        // Now we add the container
        container = new BeanItemContainer<>(CorrelationHypothesis.class);
        container.addNestedContainerProperty("createdBy.name");
        container.addNestedContainerProperty("updatedBy.name");
        table.setContainerDataSource(container);
        // Now we define which columns are visible and what are going to be their names in the table header
        table.setVisibleColumns(new Object[] { "startDate", "endDate", "bestCorrelation", "sector", "targetCountries", "sourceCountries", "variables", "created", "createdBy.name", "updated", "updatedBy.name", EDIT_BUTTON, DETAILS_BUTTON, RUN_BUTTON, DELETE_BUTTON });
        table.setColumnHeaders(new String[] { "Start", "End", "Best Corr. Factor", "Sector", "Target Countries", "Source Countries", "Variables", "Created", "Created by", "Last update", "Last updated by", "Edit", "Details", "Run", "Delete" });
        table.setColumnAlignment(EDIT_BUTTON, CustomTable.Align.CENTER);
        table.setColumnAlignment(DELETE_BUTTON, CustomTable.Align.CENTER);
        table.setCellStyleGenerator(this);
        // We make the start and end date to be formatted just month and year
        table.setConverter("startDate", new YearMonthDate());
        table.setConverter("endDate", new YearMonthDate());
        // Now we refresh the content
        refreshTableContent();
    }
    
    /**
     * It refreshes the content of the table
     */
    public void refreshTableContent() {
        // We first create the container with all the attackers and assign it to the table
        container.removeAllItems();
        container.addAll(correlationService.list());
    }
}