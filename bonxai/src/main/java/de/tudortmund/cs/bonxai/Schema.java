package de.tudortmund.cs.bonxai;

// Schemas
import de.tudortmund.cs.bonxai.dtd.DocumentTypeDefinition;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.relaxng.RelaxNGSchema;
import de.tudortmund.cs.bonxai.bonxai.Bonxai;

// Parser
import de.tudortmund.cs.bonxai.dtd.parser.DTDSAXParser;
import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.exceptions.EmptyProductTypeStateFieldException;
import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.exceptions.EmptySubsetTypeStateFieldException;
import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.exceptions.InvalidTypeStateContentException;
import de.tudortmund.cs.bonxai.xsd.automaton.exceptions.NonDeterministicFiniteAutomataException;
import de.tudortmund.cs.bonxai.xsd.parser.XSDParser;
import de.tudortmund.cs.bonxai.xsd.setOperations.SetOperationsMain;
import de.tudortmund.cs.bonxai.xsd.tools.ComplexTypeInheritanceResolver;
import de.tudortmund.cs.bonxai.xsd.tools.EDCFixer;
import de.tudortmund.cs.bonxai.xsd.tools.ElementInheritanceResolver;
import de.tudortmund.cs.bonxai.relaxng.parser.RNGParser;
// import de.tudortmund.cs.bonxai.bonxai.parser.Parser;

// Writer
import de.tudortmund.cs.bonxai.dtd.writer.DTDWriter;
import de.tudortmund.cs.bonxai.xsd.writer.XSDWriter;
import de.tudortmund.cs.bonxai.relaxng.writer.RNGWriter;
import de.tudortmund.cs.bonxai.bonxai.parser.BonxaiParser;
import de.tudortmund.cs.bonxai.bonxai.parser.CompactSyntaxParser;
import de.tudortmund.cs.bonxai.bonxai.writer.Writer;
import de.tudortmund.cs.bonxai.bonxai.writer.CompactSyntaxVisitor;

// Converter
import de.tudortmund.cs.bonxai.converter.bonxai2xsd.NewBonxai2XSDConverter;
import de.tudortmund.cs.bonxai.converter.dtd2xsd.DTD2XSDConverter;
import de.tudortmund.cs.bonxai.converter.xsd2bonxai.NewXSD2BonxaiConverter;
import de.tudortmund.cs.bonxai.converter.xsd2dtd.XSD2DTDConverter;
import de.tudortmund.cs.bonxai.converter.xsd2relaxng.XSD2RelaxNGConverter;
import de.tudortmund.cs.bonxai.converter.xsd2xsd.XSDUnreachableTypeRemover;
import de.tudortmund.cs.bonxai.converter.relaxng2xsd.RelaxNG2XSDConverter;
import de.tudortmund.cs.bonxai.converter.ConversionFailedException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Wrapper class for schemas of different types:
 * <ul>
 * <li> DTD
 * <li> XSD
 * <li> RelaxNG
 * <li> BonXai
 * </ul>
 * 
 * This class provides wrapper-functions for schema manipulations:
 * <ul>
 * <li> conversion between the different types
 * <li> set operations on schemas
 * </ul>
 * 
 * This class is intended to be used for user interfaces instead of manipulating the schemas of the different types directly.
 * @author niewerth
 *
 */
public class Schema {
	private XSDSchema xsdSchema = null;
	private RelaxNGSchema relaxNGSchema = null;
	private Bonxai bonxaiSchema = null;
	private DocumentTypeDefinition dtdSchema = null;
	private SchemaType type = SchemaType.NONE;
	private Collection<Schema> schemaCollection = null;
	
	/**
	 * Stores a string representation of the schema. The representation is computed when needed.
	 */
	private String schemaString = null;
	
	/**
	 * Stores a comment for the schema. Can be set manually, but is automatically filled during creation of the Schema object.
	 */
	private String comment = "";
	
	/**
	 * Stores the filename of the schema. Must be filled when writeSchema() is used without parameters.
	 */
	private String filename = "";
	
	public Schema() {
		
	}
	
	/**
	 * Create a wrapper object for a DTD schema
	 * @param dtdSchema
	 */
	public Schema(DocumentTypeDefinition dtdSchema) {
		this.dtdSchema = dtdSchema;
		type = SchemaType.DTD;
	}
	
	/**
	 * Create a wrapper object for an XSD schema
	 * @param xsdSchema
	 */
	public Schema(XSDSchema xsdSchema) {
		this.xsdSchema = xsdSchema;
		type = SchemaType.XSD;
	}
	
	/**
	 * Create a wrapper object for a RelaxNG schema
	 * @param relaxNGSchema
	 */
	public Schema(RelaxNGSchema relaxNGSchema) {
		this.relaxNGSchema = relaxNGSchema;
		type = SchemaType.RELAXNG;
	}
	
	/**
	 * Create a wrapper object for a BonXai schema
	 * @param bonxaiSchema
	 */
	public Schema(Bonxai bonxaiSchema) {
		this.bonxaiSchema = bonxaiSchema;
		type = SchemaType.BONXAI;
	}

	/**
	 * Create a wrapper object for a schema collection.
	 * The primary use is to store related XSD schemas of different Namespaces.
	 * @param schemaCollection
	 */
	public Schema(Collection<Schema> schemaCollection) {
		this.schemaCollection = schemaCollection;
		type = SchemaType.COLLECTION;
	}
		
	/**
	 * Load a schema from disk. The schema type is determined by filename extension.
	 * @param file
	 */
	public void loadSchema(File file) {
		String filename = file.getPath();
		String[] nameComponents = filename.split("\\.");
		String extension = "";
		if (nameComponents.length>0) { 
			extension = nameComponents[nameComponents.length - 1];
		}
		
		if (extension.equals("xsd")) {
			loadXSD(file);
		} else if (extension.equals("dtd") || extension.equals("xml")) {
			loadDTD(file);
		} else if (extension.equals("rng")) {
			loadRelaxNG(file);
		} else if (extension.equals("bonxai")) {
			loadBonxai(file);
		} else {
			System.out.println("Unknown filetype " + extension);
		}
	}
		
	/**
	 * Load an XSD schema from disk.
	 * @param file
	 */
	public void loadXSD(File file) {
		clear();
		
		try {
			XSDParser xsdParser = new XSDParser(false, false);
			XSDSchema schema = xsdParser.parse(file.getPath());
			type = SchemaType.XSD;
			xsdSchema = schema;
			this.filename = file.getPath();
            this.comment = file.getPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load a DTD schema from disk.
	 * @param file
	 */
	public void loadDTD(File file) {
		clear();
		
		try {
			DTDSAXParser dtdParser = new DTDSAXParser();
			DocumentTypeDefinition schema = dtdParser.parseXML(file.getPath());
			type = SchemaType.DTD;
			dtdSchema = schema;
			this.filename = file.getPath();
            this.comment = file.getPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load a RelaxNG schema from disk.
	 * @param file
	 */
	public void loadRelaxNG(File file) {
		clear();
		
		try {
            RNGParser rngParser = new RNGParser(file.getPath(), false);
            RelaxNGSchema schema = rngParser.getRNGSchema();
            type = SchemaType.RELAXNG;
            relaxNGSchema = schema;
            this.filename = file.getPath();
            this.comment = file.getPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Load a BonXai Schema from disk. (not working yet)
	 * @param file
	 */
	public void loadBonxai(File file) {
		clear();
		
		try {
			BonxaiParser parser = new CompactSyntaxParser();
            Bonxai schema = parser.parse(file);
            type = SchemaType.BONXAI;
            bonxaiSchema = schema;
            comment = file.getPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write the schema to the stored filename.
	 * @throws IOException
	 */
	public void writeSchema() throws IOException {
		writeSchema(new File(filename));
	}

	/**
	 * Write the schema to the specified file.
	 * @param file
	 * @throws IOException
	 */
	public void writeSchema(File file) throws IOException {
		fillSchemaString();
		FileOutputStream fos = new FileOutputStream(file);
        fos.write(schemaString.getBytes(), 0, schemaString.length());
        fos.close();
    }

	/**
	 * Convert a schema to a different type. Until now only conversion from and to XSD are supported. 
	 * @param targetType
	 * @return The converted Schema
	 * @throws ConversionFailedException
	 */
	public Schema convert(SchemaType targetType) throws ConversionFailedException {
		Schema newSchema = new Schema();
		SchemaType sourceType = type;
		
		switch (sourceType) {
		case DTD:
			switch (targetType) {
			case DTD: throw new ConversionFailedException("Identical source- and targettype");  
			case XSD: 
               	DTD2XSDConverter dtd2XSDConverter = new DTD2XSDConverter(dtdSchema);
               	XSDSchema xmlSchema = dtd2XSDConverter.convert(true);
               	newSchema = new Schema(xmlSchema);
               	newSchema.comment = "Converted from \"" + this.comment +"\"";
				break;
			case RELAXNG: throw new ConversionFailedException("Conversion from DTD to RelaxNG not supported."); 
			case BONXAI: throw new ConversionFailedException("Conversion from DTD to BonXai not supported.");
			} break;
		
		case XSD:
			switch (targetType) {
			case DTD: 
               	XSD2DTDConverter xsd2dtdConverter = new XSD2DTDConverter(xsdSchema, false);
               	DocumentTypeDefinition dtd = xsd2dtdConverter.convert();
               	newSchema = new Schema(dtd);
               	newSchema.comment = "Converted from \"" + this.comment +"\"";
				break;  
			case XSD: throw new ConversionFailedException("Identical source- and targettype");
			case RELAXNG:
				XSD2RelaxNGConverter xsd2RelaxNGConverter = new XSD2RelaxNGConverter(xsdSchema, false);
				RelaxNGSchema rng = xsd2RelaxNGConverter.convert();
				newSchema = new Schema(rng);
				newSchema.comment = "Converted from \"" + this.comment +"\"";
				break;
			case BONXAI: 
//				XSD2BonxaiConverter converter = new XSD2BonxaiConverter(xsdSchema);
//				Bonxai bonxai = converter.getBonxai();
				NewXSD2BonxaiConverter converter = new NewXSD2BonxaiConverter();
				Bonxai bonxai = converter.convert(this.xsdSchema);
				newSchema = new Schema(bonxai);
				newSchema.comment = "Converted from \"" + this.comment +"\"";
				break;
			} break;
		
		case RELAXNG:
			switch (targetType) {
			case DTD: throw new ConversionFailedException("Conversion from RelaxNG to DTD not supported.");  
			case XSD: 
				RelaxNG2XSDConverter rng2xsdConverter = new RelaxNG2XSDConverter(relaxNGSchema);
				XSDSchema xmlSchema = rng2xsdConverter.convert();
				newSchema = new Schema(xmlSchema);
				newSchema.comment = "Converted from \"" + this.comment +"\"";
				break;
			case RELAXNG: throw new ConversionFailedException("Identical source- and targettype");
			case BONXAI: throw new ConversionFailedException("Conversion from RelaxNG to BonXai not supported.");
			} break;
		
		case BONXAI:
			switch (targetType) {
			case DTD: throw new ConversionFailedException("Conversion from BonXai to DTD not supported.");
			case XSD: 
				// Bonxai2XSDConverter converter = new Bonxai2XSDConverter();
				Map<String,XSDSchema> resultSchemas = NewBonxai2XSDConverter.convert(bonxaiSchema);
				
				if (resultSchemas.size() == 1) {
					newSchema = new Schema(resultSchemas.values().iterator().next());
					newSchema.comment = "Converted from \"" + this.comment + "\"";
				} else {
					Collection<Schema> newSchemas = new LinkedList<Schema>();
					for (String namespace: resultSchemas.keySet()) {
						Schema schema = new Schema(resultSchemas.get(namespace));
						schema.comment = "Converted from \"" + this.comment + "\" Namespace: " + namespace;
						newSchemas.add(schema);
					}
					newSchema = new Schema(newSchemas);
					newSchema.comment = "Schema Collection from conversion of \"" + this.comment + "\"";
				}
				break; // TODO we need to handle multiple schemas here
			case RELAXNG: throw new ConversionFailedException("Conversion from BonXai to RelaxNG not supported.");
			case BONXAI: throw new ConversionFailedException("Identical source- and targettype");
			} break;
		case COLLECTION: throw new ConversionFailedException("Conversion of Collections is not supported.");
		} 
		
		return newSchema;
	}
	
	/**
	 * Computes a schema for the union of a set of schemas. Only works for XSD schemas.
	 * @param schemas
	 * @param workingDirectory
	 * @return
	 * @throws ConversionFailedException
	 */
	public static Schema union(Collection<Schema> schemas, String workingDirectory) throws ConversionFailedException {
		Schema newSchema = new Schema();
		LinkedList<XSDSchema> xsdSchemas = new LinkedList<XSDSchema>();
		
		for (Schema schema: schemas) {
			xsdSchemas.add(schema.getXSD());
		}
		
		SetOperationsMain setOp = new SetOperationsMain();
		
		try {
			Collection<XSDSchema> newSchemas = setOp.union(xsdSchemas, workingDirectory);
		
			newSchema = getSchemaFromXSDCollection(newSchemas, "Union");
		
			return newSchema;
		} catch (Exception e) {
			throw new ConversionFailedException("Union failed", e);
		}
	}

	/**
	 * Computes a schema for the intersection of a set of schemas. Only works for XSD schemas.
	 * @param schemas
	 * @param workingDirectory
	 * @return
	 * @throws ConversionFailedException
	 */
	public static Schema intersect(Collection<Schema> schemas, String workingDirectory) throws ConversionFailedException {
		Schema newSchema = new Schema();
		LinkedList<XSDSchema> xsdSchemas = new LinkedList<XSDSchema>();
		
		for (Schema schema: schemas) {
			xsdSchemas.add(schema.getXSD());
		}
		
		SetOperationsMain setOp = new SetOperationsMain();
		
		try {
			Collection<XSDSchema> newSchemas = setOp.intersection(xsdSchemas, workingDirectory);
		
			newSchema = getSchemaFromXSDCollection(newSchemas, "Intersection");
		
			return newSchema;
		} catch (Exception e) {
			throw new ConversionFailedException("Intersection failed", e);
		}
	}

	/**
	 * Computes a schema for the difference of two schemas. Only works for XSD schemas.
	 * @param schema
	 * @param workingDirectory
	 * @return
	 * @throws ConversionFailedException
	 */
	public Schema substract(Schema schema, String workingDirectory) throws ConversionFailedException {
		Schema newSchema = new Schema();
		SetOperationsMain setOp = new SetOperationsMain();
		
		try {
			Collection<XSDSchema> newSchemas = setOp.difference(this.getXSD(), schema.getXSD(), workingDirectory);
		
			newSchema = getSchemaFromXSDCollection(newSchemas, "Difference");
		
			return newSchema;
		} catch (Exception e) {
			throw new ConversionFailedException("Difference failed", e);
		}
	}

	/**
	 * Deletes the referenced schema.
	 */
	public void clear() {
		type = SchemaType.NONE;
		xsdSchema = null;
		bonxaiSchema = null;
		dtdSchema = null;
		relaxNGSchema = null;
		schemaCollection = null;
		schemaString = null;
	}
	
	/**
	 * Returns a string representation of a schema which might be written do disk.
	 * @return
	 */
	public String getSchemaString() {
		if (schemaString == null) {
			fillSchemaString();
		}
		
		return schemaString;
	}
	
	public void resolveElementInheritance() throws ConversionFailedException {
		if (type != SchemaType.XSD) {
			throw new ConversionFailedException("Inheritance resolving is only supported for XSDs.");
		}
		
		ElementInheritanceResolver elementInheritanceResolver = new ElementInheritanceResolver(xsdSchema);
        elementInheritanceResolver.resolveSubstitutionGroups();
	}
	
	public Schema resolveComplexTypeInheritance(String workingDirectory) throws ConversionFailedException {
		if (type != SchemaType.XSD) {
			throw new ConversionFailedException("Inheritance resolving is only supported for XSDs.");
		}
		
		try {
			ComplexTypeInheritanceResolver complexTypeInheritanceResolver = new ComplexTypeInheritanceResolver(xsdSchema);
	    	XSDSchema newXSDSchema = complexTypeInheritanceResolver.getSchemaWithoutComplexTypeInheritance(workingDirectory);
	    	Schema newSchema = new Schema(newXSDSchema);
	    	newSchema.setComment(comment);
	    	return newSchema;
		} catch (Exception e) {
			throw new ConversionFailedException(e);
		}
	}
	
	/**
	 * Returns the stored comment
	 * @return
	 */
	public String getComment() {
		return comment;
	}
	
	/**
	 * Sets the comment for the schema.
	 * @param comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * Returns the stored XSD schema or null if the schema is of different type.
	 * @return
	 */
	public XSDSchema getXSD() {
		return xsdSchema;
	}

	/**
	 * Returns the stored DTD schema or null if the schema is of different type.
	 * @return
	 */
	public DocumentTypeDefinition getDTD() {
		return dtdSchema;
	}

	/**
	 * Returns the stored BonXai schema or null if the schema is of different type.
	 * @return
	 */
	public Bonxai getBonxai() {
		return bonxaiSchema;
	}

	/**
	 * Returns the stored RelaxNG schema or null if the schema is of different type.
	 * @return
	 */
	public RelaxNGSchema getRelaxNG() {
		return relaxNGSchema;
	}
	
	/**
	 * Returns the type of the schema.
	 * @return
	 */
	public SchemaType getType() {
		return type;
	}
	
	/**
	 * Returns the stored schema collection, or null if the schema is not a collection.
	 * @return
	 */
	public Collection<Schema> getCollection() {
		return schemaCollection;
	}
	
	private static Schema getSchemaFromXSDCollection(Collection<XSDSchema> schemas, String comment) {
		Schema newSchema;
		if (schemas.size() == 1) {
			newSchema = new Schema(schemas.iterator().next());
			newSchema.comment = comment;
		} else {
			Collection<Schema> newSchemas = new LinkedList<Schema>();
			for (XSDSchema xsdSchema: schemas) {
				Schema schema = new Schema(xsdSchema);
				schema.comment = comment + " Namespace: " + xsdSchema.getTargetNamespace();
				newSchemas.add(schema);
			}
			newSchema = new Schema(newSchemas);
			newSchema.comment = "Schema Collection: " + comment;
		}		
		return newSchema;
	}
	
	/**
	 * Fills the string representation of the schema. This is used by getSchemaString() and writeSchema() 
	 */
	private void fillSchemaString() {
		String schemaString;
		try {
			switch (type) {
			case NONE: schemaString = "There is no schema."; break;
			case DTD:
				DTDWriter dtdWriter = new DTDWriter(dtdSchema);
				schemaString = dtdWriter.getXMLWithFullDTDDeclarationString();
				break;
			case XSD:
				XSDWriter xsdWriter = new XSDWriter(xsdSchema);
				schemaString = xsdWriter.getXSDString();
				this.schemaString = schemaString;
				break;
			case RELAXNG:
				RNGWriter rngWriter = new RNGWriter(relaxNGSchema);
				schemaString = rngWriter.getRNGString();
				this.schemaString = schemaString;
				break;
			case BONXAI:
				Writer bonxaiWriter = new Writer();
				CompactSyntaxVisitor visitor = new CompactSyntaxVisitor();
				schemaString = bonxaiWriter.write(bonxaiSchema, visitor);
				this.schemaString = schemaString;
				break;
			case COLLECTION:
				schemaString = "This is a collection.";
				break;
			default:  //should be unreachable
				schemaString = "";
				break;
			}
			this.schemaString = schemaString;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Schema fixEDC(String workingDirectory) throws ConversionFailedException {
		if (this.getType() != SchemaType.XSD) {
			throw new ConversionFailedException("Can only fix EDC for xml schema");
		}
		XSDSchema schema = this.getXSD();
		EDCFixer edcFixer = new EDCFixer();
		try {
			Schema newSchema = new Schema(edcFixer.fixEDCWithoutInheritance(schema, workingDirectory));
			return newSchema;
		} catch (Exception e) {
			throw new ConversionFailedException(e);
		} 
	}
	
	public void removeUnreachableTypes() throws ConversionFailedException {
		if (this.getType() != SchemaType.XSD) {
			throw new ConversionFailedException("Can only remove unused types in xml schema");
		}
		XSDUnreachableTypeRemover.removeUnreachableTypes(this.getXSD());
		this.schemaString = null;
	}
}