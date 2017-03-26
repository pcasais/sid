package com.damosais.sid.webapp;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.tepi.filtertable.FilterTable;

import com.damosais.sid.database.beans.Owner;
import com.damosais.sid.database.beans.Target;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.beans.UserRole;
import com.damosais.sid.database.services.OwnerService;
import com.damosais.sid.database.services.TargetService;
import com.damosais.sid.webapp.windows.OwnerWindow;
import com.damosais.sid.webapp.windows.TargetWindow;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

/**
 * This is the view for the victims screen where we can add, edit or remove victims and their associated targets
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@SpringView(name = OwnersView.VIEW_NAME)
public class OwnersView extends VerticalLayout implements View, ColumnGenerator, ClickListener {
    private static final long serialVersionUID = -3226311660570691680L;
    public static final String VIEW_NAME = "VictimScreen";
    private static final String EDIT_BUTTON = "editButton";
    private static final String DELETE_BUTTON = "deleteButton";
    private static final String UPDATED_BY_NAME = "updatedBy.name";
    private static final String CREATED_BY_NAME = "createdBy.name";
    private static final String COUNTRY_NAME = "country.name";
    private BeanItemContainer<Owner> ownersContainer;
    private BeanItemContainer<Target> targetsContainer;
    private Button addOwner;
    private Button addTarget;
    private FilterTable ownersTable;
    private FilterTable targetsTable;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private TargetService targetService;

    @Autowired
    private OwnerWindow ownerWindow;
    
    @Autowired
    private TargetWindow targetWindow;

    /**
     * The constructor just makes the component to space and have margins
     */
    public OwnersView() {
        setSpacing(true);
        setMargin(true);
    }
    
    @Override
    public void buttonClick(ClickEvent event) {
        final Button button = event.getButton();
        final User user = ((WebApplication) getUI()).getUser();
        if (addOwner.equals(button) && user.getRole() == UserRole.EDIT_DATA) {
            ownerWindow.setAddMode(this);
            getUI().addWindow(ownerWindow);
        } else if (addTarget.equals(button) && user.getRole() == UserRole.EDIT_DATA) {
            final Owner owner = (Owner) ownersTable.getValue();
            if (owner == null) {
                new Notification("Missing owner", "To create a target you need to select an owner first. Please click on an owner and try again", Type.ERROR_MESSAGE).show(getUI().getPage());
            } else {
                targetWindow.setAddMode(owner, this);
                getUI().addWindow(targetWindow);
            }
        } else {
            // In this case we are dealing with the buttons of a row
            final Object item = button.getData();
            if (item instanceof Owner) {
                final Owner owner = (Owner) item;
                if (GraphicResources.EDIT_ICON.equals(button.getIcon())) {
                    ownerWindow.setEditMode(owner, this);
                    getUI().addWindow(ownerWindow);
                } else if (GraphicResources.DELETE_ICON.equals(button.getIcon()) && user.getRole() == UserRole.EDIT_DATA) {
                    ownerService.delete(owner);
                    refreshOwnersTableContent();
                    refreshTargetsTableContent(null);
                }
            } else if (item instanceof Target) {
                final Target target = (Target) item;
                if (GraphicResources.EDIT_ICON.equals(button.getIcon())) {
                    targetWindow.setEditMode(target, this);
                    getUI().addWindow(targetWindow);
                } else if (GraphicResources.DELETE_ICON.equals(button.getIcon()) && user.getRole() == UserRole.EDIT_DATA) {
                    final Owner owner = target.getOwner();
                    owner.getTargets().remove(target);
                    targetService.delete(target);
                    ownerService.save(owner);
                    refreshTargetsTableContent(owner);
                }
            }
        }
    }

    private void createButtons() {
        addOwner = new Button("Add owner", this);
        addOwner.setStyleName("link");
        addOwner.setIcon(GraphicResources.ADD_ICON);
        addTarget = new Button("Add target", this);
        addTarget.setStyleName("link");
        addTarget.setIcon(GraphicResources.ADD_ICON);
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
        if (ownersTable.equals(source)) {
            final BeanItem<Owner> currentItem = ownersContainer.getItem(itemId);
            button.setData(currentItem.getBean());
        } else {
            final BeanItem<Target> currentItem = targetsContainer.getItem(itemId);
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
        final Label ownerLabel = new Label("<center><p>The <b>owner</b> or victim of a security incident is the person, company or legal entity that owns the target of the attack.<br/>In this screen you can review, edit, delete and add owners and also review, edit, delete or add targets owned by them.</p></center>", ContentMode.HTML);
        addComponent(ownerLabel);
        addComponent(addOwner);
        setComponentAlignment(addOwner, Alignment.TOP_CENTER);
        addComponent(ownersTable);
        setComponentAlignment(ownersTable, Alignment.TOP_CENTER);
        
        final Label targetLabel = new Label("<center><p>A target in military terminology is an object that is shot during practice. In our case it represents an asset owned by a victim which is the objective of a security incident.<br/> Once you have selected a owner in the above table you will be able to see, edit, add or remove targets for it in the table below.</p></center>", ContentMode.HTML);
        addComponent(targetLabel);
        addComponent(addTarget);
        setComponentAlignment(addTarget, Alignment.MIDDLE_CENTER);
        addComponent(targetsTable);
        setComponentAlignment(targetsTable, Alignment.MIDDLE_CENTER);
    }

    private void initializeTables() {
        // We create the tables
        ownersTable = new FilterTable();
        ownersTable.setFilterBarVisible(true);
        targetsTable = new FilterTable();
        targetsTable.setFilterBarVisible(true);
        // We add a column with the button to edit the details
        ownersTable.addGeneratedColumn(EDIT_BUTTON, this);
        targetsTable.addGeneratedColumn(EDIT_BUTTON, this);
        // We add the columns for the delete button
        ownersTable.addGeneratedColumn(DELETE_BUTTON, this);
        targetsTable.addGeneratedColumn(DELETE_BUTTON, this);
        // Now we handle the containers
        ownersContainer = new BeanItemContainer<>(Owner.class);
        ownersContainer.addNestedContainerProperty(COUNTRY_NAME);
        ownersContainer.addNestedContainerProperty("sector.name");
        ownersContainer.addNestedContainerProperty(CREATED_BY_NAME);
        ownersContainer.addNestedContainerProperty(UPDATED_BY_NAME);
        ownersTable.setContainerDataSource(ownersContainer);
        targetsContainer = new BeanItemContainer<>(Target.class);
        targetsContainer.addNestedContainerProperty(COUNTRY_NAME);
        targetsContainer.addNestedContainerProperty(CREATED_BY_NAME);
        targetsContainer.addNestedContainerProperty(UPDATED_BY_NAME);
        targetsTable.setContainerDataSource(targetsContainer);
        // Now we define which columns are visible and what are going to be their names in the table header
        ownersTable.setVisibleColumns(new Object[] { "name", COUNTRY_NAME, "sector.name", "created", CREATED_BY_NAME, "updated", UPDATED_BY_NAME, EDIT_BUTTON, DELETE_BUTTON });
        ownersTable.setColumnHeaders(new String[] { "Name", "Country", "Sector", "Created", "Created by", "Last update", "Last update by", "Edit", "Delete" });
        targetsTable.setVisibleColumns(new Object[] { "siteName", "ips", COUNTRY_NAME, "created", CREATED_BY_NAME, "updated", UPDATED_BY_NAME, EDIT_BUTTON, DELETE_BUTTON });
        targetsTable.setColumnHeaders(new String[] { "Site name", "IPs", "country", "Created", "Created by", "Last update", "Last update by", "Edit", "Delete" });
        // We then align the buttons to the middle
        ownersTable.setColumnAlignment(EDIT_BUTTON, CustomTable.Align.CENTER);
        ownersTable.setColumnAlignment(DELETE_BUTTON, CustomTable.Align.CENTER);
        targetsTable.setColumnAlignment(EDIT_BUTTON, CustomTable.Align.CENTER);
        targetsTable.setColumnAlignment(DELETE_BUTTON, CustomTable.Align.CENTER);
        // Finally we add the selectable behaviour to the owners table to link both tables
        ownersTable.setSelectable(true);
        ownersTable.setMultiSelect(false);
        ownersTable.setImmediate(true);
        // Handle selection change.
        ownersTable.addValueChangeListener(event -> refreshTargetsTableContent((Owner) ownersTable.getValue()));
        // Now we refresh the content of the tables
        refreshOwnersTableContent();
        refreshTargetsTableContent(null);
    }

    /**
     * Refreshes the table with the owners data
     */
    public void refreshOwnersTableContent() {
        // We first create the container with all the owners and assign it to the table
        ownersContainer.removeAllItems();
        ownersContainer.addAll(ownerService.list());
    }

    /**
     * Refreshes the table with the targets data
     *
     * @param owner
     *            The owner currently showing
     */
    public void refreshTargetsTableContent(Owner owner) {
        // We first create the container with all the targets and assign it to the table
        final List<Target> targets = new ArrayList<>();
        if (owner != null) {
            targets.addAll(targetService.listByOwner(owner));
        }
        targetsContainer.removeAllItems();
        targetsContainer.addAll(targets);
    }
}