package com.damosais.sid.database.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.damosais.sid.database.beans.CountryVariableValue;
import com.damosais.sid.database.beans.SocioeconomicVariable;
import com.damosais.sid.database.dao.CountryVariableValueDAO;
import com.neovisionaries.i18n.CountryCode;

/**
 * This service is responsible of retrieving, creating, deleting and updating any country variable value in the database
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Service
public class CountryVariableValueService {
    @Autowired
    private CountryVariableValueDAO countryVariableValueDao;

    /**
     * Deletes an country variable value from the database
     *
     * @param countryVariableValue
     *            The country variable value to be deleted
     */
    public void delete(CountryVariableValue countryVariableValue) {
        countryVariableValueDao.delete(countryVariableValue);
    }

    /**
     * Returns the existing country variable values in the database
     *
     * @return The existing country variable values in the database
     */
    public List<CountryVariableValue> list() {
        final List<CountryVariableValue> countryVariableValues = new ArrayList<>();
        countryVariableValueDao.findAll().forEach(countryVariableValues::add);
        return countryVariableValues;
    }

    /**
     * Returns the different country variable values for a specific country and date
     *
     * @param country
     *            The selected country
     * @param date
     *            The selected date
     * @return A list of the different country variable values
     */
    public List<CountryVariableValue> listByCountryAndDates(CountryCode country, Date start, Date end) {
        final List<CountryVariableValue> countryVariableValues = new ArrayList<>();
        countryVariableValueDao.findByCountryAndDateBetween(country, start, end).forEach(countryVariableValues::add);
        return countryVariableValues;
    }
    
    /**
     * Returns the different year country variable values for a specific country and variable
     *
     * @param country
     *            The selected country
     * @param variable
     *            The selected variable
     * @return A list of the country variable values ordered by year
     */
    public List<CountryVariableValue> listByCountryAndVariable(CountryCode country, SocioeconomicVariable variable) {
        final List<CountryVariableValue> countryVariableValues = new ArrayList<>();
        countryVariableValueDao.findByCountryAndVariableOrderByDate(country, variable).forEach(countryVariableValues::add);
        return countryVariableValues;
    }

    /**
     * Returns the different country variable values for a specific variable and date
     *
     * @param variable
     *            The selected variable
     * @param date
     *            The selected date
     * @return A list of the different country variable values
     */
    public List<CountryVariableValue> listByVariableAndDate(SocioeconomicVariable variable, Date date) {
        final List<CountryVariableValue> countryVariableValues = new ArrayList<>();
        countryVariableValueDao.findByVariableAndDate(variable, date).forEach(countryVariableValues::add);
        return countryVariableValues;
    }

    /**
     * Saves an existing country variable value to the database
     *
     * @param countryVariableValue
     *            The country variable value to save
     */
    public void save(CountryVariableValue countryVariableValue) {
        countryVariableValueDao.save(countryVariableValue);
    }
}