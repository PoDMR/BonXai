package eu.fox7.schematoolkit;

import eu.fox7.schematoolkit.exceptions.ConversionFailedException;

public abstract class AbstractSchemaConverter implements SchemaConverter {
	@Override
	public SchemaHandler convert(SchemaHandler schemaHandler)
			throws ConversionFailedException {
		Schema source = schemaHandler.getSchema();
		Schema target = this.convert(source);
		return target.getSchemaHandler();
	}

}
