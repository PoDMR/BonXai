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
package eu.fox7.schematoolkit.bonxai.om;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaLanguage;
import eu.fox7.schematoolkit.bonxai.BonxaiSchemaHandler;
import eu.fox7.schematoolkit.common.*;

/**
 * Class representing an Bonxai XSDSchema
 */
public class Bonxai implements Schema {

    private static Set<String> keywords = null;
    
    public static boolean isKeyword(String string) {
    	if (keywords==null) {
    		keywords = new HashSet<String>();
    		keywords.add("empty");
    		keywords.add("missing");
    		keywords.add("fixed");
    		keywords.add("default");
    		keywords.add("mixed");
    		keywords.add("strict");
    		keywords.add("lax");
    		keywords.add("skip");
    		keywords.add("*");
    		keywords.add("type");
    		keywords.add("group");
    		keywords.add("groups");
    		keywords.add("attribute-group");
    		keywords.add("namespace");
    		keywords.add("value");
    		keywords.add("datatypes");
    		keywords.add("import");
    		keywords.add("unique");
    		keywords.add("key");
    		keywords.add("keyref");
    		keywords.add("grammar");
    		keywords.add("element");
    		keywords.add("attribute");
    	} 
    	return keywords.contains(string);
    }
 
	
    /**
     * List of constrains
     */
    private List<Constraint> constraints = new LinkedList<Constraint>();
    /**
     * Grammar part of the schema
     */
    private List<Expression> expressions = new LinkedList<Expression>();;
    /**
     * Group part of the schema
     */
    private List<BonxaiAbstractGroup> groups = new LinkedList<BonxaiAbstractGroup>();

    /**
     * TODO: DatatypeList
     */
    
    /**
     * RootElementList
     */
    private List<QualifiedName> rootElementNames = new LinkedList<QualifiedName>();

    private NamespaceList namespaceList = new NamespaceList();
    
    /**
     * Construct Bonxai schema.
     *
     * Initializes the required symbol tables.
     */
    public Bonxai() {
    }

   
    /**
     * Returns the list of constraints
     * @return constraints
     */
    public List<Constraint> getConstraints() {
        return constraints;
    }

    /**
     * Sets the list of constrains
     * @param constraints
     */
    public void setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
    }
    
    /**
     * Add a constraint
     * @param constraint
     */
    public void addConstraint(Constraint constraint) {
    	this.constraints.add(constraint);
    }
    
    

    /**
     * Returns the list of rules
     * @return expressions
     */
    public List<Expression> getExpressions() {
        return expressions;
    }

    /**
     * Sets the rules
     * @param expressions
     */
    public void setExpressions(List<Expression> expressions) {
//        this.removeChildren(this.expressions);
    	this.expressions = expressions;
//    	this.addChildren(expressions);
    }
    
    /**
     * Adds an expression
     * @param expression
     */
    public void addExpression(Expression expression) {
    	this.expressions.add(expression);
//    	this.addChild(expression);
    }

    /**
     * Returns the list of groups
     * @return groups
     */
    public List<BonxaiAbstractGroup> getGroups() {
        return groups;
    }

    /**
     * Adds a group
     * @param group
     */
    public void addGroup(BonxaiAbstractGroup group) {
    	this.groups.add(group);
    }
    
	public void setDefaultNamespace(DefaultNamespace defaultNamespace) {
		this.namespaceList.setDefaultNamespace(defaultNamespace);
	}

	public DefaultNamespace getDefaultNamespace() {
		return this.namespaceList.getDefaultNamespace();
	}

	public List<IdentifiedNamespace> getNamespaces() {
		return this.namespaceList.getNamespaces();
	}
	
	public void addNamespace(IdentifiedNamespace namespace) {
		this.namespaceList.addNamespace(namespace);
	}
	
	public NamespaceList getNAmespaceList() {
		return this.namespaceList;
	}

	/**
	 * @param rootElementNames the rootElementNames to set
	 */
	public void setRootElementNames(List<QualifiedName> rootElementNames) {
		this.rootElementNames = rootElementNames;
	}

	/**
	 * @return the rootElementNames
	 */
	public List<QualifiedName> getRootElementNames() {
		return rootElementNames;
	}
	
	public void addRootElementName(QualifiedName rootElement) {
		this.rootElementNames.add(rootElement);
	}

	public Namespace getNamespaceByIdentifier(String identifier) {
		return this.namespaceList.getNamespaceByIdentifier(identifier);
	}
	
	/*
	 * returns a namespace object, given a uri. If there are several copies 
	 * of the same namespace (with different identifiers), one of them is returned.
	 * null is returned if the namespace does not exists.
	 * @param uri
	 * @return namespace 
	 */
	public Namespace getNamespaceByUri(String uri) {
		return this.namespaceList.getNamespaceByUri(uri);
	}

	public BonxaiAttributeGroup getAttributeGroup(QualifiedName name) {
		for (BonxaiAbstractGroup group: this.groups) {
			if ((group  instanceof BonxaiAttributeGroup) && group.getName().equals(name))
				return (BonxaiAttributeGroup) group;
		}
		return null;
	}

	public BonxaiGroup getElementGroup(QualifiedName name) {
		for (BonxaiAbstractGroup group: this.groups) {
			if ((group  instanceof BonxaiGroup) && group.getName().equals(name))
				return (BonxaiGroup) group;
		}
		return null;
	}


	@Override
	public SchemaHandler getSchemaHandler() {
		return new BonxaiSchemaHandler(this);
	}


	@Override
	public SchemaLanguage getSchemaLanguage() {
		return SchemaLanguage.BONXAI;
	}

}