package eu.fox7.schematoolkit.xsd.om;


/*
 * Test concerning method add still failing.
 */


import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.schematoolkit.common.SymbolTableRef;
import eu.fox7.schematoolkit.xsd.om.Key;
import eu.fox7.schematoolkit.xsd.om.KeyRef;
import eu.fox7.schematoolkit.xsd.om.SimpleConstraint;
import eu.fox7.schematoolkit.xsd.om.Unique;

/**
 *
 */
public class SimpleConstraintTest extends junit.framework.TestCase {
    /**
     * Test of getFields method, of class SimpleConstraint. As it is abstract,
     * the method must be tested in all subclasses.
     *
     * @TODO: should be refactored to use proper Mock
     */
    @Test
    public void testGetFieldsKey() {
        SimpleConstraint key = new Key("{}someKey", "someSelector");
        String expResult = "someKeyFieldText";
        key.addField(expResult);
        HashSet<String> result = key.getFields();
        assertTrue(result.contains(expResult));
    }

    @Test
    public void testGetFieldsKeyRef() {
        SymbolTableRef<SimpleConstraint> ref = new SymbolTableRef<SimpleConstraint>("Keyref");
        SimpleConstraint key = new KeyRef("someKey", "someSelector", ref);
        String expResult = "someKeyRefFieldText";
        key.addField(expResult);
        HashSet<String> result = key.getFields();
        assertTrue(result.contains(expResult));
    }

    @Test
    public void testGetFieldsUnique() {
        SimpleConstraint key = new Unique("someKey", "someSelector");
        String expResult = "someUniqueFieldText";
        key.addField(expResult);
        HashSet<String> result = key.getFields();
        assertTrue(result.contains(expResult));
    }
    /**
     * Test of getName method, of class SimpleConstraint.As it is abstract,
     * the method must be tested in all subclasses.
     *
     * @TODO: should be refactored to use proper Mock
     */
    @Test
    public void testGetNameKey() {
        String expResult = "{}keyName";
        SimpleConstraint key = new Key(expResult, "selector");
        String result = key.getName();
        assertEquals(expResult, result);

    }

    @Test
    public void testGetNameKeyRef() {
        String expResult = "keyName";
        SymbolTableRef<SimpleConstraint> ref = new SymbolTableRef<SimpleConstraint>("keyref");
        SimpleConstraint keyRef = new KeyRef(expResult, "selector", ref);
        String result = keyRef.getName();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetNameUnique() {
        String expResult = "keyName";
        SimpleConstraint unique = new Unique(expResult, "selector");
        String result = unique.getName();
        assertEquals(expResult, result);
    }
    /**
     * Test of getSelector method, of class SimpleConstraint. As it is abstract,
     * the method must be tested in all subclasses.
     *
     * @TODO: should be refactored to use proper Mock
     */
    @Test
    public void testGetSelectorKey() {
        String expResult = "selector";
        SimpleConstraint instance = new Key("{}name", expResult);
        String result = instance.getSelector();
        assertEquals(expResult, result);
    }
    @Test
    public void testGetSelectorKeyRef() {
        String expResult = "selector";
        SymbolTableRef<SimpleConstraint> ref = new SymbolTableRef<SimpleConstraint>("keyref");
        SimpleConstraint instance = new KeyRef("name", expResult, ref);
        String result = instance.getSelector();
        assertEquals(expResult, result);
    }
    @Test
    public void testGetSelectorUnique() {
        String expResult = "selector";
        SimpleConstraint instance = new Unique("name", expResult);
        String result = instance.getSelector();
        assertEquals(expResult, result);
    }
    /**
     * Test of addField method, of class SimpleConstraint. As it is abstract,
     * the method must be tested in all subclasses.
     *
     * @TODO: should be refactored to use proper Mock
     */
    @Test
    public void testAddFieldKey() {
        String expResult = "field";
        SimpleConstraint instance =  new Key("{}name", "selector");
        instance.addField(expResult);
        String result = instance.fields.toString();
        assertTrue(result.contains(expResult));
    }
    @Test
    public void testAddFieldKeyRef() {
        String expResult = "field";
        SymbolTableRef<SimpleConstraint> ref = new SymbolTableRef<SimpleConstraint>("keyref");
        SimpleConstraint instance =  new KeyRef("name", "selector", ref);
        instance.addField(expResult);
        String result = instance.fields.toString();
        assertTrue(result.contains(expResult));
    }
    @Test
    public void testAddFieldUnique() {
        String expResult = "field";
        SimpleConstraint instance =  new Unique("name", "selector");
        instance.addField(expResult);
        String result = instance.fields.toString();
        assertTrue(result.contains(expResult));
    }
}
