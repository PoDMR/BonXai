package uh.df.xsd.analysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;

public class AdaptedTypeAnalysis {

	protected Map<XSDTypeDefinition, String> typeNames = new HashMap<XSDTypeDefinition, String>();

	protected Stack<String> namePath = new Stack<String>();

	protected Map<String, Set<XSDTypeDefinition>> typeMap = new HashMap<String, Set<XSDTypeDefinition>>();

	protected Map<String, Set<Occurrence>> occurrenceMap = new HashMap<String, Set<Occurrence>>();

	public AdaptedTypeAnalysis(XSDSchema xsd) {
		super();
		for (Iterator<XSDTypeDefinition> typeDefIt = xsd.getTypeDefinitions().iterator(); typeDefIt.hasNext();) {
			XSDTypeDefinition typeDef = typeDefIt.next();
			typeNames.put(typeDef, typeDef.getURI());
			findLocalTypes(typeDef);
		}
		for (Iterator<XSDElementDeclaration> elementIt = xsd.getElementDeclarations().iterator(); elementIt.hasNext();) {
			XSDElementDeclaration element = elementIt.next();
			findLocalTypes(element);
		}
	}

	public Iterator<String> getElementNameIterator() {
		return typeMap.keySet().iterator();
	}

	public Set<XSDTypeDefinition> getTypes(String elementName) {
		if (typeMap.containsKey(elementName))
			return Collections.unmodifiableSet(typeMap.get(elementName));
		else
			return new HashSet<XSDTypeDefinition>();
	}

	public Iterator<XSDTypeDefinition> getTypeDefIterator() {
		return typeNames.keySet().iterator();
	}

	public String getTypeName(XSDTypeDefinition typeDef) {
		return typeNames.get(typeDef);
	}

	public Set<Occurrence> getOccurrence(String name) {
		if (occurrenceMap.containsKey(name))
			return Collections.unmodifiableSet(occurrenceMap.get(name));
		else
			return new HashSet<Occurrence>();
	}

	public boolean foundTrueSingleTypeDefs() {
		for (Iterator<String> elementNameIt = getElementNameIterator(); elementNameIt.hasNext();) {
			Set<XSDTypeDefinition> types = getTypes(elementNameIt.next());
			if (types.size() > 1)
				return true;
		}
		return false;
	}

	public Set<String> getTrueSingleTypeNames() {
		Set<String> singleTypes = new HashSet<String>();
		for (Iterator<String> elementNameIt = getElementNameIterator(); elementNameIt.hasNext();) {
			String elementName = elementNameIt.next();
			Set<XSDTypeDefinition> types = getTypes(elementName);
			if (types.size() > 1)
				singleTypes.add(elementName);
		}
		return singleTypes;
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
				if (!typeMap.containsKey(name)) {
					typeMap.put(name, new HashSet<XSDTypeDefinition>());
					occurrenceMap.put(name, new HashSet<Occurrence>());
				}
				if (!typeMap.get(name).contains(typeDef)) {
					typeMap.get(name).add(typeDef);
					occurrenceMap.get(name).add(new Occurrence(name, typeName(), typeDef));
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
			for (Iterator<XSDParticle> contentsIt = modelGroup.getParticles().iterator(); contentsIt.hasNext();) {
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
		if (!typeMap.containsKey(elementName)) {
			typeMap.put(elementName, new HashSet<XSDTypeDefinition>());
			occurrenceMap.put(elementName, new HashSet<Occurrence>());
		}
		if (!typeMap.get(elementName).contains(localTypeDef)) {
			typeMap.get(elementName).add(localTypeDef);
			occurrenceMap.get(elementName).add(new Occurrence(elementName, typeName(), localTypeDef));
		}
		namePath.pop();
	}

	protected String typeName() {
		return StringUtils.join(namePath.iterator(), "/");
	}

	protected static class Occurrence {

		protected String name;

		protected String context;

		protected XSDTypeDefinition typeDef;

		protected Occurrence(String name, String context, XSDTypeDefinition typeDef) {
			this.name = name;
			this.context = context;
			this.typeDef = typeDef;
		}

		public String getContext() {
			return context;
		}

		public String getName() {
			return name;
		}

		public XSDTypeDefinition getTypeDef() {
			return typeDef;
		}

	}

}
