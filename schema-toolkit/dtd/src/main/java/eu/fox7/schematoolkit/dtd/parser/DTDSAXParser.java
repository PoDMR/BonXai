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

package eu.fox7.schematoolkit.dtd.parser;

import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.sun.xml.dtdparser.DTDParser;

/**
 * Class DTDSAXParser. This is a SAX event parser for the DTD Content of a XML file.
 * @author Lars Schmidt
 */
public class DTDSAXParser {

    public DocumentTypeDefinition parse(InputStream inputStream) throws SAXException, IOException {
    	DTDParser parser = new DTDParser();
    	
    	DTDParserEventListener handler = new DTDParserEventListener();
    	
    	parser.setDtdHandler(handler);
    	
    	InputSource in = new InputSource();
    	in.setByteStream(inputStream);

    	parser.parse(in);
    	
    	return handler.getDTD();
    }


    /**
     * Constructor of this class DTDSAXParser 
     */
    public DTDSAXParser() {
        super();
    }
}
