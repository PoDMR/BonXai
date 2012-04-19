/**
 * This file is part of BonXai.
 *
 * BonXai is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BonXai is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.fox7.schematoolkit.xsd.writer;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.xsd.om.*;

/**
 *
 */
public class XSDWriter {

    protected XSDSchema schema;
    protected Document xmldoc;
    // A global root Node simplifies testing
    protected Node root;

    /**
     * The XML namespace.
     */
    public static final String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";

    /**
     * Creates an instance of the XSD-Writer. Call the appropriate methods to
     * get the resulting schema.
     *
     * @param schema
     */
    public XSDWriter(XSDSchema schema) {
        this.schema = schema;
    }

    /**
     * Sets the schema in Order to reuse a Writer Object for creating a new
     * XSDSchema.
     *
     * @param schema
     */
    public void setSchema(XSDSchema schema) {
        this.schema = schema;
        xmldoc = null;
    }

    /**
     * Write XSD to writer
     *
     * @return
     * @throws Exception
     */
    public void writeXSD(Writer writer) throws SchemaToolkitException {
    	System.setProperty("javax.xml.transform.TransformerFactory","com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
    	TransformerFactory transformerFactory;
    	Transformer transformer;
    	DOMSource source;
    	if (xmldoc == null) {
    		createXSD();
    	}
    	transformerFactory = TransformerFactory.newInstance();
    	try {
    		transformerFactory.setAttribute("indent-number", 2);
    	} catch (IllegalArgumentException e) {
    		System.err.println("Indentation failed to set. transformerFactory used: "+transformerFactory.getClass() + System.getProperty("javax.xml.transform.TransformerFactory"));
    	}

    	try {
			transformer = transformerFactory.newTransformer();
			source = new DOMSource(xmldoc);
			StreamResult result = new StreamResult(writer);
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
            throw new SchemaToolkitException(e);
		} catch (IllegalArgumentException e) {
            throw new SchemaToolkitException(e);
		} catch (TransformerException e) {
            throw new SchemaToolkitException(e);
		}
    }


    /**
     * converts the given schema to an XSD. The XSD can then be retrieved using
     * the getXSD* Methods.
     *
     * @throws Exception
     */
    protected void createXSD() throws SchemaToolkitException {
        DocumentBuilder db;
        DocumentBuilderFactory dbf;
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            db = dbf.newDocumentBuilder();
            xmldoc = db.newDocument();
            writeSchemaHeader(schema);
            root = xmldoc.getDocumentElement();
            writeTypes(root, schema.getTypes());
            writeAttributes(root, schema.getAttributes());
            writeAttributeGroups(root, schema.getAttributeGroups());
            writeGroups(root, schema.getGroups());
            writeElements(root, schema.getElements());
        } catch (ParserConfigurationException e) {
            throw new SchemaToolkitException(e);
        }
    }

    /**
     * Writes the AttributeGroups to the given Node
     *
     * @param root
     * @param attributeGroups
     * @param foundElements
     */
    protected void writeAttributeGroups(Node root, Collection<AttributeGroup> attributeGroups) {
        for (AttributeGroup ag : attributeGroups) {
            AttributeWriter.writeAttributeGroup(root, ag, schema);
        }
    }

    /**
     * Writes the Groups to the given Node
     *
     * @param root
     * @param groups
     * @param foundElements
     */
    protected void writeGroups(Node root, Collection<eu.fox7.schematoolkit.xsd.om.Group> groups) {
        for (Group g : groups) {
            GroupWriter.writeGroup(root, g, schema);
        }
    }

    /**
     * Writes the Attributes to the given Node
     *
     * @param root
     * @param attributes
     * @param foundElements
     */
    protected void writeAttributes(Node root, Collection<Attribute> attributes) {
        AttributeWriter.writeAttributeList(root, attributes, schema);
    }

    /**
     * Writes the Types contained within the given TypeList to the XSD. The
     * Types are written to the given root Node. If other Types are found within
     * the schema, they are also written to the XSD if they have not already
     * been written (this means, they are not contained within foundElements).
     *
     * @param root
     * @param types
     * @param foundElements
     */
    protected void writeTypes(Node root, Collection<Type> types) {
        for (Type t : types)
        	if (!t.isAnonymous())
                TypeWriter.writeType(root, t, schema, true);
    }

    /**
     * Writes the Elements from the given List to the given root Node and saves
     * the written Elements to the foundElements.
     *
     * @param root
     * @param elements
     * @param foundElements
     */
    protected void writeElements(Node root, Collection<eu.fox7.schematoolkit.xsd.om.Element> elements) {
        for (eu.fox7.schematoolkit.xsd.om.Element e : elements) {
            ParticleWriter.writeElement(root, e, schema);
        }

    }

    /**
     * Writes the schema node including References to foreign schemas to the
     * local Document
     *
     * @param schema
     * @param foundElements
     * @throws Exception
     *
     */
    protected void writeSchemaHeader(XSDSchema schema) {
        org.w3c.dom.Element root;
        org.w3c.dom.Element fsElement;
        String value;
        root = xmldoc.createElementNS("http://www.w3.org/2001/XMLSchema", "schema");
        DOMHelper.setXSDPrefix(root, schema);
        // write the id
        if (schema.getId() != null) {
            root.setAttribute("id", schema.getId());
        }
        // write namespaces
        if (schema.getDefaultNamespace() != null && !schema.getDefaultNamespace().getUri().equals(XSDWriter.XML_NAMESPACE)) {
            root.setAttribute("xmlns", schema.getDefaultNamespace().getUri());
        }
        // Do we have to differ between the targetNamespace and the defaultNamespace?
        // If we have to, we have to change the datastructure in the schema class regarding to this.
        if (schema.getTargetNamespace() != null && !schema.getTargetNamespace().getUri().equals("")) {
            root.setAttribute("targetNamespace", schema.getTargetNamespace().getUri());
        }
        // Write identified namespaces
        for (IdentifiedNamespace ns : schema.getNamespaces()) {
            root.setAttribute("xmlns:" + ns.getIdentifier(), ns.getUri());
        }
        if (schema.getElementFormDefault().equals(XSDSchema.Qualification.qualified)) {
            root.setAttribute("elementFormDefault", "qualified");
        }
        if (schema.getAttributeFormDefault().equals(XSDSchema.Qualification.qualified)) {
            root.setAttribute("attributeFormDefault", "qualified");
        }
        String finalDefaults = "";
        if (!schema.getFinalDefaults().isEmpty()) {
            boolean finalExtension = false, finalRestriction = false, finalList = false, finalUnion = false;
            for (XSDSchema.FinalDefault currentfinalDefaultValue : schema.getFinalDefaults()) {
                switch (currentfinalDefaultValue) {
                    case extension:
                        finalExtension = true;
                        break;
                    case restriction:
                        finalRestriction = true;
                        break;
                    case list:
                        finalList = true;
                        break;
                    case union:
                        finalUnion = true;
                        break;
                }
            }
            if (finalExtension && finalRestriction && finalList && finalUnion) {
                finalDefaults = "#all";
            } else {
                if (finalExtension) {
                    finalDefaults = finalDefaults + "extension ";
                }
                if (finalRestriction) {
                    finalDefaults = finalDefaults + "restriction ";
                }
                if (finalList) {
                    finalDefaults = finalDefaults + "list ";
                }
                if (finalUnion) {
                    finalDefaults = finalDefaults + "union ";
                }
                if (finalDefaults.length() > 0) {
                    finalDefaults = finalDefaults.substring(0, finalDefaults.length() - 1);
                }
            }
        }
        if (finalDefaults.length() > 0) {
            root.setAttribute("finalDefault", finalDefaults);
        }
        String blockDefaults = "";
        if (!schema.getBlockDefaults().isEmpty()) {
            boolean blockExtension = false, blockRestriction = false, blockSubstitution = false;
            for (XSDSchema.BlockDefault currentblockDefaultValue : schema.getBlockDefaults()) {
                switch (currentblockDefaultValue) {
                    case extension:
                        blockExtension = true;
                        break;
                    case restriction:
                        blockRestriction = true;
                        break;
                    case substitution:
                        blockSubstitution = true;
                        break;
                }
            }
            if (blockExtension && blockRestriction && blockSubstitution) {
                blockDefaults = "#all";
            } else {
                if (blockExtension) {
                    blockDefaults = blockDefaults + "extension ";
                }
                if (blockRestriction) {
                    blockDefaults = blockDefaults + "restriction ";
                }
                if (blockSubstitution) {
                    blockDefaults = blockDefaults + "substitution ";
                }
                if (blockDefaults.length() > 0) {
                    blockDefaults = blockDefaults.substring(0, blockDefaults.length() - 1);
                }
            }
        }
        if (blockDefaults.length() > 0) {
            root.setAttribute("blockDefault", blockDefaults);
        }
        xmldoc.appendChild(root);
        for (ForeignSchema fs : schema.getForeignSchemas()) {
            if (fs instanceof IncludedSchema) {
//                <include id = ID
//                schemaLocation = anyURI >
//                Content: (annotation?) </include>
                fsElement = xmldoc.createElementNS("http://www.w3.org/2001/XMLSchema", "include");
                DOMHelper.setXSDPrefix(fsElement, schema);
                value = ((IncludedSchema) fs).getSchemaLocation();
                if (fs.getId() != null) {
                    fsElement.setAttribute("id", fs.getId());
                }
                fsElement.setAttribute("schemaLocation", value);
                AnnotationWriter.writeAnnotation(fsElement, fs, schema);
                root.appendChild(fsElement);
            } else if (fs instanceof ImportedSchema) {
//                <import id = ID
//                namespace = anyURI
//                schemaLocation = anyURI>
//                Content: (annotation?) </import>
                fsElement = xmldoc.createElementNS("http://www.w3.org/2001/XMLSchema", "import");
                DOMHelper.setXSDPrefix(fsElement, schema);
                value = ((ImportedSchema) fs).getNamespace().getUri();
                if (value != null && !value.equals("")) {
                    fsElement.setAttribute("namespace", value);
                }
                value = ((ImportedSchema) fs).getSchemaLocation();
                if (value != null && !value.equals("")) {
                    fsElement.setAttribute("schemaLocation", value);
                }
                if (fs.getId() != null) {
                    fsElement.setAttribute("id", fs.getId());
                }
                AnnotationWriter.writeAnnotation(fsElement, fs, schema);
                root.appendChild(fsElement);
//            } else if (fs instanceof RedefinedSchema) {
//                <redefine id = ID
//                schemaLocation = anyURI>
//                Content: (annotation | (simpleType | complexType | group | attributeGroup))*
//                </redefine>
//                RedefinedSchema rdf = (RedefinedSchema) fs;
//                fsElement = xmldoc.createElementNS("http://www.w3.org/2001/XMLSchema", "redefine");
//                DOMHelper.setXSDPrefix(fsElement, schema);
//                value = rdf.getSchemaLocation();
//                fsElement.setAttribute("schemaLocation", value);
//                writeTypes(fsElement, rdf.getTypes());
//                writeGroups(fsElement, rdf.getGroups());
//                writeAttributeGroups(fsElement, rdf.getAttributeGroups());
//                if (fs.getId() != null) {
//                    fsElement.setAttribute("id", fs.getId());
//                }
//                AnnotationWriter.writeAnnotation(fsElement, fs, schema);
//                root.appendChild(fsElement);
            }

        }
    }
}
