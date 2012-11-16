/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.converter.relaxng2xsd;

import eu.fox7.schematoolkit.common.AnyAttribute;
import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.NamespaceList;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.converter.relaxng2xsd.exceptions.PatternNotAllowedException;
import eu.fox7.schematoolkit.relaxng.om.*;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * Class NameClassAnalyzer
 * @author Lars Schmidt
 */
public class NameClassAnalyzer {

    // The nameclass as basis of the generation
    private final NameClass nameClass;
    // Store the generated names
    private LinkedHashSet<QualifiedName> names;
    // Namespaces for anyPattern
    private LinkedHashSet<Namespace> namespaces;
    // The pattern as the bases of the generation
    private Pattern pattern = null;

    /**
     * Constructor of class NameClassAnalyzer
     * @param pattern
     * @throws PatternNotAllowedException
     */
    public NameClassAnalyzer(Pattern pattern) throws PatternNotAllowedException {
        this.names = new LinkedHashSet<QualifiedName>();
        this.pattern = pattern;

        String nameAttribute = null;
        NamespaceList namespaceList = null;
        Namespace attributeNamespace = null;
        NameClass localNameClass = null;

        if (pattern instanceof Attribute) {
            Attribute attribute = (Attribute) pattern;
            nameAttribute = attribute.getNameAttribute();
            attributeNamespace = attribute.getAttributeNamespace();
            namespaceList = attribute.getNamespaceList();
            localNameClass = attribute.getNameClass();
        } else if (pattern instanceof Element) {
            Element element = (Element) pattern;
            nameAttribute = element.getNameAttribute();
            attributeNamespace = element.getAttributeNamespace();
            namespaceList = element.getNamespaceList();
            localNameClass = element.getNameClass();
        } else {
            // Exception: The current pattern is not allowed in this context.
            throw new PatternNotAllowedException(pattern.getClass().getName(), "analyze attribute or element nameClasses");
        }
        

        if (nameAttribute != null) {
            this.nameClass = null;

            String[] nameArray = null;
            if (nameAttribute.contains(":")) {
                nameArray = nameAttribute.split(":");
            }

            String localname;
            Namespace namespace;
            if (nameArray != null && nameArray.length == 2) {
                // Namespace handling
                namespace = namespaceList.getNamespaceByIdentifier(nameArray[0]);
                localname = nameArray[1];
            } else {
            	namespace = attributeNamespace;
                localname = nameAttribute;
            }
            this.names.add(new QualifiedName(namespace, localname));
        } else {
            this.nameClass = localNameClass;
        }
    }

    /**
     * Method: analyze
     *
     * This is the main method of this class for starting the analysis
     *
     * @return LinkedHashMap<String, Object>
     */
    public void analyze() {
        if (this.nameClass != null) {

            LinkedHashSet<NameClass> nameClasses = new LinkedHashSet<NameClass>();
            nameClasses.add(this.nameClass);
            this.recurseNameClasses(nameClasses, true, null);
            addNamesToNameInfoMap();
        }
    }

    /**
     * Getter for the generated name information
     * @return LinkedHashMap<String, Object>    nameInfos
     */
    public LinkedHashSet<QualifiedName> getNames() {
        return this.names;
    }

    /**
     * Recursive method for generating the information about the given nameclasses
     * @param nameClasses
     * @param positive
     * @param parentNamespace
     */
    private void recurseNameClasses(LinkedHashSet<NameClass> nameClasses, String parentNamespace) {
        for (NameClass currentNameClass: nameClasses) {
           if (currentNameClass instanceof Name) {
        	   Name name = (Name) currentNameClass;
        	   QualifiedName qName = convertToQualifiedName(name);
        	   this.names.add(qName);
           } else if (currentNameClass instanceof NsName) {
                NsName nsName = (NsName) currentNameClass;

                Namespace namespace = nsName.getAttributeNamespace();
                if (namespace == null || namespace.equals("")) {
                    namespace = Namespace.LOCAL_NAMESPACE;
                }
                
                this.nameSet.add(namespace);

                recurseNameClasses(nsName.getExceptNames(), !positive, namespace);
            } else if (currentNameClass instanceof AnyName) {
            	AnyName anyName = (AnyName) currentNameClass;

            	if (anyName.getExceptNames().isEmpty()) {
            		this.nameSet.add("##any");
            	} else {
            		recurseNameClasses(anyName.getExceptNames(), !positive, "##any");
            	}
            } else if (currentNameClass instanceof NameClassChoice) {
                NameClassChoice nameClassChoice = (NameClassChoice) currentNameClass;
                recurseNameClasses(nameClassChoice.getChoiceNames(), parentNamespace);
            }
        }
    }

    private QualifiedName convertToQualifiedName(Name name) {
        String namespaceURI = name.getAttributeNamespace().getUri();

        String[] nameArray = null;
        if (name.getContent().contains(":")) {
            nameArray = name.getContent().split(":");
        }
        
        String localname;
        Namespace idNamespace;
        if (nameArray != null && nameArray.length == 2) {
            // Namespace handling
            idNamespace = name.getNamespaceList().getNamespaceByIdentifier(nameArray[0]);
            localname = nameArray[1];
        } else {
            localname = name.getContent();
            idNamespace = name.getNamespaceList().getNamespaceByIdentifier(namespaceURI);
        }
		return new QualifiedName(idNamespace, localname);
	}
}
