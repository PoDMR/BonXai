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

package eu.fox7.schematoolkit.typeautomaton;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaToolkitException;

public class TypeAutomatonSchemaHandler extends SchemaHandler {

	public TypeAutomatonSchemaHandler(
			TypeAutomaton typeAutomaton) {
		super(typeAutomaton);
	}

	@Override
	public void parseSchema(InputStream stream) throws IOException,
			SchemaToolkitException {
		throw new SchemaToolkitException("TypeAutomata cannot be parsed.");
	}

	@Override
	public void writeSchema(Writer writer) throws IOException,
			SchemaToolkitException {
		throw new SchemaToolkitException("TypeAutomata cannot be written.");
	}

}
