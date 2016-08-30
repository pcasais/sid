package com.damosais.sid.webapp;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.tepi.filtertable.FilterTable;

import com.damosais.sid.database.beans.CountryVariableValue;
import com.damosais.sid.database.beans.SocioeconomicVariable;
import com.damosais.sid.database.services.CountryVariableValueService;
import com.damosais.sid.webapp.customfields.CountryFieldConverter;
import com.damosais.sid.webapp.windows.CountryVariableValueWindow;
import com.neovisionaries.i18n.CountryCode;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.ContainerDataSeries;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.CustomTable.ColumnGenerator;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;

/**
 * This is the screen from which users can see the different socioeconomical variables and their values per country and year
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@SpringView(name = CountryStatisticsView.VIEW_NAME)
public class CountryStatisticsView extends HorizontalSplitPanel implements View, ClickListener, ColumnGenerator {
    private static final long serialVersionUID = -521722801480373440L;
    public static final String VIEW_NAME = "CountryStatsScreen";
    private static final String EDIT_BUTTON = "editButton";
    private static final String DELETE_BUTTON = "deleteButton";
    private static final String BY_VARIABLE_AND_DATE = "By Variable and Date";
    private static final String BY_COUNTRY_AND_DATES = "By Country and Dates";
    private static final String BY_COUNTRY_AND_VARIABLE = "By Country and Variable";
    private final VerticalLayout tableLayout;
    private final VerticalLayout graphLayout;
    private VerticalLayout formLayout;
    private BeanItemContainer<CountryVariableValue> container;
    private Button addStatistic;
    private Button generateGraph;
    private FilterTable table;
    
    @Autowired
    private CountryVariableValueService countryVariableValueService;

    @Autowired
    private CountryVariableValueWindow countryVariableValueWindow;

    /**
     * The constructor just creates the initial layout
     */
    public CountryStatisticsView() {
        setSplitPosition(70, Unit.PERCENTAGE);
        tableLayout = new VerticalLayout();
        tableLayout.setMargin(true);
        tableLayout.setSpacing(true);
        addComponent(tableLayout);
        graphLayout = new VerticalLayout();
        graphLayout.setMargin(true);
        graphLayout.setSpacing(true);
        addComponent(graphLayout);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        final Button button = event.getButton();
        if (addStatistic.equals(button)) {
            countryVariableValueWindow.setAddMode(this);
            getUI().addWindow(countryVariableValueWindow);
        } else {
            // In this case we are dealing with the buttons of a row
            final CountryVariableValue countryVariableValueToAlter = (CountryVariableValue) button.getData();
            if (GraphicResources.EDIT_ICON.equals(button.getIcon())) {
                countryVariableValueWindow.setEditMode(countryVariableValueToAlter, this);
                getUI().addWindow(countryVariableValueWindow);
            } else if (GraphicResources.DELETE_ICON.equals(button.getIcon())) {
                countryVariableValueService.delete(countryVariableValueToAlter);
                refreshTableContent();
            }
        }
    }

    private void createGraphForm() {
        // 1st) We create the form
        formLayout = new VerticalLayout();
        formLayout.setMargin(true);
        formLayout.setSpacing(true);

        // 2nd) We add the selector of the type of search
        final OptionGroup selectionType = new OptionGroup("Search type");
        selectionType.addItems(BY_COUNTRY_AND_VARIABLE, BY_COUNTRY_AND_DATES, BY_VARIABLE_AND_DATE);
        selectionType.setImmediate(true);
        formLayout.addComponent(selectionType);

        // 3rd) We add the country selector (We need a hack because it doesn't have a nice toString() method)
        final BeanContainer<Integer, CountryCode> countryContainer = new BeanContainer<>(CountryCode.class);
        countryContainer.setBeanIdProperty("numeric");
        countryContainer.addAll(EnumSet.allOf(CountryCode.class));
        final ComboBox countryField = new ComboBox("Country", countryContainer);
        countryField.setItemCaptionPropertyId("name");
        countryField.setConverter(new CountryFieldConverter());
        formLayout.addComponent(countryField);

        // 4th) We add the variable selector
        final ComboBox variableField = new ComboBox("Variable", Arrays.asList(SocioeconomicVariable.values()));
        formLayout.addComponent(variableField);

        // 5th) We add the date selectors
        final PopupDateField startDateField = new PopupDateField("Start Date");
        startDateField.setResolution(Resolution.MONTH);
        startDateField.setDateFormat("yyyy-MM");
        formLayout.addComponent(startDateField);
        final PopupDateField endDateField = new PopupDateField("End Date");
        endDateField.setResolution(Resolution.MONTH);
        endDateField.setDateFormat("yyyy-MM");
        formLayout.addComponent(endDateField);

        // 6th) We add now the behaviour of the option group
        selectionType.addValueChangeListener(event -> {
            if (BY_COUNTRY_AND_VARIABLE.equals(event.getProperty().getValue())) {
                // In this case we enable the country and variable and disable the dates
                countryField.setEnabled(true);
                variableField.setEnabled(true);
                startDateField.setEnabled(false);
                endDateField.setEnabled(false);
            } else if (BY_COUNTRY_AND_DATES.equals(event.getProperty().getValue())) {
                // In this case we enable the country and the two dates and disable the variable
                countryField.setEnabled(true);
                variableField.setEnabled(false);
                startDateField.setCaption("Start Date");
                startDateField.setEnabled(true);
                endDateField.setEnabled(true);
            } else if (BY_VARIABLE_AND_DATE.equals(event.getProperty().getValue())) {
                // In this case we enable the variable and the first date and disable the country and the other date
                countryField.setEnabled(false);
                variableField.setEnabled(true);
                startDateField.setCaption("Date");
                startDateField.setEnabled(true);
                endDateField.setEnabled(false);
            }
        });

        // 7th) We set the option group to the first option by default
        selectionType.setValue(BY_COUNTRY_AND_VARIABLE);

        // 8th) We add the button to generate the graphs
        generateGraph = new Button("Generate Graph", GraphicResources.RUN_ICON);
        formLayout.addComponent(generateGraph);
        generateGraph.addClickListener(event -> {
            CountryCode country = null;
            if (countryField.isEnabled()) {
                country = (CountryCode) countryField.getConvertedValue();
                if (country == null) {
                    new Notification("Failure", "You need to select a country", Notification.Type.ERROR_MESSAGE);
                }
            }
            SocioeconomicVariable variable = null;
            if (variableField.isEnabled()) {
                variable = (SocioeconomicVariable) variableField.getValue();
                if (variable == null) {
                    new Notification("Failure", "You need to select a variable", Notification.Type.ERROR_MESSAGE);
                }
            }
            Date startDate = null;
            if (startDateField.isEnabled()) {
                startDate = startDateField.getValue();
                if (startDate == null) {
                    new Notification("Failure", "You need to select a date", Notification.Type.ERROR_MESSAGE);
                }
            }
            Date endDate = null;
            if (endDateField.isEnabled()) {
                endDate = endDateField.getValue();
                if (endDate == null) {
                    new Notification("Failure", "You need to select an end date", Notification.Type.ERROR_MESSAGE);
                }
            }
            renderGraph(country, variable, startDate, endDate);
        });
    }
    
    private void createTableButtons() {
        final HorizontalLayout hl = new HorizontalLayout();
        addStatistic = new Button("Add values", this);
        addStatistic.setStyleName("link");
        addStatistic.setIcon(GraphicResources.ADD_ICON);
        hl.addComponent(addStatistic);
        hl.setComponentAlignment(addStatistic, Alignment.MIDDLE_CENTER);
        tableLayout.addComponent(hl);
        tableLayout.setComponentAlignment(hl, Alignment.TOP_CENTER);
    }
    
    @Override
    public void enter(ViewChangeEvent event) {
        // We do nothing on enter
    }
    
    // This method generates the cells for the different buttons
    @Override
    public Object generateCell(CustomTable source, Object itemId, Object columnId) {
        // First we create a button and set its data with the country variable value
        final Button button = new Button("", this);
        button.setStyleName("link");
        // From the container we find the item with the ID itemId that is the one for which we are drawing the cell
        final BeanItem<CountryVariableValue> currentItem = container.getItem(itemId);
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
     * When we start the AttackersView we create the table and the buttons
     */
    @PostConstruct
    public void init() {
        // 1st) thing we initialise the table
        initializeTable();
        // 2nd) We add a small description explaining what we are doing
        final Label attackerLabel = new Label("<center><p>The objective of this application is to help correlating security incidents with real world data.<br/>In this screen you can observe the existing real world data, edit it, delete it, or add new one for the different countries and dates.</p></center>", ContentMode.HTML);
        tableLayout.addComponent(attackerLabel);
        // 3rd) Now we create the buttons for the generic actions on the table
        createTableButtons();
        // 4th) Now we add the table to the view
        tableLayout.addComponent(table);
        tableLayout.setComponentAlignment(table, Alignment.TOP_CENTER);
        // 5th) We now add create the form to select the grap
        createGraphForm();
        graphLayout.addComponent(formLayout);
    }
    
    /**
     * This method generates the table for first time, only to be called when initialising the table
     */
    private void initializeTable() {
        // We create a table and set the source of data as the container
        table = new FilterTable();
        table.setFilterBarVisible(true);
        // We add a column with the button to edit the attacker details
        table.addGeneratedColumn(EDIT_BUTTON, this);
        // We add a column with the button to delete the attacker
        table.addGeneratedColumn(DELETE_BUTTON, this);
        // Now we add the container
        container = new BeanItemContainer<>(CountryVariableValue.class);
        container.addNestedContainerProperty("country.name");
        table.setContainerDataSource(container);
        // Now we define which columns are visible and what are going to be their names in the table header
        table.setVisibleColumns(new Object[] { "country.name", "variable", "date", "value", EDIT_BUTTON, DELETE_BUTTON });
        table.setColumnHeaders(new String[] { "Country", "Variable", "Date", "Value", "Edit", "Delete" });
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
        container.addAll(countryVariableValueService.list());
    }
    
    private void renderGraph(CountryCode country, SocioeconomicVariable variable, Date startDate, Date endDate) {
        // 1st) We retrieve the corresponding lists
        Chart chart = null;
        if (country != null && variable != null) {
            // In this case we generate a plot with the evolution of the variable in the country
            final ContainerDataSeries series = new ContainerDataSeries(new BeanItemContainer<>(CountryVariableValue.class, countryVariableValueService.listByCountryAndVariable(country, variable)));
            series.setXPropertyId("date");
            series.setYPropertyId("value");
            series.setName(variable.getName());

            chart = new Chart(ChartType.SPLINE);
            final Configuration configuration = chart.getConfiguration();
            configuration.setTitle(country.getName() + " - " + variable.getName());
            configuration.getxAxis().setTitle("Date");
            configuration.getxAxis().setType(AxisType.DATETIME);
            configuration.getyAxis().setTitle(variable.getName() + " (" + variable.getUnit() + ")");
            chart.getConfiguration().addSeries(series);
        } else if (variable != null && startDate != null) {
            // In this case we generate a set of columns showing the value of the variable in that date for all countries
            final ContainerDataSeries series = new ContainerDataSeries(new BeanItemContainer<>(CountryVariableValue.class, countryVariableValueService.listByVariableAndDate(variable, startDate)));
            series.setXPropertyId("country");
            series.setYPropertyId("value");
            series.setName(variable.getName());
            
            chart = new Chart(ChartType.BAR);
            final Configuration configuration = chart.getConfiguration();
            configuration.setTitle(variable.getName() + " - " + new SimpleDateFormat("yyyy-MM").format(startDate));
            configuration.getxAxis().setTitle("Country");
            configuration.getyAxis().setTitle(variable.getName() + " (" + variable.getUnit() + ")");
            chart.getConfiguration().addSeries(series);
        }
        // TODO: Continue here
        if (chart != null) {
            if (graphLayout.getComponentCount() > 1) {
                graphLayout.removeAllComponents();
                graphLayout.addComponent(formLayout);
            }
            graphLayout.addComponent(chart);
        }
    }
}