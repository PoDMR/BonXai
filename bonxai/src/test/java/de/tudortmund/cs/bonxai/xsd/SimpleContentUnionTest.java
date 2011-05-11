package de.tudortmund.cs.bonxai.xsd;

import java.util.HashSet;

import org.junit.Test;

import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import java.util.LinkedList;

public class SimpleContentUnionTest extends junit.framework.TestCase {

    /**
     * Test method for {@link SimpleContentUnion#SimpleContentUnion(SymbolTableRef)}.
     */
    @Test
    public final void testSimpleContentUnion() {
        SymbolTableRef<Type> baseType = new SymbolTableRef<Type>("Test");

        LinkedList<SymbolTableRef<Type>> base = new LinkedList<SymbolTableRef<Type>>();
        base.add(baseType);

        SimpleContentUnion test = new SimpleContentUnion(base);
        assertNotNull(test);
    }

    /**
     * Test method for {@link SimpleContentUnion#getAnonymousSimpleTypes()}.
     */
    @Test
    public final void testGetSimpleTypes() {
        SymbolTableRef<Type> baseType = new SymbolTableRef<Type>("Test");

        SimpleType value = new SimpleType("{}Test", null);
        baseType.setReference(value);

        LinkedList<SymbolTableRef<Type>> base = new LinkedList<SymbolTableRef<Type>>();
        base.add(baseType);

        SymbolTableRef<Type> secondType = new SymbolTableRef<Type>("{}Test2");

        SimpleType value2 = new SimpleType("{}Test2", null);
        secondType.setReference(value2);

        SimpleContentUnion test = new SimpleContentUnion(base);
        test.addMemberType(secondType);

        assertTrue(test.getMemberTypes().size() == 2);
    }

    @Test
    public final void testGetSimpleTypesSameTypeAddedTwice() {
        SymbolTableRef<Type> baseType = new SymbolTableRef<Type>("Test");

        SimpleType value = new SimpleType("{}Test", null);
        baseType.setReference(value);

        LinkedList<SymbolTableRef<Type>> base = new LinkedList<SymbolTableRef<Type>>();
        base.add(baseType);

        SymbolTableRef<Type> secondType = new SymbolTableRef<Type>("Test");

        SimpleType value2 = new SimpleType("{}Test", null);
        secondType.setReference(value);

        SimpleContentUnion test = new SimpleContentUnion(base);

        try {
            test.addMemberType(secondType);
            fail("Exception not thrown on addition of duplicate type.");
        } catch (RuntimeException e) {}

        assertTrue(test.getMemberTypes().size() == 1);
    }
}
