package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import java.util.LinkedList;
import java.util.HashSet;
import java.io.*;

import org.junit.*;
import static org.junit.Assert.*;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.bonxai.Bonxai;

import de.tudortmund.cs.bonxai.bonxai.writer.*;

public class IntegrationTest extends junit.framework.TestCase {

    @Test
    public void testConvertSchema() throws Exception {
        XSDSchema schema = new XSDSchema();
        DefaultNamespace defaultNameSpace = new DefaultNamespace("http://www.example.org/books");
        NamespaceList namespaceList = new NamespaceList(defaultNameSpace);
        namespaceList.addIdentifiedNamespace(new IdentifiedNamespace("xs", "http://www.w3.org/2001/XMLSchema"));
        namespaceList.addIdentifiedNamespace(new IdentifiedNamespace("lib", "http://www.example.org/library"));
        namespaceList.addIdentifiedNamespace(new IdentifiedNamespace("xml", "http://www.w3.org/XML/1998/namespace"));
        schema.setNamespaceList(namespaceList);

        //foreign schemas
        // Removed for now, due to missing parser...
        // schema.addForeignSchema(new ImportedSchema("http://www.w3.org/XML/1998/namespace", "myxml.xsd"));
        // schema.addForeignSchema(new IncludedSchema("character.xsd"));
        // RedefinedSchema redefinedSchema = new RedefinedSchema("character12.xsd");

        SimpleType stringType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SymbolTableRef<Type> stringTypeRef = new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string");
        stringTypeRef.setReference(stringType);

        SimpleContentRestriction simpleContentInheritance = new SimpleContentRestriction(stringTypeRef);
        simpleContentInheritance.setMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(40, false));
        SimpleType nameType = new SimpleType("{http://www.example.org/books}nameType", simpleContentInheritance);

        SymbolTableRef<Type> nameTypeRef = new SymbolTableRef<Type>("{http://www.example.org/books}nameType");
        nameTypeRef.setReference(nameType);
        // redefinedSchema.addType(nameTypeRef);

        // schema.addForeignSchema(redefinedSchema);

        //definition of simple types

        //isbn type
        stringType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.w3.org/2001/XMLSchema}string", stringType);

        simpleContentInheritance = new SimpleContentRestriction(schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        simpleContentInheritance.setPattern(new SimpleContentFixableRestrictionProperty<String>("[0-9]{10}", false));
        SimpleType restrictedStringType = new SimpleType("{http://www.example.org/books}restrictedStringType", simpleContentInheritance);
        SymbolTableRef<Type> restrictedStringTypeRef = new SymbolTableRef<Type>("{http://example.org/books}restrictedStringType");
        restrictedStringTypeRef.setReference(restrictedStringType);

        SimpleType nmTokenType = new SimpleType("{http://www.w3.org/2001/XMLSchema}NMToken", null);
        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.w3.org/2001/XMLSchema}NMToken", nmTokenType);

        simpleContentInheritance = new SimpleContentRestriction(schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}NMToken"));
        LinkedList<String> enumeration = new LinkedList<String>();
        enumeration.add("TBD");
        enumeration.add("NA");
        simpleContentInheritance.addEnumeration(enumeration);
        SimpleType restrictedNMTokenType = new SimpleType("{http://www.example.org/books}restrictedNMTokenType", simpleContentInheritance);
        SymbolTableRef<Type> restrictedNMTokenTypeRef = new SymbolTableRef<Type>("{http://example.org/books}restrictedNMTokenType");
        restrictedNMTokenTypeRef.setReference(restrictedNMTokenType);

        LinkedList<SymbolTableRef<Type>> unionTypes = new LinkedList<SymbolTableRef<Type>>();
        unionTypes.add(restrictedStringTypeRef);
        unionTypes.add(restrictedNMTokenTypeRef);

        SimpleContentUnion simpleContentUnion = new SimpleContentUnion(unionTypes);

        SimpleType isbnType = new SimpleType("{http://www.example.org/books}isbn", simpleContentUnion);
        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.example.org/books}isbn", isbnType);
        schema.addType(schema.getTypeSymbolTable().getReference("{http://www.example.org/books}isbn"));

        //definition of complex types

        //descType
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Skip, "http://www.w3.org/1999/xhtml");
        CountingPattern countingPattern = new CountingPattern(0,null);
        countingPattern.addParticle(anyPattern);

        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(countingPattern);

        ComplexContentType complexContentType = new ComplexContentType();
        complexContentType.setParticle(sequencePattern);
        complexContentType.setMixed(true);
        ComplexType complexType = new ComplexType("{http://www.example.org/books}descType", complexContentType);
        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.example.org/books}descType", complexType);
        schema.addType(schema.getTypeSymbolTable().getReference("{http://www.example.org/books}descType"));

        //characterType
        GroupRef groupRef = new GroupRef(schema.getGroupSymbolTable().getReference("{http://www.example.org/books}nameTypes"));
        sequencePattern = new SequencePattern();
        sequencePattern.addParticle(groupRef);

        SimpleType type = new SimpleType("{http://www.example.org/library}nameType", null);
        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.example.org/library}nameType", type);
        type = new SimpleType("{http://www.example.org/library}sinceType", null);
        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.example.org/library}sinceType", type);

        de.tudortmund.cs.bonxai.xsd.Element element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}friend-of", schema.getTypeSymbolTable().getReference("{http://www.example.org/library}nameType"));
        countingPattern = new CountingPattern(0,null);
        countingPattern.addParticle(element);
        sequencePattern.addParticle(countingPattern);

        element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}since", schema.getTypeSymbolTable().getReference("{http://www.example.org/library}sinceType"));
        sequencePattern.addParticle(element);

        element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}qualification", schema.getTypeSymbolTable().getReference("{http://www.example.org/books}descType"));
        sequencePattern.addParticle(element);

        complexContentType = new ComplexContentType();
        complexContentType.setParticle(sequencePattern);
        complexType = new ComplexType("{http://www.example.org/books}characterType", complexContentType);
        complexType.addFinalModifier(ComplexTypeInheritanceModifier.Restriction);
        complexType.addFinalModifier(ComplexTypeInheritanceModifier.Extension);

        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.example.org/books}characterType", complexType);
        schema.addType(schema.getTypeSymbolTable().getReference("{http://www.example.org/books}characterType"));

        //book type

        //inner title element
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        SimpleContentType simpleContentType = new SimpleContentType();
        simpleContentType.setInheritance(simpleContentExtension);

        Attribute attribute = new Attribute("{http://www.w3.org/XML/1998/namespace}lang", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        schema.getAttributeSymbolTable().updateOrCreateReference("{http://www.w3.org/XML/1998/namespace}lang", attribute);
        AttributeRef attributeRef = new AttributeRef(schema.getAttributeSymbolTable().getReference("{http://www.w3.org/XML/1998/namespace}lang"));
        complexType = new ComplexType("{http://www.example.org/books}titleType", simpleContentType);
        complexType.addAttribute(attributeRef);
        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.example.org/books}titleType", complexType);

        element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}title", schema.getTypeSymbolTable().getReference("{http://www.example.org/books}titleType"));
        AllPattern allPattern = new AllPattern();
        allPattern.addParticle(element);

        //inner author element
        element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}author", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        allPattern.addParticle(element);

        //inner character element
        countingPattern = new CountingPattern(0,1);
        element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}character", schema.getTypeSymbolTable().getReference("{http://www.example.org/books}characterType"));
        countingPattern.addParticle(element);
        allPattern.addParticle(countingPattern);

        complexType = new ComplexType("{http://www.example.org/library}bookType", null);
        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.example.org/library}bookType", complexType);
        ComplexContentExtension complexContentExtension = new ComplexContentExtension(schema.getTypeSymbolTable().getReference("{http://www.example.org/library}bookType"));
        complexContentType = new ComplexContentType();
        complexContentType.setInheritance(complexContentExtension);
        complexContentType.setParticle(allPattern);
        complexType = new ComplexType("{http://www.example.org/books}bookType", complexContentType);

        AttributeGroup attributeGroup = new AttributeGroup("{http://www.example.org/books}bookAttributes");
        schema.getAttributeGroupSymbolTable().updateOrCreateReference("{http://www.example.org/books}bookAttributes", attributeGroup);
        AttributeGroupRef attributeGroupRef = new AttributeGroupRef(schema.getAttributeGroupSymbolTable().getReference("{http://www.example.org/books}bookAttributes"));
        complexType.addAttribute(attributeGroupRef);

        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.example.org/books}bookType", complexType);
        schema.addType(schema.getTypeSymbolTable().getReference("{http://www.example.org/books}bookType"));

        //book element
        element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}book", schema.getTypeSymbolTable().getReference("{http://www.example.org/books}bookType"));

        Key key = new Key("{http://www.example.org/books}charName", "character");
        key.addField("name");
        schema.getKeyAndUniqueSymbolTable().updateOrCreateReference("{http://www.example.org/books}charName", key);
        element.addConstraint(key);

        KeyRef keyRef = new KeyRef("charNameRef", "character", schema.getKeyAndUniqueSymbolTable().getReference("{http://www.example.org/books}charName"));
        keyRef.addField("friend-of");
        element.addConstraint(keyRef);

        schema.getElementSymbolTable().updateOrCreateReference("{http://www.example.org/books}book", element);
        schema.addElement(schema.getElementSymbolTable().getReference("{http://www.example.org/books}book"));

        //element group nameTypes
        ChoicePattern choicePattern = new ChoicePattern();
        element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}name", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        choicePattern.addParticle(element);

        sequencePattern = new SequencePattern();
        element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}firstName", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        sequencePattern.addParticle(element);

        countingPattern = new CountingPattern(0,1);
        element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}middleName", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        countingPattern.addParticle(element);
        sequencePattern.addParticle(countingPattern);

        element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}lastName", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        sequencePattern.addParticle(element);

        choicePattern.addParticle(sequencePattern);

        de.tudortmund.cs.bonxai.xsd.Group group = new de.tudortmund.cs.bonxai.xsd.Group("{http://www.example.org/books}nameTypes", choicePattern);
        schema.getGroupSymbolTable().updateOrCreateReference("{http://www.example.org/books}nameTypes", group);
        schema.addGroup(schema.getGroupSymbolTable().getReference("{http://www.example.org/books}nameTypes"));

        //attribute group bookAttributes
        attributeGroup = new AttributeGroup("{http://www.example.org/books}bookAttributes");
        attribute = new Attribute("{http://www.example.org/books}isbn", schema.getTypeSymbolTable().getReference("{http://www.example.org/books}isbn"));
        attribute.setUse(AttributeUse.Required);
        attributeGroup.addAttributeParticle(attribute);

        attribute = new Attribute("{http://www.example.org/books}available", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        attributeGroup.addAttributeParticle(attribute);

        schema.getAttributeGroupSymbolTable().updateOrCreateReference("{http://www.example.org/books}bookAttributes", attributeGroup);
        schema.addAttributeGroup(schema.getAttributeGroupSymbolTable().getReference("{http://www.example.org/books}bookAttributes"));

        XSD2BonxaiConverter transformer = new XSD2BonxaiConverter(schema);

        Bonxai bonxai = transformer.getBonxai();

        assertSchemaSame(bonxai, "integration");
    }

    /**
     * Compare the given schema instance with the stored output of the given
     * name.
     */
    protected void assertSchemaSame(Bonxai bonxai, String name) {
        de.tudortmund.cs.bonxai.bonxai.writer.Writer writer = new de.tudortmund.cs.bonxai.bonxai.writer.Writer();

        boolean failed  = false;
        String actual   = "";
        String expected = "";

        try {

            actual = writer.write(bonxai, new CompactSyntaxVisitor());

            assertNotNull(actual);

            File file         = new File(
                getClass().getClassLoader().getResource(
                    "de/tudortmund/cs/bonxai/converter/xsd2bonxai/data/" + name + ".bonxai"
                ).getPath()
            );
            FileReader reader = new FileReader(file);
            char[] buffer     = new char[(int) file.length()];
            reader.read(buffer);
            expected = new String(buffer);

            failed = !(expected.equals(actual));
        } catch (Exception e) {
            System.out.println(e);
            failed = true;
        }

        // Store output in temp file, if the test failed.
        if (failed) {
            try{
                FileWriter fstream = new FileWriter("temp/" + name + ".bonxai");
                BufferedWriter out = new BufferedWriter(fstream);
                out.write(actual);
                out.close();
            } catch (IOException e) {
                System.out.println("Could not write test comparision file: " + e);
            }
        }

        assertEquals(expected, actual);
    }
}
