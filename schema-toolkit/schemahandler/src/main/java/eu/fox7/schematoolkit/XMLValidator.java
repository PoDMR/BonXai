package eu.fox7.schematoolkit;

import java.io.File;
import java.io.IOException;

public interface XMLValidator {
	public boolean validate(File file) throws SchemaToolkitException, IOException;
	public boolean validate(String xmlString) throws SchemaToolkitException;
	public void setSchema(Schema schema) throws SchemaToolkitException;
}
