package eu.fox7.schematoolkit.converter.relaxng2xsd;

import eu.fox7.schematoolkit.common.DefaultNamespace;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.relaxng.om.*;
import eu.fox7.schematoolkit.xsd.om.ForeignSchema;
import eu.fox7.schematoolkit.xsd.om.ImportedSchema;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Class ConverterBase
 * @author Lars Schmidt
 */
public abstract class ConverterBase {

    /**
     * Source of the conversion
     */
    protected RelaxNGSchema relaxng;
    /**
     * Target for the conversion
     */
    protected XSDSchema xmlSchema;
    /**
     * Information about the patterns within the RELAX NG XSDSchema
     * This will be filled up by the PatternDataCollector during the preprocessing
     */
    protected HashMap<Pattern, HashSet<String>> patternInformation;
    /**
     * Set of all used names within the RELAX NG XSDSchema
     * This is used as a blacklist for finding unique names for new groups or elements
     */
    protected HashSet<String> usedLocalNames;
    /**
     * Set of all used type names within the generated XML XSDSchema
     * This is used as a blacklist for finding unique names for new types,
     * groups or elements
     */
    protected HashSet<String> usedLocalTypeNames;

    /**
     * Constructor of class ConverterBase
     * @param xmlSchema
     * @param relaxng
     * @param patternInformation
     * @param usedLocalNames
     */
    public ConverterBase(XSDSchema xmlSchema, RelaxNGSchema relaxng, HashMap<Pattern, HashSet<String>> patternInformation, HashSet<String> usedLocalNames, HashSet<String> usedLocalTypeNames) {
        this.xmlSchema = xmlSchema;
        this.relaxng = relaxng;
        this.patternInformation = patternInformation;
        this.usedLocalNames = usedLocalNames;
        this.usedLocalTypeNames = usedLocalTypeNames;
    }

    /**
     * Getter for the RelaxNGSchema
     * This can be modified during the conversion process, to replenish
     * necessary information or bind external schemas to the root schema.
     * @return RelaxNGSchema        Current version of the RelaxNGSchema used as source of the conversion
     */
    public RelaxNGSchema getRelaxNGSchema() {
        return relaxng;
    }

    /**
     * Getter for the XML XSDSchema
     * @return XSDSchema        Returns the result of the conversion progress.
     */
    public XSDSchema getXmlSchema() {
        return xmlSchema;
    }

    /**
     * Update or create a foreign schema object according to the given namespace
     * @param namespace          String namespace as key for the imported namespace
     * @return ImportedSchema    Imported schema with the given namespace
     * @throws Exception
     */
    protected ImportedSchema updateOrCreateImportedSchema(Namespace namespace) throws Exception {
        // Check if there is already an imported schema for the current namespace and return it.
        for (Iterator<ForeignSchema> it1 = this.xmlSchema.getForeignSchemas().iterator(); it1.hasNext();) {
            ForeignSchema foreignSchema = (ImportedSchema) it1.next();
            if (foreignSchema instanceof ImportedSchema) {
                ImportedSchema importedSchema = (ImportedSchema) foreignSchema;
                if (importedSchema.getNamespace().equals(namespace)) {
                    return importedSchema;
                }
            }
        }


        // If there is no imported schema for the given namespace, create a new one
//        if (this.xmlSchema.getNamespaceList().getNamespaceByUri(namespace).getIdentifier() == null) {
//            IdentifiedNamespace newIdentifiedNamespace = new IdentifiedNamespace("ns_" + this.xmlSchema.getNamespaceList().getIdentifiedNamespaces().size(), namespace);
//            this.xmlSchema.getNamespaceList().addIdentifiedNamespace(newIdentifiedNamespace);
//        }

        String schemaLocation = "importedSchema_" + namespace.getIdentifier() + ".xsd";

        if (this.xmlSchema.getSchemaLocation() != null) {
            schemaLocation = this.getPathFromLocation(this.xmlSchema.getSchemaLocation()) + "/" + schemaLocation;
        }

        ImportedSchema importedSchema = new ImportedSchema(namespace, schemaLocation);
        importedSchema.setParentSchema(this.xmlSchema);
        XSDSchema newExternalSchema = new XSDSchema();
        newExternalSchema.setTargetNamespace(namespace);
        newExternalSchema.setSchemaLocation(schemaLocation);

        // Set the XML XSDSchema namespace to the identifiedNamespacelist of the new schema
        IdentifiedNamespace xmlSchemaIdentifiedNamespace = new IdentifiedNamespace("xs", XSDSchema.XMLSCHEMA_NAMESPACE);
        newExternalSchema.addIdentifiedNamespace(xmlSchemaIdentifiedNamespace);

        importedSchema.setSchema(newExternalSchema);
        this.xmlSchema.addForeignSchema(importedSchema);

        // return the new imported schema
        return importedSchema;
    }

    /**
     * Getter for the global set of all usedLocalNames
     * @return HashSet<String>      used local names
     */
    public HashSet<String> getUsedLocalNames() {
        return usedLocalNames;
    }

    /**
     * Getter for the global set of all usedLocalTypeNames
     * @return HashSet<String>      used local type names
     */
    public HashSet<String> getUsedLocalTypeNames() {
        return usedLocalTypeNames;
    }

    /**
     * Getter for the global pattern information map
     * @return HashMap<Pattern, HashSet<String>>        pattern information map
     */
    public HashMap<Pattern, HashSet<String>> getPatternInformation() {
        return patternInformation;
    }

    protected String getPathFromLocation(String resultLocationFilename) {

        resultLocationFilename = this.getCanonicalPath(resultLocationFilename);
        int sep = resultLocationFilename.lastIndexOf("/");
        if (sep == -1) {
            sep = 0;
        }
        return resultLocationFilename.substring(0, sep);
    }

    protected String getCanonicalPath(String path) {

        while (path.contains("/./")) {
            path = path.replaceFirst("[/][.][/]", "/");
        }

        while (path.contains("../")) {
            int pos = path.indexOf("../");
            String pathBefore = path.substring(0, pos - 1);
            String pathAfter = path.substring(pos + 2, path.length());
            int posLastSlash = pathBefore.lastIndexOf("/");
            pathBefore = pathBefore.substring(0, posLastSlash);
            path = pathBefore + pathAfter;
        }

        return path;
    }
}
