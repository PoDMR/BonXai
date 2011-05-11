
package de.tudortmund.cs.bonxai.bonxai.writer;

import java.io.*;
import java.util.Vector;
import java.util.HashSet;

import org.junit.Test;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.bonxai.*;
import de.tudortmund.cs.bonxai.bonxai.writer.*;

/**
 *
 */
public class CompactSyntaxVisitorTest extends junit.framework.TestCase {

    protected String readFile(String filename) throws IOException {
        File file         = new File("tests/de/tudortmund/cs/bonxai/bonxai/writer/data/" + filename);
        FileReader reader = new FileReader(file);
        char[] buffer     = new char[(int) file.length()];
        reader.read(buffer);
        return new String(buffer);
    }

    @Test
    public void testMinimalSchema() throws IOException {
        Bonxai schema = new Bonxai();
        schema.setDeclaration(new Declaration(
            new ImportList(),
            new DataTypeList(),
            new NamespaceList(new DefaultNamespace("http://example.com/ns"))
        ));
        schema.setGroupList(new GroupList());

        Writer writer = new Writer();
        String output = writer.write(schema, new CompactSyntaxVisitor());

        assertEquals(
            this.readFile("minimal.bonxai"),
            output
        );
    }

    @Test
    public void testIdentifiedNamespacesSchema() throws IOException {
        Bonxai schema = new Bonxai();

        NamespaceList namespaces = new NamespaceList(new DefaultNamespace("http://example.com/ns"));
        namespaces.addIdentifiedNamespace(new IdentifiedNamespace("xsd", "http://example.com/xsd"));
        namespaces.addIdentifiedNamespace(new IdentifiedNamespace("foo", "http://example.com/foo"));

        schema.setDeclaration(new Declaration(
            new ImportList(),
            new DataTypeList(),
            namespaces
        ));
        schema.setGroupList(new GroupList());

        Writer writer = new Writer();
        String output = writer.write(schema, new CompactSyntaxVisitor());

        assertEquals(
            this.readFile("identified_namespaces.bonxai"),
            output
        );
    }

    @Test
    public void testImportSchema() throws IOException {
        Bonxai schema = new Bonxai();

        NamespaceList namespaces = new NamespaceList(new DefaultNamespace("http://example.com/ns"));

        ImportList importList = new ImportList();
        importList.addImport(new Import("http://example.com/foo"));
        importList.addImport(new Import("http://example.com/barUrl", "http://example.com/bar"));

        schema.setDeclaration(new Declaration(
            importList,
            new DataTypeList(),
            namespaces
        ));
        schema.setGroupList(new GroupList());

        Writer writer = new Writer();
        String output = writer.write(schema, new CompactSyntaxVisitor());


        assertEquals(
            this.readFile("import.bonxai"),
            output
        );
    }

    @Test
    public void testDataTypeSchema() throws IOException {
        Bonxai schema = new Bonxai();

        NamespaceList namespaces = new NamespaceList(new DefaultNamespace("http://example.com/ns"));

        DataTypeList dataTypeList = new DataTypeList();
        dataTypeList.addDataType(new DataType("foo", "http://example.com/fooTypes"));
        dataTypeList.addDataType(new DataType("bar", "http://example.com/barTypes"));

        schema.setDeclaration(new Declaration(
            new ImportList(),
            dataTypeList,
            namespaces
        ));
        schema.setGroupList(new GroupList());

        Writer writer = new Writer();
        String output = writer.write(schema, new CompactSyntaxVisitor());

        assertEquals(
            this.readFile("datatype.bonxai"),
            output
        );
    }

    @Test
    public void testAttributeGroupSchema() throws IOException {
        Bonxai schema = new Bonxai();

        NamespaceList namespaces = new NamespaceList(new DefaultNamespace("http://example.com/ns"));
        namespaces.addIdentifiedNamespace(new IdentifiedNamespace("xsd", "http://www.w3.org/2001/XMLSchema"));
        namespaces.addIdentifiedNamespace(new IdentifiedNamespace("some", "http://example.com/some"));

        schema.setDeclaration(new Declaration(
            new ImportList(),
            new DataTypeList(),
            namespaces
        ));

        Vector<AttributeListElement> attributeGroupList = new Vector<AttributeListElement>();
        attributeGroupList.add(new Attribute("http://example.com/ns", "fooAttribute", new BonxaiType("http://www.w3.org/2001/XMLSchema", "float"), true));
        attributeGroupList.add(new Attribute("http://example.com/some", "barAttribute", new BonxaiType("http://www.w3.org/2001/XMLSchema", "boolean")));

        AttributeGroupElement attributeGroup = new AttributeGroupElement(
            "barAttributeGroup",
            new AttributePattern(attributeGroupList)
        );

        Vector<GroupElement> groupElements = new Vector<GroupElement>();
        groupElements.add(attributeGroup);

        schema.setGroupList(new GroupList(groupElements));

        Writer writer = new Writer();
        String output = writer.write(schema, new CompactSyntaxVisitor());

        assertEquals(
            this.readFile("attribute_group.bonxai"),
            output
        );
    }

    @Test
    public void testGroupsSchema() throws IOException {
        Bonxai schema = new Bonxai();

        NamespaceList namespaces = new NamespaceList(new DefaultNamespace("http://example.com/ns"));
        namespaces.addIdentifiedNamespace(new IdentifiedNamespace("xsd", "http://www.w3.org/2001/XMLSchema"));
        namespaces.addIdentifiedNamespace(new IdentifiedNamespace("some", "http://example.com/some"));

        schema.setDeclaration(new Declaration(
            new ImportList(),
            new DataTypeList(),
            namespaces
        ));

        SequencePattern sequence = new SequencePattern();
        sequence.addParticle(
            new de.tudortmund.cs.bonxai.bonxai.Element(
                "http://example.com/ns",
                "fooElement",
                new BonxaiType("http://www.w3.org/2001/XMLSchema", "int")
            )
        );
        sequence.addParticle(
            new de.tudortmund.cs.bonxai.bonxai.Element(
                "http://example.com/some",
                "barElement"
            )
        );

        ChoicePattern choice = new ChoicePattern();
        choice.addParticle(
            new de.tudortmund.cs.bonxai.bonxai.Element(
                "http://example.com/ns",
                "bazElement"
            )
        );
        choice.addParticle(
            new de.tudortmund.cs.bonxai.bonxai.Element(
                "http://example.com/ns",
                "bamElement",
                new BonxaiType("http://www.w3.org/2001/XMLSchema", "int"),
                true
            )
        );

        de.tudortmund.cs.bonxai.bonxai.Element defaultElement =
            new de.tudortmund.cs.bonxai.bonxai.Element("http://example.com/ns", "lala", new BonxaiType("http://www.w3.org/2001/XMLSchema", "int"));
        defaultElement.setDefault("23");

        choice.addParticle(defaultElement);

        CountingPattern optionalPattern = new CountingPattern(0,1);

        optionalPattern.addParticle(new AnyPattern(ProcessContentsInstruction.Lax, "##any"));

        choice.addParticle(optionalPattern);

        sequence.addParticle(choice);

        de.tudortmund.cs.bonxai.bonxai.Element fixedElement =
            new de.tudortmund.cs.bonxai.bonxai.Element("http://example.com/some", "lolo", new BonxaiType("http://www.w3.org/2001/XMLSchema", "string"));
        fixedElement.setFixed("nothing");

        CountingPattern countingPattern = new CountingPattern(23, 42);

        countingPattern.addParticle(fixedElement);

        sequence.addParticle(countingPattern);

        ElementGroupElement elementGroup = new ElementGroupElement("fooElementGroup", sequence);

        Vector<GroupElement> groupElements = new Vector<GroupElement>();
        groupElements.add(elementGroup);

        schema.setGroupList(new GroupList(groupElements));

        Writer writer = new Writer();
        String output = writer.write(schema, new CompactSyntaxVisitor());

        assertEquals(
            this.readFile("group.bonxai"),
            output
        );
    }

    @Test
    public void testGrammarSchema() throws IOException {
        Bonxai schema = new Bonxai();

        NamespaceList namespaces = new NamespaceList(new DefaultNamespace("http://example.com/ns"));
        namespaces.addIdentifiedNamespace(new IdentifiedNamespace("xsd", "http://www.w3.org/2001/XMLSchema"));
        namespaces.addIdentifiedNamespace(new IdentifiedNamespace("some", "http://example.com/some"));

        schema.setDeclaration(new Declaration(
            new ImportList(),
            new DataTypeList(),
            namespaces
        ));

        GrammarList grammar = new GrammarList();

        CardinalityParticle secondCardinalityParticle = new CardinalityParticle(
            new DoubleSlashPrefixElement("http://example.com/ns", "bar"),
            0
        );

        Vector<AncestorPatternParticle> secondSequenceList = new Vector<AncestorPatternParticle>();

        secondSequenceList.add(secondCardinalityParticle);
        secondSequenceList.add(new SingleSlashPrefixElement("http://example.com/some", "baz"));

        SequenceExpression secondSequenceExpression = new SequenceExpression(secondSequenceList);

        AncestorPattern firstAPattern = new AncestorPattern(new DoubleSlashPrefixElement("http://example.com/ns", "foo"));
        AncestorPattern secondAPattern = new AncestorPattern(secondSequenceExpression);

        Expression firstExpression = new Expression();
        Expression secondExpression = new Expression();

        firstExpression.setAncestorPattern(firstAPattern);
        secondExpression.setAncestorPattern(secondAPattern);

        ChildPattern firstChildPattern = new ChildPattern(new AttributePattern(), null);

        Vector<AttributeListElement> secondAttributeList = new Vector<AttributeListElement>();
        secondAttributeList.add(new Attribute("http://example.com/some", "thing", new BonxaiType("http://www.w3.org/2001/XMLSchema", "int")));

        AttributePattern secondAttributePattern = new AttributePattern(secondAttributeList);

        ElementPattern secondElementPattern = new ElementPattern(new BonxaiType("http://www.w3.org/2001/XMLSchema", "string"));
        secondElementPattern.setDefault("nothing");

        ChildPattern secondChildPattern = new ChildPattern(secondAttributePattern, secondElementPattern);

        firstExpression.setChildPattern(firstChildPattern);
        secondExpression.setChildPattern(secondChildPattern);

        grammar.addExpression(firstExpression);
        grammar.addExpression(secondExpression);

        schema.setGrammarList(grammar);

        Writer writer = new Writer();
        String output = writer.write(schema, new CompactSyntaxVisitor());

        assertEquals(
            this.readFile("grammar.bonxai"),
            output
        );
    }

    @Test
    public void testConstraintSchema() throws IOException {
        Bonxai schema = new Bonxai();

        NamespaceList namespaces = new NamespaceList(new DefaultNamespace("http://example.com/ns"));
        namespaces.addIdentifiedNamespace(new IdentifiedNamespace("xsd", "http://www.w3.org/2001/XMLSchema"));
        namespaces.addIdentifiedNamespace(new IdentifiedNamespace("some", "http://example.com/some"));

        schema.setDeclaration(new Declaration(
            new ImportList(),
            new DataTypeList(),
            namespaces
        ));

        ConstraintList constraintList = new ConstraintList();

        HashSet<String> fieldList = new HashSet<String>();
        fieldList.add("@firstname");
        fieldList.add("@lastname");

        AncestorPattern fooAPattern = new AncestorPattern(new DoubleSlashPrefixElement("http://example.com/ns", "foo"));
        AncestorPattern barAPattern = new AncestorPattern(new DoubleSlashPrefixElement("http://example.com/ns", "bar"));

        constraintList.addConstraint(new UniqueConstraint(fooAPattern, "./", fieldList));
        constraintList.addConstraint(new KeyConstraint("fooKey", fooAPattern, "./", fieldList));
        constraintList.addConstraint(new KeyRefConstraint("fooKey", barAPattern, "./", fieldList));

        schema.setConstraintList(constraintList);

        Writer writer = new Writer();
        String output = writer.write(schema, new CompactSyntaxVisitor());

        assertEquals(
            this.readFile("constraint.bonxai"),
            output
        );
    }
}
