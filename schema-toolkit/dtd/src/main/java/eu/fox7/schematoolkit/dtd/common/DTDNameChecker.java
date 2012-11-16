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

package eu.fox7.schematoolkit.dtd.common;

import eu.fox7.schematoolkit.common.QualifiedName;

/**
 * Checking utility for NAME, NAMES, NMTOKEN, NMTOKENS
 * @author Lars Schmidt
 */
public class DTDNameChecker {

    public DTDNameChecker() {

    }

    /**
     * Check the given name against the building rules of a correct XML Name
     * @param qualifiedName
     * @return  returns true if the string is a XML Name
     */
    public boolean checkForXMLName(QualifiedName name) {
    	return this.checkForXMLName(name.getName());
    }
    
    /**
     * Check the given name against the building rules of a correct XML Name
     * @param qualifiedName
     * @return  returns true if the string is a XML Name
     */
    public boolean checkForXMLName(String name){
        if (name.matches("[a-zA-Z\\_\\:][0-9a-zA-Z\\.\\-\\:\\_\\·]*")) {
            return true;
        }
        return false;
    }

    /**
     * Check the given name against the building rules of a correct XML Name
     * @param checkForNames
     * @return  returns true if the string is a XML Name
     */
    public boolean checkForXMLNames(String checkForNames){
        String[] nameArray = checkForNames.split(" ");
        int i = 0;
        for (String currentName : nameArray) {
            if (checkForXMLName(currentName)) { 
                i++;
            }
        }
        if (i == nameArray.length && nameArray.length > 0) {
            return true;
        }
        return false;
    }

    /**
     * Check the given string against the building rules of a correct XML NMTOKEN
     * @param checkForNMTOKEN
     * @return  returns true if the string is a XML Name
     */
    public boolean checkForXMLNmtoken(String checkForNMTOKEN){
        if (checkForNMTOKEN.matches("[0-9a-zA-Z\\.\\-\\:\\_\\·]+")) {
            return true;
        }
        return false;
    }

    /**
     * Check the given list of NMTOKEN against the building rules of correct XML NMTOKENS
     * @param checkForNMTOKENS
     * @return  returns true if the string corresponds to XML NMTOKENS
     */
    public boolean checkForXMLNmtokens(String checkForNMTOKENS){
        String[] nameArray = checkForNMTOKENS.split(" ");
        int i = 0;
        for (String currentNMTOKEN : nameArray) {
            if (checkForXMLNmtoken(currentNMTOKEN)) {
                i++;
            }
        }
        if (i == nameArray.length && nameArray.length > 0) {
            return true;
        }
        return false;
    }

}
