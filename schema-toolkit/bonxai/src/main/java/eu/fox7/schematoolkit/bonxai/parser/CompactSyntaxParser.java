package eu.fox7.schematoolkit.bonxai.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import eu.fox7.schematoolkit.bonxai.om.Bonxai;

public class CompactSyntaxParser implements BonxaiParser {

	@Override
	public Bonxai parse(File file) throws IOException, ParseException {
		Bonxai bonxai;
		BufferedReader rdr = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		CharStream charStream = new BackupCharStream(rdr);
		BonxaiJJParser parser = new BonxaiJJParser(charStream);
		
		bonxai = parser.bonxai();
		return bonxai;
	}
	
	@Override
	public Bonxai parse(String bonxaiString) throws ParseException, IOException {
		return parse(new StringReader(bonxaiString));		
	}

	@Override
	public Bonxai parse(Reader reader) throws IOException, ParseException {
		CharStream charStream = new BackupCharStream(reader);
		BonxaiJJParser parser = new BonxaiJJParser(charStream);
		
		Bonxai bonxai = parser.bonxai();
		return bonxai;		
		
		
	}

}
