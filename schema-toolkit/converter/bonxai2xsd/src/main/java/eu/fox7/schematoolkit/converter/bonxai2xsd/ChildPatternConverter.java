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

package eu.fox7.schematoolkit.converter.bonxai2xsd;


import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.schematoolkit.bonxai.om.Bonxai;
import eu.fox7.schematoolkit.bonxai.om.ChildPattern;
import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.common.DefaultNamespace;
import eu.fox7.schematoolkit.common.EmptyPattern;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.typeautomaton.TypeAutomaton;
import eu.fox7.schematoolkit.xsd.om.ComplexContentType;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.SimpleContentExtension;
import eu.fox7.schematoolkit.xsd.om.SimpleContentType;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

public class ChildPatternConverter {
	
	ParticleConverter converter;
    private DefaultNamespace targetNamespace;
    public ChildPatternConverter(TypeAutomaton typeAutomaton, DefaultNamespace namespace, boolean eliminateGroups) {
    	this.targetNamespace = namespace;
    	this.converter = new ParticleConverter(typeAutomaton, eliminateGroups);
    }
    
    /**
     * Convert a child pattern to an XSD Type.
     *
     * @return Type
     */
    public Type convertChildPattern(ChildPattern childPattern, QualifiedName typename, State sourceState) {
        Type type = null;
        // Convert simple types, if no attributes are set, otherwise it will
        // get a bit more complicated...
        if ((childPattern.getElementPattern() != null) &&
            (childPattern.getElementPattern().getBonxaiType() != null)) {
            QualifiedName typeName = childPattern.getElementPattern().getBonxaiType().getTypename();

        	Type simpleType = new SimpleType(typeName, null);
        	simpleType.setIsAnonymous(true);

            if (childPattern.getAttributePattern() == null ) {
                type = null;
            } else {
                SimpleContentExtension extension = new SimpleContentExtension(typeName);

                SimpleContentType content = new SimpleContentType();
                content.setInheritance(extension);

                ComplexType complexType = new ComplexType(typeName, content);

                for (AttributeParticle attrParticle: converter.convertParticle(childPattern.getAttributePattern())) {
                    complexType.addAttribute(attrParticle);
                }

                type = complexType;
            }
        }

        // Convert complex types
        if ((childPattern.getElementPattern() != null) &&
            (childPattern.getElementPattern().getRegexp() != null)) {

            ComplexContentType content = new ComplexContentType();
            content.setParticle(converter.convertParticle(targetNamespace, childPattern.getElementPattern().getRegexp(), sourceState));
            content.setMixed(childPattern.isMixed());
            ComplexType complexType = new ComplexType(typename, content);
         

            // Append attributes, if existing
            if (childPattern.getAttributePattern() != null)
            {
                for (AttributeParticle attrParticle: converter.convertParticle(childPattern.getAttributePattern())) {
                    complexType.addAttribute(attrParticle);
                }
            }

            complexType.setIsAnonymous(false);
            
            type = complexType;
        }

        // Convert attributes, if no element pattern has been specified
        if ((childPattern.getAttributePattern() != null) &&
            ((childPattern.getElementPattern() == null) || (childPattern.getElementPattern().getRegexp() instanceof EmptyPattern))) {
            ComplexType complexType = new ComplexType(typename, null);
            for (AttributeParticle attrParticle: converter.convertParticle(childPattern.getAttributePattern())) {
                complexType.addAttribute(attrParticle);
            }
            
            complexType.setIsAnonymous(false);
            
            type = complexType;
        }
        
        return type;
    }
    
    public void convertGroups(Bonxai bonxai, XSDSchema xsdSchema) {
    	this.converter.convertGroups(bonxai, xsdSchema);
    }
 }
