/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.xsd.saxparser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

public class XSDSaxParser {
	private XSDParserHandler xsdParserHandler = new XSDParserHandler();
	
	public XSDSchema parse(File file) throws IOException, SchemaToolkitException {
	    // creates and returns new instance of SAX-implementation:
	    SAXParserFactory factory = SAXParserFactory.newInstance();
	    factory.setNamespaceAware(true);
	    try {
			// create SAX-parser...
			SAXParser parser = factory.newSAXParser();
			parser.parse(file, xsdParserHandler);
		} catch (ParserConfigurationException e) {
			throw new SchemaToolkitException(e);
		} catch (SAXException e) {
			throw new SchemaToolkitException(e);
		}

		return xsdParserHandler.getSchema();
	}

	public Schema parse(InputStream stream) throws IOException, SchemaToolkitException {
	    // creates and returns new instance of SAX-implementation:
	    SAXParserFactory factory = SAXParserFactory.newInstance();
	    factory.setNamespaceAware(true);
	    try {
			// create SAX-parser...
			SAXParser parser = factory.newSAXParser();
			parser.parse(stream, xsdParserHandler);
		} catch (ParserConfigurationException e) {
			throw new SchemaToolkitException(e);
		} catch (SAXException e) {
			throw new SchemaToolkitException(e);
		}

		return xsdParserHandler.getSchema();
	}
	
}
