/*
 * Created on Apr 20, 2006
 * Modified on $Date: 2009-05-06 14:05:19 $
 */
package eu.fox7.xml.xsdanalyser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;

/**
 * @author gjb
 * @version $Revision: 1.9 $
 * 
 */
public class SymbolOccurrenceAnalysis {

    protected TypeNameAnalysis typeNameAnalysis;
    protected Map<XSDTypeDefinition,List<String>> elementNames = new HashMap<XSDTypeDefinition,List<String>>();
    protected Set<XSDTypeDefinition> nonSingleOccurrenceTypeDefs = new HashSet<XSDTypeDefinition>();
    protected Set<XSDTypeDefinition> typesDone = new HashSet<XSDTypeDefinition>();

    public SymbolOccurrenceAnalysis(XSDSchema xsd, TypeNameAnalysis typeNameAnalysis) {
        super();
        this.typeNameAnalysis = typeNameAnalysis;
        for (Iterator<XSDTypeDefinition> typeDefIt = xsd.getTypeDefinitions().iterator();
             typeDefIt.hasNext(); ) {
            XSDTypeDefinition typeDef = typeDefIt.next();
            findElementNames(typeDef);
        }
        for (Iterator<XSDElementDeclaration> elementIt = xsd.getElementDeclarations().iterator();
             elementIt.hasNext(); ) {
            XSDElementDeclaration element = elementIt.next();
            findElementNames(element);
        }
        for (XSDTypeDefinition typeDef : elementNames.keySet()) {
            if (!isSingleOccurrence(elementNames.get(typeDef)))
                nonSingleOccurrenceTypeDefs.add(typeDef);
        }
    }

    public Iterator<XSDTypeDefinition> getTypeDefIterator() {
        return elementNames.keySet().iterator();
    }

    public Iterator<XSDTypeDefinition> getNonSingleOccurrenceTypeDefIterator() {
        return nonSingleOccurrenceTypeDefs.iterator();
    }

    public String getTypeName(XSDTypeDefinition typeDef) {
        return typeNameAnalysis.getTypeName(typeDef);
    }

    public List<String> getSymbols(XSDTypeDefinition typeDef) {
        return elementNames.get(typeDef);
    }

    public boolean foundNonSingleOccurrenceTypeDef() {
        return nonSingleOccurrenceTypeDefs.size() > 0;
    }

    protected void findElementNames(XSDElementDeclaration element) {
        XSDTypeDefinition localTypeDef = element.getTypeDefinition();
        if (!elementNames.containsKey(localTypeDef)) {
            findElementNames(localTypeDef);
        }
    }

    protected void findElementNames(XSDTypeDefinition typeDef) {
        if (typeDef instanceof XSDComplexTypeDefinition &&
                !elementNames.containsKey(typeDef) && !typesDone.contains(typeDef)) {
            typesDone.add(typeDef);
            XSDParticle particle = typeDef.getComplexType();
            if (particle != null)
                elementNames.put(typeDef, findElementNames(particle.getTerm()));
        }
    }

    protected List<String> findElementNames(XSDTerm term) {
        List<String> elementNames = new LinkedList<String>();
        if (term instanceof XSDElementDeclaration) {
            XSDElementDeclaration element = (XSDElementDeclaration) term;
            elementNames.add(elementName(element));
            findElementNames(element);
        } else if (term instanceof XSDModelGroup) {
            XSDModelGroup modelGroup = (XSDModelGroup) term;
            for (Iterator<XSDParticle> contentsIt = modelGroup.getParticles().iterator();
                 contentsIt.hasNext(); ) {
                XSDParticle contentParticle = contentsIt.next();
                elementNames.addAll(findElementNames(contentParticle.getTerm()));
            }
        }
        return elementNames;
    }

    protected String elementName(XSDElementDeclaration element) {
        return element.getURI();
    }

    protected static Map<String,Integer> countOccurrences(List<String> names) {
        Map<String,Integer> count = new HashMap<String,Integer>();
        for (String name : names) {
            if (!count.containsKey(name))
                count.put(name, 0);
            count.put(name, count.get(name) + 1);
        }
        return count;
    }

    protected static boolean isSingleOccurrence(List<String> names) {
        Set<String> set = new HashSet<String>();
        for (String name : names) {
            if (!set.contains(name))
                set.add(name);
            else
                return false;
        }
        return true;
    }

}
