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

package eu.fox7.schematoolkit.xsd.tools;

public class NameChecker {
    /**
     * Checks if a given name is a correct XSD Name
     * @param name  Name for which is checked if it is an XSD Name
     * @return  True if the specified name is an XSD Name
     */
    public static boolean isName(String name) {
        return (name.matches("[a-zA-Z\\_\\:][0-9a-zA-Z\\. \\-\\:\\_]*"));
    }

    /**
     * Checks if a given name is a correct XSD NCName
     * @param name  Name for which is checked if it is an XSD NCName
     * @return  True if the specified name is an XSD NCName
     */
    public static boolean isNCName(String name) {
        return (isName(name) && !name.contains(":"));
    }

    /**
     * Checks if a given name is a correct XSD QName
     * @param name  Name for which is checked if it is an XSD QName
     * @return  True if the specified name is an XSD QName
     */
    public static boolean isQName(String name) {
        return (name.matches("([a-zA-Z\\_][0-9a-zA-Z\\.\\-\\_]*:)?[a-zA-Z\\_][0-9a-zA-Z\\. \\-\\_]*"));
    }

    /**
     * Checks if a given string is a correct anyUri
     * @param uri  String for which is checked if it is an anyUri
     * @return  True if the specified name is an anyUri
     */
    public static boolean isAnyUri(String uri) {
        return (uri.matches("(([a-zA-Z][0-9a-zA-Z+\\-\\.]*:)?/{0,2}[0-9a-zA-Z;/?:@&=+$\\.\\-_!~*'()%]+)?(#[0-9a-zA-Z;/?:@&=+$\\.\\-_!~*'()%]+)?"));
    }
    
    /**
     * Check the given name against the building rules of a correct XML Name
     * @param checkForName
     * @return  returns true if the string is a XML Name
     */
    public static boolean checkForXMLName(String checkForName) {
        if (checkForName.matches("[a-zA-Z\\_\\:][0-9a-zA-Z\\.\\-\\:\\_\\·]*")) {
            return true;
        }
        return false;
    }

    /**
     * Check the given name against the building rules of a correct XML Name
     * @param checkForNames
     * @return  returns true if the string is a XML Name
     */
    public static boolean checkForXMLNames(String checkForNames) {
        String[] nameArray = checkForNames.split(" ");
        int i = 0;
        for (String currentName : nameArray) {
            if (checkForXMLName(currentName)) {
                i++;
            }
        }
        return (i == nameArray.length && nameArray.length > 0);
    }

    /**
     * Check the given string against the building rules of a correct XML NMTOKEN
     * @param checkForNMTOKEN
     * @return  returns true if the string is a XML Name
     */
    public static boolean checkForXMLNmtoken(String checkForNMTOKEN) {
        return (checkForNMTOKEN.matches("[0-9a-zA-Z\\.\\-\\:\\_\\·]+"));
    }

    /**
     * Check the given list of NMTOKEN against the building rules of correct XML NMTOKENS
     * @param checkForNMTOKENS
     * @return  returns true if the string corresponds to XML NMTOKENS
     */
    public static boolean checkForXMLNmtokens(String checkForNMTOKENS) {
        String[] nameArray = checkForNMTOKENS.split(" ");
        int i = 0;
        for (String currentNMTOKEN : nameArray) {
            if (checkForXMLNmtoken(currentNMTOKEN)) {
                i++;
            }
        }
        return (i == nameArray.length && nameArray.length > 0);
    }
}
