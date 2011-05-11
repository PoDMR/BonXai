package de.tudortmund.cs.bonxai.relaxng;

import de.tudortmund.cs.bonxai.common.DefaultNamespace;
import de.tudortmund.cs.bonxai.common.NamespaceList;

/**
 * Abstract class for identification of all the nameClass Elements of RelaxNG
 *
 * These are the following:
 * - Name
 * - AnyName
 * - NameClassChoice
 * - NsName
 *
 * @author Lars Schmidt
 */
public abstract class NameClass {

    /**
     * String variable for the namespace attribute "ns"
     */
    protected String attributeNamespace;
    /**
     * Variable for holding the list of identified namespaces
     */
    protected NamespaceList namespaceList;

    /**
     * Constructor of class NameClass
     */
    public NameClass() {
        this.namespaceList = new NamespaceList(null);
    }

    /**
     * Getter of the XML attribute namespace.
     * @return String   A string containing the value of the namespace
     */
    public String getAttributeNamespace() {
        return attributeNamespace;
    }

    /**
     * Setter of the XML attribute namespace.
     * @param namespace
     */
    public void setAttributeNamespace(String namespace) {
        this.attributeNamespace = namespace;
    }

    /**
     * Getter of the XML attribute xmlns default namespace.
     * @return String   A string containing the value of the xmlns default namespace.
     */
    public String getDefaultNamespace() {
        if (this.namespaceList != null && this.namespaceList.getDefaultNamespace() != null) {
            return this.namespaceList.getDefaultNamespace().getUri();
        } else {
            return null;
        }
    }

    /**
     * Setter of the XML attribute xmlns default namespace.
     * @param xmlns
     */
    public void setDefaultNamespace(String xmlns) {
        if (this.namespaceList == null) {
            this.namespaceList = new NamespaceList(new DefaultNamespace(xmlns));
        } else {
            this.namespaceList.setDefaultNamespace(new DefaultNamespace(xmlns));
        }
    }

    /**
     * Returns the list of namespaces.
     * This list contains a mapping between namespace shortcuts and full
     * namespace URIs for all namespaces defined in this grammar.
     * @return NamespaceList
     */
    public NamespaceList getNamespaceList() {
        if (this.namespaceList == null) {
            this.namespaceList = new NamespaceList(null);
        }
        return namespaceList;
    }

    /**
     * Setter of the namespacelist
     * This list contains a mapping between namespace shortcuts and full
     * namespace URIs for all namespaces defined in this grammar.
     * @param namespaceList
     */
    public void setNamespaceList(NamespaceList namespaceList) {
        this.namespaceList = namespaceList;
    }
}
