package eu.fox7.schematoolkit.dtd.om;

import java.util.Collection;
import java.util.Map;

import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.QualifiedName;

/**
 * Main class of a datastructure for representing Document Type Definitions of XML
 * @author Lars Schmidt
 */
public class DocumentTypeDefinition {
    /**
     * This SymbolTable holds references for ALL elements of the DTD
     */
    private Map<QualifiedName,Element> elements;
    /**
     * This SymbolTable holds references for ALL internal entities of the DTD
     */
    private Map<String,InternalEntity> internalEntities;
    /**
     * This SymbolTable holds references for ALL external entities of the DTD
     */
    private Map<String,ExternalEntity> externalEntities;
    /**
     * This SymbolTable holds references for ALL notation of the DTD
     */
    private Map<String,Notation> notations;
    /**
     * These two variables hold the public and system ID of the current DTD schema
     */
    private String publicID, systemID;
	private Element rootElement = null;

    /**
     * Constructor of the class DocumentTypeDefinition
     */
    public DocumentTypeDefinition() {
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
     * Method for adding an internal entity to the symbolTable for ALL internal
     * entities defined in this DTD
     * @param internalEntity
     */
    public void addInternalEntity(InternalEntity internalEntity) {
        this.internalEntities.put(internalEntity.getName(), internalEntity);
    }

    /**
     * Method for adding an external entity to the symbolTable for ALL external
     * entities defined in this DTD
     * @param externalEntity
     */
    public void addExternalEntity(ExternalEntity externalEntity) {
        this.externalEntities.put(externalEntity.getName(), externalEntity);
    }


    /**
     * Method for adding a notation to the symbolTable for ALL notations
     * defined in this DTD
     * @param notation
     */
    public void addNotation(Notation notation) {
        this.notations.put(notation.getName(), notation);
    }

	public void setRootElement(Element rootElement) {
		this.rootElement = rootElement;
	}

	public void addElement(Element element) {
		this.elements.put(element.getName(), element);
	}

	public Element getElement(String elementName) {
		return this.elements.get(new QualifiedName(Namespace.EMPTY_NAMESPACE, elementName));
	}

	public Element getRootElement() {
		return this.rootElement;
	}

	public Collection<Element> getElements() {
		return this.elements.values();
	}

	public Collection<InternalEntity> getInternalEntitys() {
		return this.internalEntities.values();
	}

	public Collection<Notation> getNotations() {
		return this.notations.values();
	}
}
