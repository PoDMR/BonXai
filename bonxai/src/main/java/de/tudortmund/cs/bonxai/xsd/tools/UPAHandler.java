package de.tudortmund.cs.bonxai.xsd.tools;

import de.tudortmund.cs.bonxai.xsd.XSDSchema;

/**
 * UPA FIX:
 * --------
 * The unique particle attribution constraint of XML XSDSchema has to be
 * fixed for each valid XML XSDSchema document.
 * This may be managed within this class.
 *
 * The implementation of an UPA Repair tool is not part of the current
 * diplomathesis. This class stub has been written to provide an
 * interface to link an UPA-Tool in the future.
 *
 * @author Lars Schmidt
 */
public class UPAHandler {

    XSDSchema xmlSchema;

    public UPAHandler(XSDSchema xmlSchema) {
        this.xmlSchema = xmlSchema;
    }

    public XSDSchema repairUPA() {

        // The fix of the unique particle attribution constraint of
        // XML XSDSchema is not implemented yet.

        return this.xmlSchema;
    }
}
