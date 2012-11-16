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

package eu.fox7.schematoolkit.dtd;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.xml.sax.SAXException;

import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;
import eu.fox7.schematoolkit.dtd.parser.DTDSAXParser;
import eu.fox7.schematoolkit.dtd.writer.DTDWriter;

public class DTDSchemaHandler extends SchemaHandler {

	public DTDSchemaHandler() {
	}
	
	public DTDSchemaHandler(DocumentTypeDefinition dtd) {
		this.schema = dtd;
	}
	
	@Override
	public void parseSchema(InputStream stream) throws IOException,
			SchemaToolkitException {
		DTDSAXParser parser = new DTDSAXParser();
		try {
			this.schema = parser.parseDTDOnly(stream);
		} catch (SAXException e) {
			throw new SchemaToolkitException(e);
		}
		

	}

	@Override
	public void writeSchema(Writer writer) throws IOException,
			SchemaToolkitException {
		DTDWriter dtdWriter = new DTDWriter((DocumentTypeDefinition) schema);
		String dtd = dtdWriter.getExternalSubsetString();
		writer.append(dtd); 
	}

}
