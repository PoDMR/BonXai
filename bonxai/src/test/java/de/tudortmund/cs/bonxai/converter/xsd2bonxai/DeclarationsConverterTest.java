package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import java.util.TreeSet;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.bonxai.*;

import org.junit.Test;
import org.junit.Assert.*;

public class DeclarationsConverterTest extends ConverterTest {

    @Test
    public void testConvertDeclarationsNoExternal() {
        NamespaceList namespaces = new NamespaceList(new DefaultNamespace("http://example.com"));

        XSDSchema xsd = new XSDSchema();
        xsd.setNamespaceList(namespaces);

        Bonxai bonxai = new Bonxai();
        TypeAutomaton typeAutomaton = new TypeAutomaton(null);

        DeclarationsConverter converter = new DeclarationsConverter();

        Bonxai result = converter.convert(xsd, typeAutomaton, bonxai);

        assertSame(bonxai, result);

        assertNotNull(bonxai.getDeclaration());

        assertNotNull(bonxai.getDeclaration().getImportList());

        assertEquals(0, bonxai.getDeclaration().getImportList().getUris().size());

        assertNotNull(bonxai.getDeclaration().getDataTypeList());

        assertEquals(0, bonxai.getDeclaration().getDataTypeList().getUris().size());

        assertSame(namespaces, bonxai.getDeclaration().getNamespaceList());
    }

    @Test
    public void testConvertDeclarationsExternals() {
        NamespaceList namespaces = new NamespaceList(new DefaultNamespace("http://example.com"));
        namespaces.addIdentifiedNamespace(new IdentifiedNamespace("foo", "http://example.com/foo"));
        namespaces.addIdentifiedNamespace(new IdentifiedNamespace("bar", "http://example.com/bar"));

        XSDSchema xsd = new XSDSchema();
        xsd.setNamespaceList(namespaces);

        Bonxai bonxai = new Bonxai();
        TypeAutomaton typeAutomaton = new TypeAutomaton(null);

        DeclarationsConverter converter = new DeclarationsConverter();

        Bonxai result = converter.convert(xsd, typeAutomaton, bonxai);

        assertSame(bonxai, result);

        assertNotNull(bonxai.getDeclaration());

        assertNotNull(bonxai.getDeclaration().getImportList());

        assertEquals(0, bonxai.getDeclaration().getImportList().getUris().size());

        assertNotNull(bonxai.getDeclaration().getDataTypeList());

        assertEquals(0, bonxai.getDeclaration().getDataTypeList().getUris().size());

        assertSame(namespaces, bonxai.getDeclaration().getNamespaceList());
    }

}

