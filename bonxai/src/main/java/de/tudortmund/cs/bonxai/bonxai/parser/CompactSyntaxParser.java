package de.tudortmund.cs.bonxai.bonxai.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import de.tudortmund.cs.bonxai.bonxai.Bonxai;

public class CompactSyntaxParser implements BonxaiParser {

	@Override
	public Bonxai parse(File file) throws IOException, ParseException {
		Bonxai bonxai;
		BufferedReader rdr = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		bonXaiTree parser = new bonXaiTree(rdr);
		
		SimpleNode simpleNode = parser.bonXai();
		Visitor visitor = new Visitor(new Bonxai());
		bonxai = visitor.visit(simpleNode, null);
		return bonxai;
	}

}
