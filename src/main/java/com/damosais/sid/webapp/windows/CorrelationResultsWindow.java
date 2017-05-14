package com.damosais.sid.webapp.windows;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tepi.filtertable.FilterTable;

import com.damosais.sid.database.beans.CorrelationHypothesis;
import com.damosais.sid.database.beans.CorrelationResult;
import com.damosais.sid.database.beans.CountryVariableValue;
import com.damosais.sid.database.services.CorrelationHypothesisService;
import com.damosais.sid.database.services.CountryVariableValueService;
import com.damosais.sid.webapp.GraphicResources;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.ContainerDataSeries;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.CustomTable.CellStyleGenerator;
import com.vaadin.ui.CustomTable.ColumnGenerator;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This class represents the window used to see the results of a correlation hypothesis
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class CorrelationResultsWindow extends Window implements CellStyleGenerator, ColumnGenerator, ClickListener {
    private static final long serialVersionUID = 7026874411087199862L;
    private final VerticalLayout content;
    private BeanItemContainer<CorrelationResult> container;
    private CorrelationHypothesis correlationHypothesis;
    private HorizontalLayout graphLayout;

    @Autowired
    private CountryVariableValueService countryVariableValueService;

    @Autowired
    private CorrelationHypothesisService correlationHypothesisService;
    
    public CorrelationResultsWindow() {
        setModal(true);
        setSizeFull();
        content = new VerticalLayout();
        content.setSpacing(true);
        content.setMargin(true);
        content.setSizeFull();
        setContent(content);
    }
    
    private void addGraphs(CorrelationResult resultToShow) {
        // 1st) We generate the chart for the variable values
        final ContainerDataSeries seriesValues = new ContainerDataSeries(new BeanItemContainer<>(CountryVariableValue.class, resultToShow != null ? countryVariableValueService.listByCountryAndVariable(resultToShow.getCountry(), resultToShow.getVariable()) : new ArrayList<>()));
        seriesValues.setXPropertyId("date");
        seriesValues.setYPropertyId("value");
        seriesValues.setName(resultToShow != null ? resultToShow.getVariable().getName() : "No Data");
        final Chart chartValues = new Chart(ChartType.SPLINE);
        final Configuration chartValuesConfiguration = chartValues.getConfiguration();
        chartValuesConfiguration.setTitle(resultToShow != null ? resultToShow.getCountry().getName() + " - " + resultToShow.getVariable().getName() : "No Data");
        chartValuesConfiguration.getxAxis().setTitle("Date");
        chartValuesConfiguration.getxAxis().setType(AxisType.DATETIME);
        chartValuesConfiguration.getyAxis().setTitle(resultToShow != null ? resultToShow.getVariable().getName() + " (" + resultToShow.getVariable().getUnit() + ")" : "No Data");
        chartValues.getConfiguration().addSeries(seriesValues);
        graphLayout.addComponent(chartValues);
        
        // 2nd) We generate the graph for the events
        final List<YearMonth> timeBuckets = correlationHypothesisService.generateTimeBuckets(correlationHypothesis);
        final double[] events = correlationHypothesisService.getEventsDataArray(correlationHypothesisService.retrieveEventData(correlationHypothesis), timeBuckets);
        final DataSeries eventsSeries = new DataSeries();
        eventsSeries.setName("Events per month");
        int i = 0;
        for (final YearMonth timeBucket : timeBuckets) {
            eventsSeries.add(new DataSeriesItem(Date.from(timeBucket.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant()), events[i++]));
        }
        final Chart chartEvents = new Chart(ChartType.SPLINE);
        final Configuration chartEventsConfiguration = chartEvents.getConfiguration();
        chartEventsConfiguration.setTitle(correlationHypothesis.getTargetCountry().getName() + " - Events per month");
        chartEventsConfiguration.getxAxis().setTitle("Date");
        chartEventsConfiguration.getxAxis().setType(AxisType.DATETIME);
        chartEventsConfiguration.getyAxis().setTitle("Num. Events");
        chartEvents.getConfiguration().addSeries(eventsSeries);
        graphLayout.addComponent(chartEvents);
    }
    
    public void addValues(CorrelationHypothesis correlationHypothesis) {
        this.correlationHypothesis = correlationHypothesis;
        content.removeAllComponents();
        // 1st) We add the header which contains the definition of the hypothesis and the graph area
        final DateFormat yearMonth = new SimpleDateFormat("yyyy-MMM");
        final HorizontalSplitPanel header = new HorizontalSplitPanel();
        header.setSplitPosition(30, Unit.PERCENTAGE);
        header.setSizeFull();
        final VerticalLayout definitionLayout = new VerticalLayout();
        definitionLayout.setSizeFull();
        definitionLayout.addComponent(new Label("<b>Start: </b>" + yearMonth.format(correlationHypothesis.getEffectiveStartDate()), ContentMode.HTML));
        definitionLayout.addComponent(new Label("<b>End:   </b>" + yearMonth.format(correlationHypothesis.getEffectiveEndDate()), ContentMode.HTML));
        definitionLayout.addComponent(new Label("<b>Sector:</b>" + correlationHypothesis.getSector().getName(), ContentMode.HTML));
        definitionLayout.addComponent(new Label("<b>Target country  :</b>" + correlationHypothesis.getEffectiveTargetCountry().getName(), ContentMode.HTML));
        definitionLayout.addComponent(new Label("<b>Source countries:</b>" + correlationHypothesis.getEffectiveSourceCountries(), ContentMode.HTML));
        header.addComponent(definitionLayout);
        graphLayout = new HorizontalLayout();
        addGraphs(null);
        header.addComponent(graphLayout);
        content.addComponent(header);

        // 2nd) We add the table at the bottom
        final FilterTable table = new FilterTable();
        table.setSizeFull();
        table.setSortEnabled(true);
        table.setFilterBarVisible(true);
        // We add a column to generate the graph
        table.addGeneratedColumn("graph", this);
        container = new BeanItemContainer<>(CorrelationResult.class);
        container.addNestedContainerProperty("country.name");
        container.addNestedContainerProperty("createdBy.name");
        container.addNestedContainerProperty("updatedBy.name");
        table.setContainerDataSource(container);

        // Now we define which columns are visible and what are going to be their names in the table header
        table.setVisibleColumns(new Object[] { "country.name", "variable", "correlationCoefficient", "pValue", "standardError", "interpolatedData", "valuesNormality", "eventsNormality", "created", "createdBy.name", "updated", "updatedBy.name", "graph" });
        table.setColumnHeaders(new String[] { "Country", "Variable", "Correlation Coeficient", "P-Value", "Standard Error", "Data Interpolated", "Variable Normality", "Events Normality", "Created", "Created by", "Last update", "Last updated by", "Graph" });
        container.addAll(correlationHypothesis.getResults());
        table.setCellStyleGenerator(this);
        // We then collapse the columns that have less value
        table.setColumnCollapsingAllowed(true);
        table.setColumnCollapsed("created", true);
        table.setColumnCollapsed("createdBy.name", true);
        table.setColumnCollapsed("updated", true);
        table.setColumnCollapsed("updatedBy.name", true);
        content.addComponent(table);
    }
    
    @Override
    public void buttonClick(ClickEvent event) {
        // 1st) We remove any charts element
        graphLayout.removeAllComponents();
        
        // 2nd) We retrieve the result to show
        final CorrelationResult resultToShow = (CorrelationResult) event.getButton().getData();

        // 3rd) And we call the method to add the graphs with it
        addGraphs(resultToShow);
    }
    
    @Override
    public Object generateCell(CustomTable source, Object itemId, Object columnId) {
        // First we create a button and set its data with the event
        final Button button = new Button("", this);
        button.setStyleName("link");
        // From the container we find the item with the ID itemId that is the one for which we are drawing the cell
        final BeanItem<CorrelationResult> currentItem = container.getItem(itemId);
        // Finally we add to the button as data the computer of this item
        button.setData(currentItem.getBean());
        button.setIcon(GraphicResources.SEARCH_ICON);
        // Finally we return the button
        return button;
    }

    @Override
    public String getStyle(CustomTable source, Object itemId, Object propertyId) {
        final CorrelationResult result = (CorrelationResult) itemId;
        final double correlationCoefficient = result.getCorrelationCoefficient();
        final double pValue = result.getpValue();
        if (Math.abs(correlationCoefficient) > 0.75d && pValue < 0.05d) {
            return "green";
        } else if (Math.abs(correlationCoefficient) > 0.75) {
            return "orange";
        } else {
            return "red";
        }
    }
}