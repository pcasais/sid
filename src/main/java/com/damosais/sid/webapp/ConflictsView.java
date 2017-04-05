package com.damosais.sid.webapp;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.tepi.filtertable.FilterTable;

import com.damosais.sid.database.beans.Conflict;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.services.ConflictService;
import com.damosais.sid.webapp.windows.ConflictWindow;
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
@SpringView(name = ConflictsView.VIEW_NAME)
public class ConflictsView extends VerticalLayout implements View, ClickListener, ColumnGenerator {
    private static final long serialVersionUID = 7525785600088936353L;
    public static final String VIEW_NAME = "ConflictsScreen";
    private static final String EDIT_BUTTON = "editButton";
    private static final String DELETE_BUTTON = "deleteButton";
    private BeanItemContainer<Conflict> container;
    private Button addConflict;
    private FilterTable table;

    @Autowired
    private ConflictService conflictService;
    
    @Autowired
    private ConflictWindow conflictWindow;
    
    /**
     * The constructor just enables the spacing and margins on the layout
     */
    public ConflictsView() {
        setSpacing(true);
        setMargin(true);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        final Button button = event.getButton();
        final User user = ((WebApplication) getUI()).getUser();
        if (addConflict.equals(button) && user.getRole() == UserRole.EDIT_DATA) {
            conflictWindow.setAddMode(this);
            getUI().addWindow(conflictWindow);
        } else {
            // In this case we are dealing with the buttons of a row
            final Conflict conflictToAlter = (Conflict) button.getData();
            if (GraphicResources.EDIT_ICON.equals(button.getIcon())) {
                conflictWindow.setEditMode(conflictToAlter, this);
                getUI().addWindow(conflictWindow);
            } else if (GraphicResources.DELETE_ICON.equals(button.getIcon()) && user.getRole() == UserRole.EDIT_DATA) {
                conflictService.delete(conflictToAlter);
                refreshTableContent();
            }
        }
    }

    private void createButtons() {
        final HorizontalLayout hl = new HorizontalLayout();
        addConflict = new Button("Add conflict", this);
        addConflict.setStyleName("link");
        addConflict.setIcon(GraphicResources.ADD_ICON);
        hl.addComponent(addConflict);
        hl.setComponentAlignment(addConflict, Alignment.MIDDLE_CENTER);
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
        // First we create a button and set its data with the attacker
        final Button button = new Button("", this);
        button.setStyleName("link");
        // From the container we find the item with the ID itemId that is the one for which we are drawing the cell
        final BeanItem<Conflict> currentItem = container.getItem(itemId);
        // Finally we add to the button as data the conflict of this item
        button.setData(currentItem.getBean());
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
        // We add a column with the button to edit the conflict details
        table.addGeneratedColumn(EDIT_BUTTON, this);
        // We add a column with the button to delete the conflict
        table.addGeneratedColumn(DELETE_BUTTON, this);
        // Now we add the container
        container = new BeanItemContainer<>(Conflict.class);
        container.addNestedContainerProperty("createdBy.name");
        container.addNestedContainerProperty("updatedBy.name");
        table.setContainerDataSource(container);
        // Now we define which columns are visible and what are going to be their names in the table header
        table.setVisibleColumns(new Object[] { "start", "end", "name", "location", "partiesInvolved", "created", "createdBy.name", "updated", "updatedBy.name", EDIT_BUTTON, DELETE_BUTTON });
        table.setColumnHeaders(new String[] { "Start", "End", "Name", "Location", "Parties Involved", "Created", "Created by", "Last update", "Last updated by", "Edit", "Delete" });
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
        container.addAll(conflictService.list());
    }
}