package eu.fox7.schematoolkit;

import java.util.Collection;

public interface SchemaOperation {

	public SchemaHandler apply() throws SchemaToolkitException;
	public SchemaHandler apply(Collection<Schema> schemas) throws SchemaToolkitException;
	public void addSchema(Schema schema);

}
