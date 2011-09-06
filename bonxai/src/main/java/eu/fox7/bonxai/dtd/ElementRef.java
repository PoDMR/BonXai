package eu.fox7.bonxai.dtd;

import eu.fox7.bonxai.common.*;

/**
 * Class ElementRef for an DTD Element represented in the ContenModel (Particle)
 * of an other Element or the DTD root.
 *
 *               +-----+
 * This class is | NOT | absolutely equal to the ElementRef in in XSD!
 *               +-----+
 * 
 * In DTD attribute declarations can placed all over the document or in an
 * external DTD which will be included through an entity or other possible ways.
 * A DTD element can be referenced in a contentModel even before it is
 * declared, too.
 *
 * This leads to the following meaning:
 *
 * A DTD describes a structured tree of elements contained in particles. They
 * are all accessible from the DTD root node. Semantically all elements are
 * declared in the position in that tree, but this is not possible in DTD.
 * The declaration stands aside of this tree structure. To manage all the
 * possibilities described above, we set an ElementRef into the tree structure,
 * so that the referenced element can be declared elsewhere. The java object for
 * this element has to be the correct one in every situation.
 *
 * @author Lars Schmidt
 */
public class ElementRef extends Particle {

    /**
     * This attribute holds the referenced element.
     */
    protected SymbolTableRef<eu.fox7.bonxai.dtd.Element> reference;

    /**
     * Constructor of class ElementRef.
     * @param reference
     */
    public ElementRef(SymbolTableRef<eu.fox7.bonxai.dtd.Element> reference) {
        this.reference = reference;
    }

    /***
     * Method getElement returns the concrete element
     * @return referenced Element (eu.fox7.bonxai.dtd.Element)
     */
    public eu.fox7.bonxai.dtd.Element getElement() {
        if (reference != null) {
            return reference.getReference();
        } else {
            return null;
        }
    }

    public void setReference(SymbolTableRef<eu.fox7.bonxai.dtd.Element> reference) {
        this.reference = reference;
    }


}
