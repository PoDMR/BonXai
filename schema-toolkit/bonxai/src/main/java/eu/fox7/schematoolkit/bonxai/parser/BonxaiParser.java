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

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import eu.fox7.schematoolkit.bonxai.om.Bonxai;

public interface BonxaiParser {
	public Bonxai parse(File file) throws IOException, ParseException;
	public Bonxai parse(String bonxaiString) throws ParseException, IOException;
	public Bonxai parse(Reader reader) throws IOException, ParseException;	
}
