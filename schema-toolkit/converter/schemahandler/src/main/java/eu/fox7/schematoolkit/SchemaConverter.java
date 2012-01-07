package eu.fox7.schematoolkit;

import eu.fox7.schematoolkit.exceptions.ConversionFailedException;

public interface SchemaConverter {
	public Schema convert(Schema schema) throws ConversionFailedException;
	public SchemaHandler convert (SchemaHandler schemaHandler) throws ConversionFailedException;
}
