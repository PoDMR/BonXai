package eu.fox7.schematoolkit.bonxai.parser;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import eu.fox7.schematoolkit.bonxai.om.Bonxai;

public interface BonxaiParser {
	public Bonxai parse(File file) throws IOException, ParseException;
	public Bonxai parse(String bonxaiString) throws ParseException, IOException;
	public Bonxai parse(Reader reader) throws IOException, ParseException;	
}
