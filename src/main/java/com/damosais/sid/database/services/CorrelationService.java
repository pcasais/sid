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

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.damosais.sid.database.beans.Attacker;
import com.damosais.sid.database.beans.Correlation;
import com.damosais.sid.database.beans.CountryVariableValue;
import com.damosais.sid.database.beans.Event;
import com.damosais.sid.database.beans.SocioeconomicVariable;
import com.damosais.sid.database.dao.CorrelationDAO;
import com.damosais.sid.database.dao.CountryVariableValueDAO;
import com.damosais.sid.database.dao.EventDAO;
import com.neovisionaries.i18n.CountryCode;

/**
 * This service is responsible of retrieving, creating, deleting and updating any correlation in the database
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Service
public class CorrelationService {
    @Autowired
    private CorrelationDAO correlationDao;

    @Autowired
    private EventDAO eventDAO;
    
    @Autowired
    private CountryVariableValueDAO countryVariableValueDAO;

    /**
     * This method calculates the correlation factor
     *
     * @param correlation
     */
    public void computeCorrelationCoeficients(Correlation correlation) {
        // 1st) We need to get the event data
        final List<Event> events = retrieveEventData(correlation);

        // 2nd) We get the socioeconomic data
        final Map<CountryCode, Map<SocioeconomicVariable, List<CountryVariableValue>>> valuesPerCountryAndVariable = retrieveSocioeconomicData(correlation);

        // 3rd) We now need to generate the time buckets for the given period
        final List<YearMonth> timeBuckets = generateTimeBuckets(correlation);
        
        // 4th) We now create the vector for the events using the time buckets
        final double[] eventsArray = getEventsDataArray(events, timeBuckets);
        
        // 5th) Now we loop through the values calculating the correlations between them and the events
        final Map<CountryCode, Map<SocioeconomicVariable, Double>> correlationFactorsPerCountryAndVariable = new HashMap<>();
        for (final CountryCode country : valuesPerCountryAndVariable.keySet()) {
            final Map<SocioeconomicVariable, List<CountryVariableValue>> valuesPerVariable = valuesPerCountryAndVariable.get(country);
            final Map<SocioeconomicVariable, Double> correlationFactorsPerVariable = new HashMap<>();
            for (final SocioeconomicVariable variable : valuesPerVariable.keySet()) {
                // 5.1) First we generate a vector for the values of that variable
                final double[] valuesArray = getValuesArray(valuesPerVariable.get(variable), timeBuckets);
                // 5.2) Then we calculate the correlation factor of both arrays and store it
                correlationFactorsPerVariable.put(variable, new PearsonsCorrelation().correlation(eventsArray, valuesArray));
            }
            // 5.3) Then we store the results per country
            correlationFactorsPerCountryAndVariable.put(country, correlationFactorsPerVariable);
        }
    }

    /**
     * Deletes a correlation from the database
     *
     * @param correlation
     *            The correlation to be deleted
     */
    public void delete(Correlation correlation) {
        correlationDao.delete(correlation);
    }

    /**
     * This method generates the time buckets for the regression comparison
     *
     * @param correlation
     *            The correlation for which we are doing the comparison
     * @return A list of the time buckets we need to create
     */
    private List<YearMonth> generateTimeBuckets(Correlation correlation) {
        // 1st) We define the end and start plus the format of the buckets
        YearMonth startDate = YearMonth.from(correlation.getStartDate().toInstant());
        final YearMonth endDate = YearMonth.from(correlation.getEndDate().toInstant());

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
            final YearMonth eventBucket = YearMonth.from(event.getDate().toInstant());
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
     * @return An array with values for each time bucket
     */
    private double[] getValuesArray(List<CountryVariableValue> variableValues, List<YearMonth> timeBuckets) {
        // 1st) We create the two arrays of known values and dates
        final double[] values = new double[variableValues.size()];
        final double[] dates = new double[variableValues.size()];
        int position = 0;
        for (final CountryVariableValue variableValue : variableValues) {
            values[position] = variableValue.getValue();
            dates[position] = variableValue.getDate().getTime();
            position++;
        }

        // 2nd) We create the interpolation function
        final UnivariateFunction function = new SplineInterpolator().interpolate(dates, values);

        // 3rd) Now we go through the time buckets asking the function to provide us with values for each date
        final double[] valuesArray = new double[timeBuckets.size()];
        position = 0;
        for (final YearMonth timeBucket : timeBuckets) {
            valuesArray[position++] = function.value(Date.from(timeBucket.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime());
        }
        return valuesArray;
    }

    /**
     * Returns the existing correlations in the database
     *
     * @return The existing correlations in the database
     */
    public List<Correlation> list() {
        final List<Correlation> correlations = new ArrayList<>();
        correlationDao.findAll().forEach(correlations::add);
        return correlations;
    }

    /**
     * This method returns a list of the events that match the correlation criteria
     *
     * @param correlation
     *            the correlation for which we are trying to obtain the data
     * @return A list with the events that match the given criteria
     */
    private List<Event> retrieveEventData(Correlation correlation) {
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
    private Map<CountryCode, Map<SocioeconomicVariable, List<CountryVariableValue>>> retrieveSocioeconomicData(Correlation correlation) {
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
    public void save(Correlation correlation) {
        correlationDao.save(correlation);
    }
}