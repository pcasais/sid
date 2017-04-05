package com.damosais.sid.webapp;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.tepi.filtertable.FilterTable;

import com.damosais.sid.database.beans.Correlation;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.services.CorrelationService;
import com.damosais.sid.webapp.windows.CorrelationWindow;
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
public class CorrelationsView extends VerticalLayout implements View, ClickListener, ColumnGenerator {
    private static final long serialVersionUID = -4489257378797953375L;
    public static final String VIEW_NAME = "CorrelationsScreen";
    private static final String EDIT_BUTTON = "editButton";
    private static final String DETAILS_BUTTON = "detailsButton";
    private static final String RUN_BUTTON = "runButton";
    private static final String DELETE_BUTTON = "deleteButton";
    private BeanItemContainer<Correlation> container;
    private Button addCorrelation;
    private FilterTable table;
    
    @Autowired
    private CorrelationService correlationService;

    @Autowired
    private CorrelationWindow correlationWindow;

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
        } else {
            // In this case we are dealing with the buttons of a row
            final Correlation correlationToAlter = (Correlation) button.getData();
            if (GraphicResources.EDIT_ICON.equals(button.getIcon())) {
                correlationWindow.setEditMode(correlationToAlter, this);
                getUI().addWindow(correlationWindow);
            } else if (GraphicResources.DELETE_ICON.equals(button.getIcon()) && user.getRole() == UserRole.EDIT_DATA) {
                correlationService.delete(correlationToAlter);
                refreshTableContent();
            } else if (GraphicResources.RUN_ICON.equals(button.getIcon()) && user.getRole() == UserRole.EDIT_DATA) {
                correlationService.computeCorrelationCoeficients(correlationToAlter);
                refreshTableContent();
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
        final BeanItem<Correlation> currentItem = container.getItem(itemId);
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
        // We add the columns to edit the correlation, see the details, run it and delete it
        table.addGeneratedColumn(EDIT_BUTTON, this);
        table.addGeneratedColumn(DETAILS_BUTTON, this);
        table.addGeneratedColumn(RUN_BUTTON, this);
        table.addGeneratedColumn(DELETE_BUTTON, this);
        // Now we add the container
        container = new BeanItemContainer<>(Correlation.class);
        container.addNestedContainerProperty("createdBy.name");
        container.addNestedContainerProperty("updatedBy.name");
        table.setContainerDataSource(container);
        // Now we define which columns are visible and what are going to be their names in the table header
        table.setVisibleColumns(new Object[] { "startDate", "endDate", "sector", "targetCountries", "sourceCountries", "variables", "coeficient", "created", "createdBy.name", "updated", "updatedBy.name", EDIT_BUTTON, DETAILS_BUTTON, RUN_BUTTON, DELETE_BUTTON });
        table.setColumnHeaders(new String[] { "Start", "End", "Sector", "Target Countries", "Source Countries", "Variables", "Corr. Coeficient", "Created", "Created by", "Last update", "Last updated by", "Edit", "Details", "Run", "Delete" });
        table.setColumnAlignment(EDIT_BUTTON, CustomTable.Align.CENTER);
        table.setColumnAlignment(DELETE_BUTTON, CustomTable.Align.CENTER);
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