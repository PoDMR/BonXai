package eu.fox7.schematoolkit.bonxai.parser;

import java.io.File;
import java.io.IOException;

import eu.fox7.schematoolkit.bonxai.om.Bonxai;

public interface BonxaiParser {
	public Bonxai parse(File file) throws IOException, ParseException;
	public Bonxai parse(String bonxaiString) throws ParseException;
}
