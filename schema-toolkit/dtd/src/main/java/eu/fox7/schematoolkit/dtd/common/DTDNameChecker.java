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
