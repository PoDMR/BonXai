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

public interface BonxaiJJParserConstants {

  int EOF = 0;
  int QUOTE = 5;
  int SINGLE_LINE_COMMENT = 6;
  int ROOTS = 7;
  int GRAMMAR = 8;
  int DEFAULT_NAMESPACE = 9;
  int NAMESPACE = 10;
  int TYPE = 11;
  int DEFAULTT = 12;
  int DATATYPES = 13;
  int IMPORT = 14;
  int CONSTRAINTS = 15;
  int UNIQUE = 16;
  int KEY = 17;
  int KEYREF = 18;
  int GROUPS = 19;
  int GROUP = 20;
  int ATTRIBUTEGROUP = 21;
  int ATTRIBUTEREF = 22;
  int ATTRIBUTE = 23;
  int MIXED = 24;
  int EMPTY = 25;
  int MISSING = 26;
  int ELEMENTREF = 27;
  int ELEMENT = 28;
  int STRICT = 29;
  int LAX = 30;
  int SKIPP = 31;
  int FIXED = 32;
  int CONSTANT = 33;
  int OPENING_ROUND_BRACKET = 34;
  int CLOSING_ROUND_BRACKET = 35;
  int OPENING_SQUARED_BRACKET = 36;
  int CLOSING_SQUARED_BRACKET = 37;
  int OPENING_CURLY_BRACKET = 38;
  int CLOSING_CURLY_BRACKET = 39;
  int EQUALS = 40;
  int COMMA = 41;
  int AT = 42;
  int OR = 43;
  int ANDLITERAL = 44;
  int DOUBLESLASH = 45;
  int SLASH = 46;
  int STAR = 47;
  int PLUS = 48;
  int QUESTION_MARK = 49;
  int COLON = 50;
  int POINT = 51;
  int ANNOTATION_VALUE = 52;
  int DIGIT = 53;
  int BASECHAR = 54;
  int IDEOGRAPHIC = 55;
  int COMBINING_CHAR = 56;
  int EXTENDER = 57;
  int LABEL = 58;
  int PROTOCOL = 59;
  int URI = 60;
  int QUOTATION = 61;
  int QUOT_QUOTE = 62;

  int DEFAULT = 0;
  int NAME = 1;
  int URIX = 2;
  int QUOTATIONX = 3;

  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\\"\"",
    "<SINGLE_LINE_COMMENT>",
    "\"roots\"",
    "\"grammar\"",
    "\"default namespace\"",
    "\"namespace\"",
    "\"type\"",
    "\"default\"",
    "\"datatypes \"",
    "\"import\"",
    "\"constraints\"",
    "\"unique\"",
    "\"key\"",
    "\"keyref \"",
    "\"groups\"",
    "\"group\"",
    "\"attribute-group\"",
    "\"attributeref\"",
    "\"attribute\"",
    "\"mixed\"",
    "\"empty\"",
    "\"missing\"",
    "\"elementref\"",
    "\"element\"",
    "\"strict\"",
    "\"lax\"",
    "\"skip\"",
    "\"fixed\"",
    "<CONSTANT>",
    "\"(\"",
    "\")\"",
    "\"[\"",
    "\"]\"",
    "\"{\"",
    "\"}\"",
    "\"=\"",
    "\",\"",
    "\"@\"",
    "\"|\"",
    "\"&\"",
    "\"//\"",
    "\"/\"",
    "\"*\"",
    "\"+\"",
    "\"?\"",
    "\":\"",
    "\".\"",
    "\"annotation\"",
    "<DIGIT>",
    "<BASECHAR>",
    "<IDEOGRAPHIC>",
    "<COMBINING_CHAR>",
    "<EXTENDER>",
    "<LABEL>",
    "<PROTOCOL>",
    "<URI>",
    "<QUOTATION>",
    "\"\\\"\"",
  };

}
