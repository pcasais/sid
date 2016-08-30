package com.damosais.sid.database.dao;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import com.damosais.sid.database.beans.CountryVariableValue;
import com.damosais.sid.database.beans.SocioeconomicVariable;
import com.neovisionaries.i18n.CountryCode;

/**
 * This interface expresses the database operations that can be performed on an attacker
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Transactional
public interface CountryVariableValueDAO extends CrudRepository<CountryVariableValue, Long> {

    /**
     * Returns all the socioeconomic variable values for a specific country between the specific dates
     *
     * @param country
     *            The selected country
     * @param start
     *            The start date
     * @param end
     *            The ened date
     * @return A list of the different variable values
     */
    public List<CountryVariableValue> findByCountryAndDateBetween(CountryCode country, Date start, Date end);

    /**
     * Returns one socioeconomic variable values of a specific country sorted by year
     *
     * @param country
     *            the country for which we are trying to retrieve the data
     * @param variable
     *            The socioeconomic variable
     * @return A list of values ordered by year
     */
    public List<CountryVariableValue> findByCountryAndVariableOrderByDate(CountryCode country, SocioeconomicVariable variable);
    
    /**
     * Returns the socioeconomic variable values of all countries for a specific year
     *
     * @param variable
     *            The socioeconomic variable
     * @param date
     *            The year for which we are looking the values
     * @return A list of the different country values
     */
    public List<CountryVariableValue> findByVariableAndDate(SocioeconomicVariable variable, Date date);
}