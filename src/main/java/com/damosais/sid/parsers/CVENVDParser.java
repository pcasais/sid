package com.damosais.sid.parsers;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

import com.damosais.sid.database.beans.CVEDefinition;

/**
 * This is the parser for a CVE Definition from a NVD XML file
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public class CVENVDParser implements ContentHandler {
    private static final String RANGE_NODE = "range";
    private static final Logger LOGGER = Logger.getLogger(CVENVDParser.class);
    private static final String UNEXPECTED_CHARS_ERROR = "Unexpected characters() event!";
    private static final String NVD_NODE = "nvd";
    private static final String ENTRY_NODE = "entry";
    private static final String LOSS_TYPES_NODE = "loss_types";
    private final XMLReader parser;
    private CVENVDHandler handler;
    private Deque<Object[]> context;
    private StringBuilder buffer;

    /**
     * The constructor initialises the XML parser
     *
     * @throws SAXException
     *             if there is a problem creating the parser
     */
    public CVENVDParser() throws SAXException {
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(false);
            parser = factory.newSAXParser().getXMLReader();
        } catch (final Exception e) {
            throw new SAXException("Problem initialising parser of CVE NVD files", e);
        }
    }

    @Override
    public final void characters(char[] chars, int start, int len) throws SAXException {
        buffer.append(chars, start, len);
    }

    private void dispatch(final boolean fireOnlyIfMixed) throws SAXException {
        if (fireOnlyIfMixed && buffer.length() == 0) {
            // skip it
            return;
        }
        final Object[] ctx = context.peek();
        final String here = (String) ctx[0];
        final org.xml.sax.Attributes attrs = (org.xml.sax.Attributes) ctx[1];
        if ("descript".equals(here)) {
            if (fireOnlyIfMixed) {
                throw new IllegalStateException(UNEXPECTED_CHARS_ERROR);
            }
            handler.handleDescription(buffer.length() == 0 ? null : buffer.toString(), attrs);
        }
        buffer.delete(0, buffer.length());
    }

    @Override
    public final void endDocument() throws SAXException {
        // Nothing to do when the document is finished
    }

    @Override
    public final void endElement(String ns, String name, String qname) throws SAXException {
        dispatch(false);
        context.pop();
        if (ENTRY_NODE.equals(qname)) {
            handler.endEntryNode();
        } else if (LOSS_TYPES_NODE.equals(qname)) {
            handler.endLossTypesNode();
        } else if (RANGE_NODE.equals(qname)) {
            handler.endRangeNode();
        }
    }

    @Override
    public final void endPrefixMapping(String prefix) throws SAXException {
        // Do nothing
    }

    protected ErrorHandler getDefaultErrorHandler() {
        return new ErrorHandler() {
            @Override
            public void error(SAXParseException ex) throws SAXException {
                if (context.isEmpty()) {
                    LOGGER.error("Missing DOCTYPE.");
                }
                throw ex;
            }

            @Override
            public void fatalError(SAXParseException ex) throws SAXException {
                throw ex;
            }

            @Override
            public void warning(SAXParseException ex) throws SAXException {
                // ignore
            }
        };
    }

    @Override
    public final void ignorableWhitespace(char[] chars, int start, int len) throws SAXException {
        // Do nothing
    }

    /**
     * Parses an XML file returning the list of CVE definitions contained on it
     *
     * @param xmlFile
     *            The XML file
     * @return A list of CVE definitions contained in the file
     * @throws SAXException
     *             If there is a problem reading the data
     */
    public List<CVEDefinition> parse(File xmlFile) throws SAXException {
        handler = new CVENVDHandlerImpl();
        buffer = new StringBuilder(111);
        context = new ArrayDeque<>();
        parser.setContentHandler(this);
        parser.setErrorHandler(getDefaultErrorHandler());
        try {
            parser.parse(new InputSource(xmlFile.toURI().toURL().toExternalForm()));
            return handler.getParsedData();
        } catch (final Exception e) {
            throw new SAXException("Problem parsing xml file", e);
        }
    }

    @Override
    public final void processingInstruction(String target, String data) throws SAXException {
        // Do nothing
    }

    @Override
    public final void setDocumentLocator(Locator locator) {
        // Do nothing
    }

    @Override
    public final void skippedEntity(String name) throws SAXException {
        // Do nothing
    }

    @Override
    public final void startDocument() throws SAXException {
        // Do nothing
    }

    @Override
    public final void startElement(String ns, String name, String qname, Attributes attrs) throws SAXException {
        dispatch(true);
        context.push(new Object[] { qname, new AttributesImpl(attrs) });
        if (NVD_NODE.equals(qname)) {
            handler.startNvd(attrs);
        } else if (ENTRY_NODE.equals(qname)) {
            handler.startEntry(attrs);
        } else if (LOSS_TYPES_NODE.equals(qname)) {
            handler.startLossTypes();
        } else if ("avail".equals(qname)) {
            handler.startAvailLoss();
        } else if ("conf".equals(qname)) {
            handler.startConfLoss();
        } else if ("int".equals(qname)) {
            handler.startIntLoss();
        } else if ("sec_prot".equals(qname)) {
            handler.startSecProtLoss(attrs);
        } else if (RANGE_NODE.equals(qname)) {
            handler.startRange();
        } else if ("local".equals(qname)) {
            handler.startLocal();
        } else if ("local_network".equals(qname)) {
            handler.startLocalNetwork();
        } else if ("network".equals(qname)) {
            handler.startNetwork();
        } else if ("user_init".equals(qname)) {
            handler.startUserInit();
        }
    }

    @Override
    public final void startPrefixMapping(String prefix, String uri) throws SAXException {
        // Do nothing
    }
}