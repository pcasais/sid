package com.damosais.sid.webapp.windows;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.springframework.stereotype.Component;
import org.tepi.filtertable.FilterTable;

import com.damosais.sid.database.beans.CorrelationHypothesis;
import com.damosais.sid.database.beans.CorrelationResult;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.CustomTable.CellStyleGenerator;
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
public class CorrelationResultsWindow extends Window implements CellStyleGenerator {
    private static final long serialVersionUID = 7026874411087199862L;
    private final VerticalLayout content;
    
    public CorrelationResultsWindow() {
        setModal(true);
        setSizeUndefined();
        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);
        center();
    }
    
    public void addValues(CorrelationHypothesis correlationHypothesis) {
        content.removeAllComponents();
        final DateFormat yearMonth = new SimpleDateFormat("yyyy-MMM");
        content.addComponent(new Label("<b>Start :</b>" + yearMonth.format(correlationHypothesis.getStartDate()), ContentMode.HTML));
        content.addComponent(new Label("<b>End   :</b>" + yearMonth.format(correlationHypothesis.getEndDate()), ContentMode.HTML));
        content.addComponent(new Label("<b>Sector:</b>" + correlationHypothesis.getSector().getName(), ContentMode.HTML));
        content.addComponent(new Label("<b>Target countries:</b>" + correlationHypothesis.getTargetCountries(), ContentMode.HTML));
        content.addComponent(new Label("<b>Source countries:</b>" + correlationHypothesis.getSourceCountries(), ContentMode.HTML));
        final FilterTable table = new FilterTable();
        table.setFilterBarVisible(true);
        final BeanItemContainer<CorrelationResult> container = new BeanItemContainer<>(CorrelationResult.class);
        container.addNestedContainerProperty("createdBy.name");
        container.addNestedContainerProperty("updatedBy.name");
        table.setContainerDataSource(container);
        // Now we define which columns are visible and what are going to be their names in the table header
        table.setVisibleColumns(new Object[] { "country", "variable", "correlationCoefficient", "pValue", "interpolatedData", "created", "createdBy.name", "updated", "updatedBy.name" });
        table.setColumnHeaders(new String[] { "Country", "Variable", "Correlation Coeficient", "P-Value", "Data Interpolated", "Created", "Created by", "Last update", "Last updated by" });
        container.addAll(correlationHypothesis.getResults());
        table.setCellStyleGenerator(this);
        content.addComponent(table);
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