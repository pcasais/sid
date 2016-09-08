package com.damosais.sid.parsers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.damosais.sid.database.beans.AccessComplexity;
import com.damosais.sid.database.beans.AccessVector;
import com.damosais.sid.database.beans.Authentication;
import com.damosais.sid.database.beans.CVEDefinition;
import com.damosais.sid.database.beans.Impact;
import com.damosais.sid.database.beans.LossType;
import com.damosais.sid.database.beans.RangeType;
import com.damosais.sid.database.beans.Severity;

/**
 * This class implements the handling of the NVD CVE XML files
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public class CVENVDHandlerImpl implements CVENVDHandler {
    private static final Logger LOGGER = Logger.getLogger(CVENVDHandlerImpl.class);
    private final DateFormat dateFormat;
    private List<CVEDefinition> definitions;
    private CVEDefinition definition;
    private LossType lossType;
    private RangeType rangeType;

    /**
     * The default constructor just initialises the date format
     */
    public CVENVDHandlerImpl() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }
    
    @Override
    public void endEntryNode() {
        definitions.add(definition);
    }
    
    @Override
    public void endLossTypesNode() {
        definition.setLossType(lossType);
        lossType.setDefinition(definition);
    }

    @Override
    public void endRangeNode() {
        definition.setRangeType(rangeType);
        rangeType.setDefinition(definition);
    }
    
    private void getCVSSValues(Attributes attrs) throws SAXException {
        final String baseScore = attrs.getValue("CVSS_base_score");
        if (StringUtils.isNotBlank(baseScore)) {
            try {
                definition.setCvssBaseScore(Float.parseFloat(baseScore));
            } catch (final NumberFormatException e) {
                throw new SAXException("Problem parsing CVSS base score", e);
            }
        }
        final String exploitScore = attrs.getValue("CVSS_exploit_subscore");
        if (StringUtils.isNotBlank(exploitScore)) {
            try {
                definition.setCvssExploitSubscore(Float.parseFloat(exploitScore));
            } catch (final NumberFormatException e) {
                throw new SAXException("Problem parsing CVSS exploit subscore", e);
            }
        }
        final String impactScore = attrs.getValue("CVSS_impact_subscore");
        if (StringUtils.isNotBlank(impactScore)) {
            try {
                definition.setCvssImpactSubscore(Float.parseFloat(impactScore));
            } catch (final NumberFormatException e) {
                throw new SAXException("Problem parsing CVSS impact subscore", e);
            }
        }
    }

    private void getCVSSVector(Attributes attrs) {
        final String cvssVector = attrs.getValue("CVSS_vector");
        if (StringUtils.isNotBlank(cvssVector) && cvssVector.length() > 1) {
            final String[] valuePairs = cvssVector.substring(1, cvssVector.length() - 1).split("/");
            if (valuePairs != null && valuePairs.length > 1) {
                for (final String valuePair : valuePairs) {
                    final String[] pair = valuePair.split(":");
                    processPair(pair);
                }
            }
        }
    }
    
    private void getModified(Attributes attrs) throws SAXException {
        final String modified = attrs.getValue("modified");
        if (StringUtils.isNotBlank(modified)) {
            try {
                definition.setModified(dateFormat.parse(modified));
            } catch (final ParseException e) {
                throw new SAXException("Problem parsing modified date", e);
            }
        }
    }

    private void getName(Attributes attrs) throws SAXException {
        final String name = attrs.getValue("name");
        if (StringUtils.isNotBlank(name)) {
            definition.setName(name);
        } else {
            throw new SAXException("Detected an entry node without name attribute");
        }
    }
    
    @Override
    public List<CVEDefinition> getParsedData() {
        return definitions;
    }

    private void getPublished(Attributes attrs) throws SAXException {
        final String published = attrs.getValue("published");
        if (StringUtils.isNotBlank(published)) {
            try {
                definition.setPublished(dateFormat.parse(published));
            } catch (final ParseException e) {
                throw new SAXException("Problem parsing published date", e);
            }
        }
    }
    
    @Override
    public void handleDescription(String data, Attributes attrs) {
        final String source = attrs.getValue("source");
        if ("cve".equalsIgnoreCase(source)) {
            definition.setCveDesc(data);
        } else if ("nvd".equalsIgnoreCase(source)) {
            definition.setNvdDesc(data);
        } else {
            LOGGER.error("Found description from unknown source '" + source + "'");
        }
    }

    private void processPair(String[] pair) {
        if (pair != null && pair.length == 2) {
            switch (pair[0]) {
                case "AV":
                    definition.setAccessVector(AccessVector.getByXmlCode(pair[1]));
                    break;
                case "AC":
                    definition.setAccessComplexity(AccessComplexity.getByXmlCode(pair[1]));
                    break;
                case "Au":
                    definition.setAuthentication(Authentication.getByXmlCode(pair[1]));
                    break;
                case "C":
                    definition.setConfImpact(Impact.getByXmlCode(pair[1]));
                    break;
                case "I":
                    definition.setIntegImpact(Impact.getByXmlCode(pair[1]));
                    break;
                case "A":
                    definition.setAvailImpact(Impact.getByXmlCode(pair[1]));
                    break;
                default:
                    LOGGER.error("Unrecognized CVSS vector key pair value '" + pair[0] + "'");
            }
        }
    }

    @Override
    public void startAvailLoss() {
        lossType.setAvailability(true);
    }
    
    @Override
    public void startConfLoss() {
        lossType.setConfidentiality(true);
    }

    @Override
    public void startEntry(Attributes attrs) throws SAXException {
        definition = new CVEDefinition();
        // 1st) We read the CVE name
        getName(attrs);
        // 2nd) We read the published date
        getPublished(attrs);
        // 3rd) We read the modified date
        getModified(attrs);
        // 4th) We parse the severity
        definition.setSeverity(Severity.getByDescription(attrs.getValue("severity")));
        // 5th) We now parse the CVSS scores
        getCVSSValues(attrs);
        // 6th) Finally we parse the CVSS vector
        getCVSSVector(attrs);
    }
    
    @Override
    public void startIntLoss() {
        lossType.setIntegrity(true);
    }

    @Override
    public void startLocal() {
        rangeType.setLocal(true);
    }
    
    @Override
    public void startLocalNetwork() {
        rangeType.setLocalNetwork(true);
    }
    
    @Override
    public void startLossTypes() {
        lossType = new LossType();
    }

    @Override
    public void startNetwork() {
        rangeType.setNetwork(true);
    }
    
    @Override
    public void startNvd(Attributes attrs) {
        definitions = new ArrayList<>();
    }

    @Override
    public void startRange() {
        rangeType = new RangeType();
    }
    
    @Override
    public void startSecProtLoss(Attributes attrs) {
        if (StringUtils.isNotBlank(attrs.getValue("admin"))) {
            lossType.setAdminSecurityProtection(true);
        }
        if (StringUtils.isNotBlank(attrs.getValue("user"))) {
            lossType.setUserSecurityProtection(true);
        }
        if (StringUtils.isNotBlank(attrs.getValue("other"))) {
            lossType.setOtherSecurityProtection(true);
        }
    }

    @Override
    public void startUserInit() {
        rangeType.setUserInit(true);
    }
}
