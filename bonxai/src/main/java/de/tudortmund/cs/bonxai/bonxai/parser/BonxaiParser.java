package de.tudortmund.cs.bonxai.bonxai.parser;

import java.io.File;
import java.io.IOException;

import de.tudortmund.cs.bonxai.bonxai.Bonxai;

public interface BonxaiParser {
	public Bonxai parse(File file) throws IOException, ParseException;
}
