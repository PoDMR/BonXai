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
package de.tudortmund.cs.bonxai.xsd.writer;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import de.tudortmund.cs.bonxai.common.IdentifiedNamespace;
import de.tudortmund.cs.bonxai.xsd.*;
import org.xml.sax.SAXException;

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
     * Returns the created XSD-XSDSchema as a String.
     *
     * @return
     * @throws Exception
     */
    public String getXSDString() {
    	System.setProperty("javax.xml.transform.TransformerFactory","com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
        TransformerFactory transformerFactory;
        Transformer transformer;
        DOMSource source;
        StringWriter sw;
        String xsdString = "";
        if (xmldoc == null) {
            try {
                createXSD();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            sw = new StringWriter();
            transformerFactory = TransformerFactory.newInstance();
            try {
                transformerFactory.setAttribute("indent-number", 2);
            } catch (IllegalArgumentException e) {
                System.err.println("Indentation failed to set. transformerFactory used: "+transformerFactory.getClass() + System.getProperty("javax.xml.transform.TransformerFactory"));
            }
            
            transformer = transformerFactory.newTransformer();
            source = new DOMSource(xmldoc);
            StreamResult result = new StreamResult(sw);
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.transform(source, result);
            xsdString = sw.getBuffer().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xsdString;
    }

    /**
     * returns a XML-Document of the created XSD-Document
     *
     * @return
     * @throws Exception
     */
    @Deprecated
    public Document getXSDDocument() throws Exception {
        String docStr = "";
        DocumentBuilderFactory dbf;
        DocumentBuilder db;
        Document doc = null;
        InputStream input;
        try {
            docStr = getXSDString();
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            input = new ByteArrayInputStream(docStr.getBytes());
            doc = db.parse(input);
        } catch (ParserConfigurationException pe) {
            pe.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * Writes the created XML-XSDSchema to the given file;
     *
     * @param filename
     * @throws Exception
     */
    @Deprecated
    public void writeXSD(String filename) throws Exception {
        FileWriter fw;
        String doc;
        fw = new FileWriter(filename, false);
        doc = getXSDString();
        fw.write(doc);
        fw.close();
    }

    /**
     * Writes the created XML-XSDSchema to the SchemaLocation;
     *
     * @throws Exception
     */
    @Deprecated
    public void writeXSD() throws Exception {
        writeXSD(schema.getSchemaLocation());
    }

    /**
     * converts the given schema to an XSD. The XSD can then be retrieved using
     * the getXSD* Methods.
     *
     * @throws Exception
     */
    protected void createXSD() throws Exception {
        DocumentBuilder db;
        DocumentBuilderFactory dbf;
        FoundElements foundElements;
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            db = dbf.newDocumentBuilder();
            xmldoc = db.newDocument();
            foundElements = new FoundElements();
            foundElements.setNamespaceList(schema.getNamespaceList());
            writeSchemaHeader(schema, foundElements);
            root = xmldoc.getDocumentElement();
            writeTypes(root, schema.getTypes(), foundElements);
            writeAttributes(root, schema.getAttributes(), foundElements);
            writeAttributeGroups(root, schema.getAttributeGroups(), foundElements);
            writeGroups(root, schema.getGroups(), foundElements);
            writeElements(root, schema.getElements(), foundElements);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the AttributeGroups to the given Node
     *
     * @param root
     * @param attributeGroups
     * @param foundElements
     */
    protected void writeAttributeGroups(Node root, Collection<AttributeGroup> attributeGroups, FoundElements foundElements) {
        for (AttributeGroup ag : attributeGroups) {
            AttributeWriter.writeAttributeGroup(root, ag, foundElements);
        }
    }

    /**
     * Writes the Groups to the given Node
     *
     * @param root
     * @param groups
     * @param foundElements
     */
    protected void writeGroups(Node root, Collection<de.tudortmund.cs.bonxai.xsd.Group> groups, FoundElements foundElements) {
        for (Group g : groups) {
            GroupWriter.writeGroup(root, g, foundElements);
        }
    }

    /**
     * Writes the Attributes to the given Node
     *
     * @param root
     * @param attributes
     * @param foundElements
     */
    protected void writeAttributes(Node root, LinkedList<Attribute> attributes, FoundElements foundElements) {
        AttributeWriter.writeAttributeList(root, attributes, foundElements);
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
    protected void writeTypes(Node root, Collection<Type> types, FoundElements foundElements) {
        for (Type t : types) {

            // Fixed so that all global Types are written
            //if (foundElements.containsType(t) == false) {
                TypeWriter.writeType(root, t, foundElements, true);
            //}
        }
    }

    /**
     * Writes the Elements from the given List to the given root Node and saves
     * the written Elements to the foundElements.
     *
     * @param root
     * @param elements
     * @param foundElements
     */
    protected void writeElements(Node root, LinkedList<de.tudortmund.cs.bonxai.xsd.Element> elements, FoundElements foundElements) {
        for (de.tudortmund.cs.bonxai.xsd.Element e : elements) {
            ParticleWriter.writeElement(root, e, foundElements);
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
    protected void writeSchemaHeader(XSDSchema schema, FoundElements foundElements) {
        org.w3c.dom.Element root;
        org.w3c.dom.Element fsElement;
        String value;
        root = xmldoc.createElementNS("http://www.w3.org/2001/XMLSchema", "schema");
        foundElements.setXSDPrefix(root);
        // write the id
        if (schema.getId() != null) {
            root.setAttribute("id", schema.getId());
        }
        // write namespaces
        if (schema.getNamespaceList().getDefaultNamespace() != null && !schema.getNamespaceList().getDefaultNamespace().getUri().equals(XSDWriter.XML_NAMESPACE)) {
            root.setAttribute("xmlns", schema.getNamespaceList().getDefaultNamespace().getUri());
        }
        // Do we have to differ between the targetNamespace and the defaultNamespace?
        // If we have to, we have to change the datastructure in the schema class regarding to this.
        if (schema.getNamespaceList().getDefaultNamespace() != null && !schema.getNamespaceList().getDefaultNamespace().getUri().equals("")) {
            root.setAttribute("targetNamespace", schema.getNamespaceList().getDefaultNamespace().getUri());
        }
        // Write identified namespaces
        for (IdentifiedNamespace ns : schema.getNamespaceList().getIdentifiedNamespaces()) {
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
                foundElements.setXSDPrefix(fsElement);
                value = ((IncludedSchema) fs).getSchemaLocation();
                if (fs.getId() != null) {
                    fsElement.setAttribute("id", fs.getId());
                }
                fsElement.setAttribute("schemaLocation", value);
                AnnotationWriter.writeAnnotation(fsElement, fs, foundElements);
                root.appendChild(fsElement);
            } else if (fs instanceof ImportedSchema) {
//                <import id = ID
//                namespace = anyURI
//                schemaLocation = anyURI>
//                Content: (annotation?) </import>
                fsElement = xmldoc.createElementNS("http://www.w3.org/2001/XMLSchema", "import");
                foundElements.setXSDPrefix(fsElement);
                value = ((ImportedSchema) fs).getNamespace();
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
                AnnotationWriter.writeAnnotation(fsElement, fs, foundElements);
                root.appendChild(fsElement);
            } else if (fs instanceof RedefinedSchema) {
//                <redefine id = ID
//                schemaLocation = anyURI>
//                Content: (annotation | (simpleType | complexType | group | attributeGroup))*
//                </redefine>
                RedefinedSchema rdf = (RedefinedSchema) fs;
                fsElement = xmldoc.createElementNS("http://www.w3.org/2001/XMLSchema", "redefine");
                foundElements.setXSDPrefix(fsElement);
                value = rdf.getSchemaLocation();
                fsElement.setAttribute("schemaLocation", value);
                FoundElements rsFoundElements = new FoundElements();
                rsFoundElements.setNamespaceList(foundElements.getNamespaceList());
                writeTypes(fsElement, rdf.getTypes(), rsFoundElements);
                writeGroups(fsElement, rdf.getGroups(), rsFoundElements);
                writeAttributeGroups(fsElement, rdf.getAttributeGroups(), rsFoundElements);
                if (fs.getId() != null) {
                    fsElement.setAttribute("id", fs.getId());
                }
                AnnotationWriter.writeAnnotation(fsElement, fs, foundElements);
                root.appendChild(fsElement);
            }

        }
    }
}
