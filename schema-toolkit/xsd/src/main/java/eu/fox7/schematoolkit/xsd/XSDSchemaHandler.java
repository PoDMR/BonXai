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

package eu.fox7.schematoolkit.xsd;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.parser.XSDParser;
import eu.fox7.schematoolkit.xsd.saxparser.XSDSaxParser;
import eu.fox7.schematoolkit.xsd.writer.XSDWriter;

public class XSDSchemaHandler extends SchemaHandler {
	private static boolean useSaxParser = false;
	
	public XSDSchemaHandler() {}
	public XSDSchemaHandler(XSDSchema schema) {
		super(schema);
	}
	
	@Override
	public void parseSchema(InputStream stream) throws IOException, SchemaToolkitException {
		if (useSaxParser) {
			XSDSaxParser parser = new XSDSaxParser();
			schema = parser.parse(stream);
		} else {
			XSDParser parser = new XSDParser(false, false);
			schema = parser.parse(stream);
		}
	}

	@Override
	public void writeSchema(Writer writer) throws IOException, SchemaToolkitException {
		XSDWriter xsdWriter = new XSDWriter((XSDSchema) schema);
		xsdWriter.writeXSD(writer);
	}
	
	public static void useSaxParser(boolean useSaxParser) {
		XSDSchemaHandler.useSaxParser = useSaxParser;		
	}

}
