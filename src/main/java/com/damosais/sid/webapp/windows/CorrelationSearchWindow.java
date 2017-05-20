package com.damosais.sid.webapp.windows;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.math3.exception.MathRuntimeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.damosais.sid.database.beans.CorrelationHypothesis;
import com.damosais.sid.database.beans.CorrelationResult;
import com.damosais.sid.database.beans.SocioeconomicVariable;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.services.CorrelationHypothesisService;
import com.damosais.sid.webapp.CorrelationsView;
import com.damosais.sid.webapp.GraphicResources;
import com.damosais.sid.webapp.WebApplication;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This class handles the window where the application searches for correlations in the existing data
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Component
public class CorrelationSearchWindow extends Window {
    private static final Logger LOGGER = Logger.getLogger(CorrelationSearchWindow.class);
    private static final long serialVersionUID = -2181593253759124940L;
    private final VerticalLayout content;
    
    @Autowired
    private CorrelationHypothesisService correlationHypothesisService;

    /**
     * Creates a new window to automatically search for correlations
     */
    public CorrelationSearchWindow() {
        setModal(true);
        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);
        setSizeUndefined();
    }

    private void executeSearch(Set<SocioeconomicVariable> variables, TextField minEventsField, CheckBox filterRelevantCorrelationsOnly, Label status, ProgressBar progress, Button startButton, CorrelationsView view) {
        new Thread(() -> {
            final String minEventsValueRaw = minEventsField.getValue();
            try {
                // 1st) We get the settings of the search
                final int minEventsValue = Integer.parseInt(minEventsValueRaw);
                if (minEventsValue < 0) {
                    throw new NumberFormatException("Invalid value " + minEventsValueRaw);
                }
                final boolean onlyRelevantCorrelations = filterRelevantCorrelationsOnly.getValue();

                // 2nd) We start creating the correlation hypothesis
                UI.getCurrent().access(() -> status.setValue("<b>Creating correlation hypothesis with the given criteria.</b> Please wait"));
                UI.getCurrent().push();
                final List<CorrelationHypothesis> correlationHypothesis = correlationHypothesisService.generateHypothesis(variables, minEventsValue);
                final int total = correlationHypothesis.size();

                // 3rd) We now start to run every correlation and check the results
                UI.getCurrent().access(() -> status.setValue("<b>Processing " + total + " correlation hypothesis.</b> Please wait"));
                UI.getCurrent().push();
                final User user = ((WebApplication) view.getUI()).getUser();
                final AtomicInteger index = new AtomicInteger(0);
                for (final CorrelationHypothesis hypothesis : correlationHypothesis) {
                    // 3.1) First we need to save the hypothesis
                    hypothesis.setCreatedBy(user);
                    correlationHypothesisService.save(hypothesis);
                    // 3.2) Then we calculate the values of the correlation
                    boolean error = false;
                    try {
                        correlationHypothesisService.calculateHyphotesisSimpleCorrelations(hypothesis, view);
                    } catch (final MathRuntimeException | NumberIsTooSmallException e) {
                        error = true;
                        LOGGER.error("Problem calculating correlation for hypothesis: " + e.getMessage(), e);
                    }
                    boolean delete = false;
                    if (onlyRelevantCorrelations && error) {
                        delete = true;
                    } else if (onlyRelevantCorrelations) {
                        boolean significant = false;
                        for (final CorrelationResult result : hypothesis.getResults()) {
                            final boolean pearsonSignificative = result.getPearsonCorrelationCoefficient() >= CorrelationResult.SIGNIFICATIVE_LEVEL && result.getpValuePearson() < CorrelationResult.NON_NULL_HYPOTHESIS_LEVEL;
                            final boolean spearmanSignificative = result.getSpearmanCorrelationCoefficient() >= CorrelationResult.SIGNIFICATIVE_LEVEL && result.getpValueSpearman() < CorrelationResult.NON_NULL_HYPOTHESIS_LEVEL;
                            if (pearsonSignificative || spearmanSignificative) {
                                significant = true;
                                break;
                            }
                        }
                        delete = !significant;
                    }
                    if (delete) {
                        correlationHypothesisService.delete(hypothesis);
                    }
                    UI.getCurrent().access(() -> progress.setValue((float) index.incrementAndGet() / total));
                    UI.getCurrent().push();
                }
                UI.getCurrent().access(() -> status.setValue("<b>All " + total + " correlation hypothesis processed.</b> You can now close the window"));
            } catch (final NumberFormatException e) {
                new Notification("Error", "Problem defining minimum number of events (values are 0 for no minimum, integer positive values for minimum): " + e.getMessage(), Notification.Type.ERROR_MESSAGE).show(UI.getCurrent().getPage());
                LOGGER.error("Problem with minimum events: " + e.getMessage(), e);
            }
            startButton.setEnabled(true);
            UI.getCurrent().access(() -> view.refreshTableContent());
            UI.getCurrent().push();
        }).start();
    }

    /**
     * This method generates the form content and prepares the window to run
     */
    @SuppressWarnings("unchecked")
    public void generateForm(CorrelationsView view) {
        content.removeAllComponents();
        final ListSelect socioEconomicVariables = new ListSelect("Variables", Arrays.asList(SocioeconomicVariable.values()));
        socioEconomicVariables.setImmediate(true);
        socioEconomicVariables.setMultiSelect(true);
        content.addComponent(socioEconomicVariables);
        final TextField minEvents = new TextField("Min Events", "0");
        content.addComponent(minEvents);
        final CheckBox filterRelevantCorrelationsOnly = new CheckBox("Filter relevant only", false);
        content.addComponent(filterRelevantCorrelationsOnly);
        final Label status = new Label("", ContentMode.HTML);
        status.setImmediate(true);
        content.addComponent(status);
        final ProgressBar progress = new ProgressBar(0.0f);
        progress.setImmediate(true);
        content.addComponent(progress);
        final Button startButton = new Button("Start search");
        startButton.addClickListener(event -> {
            startButton.setEnabled(false);
            executeSearch((Set<SocioeconomicVariable>) socioEconomicVariables.getValue(), minEvents, filterRelevantCorrelationsOnly, status, progress, startButton, view);
        });
        startButton.setIcon(GraphicResources.RUN_ICON);
        content.addComponent(startButton);
    }
}