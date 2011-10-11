/*
 * Created on Apr 28, 2006
 * Modified on $Date: 2009-05-06 14:05:19 $
 */
package eu.fox7.xml.xsdanalyser;

import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.Tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWildcard;

/**
 * @author gjb
 * @version $Revision: 1.4 $
 * 
 */
public class RegexAnalysis {

    protected Map<XSDTypeDefinition,Tree> regexes = new HashMap<XSDTypeDefinition,Tree>();
    protected Set<XSDTypeDefinition> typesDone = new HashSet<XSDTypeDefinition>();

    public RegexAnalysis(XSDSchema xsd) {
        super();
        for (Iterator<XSDTypeDefinition> typeDefIt = xsd.getTypeDefinitions().iterator();
             typeDefIt.hasNext(); )
            regex(typeDefIt.next());
        for (Iterator<XSDElementDeclaration> elementIt = xsd.getElementDeclarations().iterator();
             elementIt.hasNext(); )
            regex(elementIt.next());
    }

    public Iterator<XSDTypeDefinition> getTypeDefIterator() {
        return regexes.keySet().iterator();
    }

    public Tree getRegex(XSDTypeDefinition typeDef) {
        return regexes.get(typeDef);
    }

    public int numberOfRegexes() {
        return regexes.size();
    }

    protected void regex(XSDTypeDefinition typeDef) {
        if (typeDef instanceof XSDComplexTypeDefinition &&
                !regexes.containsKey(typeDef) && !typesDone.contains(typeDef)) {
            typesDone.add(typeDef);
            Tree tree = new Tree();
            XSDParticle particle = typeDef.getComplexType();
            if (particle != null) {
                String multiplicity = multiplicity(particle);
                if (multiplicity.equals(""))
                    tree.setRoot(regex(particle.getTerm()));
                else {
                    Node multiplicityNode = new Node(multiplicity);
                    multiplicityNode.addChild(regex(particle.getTerm()));
                    tree.setRoot(multiplicityNode);
                }
                regexes.put(typeDef, tree);
            }
        }
    }

    protected void regex(XSDElementDeclaration element) {
        XSDTypeDefinition localTypeDef = element.getTypeDefinition();
        if (!regexes.containsKey(localTypeDef)) {
            regex(localTypeDef);
        }
    }


    protected Node regex(XSDTerm term) {
        if (term instanceof XSDElementDeclaration) {
            XSDElementDeclaration element = (XSDElementDeclaration) term;
            XSDTypeDefinition localTypeDef = element.getTypeDefinition();
            regex(element);
            return new Node(elementName(element), localTypeDef);
        } else if (term instanceof XSDModelGroup) {
            Node operatorNode = new Node();
            XSDModelGroup modelGroup = (XSDModelGroup) term;
            String operator = null;
            String compositor = modelGroup.getCompositor().getName();
            if (compositor.equals("sequence")) {
                operator = ".";
            } else if (compositor.equals("choice")) {
                operator = "|";
            } else if (compositor.equals("all")) {
                operator = "%";
            }
            operatorNode.setKey(operator);
            for (Iterator<XSDParticle> contentsIt = modelGroup.getParticles().iterator();
                 contentsIt.hasNext(); ) {
                XSDParticle contentParticle = contentsIt.next();
                String multiplicity = multiplicity(contentParticle);
                Node childNode = regex(contentParticle.getTerm());
                if (multiplicity.equals("")) {
                    operatorNode.addChild(childNode);
                } else {
                    Node multiplicityNode = new Node(multiplicity);
                    multiplicityNode.addChild(childNode);
                    operatorNode.addChild(multiplicityNode);
                }
            }
            return operatorNode;
        } else if (term instanceof XSDWildcard) {
            return new Node("ANY");
        } else {
            throw new RuntimeException();
        }
    }

    protected static String multiplicity(XSDParticle particle) {
        int minOccurs = particle.isSetMinOccurs() ? particle.getMinOccurs() : 1;
        int maxOccurs = particle.isSetMaxOccurs() ? particle.getMaxOccurs() : 1;
        if (minOccurs == 1 && maxOccurs == 1) {
            return "";
        } else if (minOccurs == 0 && maxOccurs == 1) {
            return "?";
        } else if (maxOccurs == XSDParticle.UNBOUNDED) {
            if (minOccurs == 0) {
                return "*";
            } else if (minOccurs == 1) {
                return "+";
            } else {
                return "{" + minOccurs + ",}";
            }
        } else {
            return "{" + minOccurs + "," + maxOccurs + "}";
        }
    }

    protected String elementName(XSDElementDeclaration element) {
        return element.getURI();
    }

}
