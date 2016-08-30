package com.damosais.sid.webapp;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.tepi.filtertable.FilterTable;

import com.damosais.sid.database.beans.Tool;
import com.damosais.sid.database.services.ToolService;
import com.damosais.sid.webapp.windows.ToolWindow;
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
 * This is the screen from which users can see the different events and interact with them
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@SpringView(name = ToolsView.VIEW_NAME)
public class ToolsView extends VerticalLayout implements View, ClickListener, ColumnGenerator {
    private static final long serialVersionUID = 8550877065706074886L;
    public static final String VIEW_NAME = "ToolsScreen";
    private static final String EDIT_BUTTON = "editButton";
    private static final String DELETE_BUTTON = "deleteButton";
    private BeanItemContainer<Tool> container;
    private Button addEvent;
    private FilterTable table;
    
    @Autowired
    private ToolService toolService;

    @Autowired
    private ToolWindow toolWindow;

    /**
     * The constructor just enables the spacing and margins on the layout
     */
    public ToolsView() {
        setSpacing(true);
        setMargin(true);
    }
    
    @Override
    public void buttonClick(ClickEvent event) {
        final Button button = event.getButton();
        if (addEvent.equals(button)) {
            toolWindow.setAddMode(this);
            getUI().addWindow(toolWindow);
        } else {
            // In this case we are dealing with the buttons of a row
            final Tool toolToAlter = (Tool) button.getData();
            if (GraphicResources.EDIT_ICON.equals(button.getIcon())) {
                toolWindow.setEditMode(toolToAlter, this);
                getUI().addWindow(toolWindow);
            } else if (GraphicResources.DELETE_ICON.equals(button.getIcon())) {
                toolService.delete(toolToAlter);
                refreshTableContent();
            }
        }
    }

    private void createButtons() {
        final HorizontalLayout hl = new HorizontalLayout();
        addEvent = new Button("Add tool", this);
        addEvent.setStyleName("link");
        addEvent.setIcon(GraphicResources.ADD_ICON);
        hl.addComponent(addEvent);
        hl.setComponentAlignment(addEvent, Alignment.MIDDLE_CENTER);
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
        // First we create a button and set its data with the tool
        final Button button = new Button("", this);
        button.setStyleName("link");
        // From the container we find the item with the ID itemId that is the one for which we are drawing the cell
        final BeanItem<Tool> currentItem = container.getItem(itemId);
        // Finally we add to the button as data the computer of this item
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
     * When we start the EventsView we create the table and the buttons
     */
    @PostConstruct
    public void init() {
        // 1st) thing we initialise the table
        initializeTable();
        // 2nd) We add a small description explaining what we are doing
        final Label eventLabel = new Label("<center><p>In the <i>'Common Language'</i> a <b>Tool</b> represents a family of techniques used to exploit a <b>vulnerability</b>.<br/>In this screen you can observe the existing tools, edit, delete them, or add new ones.</p></center>", ContentMode.HTML);
        addComponent(eventLabel);
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
        // We add a column with the button to edit the attack details
        table.addGeneratedColumn(EDIT_BUTTON, this);
        // We add a column with the button to delete the attack
        table.addGeneratedColumn(DELETE_BUTTON, this);
        // Now we add the container
        container = new BeanItemContainer<>(Tool.class);
        table.setContainerDataSource(container);
        // Now we define which columns are visible and what are going to be their names in the table header
        table.setVisibleColumns(new Object[] { "name", "type", EDIT_BUTTON, DELETE_BUTTON });
        table.setColumnHeaders(new String[] { "Name", "Type", "Edit", "Delete" });
        table.setColumnAlignment(EDIT_BUTTON, CustomTable.Align.CENTER);
        table.setColumnAlignment(DELETE_BUTTON, CustomTable.Align.CENTER);
        // Now we refresh the content
        refreshTableContent();
    }
    
    /**
     * It refreshes the content of the table
     */
    public void refreshTableContent() {
        // We first create the container with all the events and assign it to the table
        container.removeAllItems();
        container.addAll(toolService.list());
    }
}