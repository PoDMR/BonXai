/*
 * Created on Feb 28, 2007
 * Modified on $Date: 2009-05-06 14:05:19 $
 */
package eu.fox7.xml.xsdanalyser;

import java.util.HashSet;
import java.util.Iterator;
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
 * @version $Revision: 1.2 $
 * 
 */
public class ElementNameAnalysis {

    protected Set<String> elementNames = new HashSet<String>();
    protected Set<XSDTypeDefinition> typesDone = new HashSet<XSDTypeDefinition>();

    public ElementNameAnalysis(XSDSchema xsd) {
        super();
        for (Iterator<XSDTypeDefinition> typeDefIt = xsd.getTypeDefinitions().iterator();
             typeDefIt.hasNext(); )
             elementNames(typeDefIt.next());
        for (Iterator<XSDElementDeclaration> elementIt = xsd.getElementDeclarations().iterator();
             elementIt.hasNext(); )
             elementNames(elementIt.next());
    }

    public int getNumberOfElements() {
        return elementNames.size();
    }

    public Iterator<String> getElementNameIterator() {
        return elementNames.iterator();
    }

    protected void elementNames(XSDTypeDefinition typeDef) {
        if (typeDef instanceof XSDComplexTypeDefinition &&
                !typesDone.contains(typeDef)) {
            typesDone.add(typeDef);
            XSDParticle particle = typeDef.getComplexType();
            if (particle != null)
                elementNames(particle.getTerm());
        }
    }

    protected void elementNames(XSDElementDeclaration element) {
        elementNames.add(element.getQName());
        XSDTypeDefinition localTypeDef = element.getTypeDefinition();
        elementNames(localTypeDef);
    }


    protected void elementNames(XSDTerm term) {
        if (term instanceof XSDElementDeclaration) {
            elementNames((XSDElementDeclaration) term);
        } else if (term instanceof XSDModelGroup) {
            XSDModelGroup modelGroup = (XSDModelGroup) term;
            for (Iterator<XSDParticle> contentsIt = modelGroup.getParticles().iterator();
                 contentsIt.hasNext(); ) {
                XSDParticle contentParticle = contentsIt.next();
                elementNames(contentParticle.getTerm());
            }
        } else if (term instanceof XSDWildcard) {
        } else {
            throw new RuntimeException();
        }
    }

}
