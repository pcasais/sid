package com.damosais.sid.webapp;

import java.security.GeneralSecurityException;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.services.UserService;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * This view is responsible of displaying the login screen and controlling the user access to the application
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@SpringView(name = LoginScreen.VIEW_NAME)
public class LoginScreen extends VerticalLayout implements View {
    private static final long serialVersionUID = 7228771403828871451L;
    public static final String VIEW_NAME = "LoginScreen";
    private static final Logger LOGGER = Logger.getLogger(LoginScreen.class);
    // The three fields used in this view
    private TextField userName;
    private PasswordField password;
    private Button loginButton;

    @Autowired
    private UserService userService;

    /**
     * The default constructor just adds the spacing and margins
     */
    public LoginScreen() {
        setSpacing(true);
        setMargin(true);
    }
    
    @Override
    public void enter(ViewChangeEvent event) {
        userName.focus();
    }
    
    @PostConstruct
    void init() {
        // First we add the logo
        final Embedded logo = new Embedded(null, GraphicResources.SECURITY_LOGO);
        addComponent(logo);
        setComponentAlignment(logo, Alignment.MIDDLE_CENTER);
        
        // Then we add the form with the user and password
        userName = new TextField("User Name");
        addComponent(userName);
        setComponentAlignment(userName, Alignment.MIDDLE_CENTER);
        password = new PasswordField("Password");
        addComponent(password);
        setComponentAlignment(password, Alignment.MIDDLE_CENTER);
        
        // Finally we add the button
        loginButton = new Button("Login", event -> {
            try {
                final User user = userService.login(userName.getValue(), password.getValue());
                if (user != null) {
                    ((WebApplication) getUI()).setUser(user);
                    ((WebApplication) getUI()).addNavigationMenu();
                    getUI().getNavigator().navigateTo(EventsView.VIEW_NAME);
                } else {
                    final Notification error = new Notification("Invalid credentials", Notification.Type.ERROR_MESSAGE);
                    error.show(Page.getCurrent());
                }
            } catch (final LoginException e) {
                LOGGER.error("Invalid login detected from " + Page.getCurrent().getWebBrowser().getAddress(), e);
            } catch (final GeneralSecurityException e) {
                LOGGER.error("Problem during call to UserService.login()", e);
                final Notification error = new Notification("Internal error", "Problem while accessing the user repository: " + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
                error.show(Page.getCurrent());
            }
        });
        loginButton.setClickShortcut(KeyCode.ENTER);
        addComponent(loginButton);
        setComponentAlignment(loginButton, Alignment.MIDDLE_CENTER);
    }
}