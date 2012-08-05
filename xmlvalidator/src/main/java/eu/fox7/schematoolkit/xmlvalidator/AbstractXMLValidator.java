package eu.fox7.schematoolkit.xmlvalidator;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.fox7.flt.automata.impl.sparse.AnnotatedStateNFA;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.XMLValidator;

public abstract class AbstractXMLValidator implements XMLValidator {
	protected abstract AnnotatedStateNFA<? extends StateNFA, ?> getContextAutomaton(String namespace);
	private Collection<XMLParser.ParsedElement> elements;
	
	public class ContextAutomatonProvider {
		public AnnotatedStateNFA<? extends StateNFA, ?> getContextAutomaton(String namespace) {
			return AbstractXMLValidator.this.getContextAutomaton(namespace);
		}
	}
	
	@Override
	public boolean validate(File file) throws SchemaToolkitException, IOException {
		XMLParser xmlParser = new XMLParser(new ContextAutomatonProvider());

        // creates and returns new instance of SAX-implementation:
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
			// create SAX-parser...
			SAXParser parser = factory.newSAXParser();
			parser.parse(file, xmlParser);
		} catch (ParserConfigurationException e) {
			throw new SchemaToolkitException(e);
		} catch (SAXException e) {
			throw new SchemaToolkitException(e);
		}
		elements = xmlParser.getElements();
		return xmlParser.isValid();
	}

	@Override
	public boolean validate(String xmlString) throws SchemaToolkitException {
		XMLParser xmlParser = new XMLParser(new ContextAutomatonProvider());

        // creates and returns new instance of SAX-implementation:
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
    		StringReader reader = new StringReader(xmlString);
    		InputSource is = new InputSource(reader);
			SAXParser parser = factory.newSAXParser();
			parser.parse(is, xmlParser);
			
        } catch (ParserConfigurationException e) {
			throw new SchemaToolkitException(e);
		} catch (SAXException e) {
			throw new SchemaToolkitException(e);
		} catch (IOException e) {
			throw new SchemaToolkitException(e);
		}
		elements = xmlParser.getElements();
		return xmlParser.isValid();
	}

	
	
	public Collection<XMLParser.ParsedElement> getElements() {
		return elements;
	}
}
