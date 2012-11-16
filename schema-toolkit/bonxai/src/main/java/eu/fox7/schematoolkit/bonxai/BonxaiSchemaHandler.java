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

package eu.fox7.schematoolkit.bonxai;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.XMLValidator;
import eu.fox7.schematoolkit.bonxai.om.Bonxai;
import eu.fox7.schematoolkit.bonxai.parser.BonxaiParser;
import eu.fox7.schematoolkit.bonxai.parser.CompactSyntaxParser;
import eu.fox7.schematoolkit.bonxai.parser.ParseException;
import eu.fox7.schematoolkit.bonxai.writer.CompactSyntaxWriter;

public class BonxaiSchemaHandler extends SchemaHandler {
	public static final String BONXAIVALIDATOR = "eu.fox7.schematoolkit.xmlvalidator.BonxaiValidator";
	
	public BonxaiSchemaHandler() {}
	
	public BonxaiSchemaHandler(Bonxai bonxai) {
		super(bonxai);
	}
	
	@Override
	public void parseSchema(InputStream stream) throws IOException, ParseException {
		BonxaiParser parser = new CompactSyntaxParser();
		Reader reader = new InputStreamReader(stream);
		this.schema = parser.parse(reader);
	}

	@Override
	public void writeSchema(Writer writer) throws IOException {
		CompactSyntaxWriter csw = new CompactSyntaxWriter();
		csw.write((Bonxai) schema, writer);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public XMLValidator getValidator() throws SchemaToolkitException {
		try {
			Class<XMLValidator> bonxaiValidator = (Class<XMLValidator>) this.getClass().getClassLoader().loadClass(BONXAIVALIDATOR);
			XMLValidator validator = bonxaiValidator.newInstance();
			validator.setSchema(schema);
			return validator;
		} catch (ClassNotFoundException e) {
			throw new SchemaToolkitException("BonxaiValidator not found.", e);
		} catch (InstantiationException e) {
			throw new SchemaToolkitException(e);
		} catch (IllegalAccessException e) {
			throw new SchemaToolkitException(e);
		}
		
	}

}
