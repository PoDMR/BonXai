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
}
