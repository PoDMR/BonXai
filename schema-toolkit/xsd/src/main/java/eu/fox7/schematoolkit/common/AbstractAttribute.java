package eu.fox7.schematoolkit.common;


public abstract class AbstractAttribute extends AttributeParticle {
    public AbstractAttribute(QualifiedName name, String defaultValue, String fixedValue, AttributeUse use) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.fixedValue = fixedValue;
        this.use = use;
    }
    
    public AbstractAttribute() {
    }

    /**
     * The name of the attribute.
     *
     * This name is full qualified in the format "{<namespace>}<localName>",
     * where "<" and ">" are only meant as placeholder indicators and do not
     * belong to the qualified name itself.
     */
    private QualifiedName name;
    /**
     * Use flag.
     */
    private AttributeUse use = AttributeUse.optional;

    /**
     * Default value.
     */
    private String defaultValue;
    
    /**
     * Fixed value.
     */
    private String fixedValue;

    /**
     * Creates a new attribute with the passed name.
     *
     * This constructor should ONLY be used with fully qualified names in the
     * form "{namespace}name".
     *
     * @TODO: This seems to be an unsafe constructor - Attribute should always
     * have a type associated.
     */
    public AbstractAttribute(QualifiedName name) {
        this.name = name;
    }



    /**
     * Returns the name of the attribute.
     *
     * This name is full qualified in the format "{<namespace>}<localName>",
     * where "<" and ">" are only meant as placeholder indicators and do not
     * belong to the qualified name itself.
     */
    public QualifiedName getName() {
        return name;
    }


    /**
     * Set the use flag.
     */
    public void setUse(AttributeUse use) {
        this.use = use;
    }

    /**
     * Returns the use flag.
     */
    public AttributeUse getUse() {
        return use;
    }

    /**
     * Set default value.
     */
    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Get default value.
     */
    public String getDefault() {
        return defaultValue;
    }

    /**
     * Set fixed value.
     */
    public void setFixed(String fixedValue) {
        this.fixedValue = fixedValue;
    }

    /**
     * Get fixed value.
     */
    public String getFixed() {
        return fixedValue;
    }

	public void setName(QualifiedName name) {
		this.name = name;
	}
}
