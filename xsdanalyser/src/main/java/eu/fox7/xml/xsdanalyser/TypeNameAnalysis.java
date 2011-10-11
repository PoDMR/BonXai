/*
 * Created on Apr 28, 2006
 * Modified on $Date: 2009-05-06 14:05:19 $
 */
package eu.fox7.xml.xsdanalyser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;

/**
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
public class TypeNameAnalysis {

    protected Map<XSDTypeDefinition,String> typeNames = new HashMap<XSDTypeDefinition,String>();
    protected Stack<String> namePath = new Stack<String>();

    public TypeNameAnalysis(XSDSchema xsd) {
        super();
        for (Iterator<XSDTypeDefinition> typeDefIt = xsd.getTypeDefinitions().iterator();
             typeDefIt.hasNext(); ) {
            XSDTypeDefinition typeDef = typeDefIt.next();
            typeNames.put(typeDef, typeDef.getURI());
            findLocalTypes(typeDef);
        }
        for (Iterator<XSDElementDeclaration> elementIt = xsd.getElementDeclarations().iterator();
             elementIt.hasNext(); ) {
            XSDElementDeclaration element = elementIt.next();
            findLocalTypes(element);
        }
    }

    public Iterator<XSDTypeDefinition> getTypeDefIterator() {
        return typeNames.keySet().iterator();
    }

    public String getTypeName(XSDTypeDefinition typeDef) {
        return typeNames.get(typeDef);
    }

    protected void findLocalTypes(XSDTypeDefinition typeDef) {
        if (typeDef instanceof XSDComplexTypeDefinition) {
            if (typeDef.getURI() != null)
                namePath.push(typeDef.getURI());
            XSDParticle particle = typeDef.getComplexType();
            if (particle != null) {
                findLocalTypes(particle.getTerm());
            } else {
                String name = typeDef.getURI() != null ? typeDef.getURI() : typeName();
                if (!typeNames.containsKey(typeDef)) {
                    typeNames.put(typeDef, name);
                }
            }
            if (typeDef.getURI() != null)
                namePath.pop();
        }
    }

    protected void findLocalTypes(XSDTerm term) {
        if (term instanceof XSDElementDeclaration) {
            XSDElementDeclaration element = (XSDElementDeclaration) term;
            findLocalTypes(element);
        } else if (term instanceof XSDModelGroup) {
            XSDModelGroup modelGroup = (XSDModelGroup) term;
            for (Iterator<XSDParticle> contentsIt = modelGroup.getParticles().iterator();
                 contentsIt.hasNext(); ) {
                XSDParticle contentParticle = contentsIt.next();
                findLocalTypes(contentParticle.getTerm());
            }
        }
    }

    protected void findLocalTypes(XSDElementDeclaration element) {
        String elementName = element.getURI();
        namePath.push(elementName);
        XSDTypeDefinition localTypeDef = element.getTypeDefinition();
        if (!typeNames.containsKey(localTypeDef)) {
            typeNames.put(localTypeDef, typeName());
            findLocalTypes(localTypeDef);
        }
        namePath.pop();
    }

    protected String typeName() {
        return StringUtils.join(namePath.iterator(), "/");
    }

}
