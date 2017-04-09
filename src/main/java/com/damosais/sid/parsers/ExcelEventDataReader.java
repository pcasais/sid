package com.damosais.sid.parsers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.damosais.sid.database.beans.Action;
import com.damosais.sid.database.beans.Attack;
import com.damosais.sid.database.beans.Attacker;
import com.damosais.sid.database.beans.AttackerType;
import com.damosais.sid.database.beans.Event;
import com.damosais.sid.database.beans.Incident;
import com.damosais.sid.database.beans.Motivation;
import com.damosais.sid.database.beans.Owner;
import com.damosais.sid.database.beans.Sector;
import com.damosais.sid.database.beans.Target;
import com.damosais.sid.database.beans.Tool;
import com.damosais.sid.database.beans.ToolType;
import com.damosais.sid.database.beans.UnathourizedResultType;
import com.damosais.sid.database.beans.UnauthorizedResult;
import com.damosais.sid.webapp.windows.ImportEventDataWindow;
import com.neovisionaries.i18n.CountryCode;

/**
 * This class implements the methods to parse an Excel document into a list of events with all the attached information
 *
 * @author Pablo Casais
 */
public class ExcelEventDataReader extends ExcelReader {
    private static final Logger LOGGER = Logger.getLogger(ExcelEventDataReader.class);
    private static final String THE_OBJECT_IN_ROW = "The object in row ";
    private static final String HAS_AN_INVALID = " has a blank or invalid ";
    private static final String SKIPPING_ROW = ". Skipping row";
    private static final String ITEM_SEPARATOR = ";";
    private static final String TRUE = "true";

    /**
     * This method returns the attack corresponding to a row
     *
     * @param rowNumber
     *            The row number being parsed
     * @param rowContents
     *            The map with the columns and their values for the row
     * @param toolsByName
     *            A map with the tools indexed by name
     * @return The attack object in this row
     */
    private Attack parseAttackFiels(int rowNumber, Map<String, Object> rowContents, Map<String, Tool> toolsByName) {
        final Attack attack = new Attack();
        // 1st) We try to match the tool details
        final Object toolNameRaw = rowContents.get(ImportEventDataWindow.TOOL_NAME_FIELD);
        if (isObjectANonEmptyString(toolNameRaw)) {
            Tool tool = toolsByName.get(StringUtils.trim((String) toolNameRaw));
            if (tool == null) {
                // 1.1) If the tool is not in the system we create it
                tool = new Tool();
                tool.setName(StringUtils.trim((String) toolNameRaw));
                final Object toolTypeRaw = rowContents.get(ImportEventDataWindow.TOOL_TYPE_FIELD);
                if (isObjectANonEmptyString(toolTypeRaw)) {
                    tool.setType(ToolType.getByName(StringUtils.trim((String) toolTypeRaw)));
                }
                if (tool.getType() == null) {
                    LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.TOOL_TYPE_FIELD);
                }
                // 1.2) We then ad the tool to the map
                toolsByName.put(tool.getName(), tool);
            }
            attack.setTool(tool);
        } else {
            LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.TOOL_NAME_FIELD);
        }
        
        // 2nd) We then parse the results of the attack
        final UnauthorizedResult result = new UnauthorizedResult();
        // 2.1) The admin access flag
        final Object adminAccessRaw = rowContents.get(ImportEventDataWindow.ADMIN_ACCESS_FIELD);
        if (adminAccessRaw != null && adminAccessRaw instanceof Boolean) {
            result.setAdminAccess((Boolean) adminAccessRaw);
        } else if (isObjectANonEmptyString(adminAccessRaw)) {
            result.setAdminAccess(TRUE.equalsIgnoreCase(StringUtils.trim((String) adminAccessRaw)));
        } else {
            LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.ADMIN_ACCESS_FIELD);
        }
        // 2.2) The average traffic for DDoS attacks
        final Object averageTrafficRaw = rowContents.get(ImportEventDataWindow.AVERAGE_TRAFFIC_FIELD);
        if (averageTrafficRaw != null && averageTrafficRaw instanceof Double) {
            result.setAverageTraffic((Double) averageTrafficRaw);
        } else if (isObjectANonEmptyString(averageTrafficRaw)) {
            try {
                result.setAverageTraffic(Double.parseDouble(StringUtils.trim((String) averageTrafficRaw)));
            } catch (final NumberFormatException e) {
                LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.AVERAGE_TRAFFIC_FIELD, e);
            }
        } else {
            LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.AVERAGE_TRAFFIC_FIELD);
        }
        // 2.3) The down time cause by a DDoS attack
        final Object downTimeRaw = rowContents.get(ImportEventDataWindow.DOWNTIME_FIELD);
        if (downTimeRaw != null && downTimeRaw instanceof Date) {
            result.setDownTime(((Date) downTimeRaw).getTime());
        } else if (downTimeRaw != null && downTimeRaw instanceof Double) {
            result.setDownTime(((Double) downTimeRaw).longValue());
        } else if (isObjectANonEmptyString(downTimeRaw)) {
            try {
                result.setDownTime(Long.parseLong(StringUtils.trim((String) downTimeRaw)));
            } catch (final NumberFormatException e) {
                LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.DOWNTIME_FIELD, e);
            }
        } else {
            LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.DOWNTIME_FIELD);
        }
        // 2.4) Now we get the economic impact of the attack
        final Object economicImpactRaw = rowContents.get(ImportEventDataWindow.ECONOMIC_IMPACT_FIELD);
        if (economicImpactRaw != null && economicImpactRaw instanceof Double) {
            result.setEconomicImpact((Double) economicImpactRaw);
        } else if (isObjectANonEmptyString(economicImpactRaw)) {
            try {
                result.setEconomicImpact(Double.parseDouble(StringUtils.trim((String) economicImpactRaw)));
            } catch (final NumberFormatException e) {
                LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.ECONOMIC_IMPACT_FIELD, e);
            }
        } else {
            LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.ECONOMIC_IMPACT_FIELD);
        }
        // 2.5) We then get the number of registers that have been obtained in a leak
        final Object numRegistersRaw = rowContents.get(ImportEventDataWindow.REGISTERS_FIELD);
        if (numRegistersRaw != null && numRegistersRaw instanceof Double) {
            result.setNumRegisters(((Double) numRegistersRaw).longValue());
        } else if (isObjectANonEmptyString(numRegistersRaw)) {
            try {
                result.setNumRegisters(Long.parseLong(StringUtils.trim((String) numRegistersRaw)));
            } catch (final NumberFormatException e) {
                LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.REGISTERS_FIELD, e);
            }
        } else {
            LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.REGISTERS_FIELD);
        }
        // 2.6) Now we get the peak traffic for DDoS attacks
        final Object peakTrafficRaw = rowContents.get(ImportEventDataWindow.PEAK_TRAFFIC_FIELD);
        if (peakTrafficRaw != null && peakTrafficRaw instanceof Double) {
            result.setPeakTraffic((Double) peakTrafficRaw);
        } else if (isObjectANonEmptyString(peakTrafficRaw)) {
            try {
                result.setPeakTraffic(Double.parseDouble(StringUtils.trim((String) peakTrafficRaw)));
            } catch (final NumberFormatException e) {
                LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.PEAK_TRAFFIC_FIELD, e);
            }
        } else {
            LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.PEAK_TRAFFIC_FIELD);
        }
        // 2.7) We now parse the type of result of the attack
        final Object typeRaw = rowContents.get(ImportEventDataWindow.UNAUTHORISED_TYPE_FIELD);
        if (isObjectANonEmptyString(typeRaw)) {
            result.setType(UnathourizedResultType.getByDescription(StringUtils.trim((String) typeRaw)));
        } else {
            LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.UNAUTHORISED_TYPE_FIELD);
        }
        // 2.8) Finally we read the user access flag
        final Object userAccessRaw = rowContents.get(ImportEventDataWindow.USER_ACCESS_FIELD);
        if (userAccessRaw != null && userAccessRaw instanceof Boolean) {
            result.setUserAccess((Boolean) userAccessRaw);
        } else if (isObjectANonEmptyString(userAccessRaw)) {
            result.setUserAccess(TRUE.equalsIgnoreCase(StringUtils.trim((String) userAccessRaw)));
        } else {
            LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.USER_ACCESS_FIELD);
        }
        
        // 2.9) Finally we set the unauthorised results
        attack.setUnauthorizedResults(result);
        attack.setEvents(new HashSet<>());
        
        return attack;
    }
    
    /**
     * This method parses the fields related to an event
     *
     * @param rowNumber
     *            The number of the row being parsed
     * @param rowContents
     *            A map with the columns and their values for this row
     * @param ownersByName
     *            A map with owners indexed by name
     * @param targetsBySiteName
     *            A map with targets indexed by site name
     * @return The event object in the row or null if is invalid
     */
    private Event parseEventFields(int rowNumber, Map<String, Object> rowContents, Map<String, Owner> ownersByName, Map<String, Target> targetsBySiteName) {
        final Event event = new Event();
        // 1st) We parse is the date which is mandatory
        boolean error = false;
        final Object dateRaw = rowContents.get(ImportEventDataWindow.DATE_FIELD);
        if (dateRaw != null && dateRaw instanceof Date) {
            event.setDate((Date) dateRaw);
        } else if (isObjectANonEmptyString(dateRaw)) {
            try {
                event.setDate(fullDate.parse(StringUtils.trim((String) dateRaw)));
            } catch (final ParseException e) {
                LOGGER.error(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.DATE_FIELD + ". The valid format is: " + FULL_DATE_FORMAT + SKIPPING_ROW, e);
                error = true;
            }
        } else {
            LOGGER.error(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.DATE_FIELD + SKIPPING_ROW);
            error = true;
        }

        // 2nd) We parse the action which is optional
        final Object actionRaw = rowContents.get(ImportEventDataWindow.ACTION_FIELD);
        if (isObjectANonEmptyString(actionRaw)) {
            event.setAction(Action.getActionByRepresentation(StringUtils.trim((String) actionRaw)));
        } else {
            LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.ACTION_FIELD);
        }

        // 3rd) We now parse the owner of the target which is mandatory
        final Object ownerNameRaw = rowContents.get(ImportEventDataWindow.OWNER_NAME_FIELD);
        Owner owner = null;
        if (isObjectANonEmptyString(ownerNameRaw)) {
            owner = ownersByName.get(StringUtils.trim((String) ownerNameRaw));
            if (owner == null) {
                // 3.1) In this case the owner is new and we have to read the values where the name, country and sector are mandatory
                owner = new Owner();
                owner.setName(StringUtils.trim((String) ownerNameRaw));
                final Object ownerCountryRaw = rowContents.get(ImportEventDataWindow.OWNER_COUNTRY_FIELD);
                if (isObjectANonEmptyString(ownerCountryRaw)) {
                    owner.setCountry(CountryCode.getByCode(StringUtils.trim((String) ownerCountryRaw), false));
                }
                if (owner.getCountry() == null) {
                    LOGGER.error(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.OWNER_COUNTRY_FIELD + SKIPPING_ROW);
                    error = true;
                }
                final Object ownerSectorRaw = rowContents.get(ImportEventDataWindow.OWNER_SECTOR_FIELD);
                if (isObjectANonEmptyString(ownerSectorRaw)) {
                    owner.setSector(Sector.getByName(StringUtils.trim((String) ownerSectorRaw)));
                }
                if (owner.getSector() == null) {
                    LOGGER.error(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.OWNER_SECTOR_FIELD + SKIPPING_ROW);
                    error = true;
                }
                // 3.2) If the owner was parsed correctly we add it to the map to reduce duplications
                if (owner.getCountry() != null && owner.getSector() != null) {
                    ownersByName.put(owner.getName(), owner);
                }
            }
        } else {
            LOGGER.error(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.OWNER_NAME_FIELD + SKIPPING_ROW);
            error = true;
        }

        // 4th) We now parse the target of the event
        final Object targetSiteNameRaw = rowContents.get(ImportEventDataWindow.SITE_NAME_FIELD);
        if (isObjectANonEmptyString(targetSiteNameRaw)) {
            Target target = targetsBySiteName.get(StringUtils.trim((String) targetSiteNameRaw));
            if (target == null) {
                // 4.1) In this case the target is new and we have to read the values where all are optional
                target = new Target();
                target.setSiteName(StringUtils.trim((String) targetSiteNameRaw));
                target.setOwner(owner);
                final Object targetIpsRaw = rowContents.get(ImportEventDataWindow.IPS_FIELD);
                if (isObjectANonEmptyString(targetIpsRaw)) {
                    target.setIps(Arrays.asList(((String) targetIpsRaw).split(ITEM_SEPARATOR)));
                } else {
                    LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + " has no IPs assigned.");
                }
                final Object targetCountryRaw = rowContents.get(ImportEventDataWindow.SITE_COUNTRY_FIELD);
                if (isObjectANonEmptyString(targetCountryRaw)) {
                    target.setCountry(CountryCode.getByCode(StringUtils.trim((String) targetCountryRaw), false));
                }
                if (target.getCountry() == null) {
                    LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + " has no country assigned to the site.");
                }
            }
            event.setTarget(target);
        } else {
            LOGGER.error(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.SITE_NAME_FIELD + SKIPPING_ROW);
            error = true;
        }
        
        // If we didn't had any fatal error then we returned the parsed object
        if (!error) {
            event.setRowNumber(rowNumber);
            return event;
        } else {
            return null;
        }
    }
    
    /**
     * This method parses the incident fields of a row
     *
     * @param rowNumber
     *            The number of the row
     * @param rowContents
     *            A map with the columns and their values for this row
     * @param incidentsByName
     *            The incidents indexed by name
     * @param attackersByName
     *            The attackers indexed by name
     * @return The incident corresponding to that row
     */
    private Incident parseIncidentFields(int rowNumber, Map<String, Object> rowContents, Map<String, Incident> incidentsByName, Map<String, Attacker> attackersByName) {
        // 1st) We get the incident name and try to find if it was already defined
        final Object incidentNameRaw = rowContents.get(ImportEventDataWindow.INCIDENT_NAME_FIELD);
        Incident incident = incidentsByName.get(incidentNameRaw);
        if (incident != null) {
            return incident;
        }
        
        // 2nd) If the incident hasn't been recorded then we create a new one
        incident = new Incident();
        incident.setName(StringUtils.trim((String) incidentNameRaw));
        
        // 3rd) Now we try to map the motivation
        final Object motivationRaw = rowContents.get(ImportEventDataWindow.MOTIVATION_FIELD);
        if (isObjectANonEmptyString(motivationRaw)) {
            incident.setMotivation(Motivation.getByName((String) motivationRaw));
        }
        if (incident.getMotivation() == null) {
            LOGGER.warn(THE_OBJECT_IN_ROW + rowNumber + HAS_AN_INVALID + ImportEventDataWindow.MOTIVATION_FIELD);
        }
        
        // 4th) We now handle the attacker(s)
        incident.setAttackers(new HashSet<>());
        // 4.1) First we get the type of the attackers
        final Object attackerTypeRaw = rowContents.get(ImportEventDataWindow.ATTACKER_TYPE_FIELD);
        AttackerType attackerType = null;
        if (isObjectANonEmptyString(attackerTypeRaw)) {
            attackerType = AttackerType.getByDescription(StringUtils.trim((String) attackerTypeRaw));
        }
        // 4.2) We then get the country of the attackers
        final Object attackerCountryRaw = rowContents.get(ImportEventDataWindow.ATTACKER_COUNTRY_FIELD);
        CountryCode attackerCountry = null;
        if (isObjectANonEmptyString(attackerCountryRaw)) {
            attackerCountry = CountryCode.getByCode(StringUtils.trim((String) attackerCountryRaw), false);
        }
        
        // 4.3) Finally we read the names of the attackers and create the corresponding ones if needed
        final Object attackerNamesRaw = rowContents.get(ImportEventDataWindow.ATTACKER_NAME_FIELD);
        if (isObjectANonEmptyString(attackerNamesRaw)) {
            for (final String attackerName : ((String) attackerNamesRaw).split(ITEM_SEPARATOR)) {
                Attacker attacker = attackersByName.get(StringUtils.trim(attackerName));
                if (attacker == null) {
                    // If the attacker didn't exist we create it
                    attacker = new Attacker();
                    attacker.setName(StringUtils.trim(attackerName));
                    attacker.setCountry(attackerCountry != null ? attackerCountry : CountryCode.UNDEFINED);
                    attacker.setType(attackerType != null ? attackerType : AttackerType.UNKNOWN);
                    attackersByName.put(attackerName, attacker);
                }
                incident.getAttackers().add(attacker);
            }
        }
        return incident;
    }

    /**
     * This method creates an event based on the data of the row
     *
     * @param rowNumber
     * @param row
     *            The row being processed
     * @param columnMap
     *            The map with the column numbers and the data it contains
     * @param incidentsByName
     *            The list of incidents parsed so far that have a name
     * @return An event with the associated data
     * @throws ParseException
     *             If there's a problem parsing the downtime
     */
    private Event processContentRow(int rowNumber, Row row, Map<Integer, String> columnMap, Map<String, Owner> ownersByName, Map<String, Target> targetsByName, Map<String, Tool> toolsByName, Map<String, Incident> incidentsByName, Map<String, Attacker> attackersByName) {
        // 1st) We get the content of the row
        final Map<String, Object> rowContents = readRowContent(row, columnMap);

        // 2nd) Now we populate the event data
        final Event event = parseEventFields(rowNumber, rowContents, ownersByName, targetsByName);
        
        // 3rd) After that we need to create the attack data
        if (event != null) {
            event.setAttack(parseAttackFiels(rowNumber, rowContents, toolsByName));
        }
        
        // 4th) We then add the incident details
        if (event != null) {
            event.getAttack().setIncident(parseIncidentFields(rowNumber, rowContents, incidentsByName, attackersByName));
        }
        
        return event;
    }
    
    /**
     * This method reads the values from a file and processes them to insert them in the database
     *
     * @param sheetName
     *            The name of the sheet in the document
     * @param mappingValues
     *            A mapping with the column as key and the value to which it maps as value
     * @param existingOwners
     *            A list with the current owners in the system
     * @param existingTargets
     *            A list with the current targets in the system
     * @param existingTools
     *            A list with the current tools in the system
     * @param existingIncidents
     *            A list with the existing incidents in the system
     * @param existingAttackers
     *            A list with the existing attackers in the system
     * @param existingEvents
     *            A list with the existing events in the system
     * @return A list with the events read from it
     */
    public List<Event> readAndProcessValues(String sheetName, Map<String, String> mappingValues, List<Owner> existingOwners, List<Target> existingTargets, List<Tool> existingTools, List<Incident> existingIncidents, List<Attacker> existingAttackers, List<Event> existingEvents) {
        // 1st) We create maps of the existing elements to avoid duplication
        final Map<String, Owner> ownersByName = new HashMap<>(existingOwners.stream().collect(Collectors.toMap(Owner::getName, Function.identity())));
        final Map<String, Target> targetsBySiteName = new HashMap<>(existingTargets.stream().collect(Collectors.toMap(Target::getSiteName, Function.identity())));
        final Map<String, Tool> toolsByName = new HashMap<>(existingTools.stream().collect(Collectors.toMap(Tool::getName, Function.identity())));
        final Map<String, Incident> incidentsByName = new HashMap<>(existingIncidents.stream().collect(Collectors.toMap(Incident::getName, Function.identity())));
        final Map<String, Attacker> attackersByName = new HashMap<>(existingAttackers.stream().collect(Collectors.toMap(Attacker::getName, Function.identity())));
        
        // 2nd) Now we start processing the rows
        final List<Event> events = new ArrayList<>();
        final XSSFSheet sheet = workbook.getSheet(sheetName);
        final Map<Integer, String> columnMap = new HashMap<>();
        final Iterator<Row> rowIterator = sheet.iterator();
        int rowNumber = 0;
        while (rowIterator.hasNext()) {
            if (rowNumber == 0) {
                // 2.1) If is the header row then we map the position of every column name
                columnMap.putAll(processHeaderRow(rowIterator.next(), mappingValues));
            } else {
                // 2.2) Otherwise we process the row to get the event that it represents it
                final Event parsedEvent = processContentRow(rowNumber, rowIterator.next(), columnMap, ownersByName, targetsBySiteName, toolsByName, incidentsByName, attackersByName);
                // 2.3) And we need to check that the event doesn't already exists before adding it
                if (parsedEvent != null && !existingEvents.contains(parsedEvent)) {
                    events.add(parsedEvent);
                }
            }
            rowNumber++;
        }
        return events;
    }
}