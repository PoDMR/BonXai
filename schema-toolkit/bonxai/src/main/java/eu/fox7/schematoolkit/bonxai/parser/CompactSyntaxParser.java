/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

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
