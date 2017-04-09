package com.damosais.sid.database.services;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.MathRuntimeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.damosais.sid.database.beans.Attacker;
import com.damosais.sid.database.beans.CorrelationHypothesis;
import com.damosais.sid.database.beans.CorrelationResult;
import com.damosais.sid.database.beans.CountryVariableValue;
import com.damosais.sid.database.beans.Event;
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

/**
 * This service is responsible of retrieving, creating, deleting and updating any correlation in the database
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Service
public class CorrelationHypothesisService {
    @Autowired
    private CorrelationResultDAO correlationResultDao;
    
    @Autowired
    private CorrelationHypothesisDAO correlationHypothesisDao;
    
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
                } catch (final OutOfRangeException e) {
                    errors.add("Problem with the correlation calculation for variable " + variable.getName() + " on " + country.getName() + ": interpolation of data for period " + timeBuckets.get(0) + " - " + timeBuckets.get(timeBuckets.size() - 1));
                }
                correlationResultDao.save(result);
                if (newItem) {
                    results.add(result);
                }
            }
        }
        correlationHypothesis.setResults(results);
        correlationHypothesisDao.save(correlationHypothesis);
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
        correlationHypothesisDao.delete(correlation);
    }
    
    /**
     * This method generates the time buckets for the regression comparison
     *
     * @param correlation
     *            The correlation for which we are doing the comparison
     * @return A list of the time buckets we need to create
     */
    private List<YearMonth> generateTimeBuckets(CorrelationHypothesis correlation) {
        // 1st) We define the end and start plus the format of the buckets
        YearMonth startDate = YearMonth.from(correlation.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        final YearMonth endDate = YearMonth.from(correlation.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        
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
    private double[] getEventsDataArray(List<Event> events, List<YearMonth> timeBuckets) {
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
                valuesArray[position++] = function.value(Date.from(timeBucket.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime());
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
        correlationHypothesisDao.findAll().forEach(correlations::add);
        return correlations;
    }
    
    /**
     * This method returns a list of the events that match the correlation criteria
     *
     * @param correlation
     *            the correlation for which we are trying to obtain the data
     * @return A list with the events that match the given criteria
     */
    private List<Event> retrieveEventData(CorrelationHypothesis correlation) {
        List<Event> events = new ArrayList<>();
        // 1st) First we get the events that match the given dates, target countries and sector
        for (final CountryCode country : correlation.getTargetCountries()) {
            events.addAll(eventDAO.findByDateBetweenAndTargetCountry(correlation.getStartDate(), correlation.getEndDate(), country).stream().filter(event -> event.getTarget().getOwner().getSector().isChildOf(correlation.getSector())).collect(Collectors.toList()));
        }
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
        countries.addAll(correlation.getTargetCountries());
        if (correlation.getSourceCountries() != null && !correlation.getSourceCountries().isEmpty()) {
            countries.addAll(correlation.getSourceCountries());
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
        correlationHypothesisDao.save(correlation);
    }
}