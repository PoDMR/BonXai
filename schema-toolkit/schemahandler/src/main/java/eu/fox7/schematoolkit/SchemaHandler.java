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

package eu.fox7.schematoolkit;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.io.Writer;

import eu.fox7.schematoolkit.exceptions.ConversionFailedException;

@SuppressWarnings("deprecation")
public abstract class SchemaHandler {
	protected Schema schema;
	
	protected StringBuffer schemaStringBuffer = null;
	
	public SchemaHandler() {}
	
	public SchemaHandler(Schema schema) {
		this.schema = schema;
	}

	public void parseSchema(String schemaString) throws IOException, SchemaToolkitException {
		StringBufferInputStream inputStream = new StringBufferInputStream(schemaString); 
		parseSchema(inputStream);
	}
	
	public abstract void parseSchema(InputStream stream) throws IOException, SchemaToolkitException;
	public abstract void writeSchema(Writer writer) throws IOException, SchemaToolkitException;
	
	public XMLValidator getValidator() throws SchemaToolkitException {
		throw new SchemaToolkitException("No validator for " + this.getSchemaLanguage() + " available.");
	}
	
	public void loadSchema(File file) throws IOException, SchemaToolkitException {
		InputStream stream = new BufferedInputStream(new FileInputStream(file));
		parseSchema(stream);
	}
	
	public void writeSchema(File file) throws IOException, SchemaToolkitException {
		Writer fileWriter = new FileWriter(file);
		BufferedWriter writer = new BufferedWriter(fileWriter);
		writeSchema(writer);
		writer.close();
	}
	
	public String getSchemaString() throws IOException, SchemaToolkitException {
		if (schemaStringBuffer == null)
			fillSchemaString();
		return schemaStringBuffer.toString();
	}
	
	public StringBuffer getSchemaStringBuffer() throws IOException, SchemaToolkitException {
		if (schemaStringBuffer == null)
			fillSchemaString();
		return this.schemaStringBuffer;
	}
	
	protected void fillSchemaString() throws IOException, SchemaToolkitException {
		StringWriter writer = new StringWriter();
		writeSchema(writer);
		schemaStringBuffer = writer.getBuffer();
	}
	
	public Schema getSchema() {
		return schema;
	}

	public SchemaLanguage getSchemaLanguage() {
		return this.getSchema().getSchemaLanguage();
	}
	
	public SchemaHandler convert(SchemaLanguage targetLanguage) throws ConversionFailedException {
		SchemaConverter converter = this.getConverter(targetLanguage);
		return converter.convert(this);
	}
	
	public SchemaConverter getConverter(SchemaLanguage targetLanguage) {
		return this.getSchemaLanguage().getConverter(targetLanguage);
	}
}
