package de.tudortmund.cs.bonxai.xsd.tools;

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
