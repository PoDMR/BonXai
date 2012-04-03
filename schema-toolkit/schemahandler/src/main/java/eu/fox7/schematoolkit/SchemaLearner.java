package eu.fox7.schematoolkit;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

public interface SchemaLearner {
	public Schema learnBonxai() throws SchemaToolkitException;
	public Schema learnXSD() throws SchemaToolkitException;
	public void addXML(File...file) throws IOException, SchemaToolkitException;
	public void addXML(Reader reader) throws IOException, SchemaToolkitException;
	public void clear();

}
