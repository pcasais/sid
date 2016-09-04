package com.damosais.sid.webapp;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.tepi.filtertable.FilterTable;

import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.services.UserService;
import com.damosais.sid.webapp.windows.UserWindow;
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
 * This is the screen from which users can see the different users and interact with them
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@SpringView(name = UsersView.VIEW_NAME)
public class UsersView extends VerticalLayout implements View, ClickListener, ColumnGenerator {
    private static final long serialVersionUID = -3215568590574729173L;
    public static final String VIEW_NAME = "UsersScreen";
    private static final String EDIT_BUTTON = "editButton";
    private static final String DELETE_BUTTON = "deleteButton";
    private BeanItemContainer<User> container;
    private Button addUser;
    private FilterTable table;

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserWindow userWindow;
    
    /**
     * The constructor just enables the spacing and margins on the layout
     */
    public UsersView() {
        setSpacing(true);
        setMargin(true);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        final Button button = event.getButton();
        final User user = ((WebApplication) getUI()).getUser();
        if (addUser.equals(button) && user.getRole() == UserRole.USER_ADMIN) {
            userWindow.setAddMode(this, true);
            getUI().addWindow(userWindow);
        } else {
            // In this case we are dealing with the buttons of a row
            final User userToAlter = (User) button.getData();
            if (GraphicResources.EDIT_ICON.equals(button.getIcon())) {
                userWindow.setEditMode(userToAlter, this, user.getRole() == UserRole.USER_ADMIN);
                getUI().addWindow(userWindow);
            } else if (GraphicResources.DELETE_ICON.equals(button.getIcon()) && user.getRole() == UserRole.USER_ADMIN) {
                userService.delete(userToAlter);
                refreshTableContent();
            }
        }
    }
    
    private void createButtons() {
        final HorizontalLayout hl = new HorizontalLayout();
        addUser = new Button("Add user", this);
        addUser.setStyleName("link");
        addUser.setIcon(GraphicResources.ADD_ICON);
        hl.addComponent(addUser);
        hl.setComponentAlignment(addUser, Alignment.MIDDLE_CENTER);
        addComponent(hl);
        setComponentAlignment(hl, Alignment.TOP_CENTER);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // Now we refresh the content
        refreshTableContent();
    }

    // This method generates the cells for the different buttons
    @Override
    public Object generateCell(CustomTable source, Object itemId, Object columnId) {
        // First we create a button and set its data with the user
        final Button button = new Button("", this);
        button.setStyleName("link");
        // From the container we find the item with the ID itemId that is the one for which we are drawing the cell
        final BeanItem<User> currentItem = container.getItem(itemId);
        // Finally we add to the button as data the user of this item
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
        // 2nd) Now we create the buttons for the generic actions
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
        // We add a column with the button to edit the user details
        table.addGeneratedColumn(EDIT_BUTTON, this);
        // We add a column with the button to delete the user
        table.addGeneratedColumn(DELETE_BUTTON, this);
        // Now we add the container
        container = new BeanItemContainer<>(User.class);
        table.setContainerDataSource(container);
        // Now we define which columns are visible and what are going to be their names in the table header
        table.setVisibleColumns(new Object[] { "name", "role", "failedLogins", "suspended", EDIT_BUTTON, DELETE_BUTTON });
        table.setColumnHeaders(new String[] { "Name", "Role", "Failed logins", "Suspended", "Edit", "Delete" });
        table.setColumnAlignment(EDIT_BUTTON, CustomTable.Align.CENTER);
        table.setColumnAlignment(DELETE_BUTTON, CustomTable.Align.CENTER);
    }

    /**
     * It refreshes the content of the table
     */
    public void refreshTableContent() {
        // We first create the container with all the users and assign it to the table
        container.removeAllItems();
        final User user = ((WebApplication) getUI()).getUser();
        if (user.getRole() == UserRole.USER_ADMIN) {
            container.addAll(userService.list());
        } else {
            container.addAll(Arrays.asList(new User[] { user }));
        }
    }
}