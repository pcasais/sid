package com.damosais.sid.webapp;

import org.springframework.beans.factory.annotation.Autowired;

import com.damosais.sid.database.beans.User;
import com.damosais.sid.webapp.windows.ImportEventDataWindow;
import com.damosais.sid.webapp.windows.ImportSocioeconomicDataWindow;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * This class represents the start point of the web application. It initialises the views and acts like navigation control
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@SpringUI
@Theme("sidtheme")
@Widgetset("AppWidgetset")
public class WebApplication extends UI {
    private static final long serialVersionUID = -8605579931378861984L;
    // The user is used to validate actions
    private User user;
    // Now we have the structure of the application
    private VerticalLayout root;
    private Navigator navigator;
    private HorizontalLayout bar;
    
    @Autowired
    private SpringViewProvider viewProvider;

    @Autowired
    private ImportSocioeconomicDataWindow importSocioeconomicDataWindow;
    
    @Autowired
    private ImportEventDataWindow importEventDataWindow;
    
    /**
     * Adds the menu on the top so the users can change the view
     */
    public void addNavigationMenu() {
        if (bar == null) {
            bar = new HorizontalLayout();
            bar.setWidth("100%");

            final MenuBar menuBar = new MenuBar();
            menuBar.setWidth(100.0f, Unit.PERCENTAGE);
            menuBar.addItem("Events", selectedItem -> navigator.navigateTo(EventsView.VIEW_NAME));
            menuBar.addItem("Attacks", selectedItem -> navigator.navigateTo(AttacksView.VIEW_NAME));
            menuBar.addItem("Incidents", selectedItem -> navigator.navigateTo(IncidentsView.VIEW_NAME));
            final MenuItem vulnerabilities = menuBar.addItem("Vulnerabilities", null, null);
            vulnerabilities.addItem("Exploitation tools", selectedItem -> navigator.navigateTo(ToolsView.VIEW_NAME));
            vulnerabilities.addItem("Vulnerabilities", selectedItem -> navigator.navigateTo(VulnerabilitiesView.VIEW_NAME));
            final MenuItem parties = menuBar.addItem("Involved parties", null, null);
            parties.addItem("Attackers", selectedItem -> navigator.navigateTo(AttackersView.VIEW_NAME));
            parties.addItem("Victims", selectedItem -> navigator.navigateTo(OwnersView.VIEW_NAME));
            final MenuItem countries = menuBar.addItem("Country Information", null);
            countries.addItem("Conflicts", selectedItem -> navigator.navigateTo(ConflictsView.VIEW_NAME));
            countries.addItem("Socioeconomic data", selectedItem -> navigator.navigateTo(CountryStatisticsView.VIEW_NAME));
            menuBar.addItem("Statistic analysis", selectedItem -> navigator.navigateTo(CorrelationsView.VIEW_NAME));
            menuBar.addItem("Users", selectedItem -> navigator.navigateTo(UsersView.VIEW_NAME));
            final MenuItem utilities = menuBar.addItem("Utilities", null);
            utilities.addItem("Import Incidents", selectedItem -> getUI().addWindow(importEventDataWindow));
            utilities.addItem("Import Socioeconomic data", selectedItem -> getUI().addWindow(importSocioeconomicDataWindow));

            final Button logout = new Button("Logout", event -> {
                user = null;
                removeNavigationMenu();
                navigator.navigateTo(LoginScreen.VIEW_NAME);
            });
            
            bar.addComponent(menuBar);
            bar.addComponent(logout);
            bar.setExpandRatio(menuBar, 1);
            bar.setComponentAlignment(logout, Alignment.MIDDLE_RIGHT);
        }
        root.addComponentAsFirst(bar);
    }

    public User getUser() {
        return user;
    }
    
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setSizeFull();
        getPage().setTitle("Security Incident Database");

        root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.setSpacing(true);
        setContent(root);

        final Panel viewContainer = new Panel();
        viewContainer.setSizeFull();
        root.addComponent(viewContainer);
        root.setComponentAlignment(viewContainer, Alignment.MIDDLE_CENTER);
        root.setExpandRatio(viewContainer, 1.0f);

        navigator = new Navigator(this, viewContainer);
        navigator.addViewChangeListener(new ViewChangeListener() {
            private static final long serialVersionUID = 3813804949308061211L;
            
            @Override
            public void afterViewChange(ViewChangeEvent event) {
                // Nothing to do for control
            }

            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                if (!(event.getNewView() instanceof LoginScreen) && user == null) {
                    Notification.show("Permission Denied", Type.ERROR_MESSAGE);
                    return false;
                }
                return true;
            }
            
        });
        navigator.addProvider(viewProvider);
        setNavigator(navigator);
        navigator.navigateTo(LoginScreen.VIEW_NAME);
    }

    /**
     * Removes the menu after the logout
     */
    public void removeNavigationMenu() {
        root.removeComponent(bar);
    }

    public void setUser(User user) {
        this.user = user;
    }
}