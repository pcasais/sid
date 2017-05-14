package com.damosais.sid.database.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathRuntimeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.damosais.sid.database.beans.Attacker;
import com.damosais.sid.database.beans.CorrelationHypothesis;
import com.damosais.sid.database.beans.CorrelationResult;
import com.damosais.sid.database.beans.CountryVariableValue;
import com.damosais.sid.database.beans.Event;
import com.damosais.sid.database.beans.Sector;
import com.damosais.sid.database.beans.SocioeconomicVariable;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.dao.CorrelationHypothesisDAO;
import com.damosais.sid.database.dao.CorrelationResultDAO;
import com.damosais.sid.database.dao.CountryVariableValueDAO;
import com.damosais.sid.database.dao.EventDAO;
import com.damosais.sid.webapp.CorrelationsView;
import com.damosais.sid.webapp.WebApplication;
import com.neovisionaries.i18n.CountryCode;
import com.vaadin.ui.Notification;

import net.sourceforge.jdistlib.disttest.NormalityTest;
import net.sourceforge.jdistlib.exception.PrecisionException;
import net.sourceforge.jdistlib.util.Utilities;

/**
 * This service is responsible of retrieving, creating, deleting and updating any correlation in the database
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Service
public class CorrelationHypothesisService {
    private static final Logger LOGGER = Logger.getLogger(CorrelationHypothesisService.class);

    @Autowired
    private CorrelationResultDAO correlationResultDAO;

    @Autowired
    private CorrelationHypothesisDAO correlationHypothesisDAO;

    @Autowired
    private EventDAO eventDAO;

    @Autowired
    private CountryVariableValueDAO countryVariableValueDAO;

    /**
     * This method calculates the correlation coefficients for the events given with all the possible variables in the hypothesis
     *
     * @param correlationHypothesis
     *            The correlation hypothesis for which we are trying to calculate the values
     * @param correlationsView
     *            The view which called the calculation
     */
    @SuppressWarnings("deprecation")
    public void calculateHyphotesisSimpleCorrelations(CorrelationHypothesis correlationHypothesis, CorrelationsView correlationsView) throws MathRuntimeException {
        // 1st) We need to get the event data
        final List<Event> events = retrieveEventData(correlationHypothesis);

        // 2nd) We get the socioeconomic data
        final Map<CountryCode, Map<SocioeconomicVariable, List<CountryVariableValue>>> valuesPerCountryAndVariable = retrieveSocioeconomicData(correlationHypothesis);

        // 3rd) We now need to generate the time buckets for the given period
        final List<YearMonth> timeBuckets = generateTimeBuckets(correlationHypothesis);
        
        // 4th) We now create the vector for the events using the time buckets
        final double[] eventsArray = getEventsDataArray(events, timeBuckets);
        
        // 5th) Now we index any previous results from running analysis so they can be updated
        final Map<CountryCode, Map<SocioeconomicVariable, CorrelationResult>> resultByCountryAndVariable = new HashMap<>();
        final Set<CorrelationResult> results = new HashSet<>();
        if (correlationHypothesis.getResults() != null && !correlationHypothesis.getResults().isEmpty()) {
            for (final CorrelationResult result : correlationHypothesis.getResults()) {
                results.add(result);
                Map<SocioeconomicVariable, CorrelationResult> resultsByVariable = resultByCountryAndVariable.get(result.getCountry());
                if (resultsByVariable == null) {
                    resultsByVariable = new HashMap<>();
                    resultByCountryAndVariable.put(result.getCountry(), resultsByVariable);
                }
                resultsByVariable.put(result.getVariable(), result);
            }
        }
        
        // 5th) Now we loop through the values calculating the correlations between them and the events
        final User user = ((WebApplication) correlationsView.getUI()).getUser();
        final List<String> errors = new ArrayList<>();
        for (final CountryCode country : valuesPerCountryAndVariable.keySet()) {
            final Map<SocioeconomicVariable, List<CountryVariableValue>> valuesPerVariable = valuesPerCountryAndVariable.get(country);
            for (final SocioeconomicVariable variable : valuesPerVariable.keySet()) {
                boolean newItem = false;
                CorrelationResult result = resultByCountryAndVariable.get(country) != null ? resultByCountryAndVariable.get(country).get(variable) : null;
                if (result == null) {
                    result = new CorrelationResult();
                    result.setCountry(country);
                    result.setVariable(variable);
                    result.setCreatedBy(user);
                    newItem = true;
                } else {
                    result.setUpdatedBy(user);
                }
                // 5.1) First we generate a vector for the values of that variable
                try {
                    final double[] valuesArray = getValuesArray(valuesPerVariable.get(variable), timeBuckets, result);
                    // 5.2) Now we generate the matrix with the data for the Rearson's correlation
                    final BlockRealMatrix matrix = new BlockRealMatrix(timeBuckets.size(), 2);
                    matrix.setColumn(0, valuesArray);
                    matrix.setColumn(1, eventsArray);
                    // 5.3) Then we calculate the correlation level and the P-Value
                    final PearsonsCorrelation pearsonCorrelation = new PearsonsCorrelation(matrix);
                    result.setCorrelationCoefficient(pearsonCorrelation.getCorrelationMatrix().getEntry(0, 1));
                    result.setpValue(pearsonCorrelation.getCorrelationPValues().getEntry(0, 1));
                    result.setStandardError(pearsonCorrelation.getCorrelationStandardErrors().getEntry(0, 1));
                    if (Double.isNaN(result.getCorrelationCoefficient())) {
                        result.setCorrelationCoefficient(0d);
                    }
                    if (Double.isNaN(result.getpValue())) {
                        result.setpValue(1.0d);
                    }
                    if (Double.isNaN(result.getStandardError())) {
                        result.setStandardError(1.0d);
                    }
                    // 5.4) We calculate the normality of both data sets. To do that we need to sort the arrays before doing it
                    Utilities.sort(valuesArray);
                    Utilities.sort(eventsArray);
                    if (valuesArray.length < 30) {
                        result.setValuesNormality(NormalityTest.shapiro_wilk_statistic(valuesArray));
                        result.setEventsNormality(NormalityTest.shapiro_wilk_statistic(eventsArray));
                    } else {
                        result.setValuesNormality(NormalityTest.kolmogorov_smirnov_statistic(valuesArray));
                        result.setEventsNormality(NormalityTest.kolmogorov_smirnov_statistic(eventsArray));
                    }
                } catch (final MathIllegalArgumentException e) {
                    errors.add("Problem with the correlation calculation for variable " + variable.getName() + " on " + country.getName() + ": interpolation of data for period " + timeBuckets.get(0) + " - " + timeBuckets.get(timeBuckets.size() - 1));
                    LOGGER.error("Failed to calculate correlation for hypothesis " + e.getMessage(), e);
                } catch (final PrecisionException e) {
                    LOGGER.debug("Failure calculating the normality of the data distributions: " + e.getMessage(), e);
                    // Do nothing
                }
                correlationResultDAO.save(result);
                if (newItem) {
                    results.add(result);
                }
            }
        }
        correlationHypothesis.setResults(results);
        correlationHypothesisDAO.save(correlationHypothesis);
        if (!errors.isEmpty()) {
            new Notification("Failure", errors.toString(), Notification.Type.ERROR_MESSAGE).show(correlationsView.getUI().getPage());
        }
    }

    /**
     * Deletes a correlation from the database
     *
     * @param correlation
     *            The correlation to be deleted
     */
    public void delete(CorrelationHypothesis correlation) {
        correlationHypothesisDAO.delete(correlation);
    }

    /**
     * This method creates all possible correlation hypothesis
     *
     * @param variables
     * @param minEventsValue
     * @return
     */
    public List<CorrelationHypothesis> generateHypothesis(Set<SocioeconomicVariable> variables, int minEventsValue) {
        // 1st) We get a list of all the existing correlations to avoid duplicates
        final List<CorrelationHypothesis> existingOnes = new ArrayList<>();
        correlationHypothesisDAO.findAll().forEach(existingOnes::add);

        // 2nd) First of all we need to get all the values of socioeconomic and check the maximum and minimum dates
        final Map<CountryCode, Map<SocioeconomicVariable, YearMonth>> minDatesByCountryAndVariable = new HashMap<>();
        final Map<CountryCode, Map<SocioeconomicVariable, YearMonth>> maxDatesByCountryAndVariable = new HashMap<>();
        final Map<CountryCode, Map<SocioeconomicVariable, Integer>> numberOfValuesByCountryAndVariable = new HashMap<>();
        for (final CountryVariableValue value : variables == null || variables.isEmpty() ? countryVariableValueDAO.findAll() : countryVariableValueDAO.findByVariableIn(variables)) {
            // 2.1) We get the values of that country
            Map<SocioeconomicVariable, YearMonth> minDatesPerVariable = minDatesByCountryAndVariable.get(value.getCountry());
            Map<SocioeconomicVariable, YearMonth> maxDatesPerVariable = maxDatesByCountryAndVariable.get(value.getCountry());
            Map<SocioeconomicVariable, Integer> numberOfValuesByVariable = numberOfValuesByCountryAndVariable.get(value.getCountry());
            if (minDatesPerVariable == null) {
                minDatesPerVariable = new HashMap<>();
                minDatesByCountryAndVariable.put(value.getCountry(), minDatesPerVariable);
            }
            if (maxDatesPerVariable == null) {
                maxDatesPerVariable = new HashMap<>();
                maxDatesByCountryAndVariable.put(value.getCountry(), maxDatesPerVariable);
            }
            if (numberOfValuesByVariable == null) {
                numberOfValuesByVariable = new HashMap<>();
                numberOfValuesByCountryAndVariable.put(value.getCountry(), numberOfValuesByVariable);
            }
            // 2.2) We then get the values that share the same variable
            final YearMonth date = YearMonth.from(value.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            final YearMonth minDate = minDatesPerVariable.get(value.getVariable());
            final YearMonth maxDate = maxDatesPerVariable.get(value.getVariable());
            Integer numberOfValues = numberOfValuesByVariable.get(value.getVariable());
            // 2.3) And add them to the correct place
            if (minDate == null || date.isBefore(minDate)) {
                minDatesPerVariable.put(value.getVariable(), date);
            }
            if (maxDate == null || date.isAfter(maxDate)) {
                maxDatesPerVariable.put(value.getVariable(), date);
            }
            if (numberOfValues == null) {
                numberOfValues = 0;
            }
            numberOfValuesByVariable.put(value.getVariable(), numberOfValues + 1);
        }

        // 3rd) Now we get all the events and we count them and check the dates
        final Map<CountryCode, Integer> eventsByTargetCountry = new HashMap<>();
        final Map<CountryCode, YearMonth> minDateByTargetCountry = new HashMap<>();
        final Map<CountryCode, YearMonth> maxDateByTargetCountry = new HashMap<>();
        final Map<CountryCode, Map<CountryCode, Integer>> eventsByTargetAndSourceCountry = new HashMap<>();
        final Map<CountryCode, Map<CountryCode, YearMonth>> minDateByTargetAndSourceCountry = new HashMap<>();
        final Map<CountryCode, Map<CountryCode, YearMonth>> maxDateByTargetAndSourceCountry = new HashMap<>();
        for (final Event event : eventDAO.findAll()) {
            // 3.1) First we update the statistics by target country
            final CountryCode targetCountry = event.getTarget().getOwner().getCountry();
            Integer totalEvents = eventsByTargetCountry.get(targetCountry);
            if (totalEvents == null) {
                totalEvents = 0;
            }
            eventsByTargetCountry.put(targetCountry, totalEvents + 1);
            final YearMonth date = YearMonth.from(event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            YearMonth minDate = minDateByTargetCountry.get(targetCountry);
            YearMonth maxDate = maxDateByTargetCountry.get(targetCountry);
            if (minDate == null || date.isBefore(minDate)) {
                minDateByTargetCountry.put(targetCountry, date);
            }
            if (maxDate == null || date.isAfter(maxDate)) {
                maxDateByTargetCountry.put(targetCountry, date);
            }
            // 3.2) Now we try to get the incident details so we can add to the by source country maps as well
            Map<CountryCode, Integer> eventsBySourceCountry = eventsByTargetAndSourceCountry.get(targetCountry);
            if (eventsBySourceCountry == null) {
                eventsBySourceCountry = new HashMap<>();
                eventsByTargetAndSourceCountry.put(targetCountry, eventsBySourceCountry);
            }
            Map<CountryCode, YearMonth> minDateBySourceCountry = minDateByTargetAndSourceCountry.get(targetCountry);
            if (minDateBySourceCountry == null) {
                minDateBySourceCountry = new HashMap<>();
                minDateByTargetAndSourceCountry.put(targetCountry, minDateBySourceCountry);
            }
            Map<CountryCode, YearMonth> maxDateBySourceCountry = maxDateByTargetAndSourceCountry.get(targetCountry);
            if (maxDateBySourceCountry == null) {
                maxDateBySourceCountry = new HashMap<>();
                maxDateByTargetAndSourceCountry.put(targetCountry, maxDateBySourceCountry);
            }
            if (event.getAttack() != null && event.getAttack().getIncident() != null && event.getAttack().getIncident().getAttackers() != null && !event.getAttack().getIncident().getAttackers().isEmpty()) {
                for (final Attacker attacker : event.getAttack().getIncident().getAttackers().stream().filter(attacker -> attacker.getCountry() != null).collect(Collectors.toList())) {
                    final CountryCode sourceCountry = attacker.getCountry();
                    totalEvents = eventsBySourceCountry.get(sourceCountry);
                    if (totalEvents == null) {
                        totalEvents = 0;
                    }
                    eventsBySourceCountry.put(sourceCountry, totalEvents + 1);
                    minDate = minDateBySourceCountry.get(sourceCountry);
                    maxDate = maxDateBySourceCountry.get(sourceCountry);
                    if (minDate == null || date.isBefore(minDate)) {
                        minDateBySourceCountry.put(sourceCountry, minDate);
                    }
                    if (maxDate == null || date.isAfter(maxDate)) {
                        maxDateBySourceCountry.put(sourceCountry, maxDate);
                    }
                }
            }
        }
        
        // 4th) We now create the hypothesis using both data sets
        final List<CorrelationHypothesis> hypothesis = new ArrayList<>();
        for (final CountryCode targetCountry : eventsByTargetCountry.keySet()) {
            // 4.1) First we create the simple hypothesis linking each targeted country with its own variables
            // 4.1.1) If the number of events doesn't reach a set minimum (non zero) then we ignore it
            if (minEventsValue > 0 && eventsByTargetCountry.get(targetCountry) < minEventsValue) {
                continue;
            }
            // 4.1.2) We now loop the variables to see which ones are available to test and take the dates there to define the ranges
            if (!minDatesByCountryAndVariable.containsKey(targetCountry)) {
                continue;
            }
            for (final SocioeconomicVariable variable : minDatesByCountryAndVariable.get(targetCountry).keySet()) {
                // We need at least three values on the variable
                if (numberOfValuesByCountryAndVariable.get(targetCountry).get(variable) < 3) {
                    continue;
                }
                // We then calculate the min and max dates to use
                final YearMonth minVariableDate = minDatesByCountryAndVariable.get(targetCountry).get(variable);
                final YearMonth minEventsDate = minDateByTargetCountry.get(targetCountry);
                final YearMonth minDate = minVariableDate.isAfter(minEventsDate) ? minVariableDate : minEventsDate;
                final YearMonth maxVariableDate = maxDatesByCountryAndVariable.get(targetCountry).get(variable);
                final YearMonth maxEventsDate = maxDateByTargetCountry.get(targetCountry);
                final YearMonth maxDate = maxVariableDate.isBefore(maxEventsDate) ? maxVariableDate : maxEventsDate;
                // And we make sure the max date is after the min date
                if (!maxDate.isAfter(minDate)) {
                    continue;
                }
                final CorrelationHypothesis correlationHypothesis = new CorrelationHypothesis();
                correlationHypothesis.setTargetCountry(targetCountry);
                correlationHypothesis.setSector(Sector.ROOT);
                correlationHypothesis.setVariables(new HashSet<>(Arrays.asList(new SocioeconomicVariable[] { variable })));
                correlationHypothesis.setStartDate(Date.from(minDate.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                correlationHypothesis.setEndDate(Date.from(maxDate.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                if (!existingOnes.contains(correlationHypothesis)) {
                    hypothesis.add(correlationHypothesis);
                } else {
                    hypothesis.add(existingOnes.get(existingOnes.indexOf(correlationHypothesis)));
                }
            }

            // 4.2) We then create the hypothesis which involves the countries source of the attacks
            if (!eventsByTargetAndSourceCountry.containsKey(targetCountry)) {
                continue;
            }
            for (final CountryCode sourceCountry : eventsByTargetAndSourceCountry.get(targetCountry).keySet()) {
                // 4.2.1) We apply the same limit in this type of correlations
                if (minEventsValue > 0 && eventsByTargetAndSourceCountry.get(targetCountry).get(sourceCountry) < minEventsValue) {
                    continue;
                }
                // 4.2.2) We ignore the source country undefined
                if (sourceCountry == CountryCode.UNDEFINED) {
                    continue;
                }

                // 4.2.3) In the case of the variables we need to get the minimum common period for these kind of analysis
                for (final SocioeconomicVariable variable : minDatesByCountryAndVariable.get(targetCountry).keySet()) {
                    // We need at least three values on the variable for the target country
                    if (numberOfValuesByCountryAndVariable.get(targetCountry).get(variable) < 3) {
                        continue;
                    }
                    final YearMonth minTargetVariableDate = minDatesByCountryAndVariable.get(targetCountry).get(variable);
                    if (!minDatesByCountryAndVariable.containsKey(sourceCountry)) {
                        continue;
                    }
                    final YearMonth minSourceVariableDate = minDatesByCountryAndVariable.get(sourceCountry).get(variable);
                    final YearMonth minEventsDate = minDateByTargetAndSourceCountry.get(targetCountry).get(sourceCountry);
                    if (minSourceVariableDate == null || minEventsDate == null) {
                        continue;
                    }
                    final YearMonth minVariableDate = minTargetVariableDate.isAfter(minSourceVariableDate) ? minTargetVariableDate.isAfter(minEventsDate) ? minTargetVariableDate : minEventsDate : minSourceVariableDate.isAfter(minEventsDate) ? minSourceVariableDate : minEventsDate;
                    final YearMonth maxTargetVariableDate = maxDatesByCountryAndVariable.get(targetCountry).get(variable);
                    final YearMonth maxSourceVariableDate = maxDatesByCountryAndVariable.get(sourceCountry).get(variable);
                    final YearMonth maxEventsDate = maxDateByTargetAndSourceCountry.get(targetCountry).get(sourceCountry);
                    if (maxSourceVariableDate == null || maxEventsDate == null) {
                        continue;
                    }
                    final YearMonth maxVariableDate = maxTargetVariableDate.isBefore(maxSourceVariableDate) ? maxTargetVariableDate.isBefore(maxEventsDate) ? maxTargetVariableDate : maxEventsDate : maxSourceVariableDate.isBefore(maxEventsDate) ? maxSourceVariableDate : maxEventsDate;
                    // We need at least three values on the variable for the source country
                    if (numberOfValuesByCountryAndVariable.get(sourceCountry).get(variable) < 3) {
                        continue;
                    }
                    if (!maxVariableDate.isAfter(minVariableDate)) {
                        continue;
                    }
                    final CorrelationHypothesis correlationHypothesis = new CorrelationHypothesis();
                    correlationHypothesis.setTargetCountry(targetCountry);
                    correlationHypothesis.setSector(Sector.ROOT);
                    correlationHypothesis.setVariables(new HashSet<>(Arrays.asList(new SocioeconomicVariable[] { variable })));
                    correlationHypothesis.setStartDate(Date.from(minVariableDate.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    correlationHypothesis.setEndDate(Date.from(maxVariableDate.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    if (!existingOnes.contains(correlationHypothesis)) {
                        hypothesis.add(correlationHypothesis);
                    } else {
                        hypothesis.add(existingOnes.get(existingOnes.indexOf(correlationHypothesis)));
                    }
                }
            }
        }
        return hypothesis;
    }

    /**
     * This method generates the time buckets for the regression comparison
     *
     * @param correlation
     *            The correlation for which we are doing the comparison
     * @return A list of the time buckets we need to create
     */
    public List<YearMonth> generateTimeBuckets(CorrelationHypothesis correlation) {
        // 1st) We define the end and start plus the format of the buckets
        YearMonth startDate = YearMonth.from(correlation.getEffectiveStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        final YearMonth endDate = YearMonth.from(correlation.getEffectiveEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        // 2nd) We loop from start to end month by month adding to the bucket
        final List<YearMonth> timeBuckets = new ArrayList<>();
        while (startDate.isBefore(endDate)) {
            timeBuckets.add(startDate);
            startDate = startDate.plusMonths(1);
        }
        timeBuckets.add(endDate);
        
        return timeBuckets;
    }

    /**
     * This method returns an array with the events frequency per time bucket so it can be used for correlations
     *
     * @param events
     *            The list of the events to place in time buckets
     * @param timeBuckets
     *            The time buckets on which place the events
     * @return An array with the frequency of the events per time bucket
     */
    public double[] getEventsDataArray(List<Event> events, List<YearMonth> timeBuckets) {
        final double[] eventsArray = new double[timeBuckets.size()];
        // We loop per each event
        for (final Event event : events) {
            final YearMonth eventBucket = YearMonth.from(event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            // We found where in the events array we need to place it and increment the counter
            final int position = timeBuckets.indexOf(eventBucket);
            eventsArray[position]++;
        }
        return eventsArray;
    }

    /**
     * This method generates an array with the data for the socioeconomic variable interpolating any missing values
     *
     * @param variableValues
     *            All the historical values
     * @param timeBuckets
     *            The list of times for which we need to return the values
     * @param result
     * @return An array with values for each time bucket
     */
    private double[] getValuesArray(List<CountryVariableValue> variableValues, List<YearMonth> timeBuckets, CorrelationResult result) throws OutOfRangeException {
        // 1st) We create the two arrays of known values and dates and a map for the values
        final double[] values = new double[variableValues.size()];
        final double[] dates = new double[variableValues.size()];
        int position = 0;
        final Map<YearMonth, CountryVariableValue> valuesPerBucket = new HashMap<>();
        for (final CountryVariableValue variableValue : variableValues) {
            values[position] = variableValue.getValue();
            dates[position] = variableValue.getDate().getTime();
            position++;
            valuesPerBucket.put(YearMonth.from(variableValue.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()), variableValue);
        }

        // 2nd) We create the interpolation function
        final PolynomialSplineFunction function = new SplineInterpolator().interpolate(dates, values);

        // 3rd) Now we go through the time buckets putting values that we have or asking the function to provide us with values for the unknowns
        final double[] valuesArray = new double[timeBuckets.size()];
        position = 0;
        boolean interpolated = false;
        for (final YearMonth timeBucket : timeBuckets) {
            if (valuesPerBucket.containsKey(timeBucket)) {
                valuesArray[position++] = valuesPerBucket.get(timeBucket).getValue();
            } else {
                try {
                    valuesArray[position++] = function.value(Date.from(timeBucket.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime());
                } catch (final OutOfRangeException e) {
                    LOGGER.debug("The value for the country " + result.getCountry() + " and date " + timeBucket + " is out of the interpolating range", e);
                    valuesArray[position - 1] = e.getArgument().doubleValue();
                }
                interpolated = true;
            }
        }
        
        // 4th) We store in the result the information regarding the interpolation
        result.setInterpolatedData(interpolated);
        return valuesArray;
    }

    /**
     * Returns the existing correlations in the database
     *
     * @return The existing correlations in the database
     */
    public List<CorrelationHypothesis> list() {
        final List<CorrelationHypothesis> correlations = new ArrayList<>();
        correlationHypothesisDAO.findAll().forEach(correlations::add);
        return correlations;
    }

    /**
     * This method returns a list of the events that match the correlation criteria
     *
     * @param correlation
     *            the correlation for which we are trying to obtain the data
     * @return A list with the events that match the given criteria
     */
    public List<Event> retrieveEventData(CorrelationHypothesis correlation) {
        List<Event> events = new ArrayList<>();
        // 1st) First we get the events that match the given dates, target countries and sector
        final LocalDate endDate = new java.sql.Date(correlation.getEndDate().getTime()).toLocalDate();
        final LocalDate lastDay = endDate.with(TemporalAdjusters.lastDayOfMonth());
        events.addAll(eventDAO.findByDateBetweenAndTargetCountry(correlation.getStartDate(), java.sql.Date.valueOf(lastDay), correlation.getEffectiveTargetCountry()).stream().filter(event -> event.getTarget().getOwner().getSector().isChildOf(correlation.getSector())).collect(Collectors.toList()));

        // 2nd) If they have defined a source country then we apply a filter on that as well
        if (correlation.getSourceCountries() != null && !correlation.getSourceCountries().isEmpty()) {
            events = events.stream().filter(event -> {
                boolean contained = false;
                for (final Attacker attacker : event.getAttack().getIncident().getAttackers()) {
                    if (correlation.getSourceCountries().contains(attacker.getCountry())) {
                        contained = true;
                        break;
                    }
                }
                return contained;
            }).collect(Collectors.toList());
        }
        return events;
    }
    
    /**
     * This method returns the socioeconomic values for the correlation given
     *
     * @param correlation
     *            The correlation for which we are retrieving the data
     * @return A map containing as key the country and as value another map which contains as key the variable and as value the values
     */
    private Map<CountryCode, Map<SocioeconomicVariable, List<CountryVariableValue>>> retrieveSocioeconomicData(CorrelationHypothesis correlation) {
        // 1st) We create a single list of countries for which we need to retrieve the data
        final Set<CountryCode> countries = new HashSet<>();
        countries.add(correlation.getEffectiveTargetCountry());
        if (correlation.getEffectiveSourceCountries() != null && !correlation.getEffectiveSourceCountries().isEmpty()) {
            countries.addAll(correlation.getEffectiveSourceCountries());
        }
        
        // 2nd) We loop per each country and retrieve all data of that country and variable (we do not filter by date so we can interpolate better)
        final Map<CountryCode, Map<SocioeconomicVariable, List<CountryVariableValue>>> valuesPerCountryAndVariable = new HashMap<>();
        for (final CountryCode country : countries) {
            final Map<SocioeconomicVariable, List<CountryVariableValue>> valuesPerVariable = new HashMap<>();
            for (final SocioeconomicVariable variable : correlation.getVariables()) {
                valuesPerVariable.put(variable, countryVariableValueDAO.findByCountryAndVariableOrderByDate(country, variable));
            }
            valuesPerCountryAndVariable.put(country, valuesPerVariable);
        }
        return valuesPerCountryAndVariable;
    }
    
    /**
     * Saves an existing correlation to the database
     *
     * @param correlation
     *            The existing correlation
     */
    public void save(CorrelationHypothesis correlation) {
        correlationHypothesisDAO.save(correlation);
    }
}