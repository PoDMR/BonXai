/*
 * Created on May 1, 2006
 * Modified on $Date: 2010-01-29 14:40:28 $
 */
package eu.fox7.xml.xsdanalyser;

import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.Tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.xsd.XSDTypeDefinition;

/**
 * @author gjb
 * @version $Revision: 1.3 $
 * 
 */
public class SingleTypeCandidateAnalysis {

    protected RegexAnalysis regexAnalysis;
    protected Map<String,Set<String>> parentMap = new HashMap<String,Set<String>>();
    protected Set<String> candidates = new HashSet<String>();

    public SingleTypeCandidateAnalysis(RegexAnalysis regexAnalysis) {
        this.regexAnalysis = regexAnalysis;
        for (Iterator<XSDTypeDefinition> typeDefIt = regexAnalysis.getTypeDefIterator(); 
             typeDefIt.hasNext(); ) {
            findParents(typeDefIt.next());
        }
        for (String childName : parentMap.keySet())
            if (parentMap.get(childName).size() > 1)
                candidates.add(childName);
    }

    public Iterator<String> getElementIterator() {
        return parentMap.keySet().iterator();
    }

    public Set<String> getParents(String childName) {
        return parentMap.get(childName);
    }

    public Iterator<String> getCandidateIterator() {
        return candidates.iterator();
    }

    public int numberOfCandidates() {
        return candidates.size();
    }

    public boolean hasSingleTypeCandidates() {
        return numberOfCandidates() > 0;
    }

    protected void findParents(XSDTypeDefinition typeDef) {
        Tree tree = regexAnalysis.getRegex(typeDef);
        if (tree != null) {
            findParents(tree.getRoot());
        }
    }

    protected void findParents(Node node) {
        if (node.isLeaf()) {
            String elementName = node.getKey();
            if (node.getValue() != null) {
                XSDTypeDefinition typeDef = (XSDTypeDefinition) node.getValue();
                Tree tree = regexAnalysis.getRegex(typeDef);
                if (tree != null && tree.getRoot() != null)
                    findChildren(elementName, tree.getRoot());
            }
        } else {
            for (int i = 0; i < node.getNumberOfChildren(); i++)
                findParents(node.child(i));
        }
    }

    protected void findChildren(String parentName, Node node) {
        if (node.isLeaf()) {
            String childName = node.getKey();
            if (!parentMap.containsKey(childName))
                parentMap.put(childName, new HashSet<String>());
            parentMap.get(childName).add(parentName);
        } else {
            for (int i = 0; i < node.getNumberOfChildren(); i++)
                findChildren(parentName, node.child(i));
        }
    }

}
