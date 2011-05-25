package de.tudortmund.cs.bonxai.converter;

import de.tudortmund.cs.bonxai.xsd.Group;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.common.*;

public class TestSchemaGenerator {
    public static void addGlobalElementWithComplexTypeToSchemaNS( XSDSchema schema ) throws SymbolAlreadyRegisteredException {
        schema.getTypeSymbolTable().createReference(
            "{}complex Type",
            new ComplexType(
                "{}complex Type",
                new ComplexContentType()
            )
        );

        de.tudortmund.cs.bonxai.xsd.Element element = new de.tudortmund.cs.bonxai.xsd.Element( "http://example.com/mynamespace", "element" );
        element.setType( schema.getTypeSymbolTable().getReference( "{}complex Type" ) );
        schema.getElementSymbolTable().createReference( "{}element", element );
        schema.addElement( schema.getElementSymbolTable().getReference( "{}element" ) );
    }

    public static void addGlobalAttributeToSchema( XSDSchema schema ) throws SymbolAlreadyRegisteredException {
        Attribute attribute = new Attribute( "{}attribute" );
        attribute.setSimpleType(
            schema.getTypeSymbolTable().getReference( "string" )
        );
        schema.getAttributeSymbolTable().createReference(
            "attribute",
            attribute
        );
        schema.addAttribute(
            schema.getAttributeSymbolTable().getReference( "attribute" )
        );
    }

    public static void addAttributeGroupToSchema( XSDSchema schema ) throws SymbolAlreadyRegisteredException {
        Attribute attribute = new Attribute( "{}attribute" );
        attribute.setSimpleType(
            schema.getTypeSymbolTable().getReference( "string" )
        );
        AttributeGroup group = new AttributeGroup( "{}attribute group" );
        group.addAttributeParticle( attribute );

        schema.getAttributeGroupSymbolTable().createReference( "{}attribute group", group );

        schema.addAttributeGroup( schema.getAttributeGroupSymbolTable().getReference( "{}attribute group" ) );
    }

    public static void addGroupToSchema( XSDSchema schema ) throws SymbolAlreadyRegisteredException {
        de.tudortmund.cs.bonxai.xsd.Element element = new de.tudortmund.cs.bonxai.xsd.Element( "http://example.com/mynamespace", "element" );

        SequencePattern sequence = new SequencePattern();

        Group group = new Group(
            "{}group",
            sequence
        );

        schema.getGroupSymbolTable().createReference( "{}group", group );

        schema.addGroup( schema.getGroupSymbolTable().getReference( "{}group" ) );
    }

    public static void addTypeToSchema( XSDSchema schema )
    throws SymbolAlreadyRegisteredException {
        schema.getTypeSymbolTable().createReference(
            "{}complex Type",
            new ComplexType(
                "{}complex Type",
                new ComplexContentType()
            )
        );
    }

    public static AttributeGroup createAttributeGroup() {
        Attribute attribute = new Attribute( "{}attribute" );
        attribute.setSimpleType(
            new SymbolTableRef<Type>( "string", null )
        );
        AttributeGroup group = new AttributeGroup( "{}attribute group" );
        group.addAttributeParticle( attribute );

        return group;
    }

    public static Group createGroup() {
        de.tudortmund.cs.bonxai.xsd.Element element = new de.tudortmund.cs.bonxai.xsd.Element( "http://example.com/mynamespace", "element" );
        element.setType(
            new SymbolTableRef<Type>(
                "{}complex Type",
                new ComplexType(
                    "{}complex Type",
                    new ComplexContentType()
                )
            )
        );

        SequencePattern sequence = new SequencePattern();
        sequence.addParticle( element );

        Group group = new Group(
            "{}group",
            sequence
        );

        return group;
    }

    public static ComplexType createComplexType( String name ) {
        ComplexType ct = new ComplexType(
            name,
            new ComplexContentType()
        );
        return ct;
    }
}
