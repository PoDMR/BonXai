package de.tudortmund.cs.bonxai.dtd;

import de.tudortmund.cs.bonxai.common.SymbolTable;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;

/**
 * Main class of a datastructure for representing Document Type Definitions of XML
 * @author Lars Schmidt
 */
public class DocumentTypeDefinition {

    /**
     * Attribute holding the root element of the DTD
     */
    private de.tudortmund.cs.bonxai.dtd.ElementRef rootElementRef;
    /**
     * This SymbolTable holds references for ALL elements of the DTD
     */
    private SymbolTable<de.tudortmund.cs.bonxai.dtd.Element> elementSymbolTable;
    /**
     * This SymbolTable holds references for ALL internal entities of the DTD
     */
    private SymbolTable<InternalEntity> internalEntities;
    /**
     * This SymbolTable holds references for ALL external entities of the DTD
     */
    private SymbolTable<ExternalEntity> externalEntities;
    /**
     * This SymbolTable holds references for ALL notation of the DTD
     */
    private SymbolTable<Notation> notations;
    /**
     * These two variables hold the public and system ID of the current DTD schema
     */
    private String publicID, systemID;

    /**
     * Constructor of the class DocumentTypeDefinition
     */
    public DocumentTypeDefinition() {
        this.elementSymbolTable = new SymbolTable<de.tudortmund.cs.bonxai.dtd.Element>();
        this.internalEntities = new SymbolTable<InternalEntity>();
        this.externalEntities = new SymbolTable<ExternalEntity>();
        this.notations = new SymbolTable<Notation>();
    }

    /**
     * Getter for the PublicID of the DTD
     * @return String
     */
    public String getPublicID() {
        return this.publicID;
    }

    /**
     * Setter for the PublicID of the DTD
     * @param publicID
     */
    public void setPublicID(String publicID) {
        this.publicID = publicID;
    }

    /**
     * Getter for the SystemID of the DTD
     * @return String
     */
    public String getSystemID() {
        return this.systemID;
    }

    /**
     * Setter for the SystemID of the DTD
     * @param systemID
     */
    public void setSystemID(String systemID) {
        this.systemID = systemID;
    }

    /**
     * Getter for the root-element of the DTD
     * @return element - Element
     */
    public Element getRootElement() {
        if (this.rootElementRef != null && this.rootElementRef.getElement() != null) {
            return this.rootElementRef.getElement();
        } else {
            return null;
        }
    }

    /**
     * Setter for the reference of the root-element of the DTD
     * @param rootElementRef
     */
    public void setRootElementRef(ElementRef rootElementRef) {
        this.rootElementRef = rootElementRef;
    }

    /**
     * Getter for the element symbolTable for ALL elements defined in this DTD
     * @return elementSymbolTable - SymbolTable<de.tudortmund.cs.bonxai.dtd.Element>
     */
    public SymbolTable<de.tudortmund.cs.bonxai.dtd.Element> getElementSymbolTable() {
        return this.elementSymbolTable;
    }

    /**
     * Method for registering an element extracted from a given contentModel of
     * another element in the symbolTable for ALL elements defined in this DTD
     * @param element
     * @return element - SymbolTableRef<de.tudortmund.cs.bonxai.dtd.Element>
     */
    public SymbolTableRef<de.tudortmund.cs.bonxai.dtd.Element> registerElementFromContentModel(de.tudortmund.cs.bonxai.dtd.Element element) {
        if (!this.elementSymbolTable.hasReference(element.getName())) {
            return this.elementSymbolTable.updateOrCreateReference(element.getName(), element);
        } else {
            return this.elementSymbolTable.getReference(element.getName());
        }
    }

    /**
     * Method for adding an internal entity to the symbolTable for ALL internal
     * entities defined in this DTD
     * @param internalEntity
     */
    public void addInternalEntity(InternalEntity internalEntity) {
        this.internalEntities.updateOrCreateReference(internalEntity.getName(), internalEntity);
    }

    /**
     * Method for adding an external entity to the symbolTable for ALL external
     * entities defined in this DTD
     * @param externalEntity
     */
    public void addExternalEntity(ExternalEntity externalEntity) {
        this.externalEntities.updateOrCreateReference(externalEntity.getName(), externalEntity);
    }

    /**
     * Getter for the internal entity symbolTable for internal entities defined in this DTD
     * @return elementSymbolTable - SymbolTable<de.tudortmund.cs.bonxai.dtd.InternalEntity>
     */
    public SymbolTable<de.tudortmund.cs.bonxai.dtd.InternalEntity> getInternalEntitySymbolTable() {
        return this.internalEntities;
    }

    /**
     * Getter for the external entity symbolTable for external entities defined in this DTD
     * @return elementSymbolTable - SymbolTable<de.tudortmund.cs.bonxai.dtd.InternalEntity>
     */
    public SymbolTable<de.tudortmund.cs.bonxai.dtd.ExternalEntity> getExternalEntitySymbolTable() {
        return this.externalEntities;
    }

    /**
     * Method for adding a notation to the symbolTable for ALL notations
     * defined in this DTD
     * @param notation
     */
    public void addNotation(Notation notation) {
        this.notations.updateOrCreateReference(notation.getName(), notation);
    }

    /**
     * Getter for the notation symbolTable for notations defined in this DTD
     * @return elementSymbolTable - SymbolTable<de.tudortmund.cs.bonxai.dtd.Notation>
     */
    public SymbolTable<de.tudortmund.cs.bonxai.dtd.Notation> getNotationSymbolTable() {
        return this.notations;
    }
}
