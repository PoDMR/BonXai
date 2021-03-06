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

package eu.fox7.schematoolkit.converter.relaxng2xsd;

import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import eu.fox7.schematoolkit.common.SymbolTableRef;
import eu.fox7.schematoolkit.converter.relaxng2xsd.PatternAttributeConverter;
import eu.fox7.schematoolkit.converter.relaxng2xsd.PatternInformationCollector;
import eu.fox7.schematoolkit.converter.relaxng2xsd.RelaxNG2XSDConverter;
import eu.fox7.schematoolkit.relaxng.*;
import eu.fox7.schematoolkit.relaxng.parser.RNGParser;
import eu.fox7.schematoolkit.relaxng.tools.XMLAttributeReplenisher;
import eu.fox7.schematoolkit.xsd.om.AttributeGroup;
import eu.fox7.schematoolkit.xsd.om.AttributeUse;
import eu.fox7.schematoolkit.xsd.om.SimpleContentRestriction;
import eu.fox7.schematoolkit.xsd.om.SimpleContentUnion;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class PatternAttributeConverter
 * @author Lars Schmidt
 */
public class PatternAttributeConverterTest extends junit.framework.TestCase {

    // Schema for this testcase
    XSDSchema schema;
    RelaxNGSchema rng;
    PatternInformationCollector patternInformationCollector;

    /**
     * Before every test the schema and schemaProcessor are refreshed
     */
    @Before
    @Override
    public void setUp() {
        this.schema = new XSDSchema();
        this.schema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("xs", RelaxNG2XSDConverter.XMLSCHEMA_NAMESPACE));
        this.rng = new RelaxNGSchema();
    }

    private void parseAndPrepareRNG(String filePath) throws Exception {
    	URL url = this.getClass().getResource("/"+filePath);
        this.rng = new RelaxNGSchema();
        RNGParser instance = new RNGParser(url.getFile(), false);
        this.rng = instance.getRNGSchema();

        XMLAttributeReplenisher xmlAttributeReplenisher = new XMLAttributeReplenisher(this.rng);
        xmlAttributeReplenisher.startReplenishment();

        this.patternInformationCollector = new PatternInformationCollector(this.rng);
        this.patternInformationCollector.collectData();
    }

    /**
     * Test of convertPatternListToAttributeParticleList method, of class PatternAttributeConverter.
     * @throws Exception
     */
    @Test
    public void testConvertPatternListToAttributeParticleList_01_standard() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/PatternAttributeConverterTest/attribute_01.rng");
        PatternAttributeConverter instance = new PatternAttributeConverter(this.schema, this.rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        Element element = (Element) this.rng.getRootPattern();
        HashSet<Pattern> patternsContainingAttribute = new HashSet<Pattern>(element.getPatterns());

        LinkedList<AttributeParticle> result = instance.convertPatternListToAttributeParticleList(patternsContainingAttribute);

        assertEquals(1, result.size());

        assertTrue(result.getFirst() instanceof eu.fox7.schematoolkit.xsd.om.Attribute);
        eu.fox7.schematoolkit.xsd.om.Attribute attribute1 = (eu.fox7.schematoolkit.xsd.om.Attribute) result.getFirst();

        assertEquals("{}a1", attribute1.getName());
        assertEquals(null, attribute1.getDefault());
        assertEquals(null, attribute1.getFixed());
        assertEquals(null, attribute1.getForm());
        assertTrue(attribute1.getTypeAttr());
        assertEquals(AttributeUse.Required, attribute1.getUse());

        assertTrue(attribute1.getSimpleType() instanceof eu.fox7.schematoolkit.xsd.om.SimpleType);
        SimpleType simpleType = attribute1.getSimpleType();

        assertEquals(null, simpleType.getFinalModifiers());
        assertEquals(null, simpleType.getInheritance());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", simpleType.getName());
    }

    /**
     * Test of convertPatternListToAttributeParticleList method, of class PatternAttributeConverter.
     * @throws Exception
     */
    @Test
    public void testConvertPatternListToAttributeParticleList_02_interleave() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/PatternAttributeConverterTest/attribute_02.rng");
        PatternAttributeConverter instance = new PatternAttributeConverter(this.schema, this.rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        Element element = (Element) this.rng.getRootPattern();
        HashSet<Pattern> patternsContainingAttribute = new HashSet<Pattern>(element.getPatterns());

        LinkedList<AttributeParticle> result = instance.convertPatternListToAttributeParticleList(patternsContainingAttribute);

        assertEquals(1, result.size());

        assertTrue(result.getFirst() instanceof eu.fox7.schematoolkit.xsd.om.Attribute);
        eu.fox7.schematoolkit.xsd.om.Attribute attribute1 = (eu.fox7.schematoolkit.xsd.om.Attribute) result.getFirst();

        assertEquals("{}a1", attribute1.getName());
        assertEquals(null, attribute1.getDefault());
        assertEquals(null, attribute1.getFixed());
        assertEquals(null, attribute1.getForm());
        assertTrue(attribute1.getTypeAttr());
        assertEquals(AttributeUse.Optional, attribute1.getUse());

        assertTrue(attribute1.getSimpleType() instanceof eu.fox7.schematoolkit.xsd.om.SimpleType);
        SimpleType simpleType = attribute1.getSimpleType();

        assertEquals(null, simpleType.getFinalModifiers());
        assertEquals(null, simpleType.getInheritance());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", simpleType.getName());
    }

    /**
     * Test of convertPatternListToAttributeParticleList method, of class PatternAttributeConverter.
     * @throws Exception
     */
    @Test
    public void testConvertPatternListToAttributeParticleList_03_mixed() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/PatternAttributeConverterTest/attribute_03.rng");
        PatternAttributeConverter instance = new PatternAttributeConverter(this.schema, this.rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        Element element = (Element) this.rng.getRootPattern();
        HashSet<Pattern> patternsContainingAttribute = new HashSet<Pattern>(element.getPatterns());

        LinkedList<AttributeParticle> result = instance.convertPatternListToAttributeParticleList(patternsContainingAttribute);

        assertEquals(1, result.size());

        assertTrue(result.getFirst() instanceof eu.fox7.schematoolkit.xsd.om.Attribute);
        eu.fox7.schematoolkit.xsd.om.Attribute attribute1 = (eu.fox7.schematoolkit.xsd.om.Attribute) result.getFirst();

        assertEquals("{}a1", attribute1.getName());
        assertEquals(null, attribute1.getDefault());
        assertEquals(null, attribute1.getFixed());
        assertEquals(null, attribute1.getForm());
        assertTrue(attribute1.getTypeAttr());
        assertEquals(AttributeUse.Optional, attribute1.getUse());

        assertTrue(attribute1.getSimpleType() instanceof eu.fox7.schematoolkit.xsd.om.SimpleType);
        SimpleType simpleType = attribute1.getSimpleType();

        assertEquals(null, simpleType.getFinalModifiers());
        assertEquals(null, simpleType.getInheritance());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", simpleType.getName());
    }

    /**
     * Test of convertPatternListToAttributeParticleList method, of class PatternAttributeConverter.
     * @throws Exception
     */
    @Test
    public void testConvertPatternListToAttributeParticleList_04_ref_parentRef() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/PatternAttributeConverterTest/attribute_04.rng");
        PatternAttributeConverter instance = new PatternAttributeConverter(this.schema, this.rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());

        Grammar grammar = (Grammar) this.rng.getRootPattern();
        Element element = (Element) grammar.getStartPatterns().getFirst();
        HashSet<Pattern> patternsContainingAttribute = new HashSet<Pattern>(element.getPatterns());

        LinkedList<AttributeParticle> result = instance.convertPatternListToAttributeParticleList(patternsContainingAttribute);

        assertEquals(1, result.size());

        assertTrue(result.getFirst() instanceof eu.fox7.schematoolkit.xsd.om.Attribute);
        eu.fox7.schematoolkit.xsd.om.Attribute attribute1 = (eu.fox7.schematoolkit.xsd.om.Attribute) result.getFirst();

        assertEquals("{}a1", attribute1.getName());
        assertEquals(null, attribute1.getDefault());
        assertEquals(null, attribute1.getFixed());
        assertEquals(null, attribute1.getForm());
        assertTrue(attribute1.getTypeAttr());
        assertEquals(AttributeUse.Optional, attribute1.getUse());

        assertTrue(attribute1.getSimpleType() instanceof eu.fox7.schematoolkit.xsd.om.SimpleType);
        SimpleType simpleType = attribute1.getSimpleType();

        assertEquals(null, simpleType.getFinalModifiers());
        assertEquals(null, simpleType.getInheritance());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", simpleType.getName());
    }

    /**
     * Test of convertPatternListToAttributeParticleList method, of class PatternAttributeConverter.
     * @throws Exception
     */
    @Test
    public void testConvertPatternListToAttributeParticleList_05_notAllowed() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/PatternAttributeConverterTest/attribute_05.rng");
        PatternAttributeConverter instance = new PatternAttributeConverter(this.schema, this.rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        Element element = (Element) this.rng.getRootPattern();
        HashSet<Pattern> patternsContainingAttribute = new HashSet<Pattern>(element.getPatterns());

        LinkedList<AttributeParticle> result = instance.convertPatternListToAttributeParticleList(patternsContainingAttribute);

        assertEquals(1, result.size());

        assertTrue(result.getFirst() instanceof eu.fox7.schematoolkit.xsd.om.Attribute);
        eu.fox7.schematoolkit.xsd.om.Attribute attribute1 = (eu.fox7.schematoolkit.xsd.om.Attribute) result.getFirst();

        assertEquals("{}a1", attribute1.getName());
        assertEquals(null, attribute1.getDefault());
        assertEquals(null, attribute1.getFixed());
        assertEquals(null, attribute1.getForm());
        assertFalse(attribute1.getTypeAttr());
        assertEquals(AttributeUse.Prohibited, attribute1.getUse());
        assertEquals(null, attribute1.getSimpleType());
    }

    /**
     * Test of convertPatternListToAttributeParticleList method, of class PatternAttributeConverter.
     *
     * Test of anyAttribute
     *
     * @throws Exception
     */
    @Test
    public void testConvertPatternListToAttributeParticleList_06_anyName() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/PatternAttributeConverterTest/attribute_06.rng");
        PatternAttributeConverter instance = new PatternAttributeConverter(this.schema, this.rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        Element element = (Element) this.rng.getRootPattern();
        HashSet<Pattern> patternsContainingAttribute = new HashSet<Pattern>(element.getPatterns());

        LinkedList<AttributeParticle> result = instance.convertPatternListToAttributeParticleList(patternsContainingAttribute);

        assertEquals(1, result.size());

        assertTrue(result.getFirst() instanceof eu.fox7.schematoolkit.common.AnyAttribute);
        eu.fox7.schematoolkit.common.AnyAttribute anyAttribute = (eu.fox7.schematoolkit.common.AnyAttribute) result.getFirst();

        assertEquals("##any", anyAttribute.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, anyAttribute.getProcessContentsInstruction());
    }

    /**
     * Test of convertPatternListToAttributeParticleList method, of class PatternAttributeConverter.
     *
     * Test of external attribute
     *
     * @throws Exception
     */
    @Test
    public void testConvertPatternListToAttributeParticleList_07_externalAttribute() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/PatternAttributeConverterTest/attribute_07.rng");
        PatternAttributeConverter instance = new PatternAttributeConverter(this.schema, this.rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        Element element = (Element) this.rng.getRootPattern();
        HashSet<Pattern> patternsContainingAttribute = new HashSet<Pattern>(element.getPatterns());

        LinkedList<AttributeParticle> result = instance.convertPatternListToAttributeParticleList(patternsContainingAttribute);

        assertEquals(1, result.size());

        assertTrue(result.getFirst() instanceof eu.fox7.schematoolkit.xsd.om.AttributeRef);
        eu.fox7.schematoolkit.xsd.om.AttributeRef attributeRef = (eu.fox7.schematoolkit.xsd.om.AttributeRef) result.getFirst();

        assertEquals("{http://www.myOtherExample.org}bob", attributeRef.getAttribute().getName());
    }

    /**
     * Test of convertPatternListToAttributeParticleList method, of class PatternAttributeConverter.
     *
     * Test of external anyAttribute
     *
     * @throws Exception
     */
    @Test
    public void testConvertPatternListToAttributeParticleList_08_externalAnyName() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/PatternAttributeConverterTest/attribute_08.rng");
        PatternAttributeConverter instance = new PatternAttributeConverter(this.schema, this.rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        Element element = (Element) this.rng.getRootPattern();
        HashSet<Pattern> patternsContainingAttribute = new HashSet<Pattern>(element.getPatterns());

        LinkedList<AttributeParticle> result = instance.convertPatternListToAttributeParticleList(patternsContainingAttribute);

        assertEquals(1, result.size());

        assertTrue(result.getFirst() instanceof eu.fox7.bonxai.xsd.AttributeGroupRef);
        eu.fox7.bonxai.xsd.AttributeGroupRef anyAttributeGroupRef = (eu.fox7.bonxai.xsd.AttributeGroupRef) result.getFirst();

        assertEquals("{http://www.example.org}externalAnyAttribute", anyAttributeGroupRef.getAttributeGroup().getName());
        assertTrue(anyAttributeGroupRef.getAttributeGroup().getAttributeParticles().getFirst() instanceof eu.fox7.schematoolkit.common.AnyAttribute);

        eu.fox7.schematoolkit.common.AnyAttribute anyAttribute = (eu.fox7.schematoolkit.common.AnyAttribute) anyAttributeGroupRef.getAttributeGroup().getAttributeParticles().getFirst();

        assertEquals("##other", anyAttribute.getNamespace());
        assertEquals(ProcessContentsInstruction.Strict, anyAttribute.getProcessContentsInstruction());
    }

    /**
     * Test of convertPatternListToAttributeParticleList method, of class PatternAttributeConverter.
     *
     * Test merging attributeLists from two attributePatterns
     *
     * @throws Exception
     */
    @Test
    public void testConvertPatternListToAttributeParticleList_09_merging() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/PatternAttributeConverterTest/attribute_09.rng");
        PatternAttributeConverter instance = new PatternAttributeConverter(this.schema, this.rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        Element element = (Element) this.rng.getRootPattern();
        HashSet<Pattern> patternsContainingAttribute = new HashSet<Pattern>(element.getPatterns());

        LinkedList<AttributeParticle> result = instance.convertPatternListToAttributeParticleList(patternsContainingAttribute);

        assertEquals(2, result.size());

        assertTrue(result.getFirst() instanceof eu.fox7.schematoolkit.xsd.om.Attribute);
        eu.fox7.schematoolkit.xsd.om.Attribute attribute1 = (eu.fox7.schematoolkit.xsd.om.Attribute) result.getFirst();

        assertEquals("{}bob", attribute1.getName());
        assertEquals(null, attribute1.getDefault());
        assertEquals(null, attribute1.getFixed());
        assertEquals(null, attribute1.getForm());
        assertTrue(attribute1.getTypeAttr());
        assertEquals(AttributeUse.Required, attribute1.getUse());

        assertTrue(attribute1.getSimpleType() instanceof eu.fox7.schematoolkit.xsd.om.SimpleType);
        SimpleType simpleType = attribute1.getSimpleType();

        assertEquals(null, simpleType.getFinalModifiers());
        assertEquals(null, simpleType.getInheritance());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", simpleType.getName());

        assertTrue(result.getLast() instanceof eu.fox7.schematoolkit.xsd.om.Attribute);
        eu.fox7.schematoolkit.xsd.om.Attribute attribute2 = (eu.fox7.schematoolkit.xsd.om.Attribute) result.getLast();
        assertEquals("{}alice", attribute2.getName());
    }

    /**
     * Test of convertPatternListToAttributeParticleList method, of class PatternAttributeConverter.
     *
     * Test merging attribute with the same name, or two occurences of anyAttributes
     *
     * @throws Exception
     */
    @Test
    public void testConvertPatternListToAttributeParticleList_10_duplicate_name() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/PatternAttributeConverterTest/attribute_10.rng");
        PatternAttributeConverter instance = new PatternAttributeConverter(this.schema, this.rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        Element element = (Element) this.rng.getRootPattern();
        HashSet<Pattern> patternsContainingAttribute = new HashSet<Pattern>(element.getPatterns());

        LinkedList<AttributeParticle> result = instance.convertPatternListToAttributeParticleList(patternsContainingAttribute);

        assertEquals(1, result.size());

        assertTrue(result.getFirst() instanceof eu.fox7.schematoolkit.xsd.om.Attribute);
        eu.fox7.schematoolkit.xsd.om.Attribute attribute1 = (eu.fox7.schematoolkit.xsd.om.Attribute) result.getFirst();

        assertEquals("{}alice", attribute1.getName());
        assertEquals(null, attribute1.getDefault());
        assertEquals(null, attribute1.getFixed());
        assertEquals(null, attribute1.getForm());
        assertFalse(attribute1.getTypeAttr());
        assertEquals(AttributeUse.Optional, attribute1.getUse());

        assertTrue(attribute1.getSimpleType() instanceof eu.fox7.schematoolkit.xsd.om.SimpleType);
        SimpleType simpleType = attribute1.getSimpleType();

        assertEquals(null, simpleType.getFinalModifiers());
        assertTrue(simpleType.getInheritance() instanceof SimpleContentUnion);
        assertEquals("{}simpleType_1", simpleType.getName());
        SimpleContentUnion simpleContentUnion = (SimpleContentUnion) simpleType.getInheritance();
        assertEquals(2, simpleContentUnion.getAllMemberTypes().size());
        assertEquals("{}simpleType", simpleContentUnion.getAllMemberTypes().getFirst().getReference().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", simpleContentUnion.getAllMemberTypes().getLast().getReference().getName());
        SimpleType simpleType2 = (SimpleType) simpleContentUnion.getAllMemberTypes().getFirst().getReference();

        assertEquals(null, simpleType2.getFinalModifiers());
        assertEquals(null, simpleType2.getFinalModifiers());
        assertTrue(simpleType2.getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleType2.getInheritance();
        assertEquals(1, simpleContentRestriction.getEnumeration().size());
        assertEquals("4", simpleContentRestriction.getEnumeration().getFirst());
    }

    /**
     * Test of convertPatternListToAttributeParticleList method, of class PatternAttributeConverter.
     *
     * Test attributeGroups
     *
     * @throws Exception
     */
    @Test
    public void testConvertPatternListToAttributeParticleList_11_attributeGroup() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/PatternAttributeConverterTest/attribute_11.rng");
        PatternAttributeConverter instance = new PatternAttributeConverter(this.schema, this.rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());

        Grammar grammar = (Grammar) this.rng.getRootPattern();
        Element element = (Element) grammar.getStartPatterns().getFirst();
        HashSet<Pattern> patternsContainingAttribute = new HashSet<Pattern>(element.getPatterns());

        LinkedList<AttributeParticle> result = instance.convertPatternListToAttributeParticleList(patternsContainingAttribute);

        assertEquals(1, result.size());

        assertTrue(result.getFirst() instanceof eu.fox7.bonxai.xsd.AttributeGroupRef);
        eu.fox7.bonxai.xsd.AttributeGroupRef anyAttributeGroupRef = (eu.fox7.bonxai.xsd.AttributeGroupRef) result.getFirst();

        assertEquals("{}def1", anyAttributeGroupRef.getAttributeGroup().getName());
        assertEquals(2, anyAttributeGroupRef.getAttributeGroup().getAttributeParticles().size());

        assertEquals("{}def1", this.schema.getAttributeGroups().getFirst().getName());
    }

    /**
     * Test of convertPatternListToAttributeParticleList method, of class PatternAttributeConverter.
     *
     * Test for duplicate attribute names (one is nested in an attributeGroup) - attributeGroup has to be resolved
     *
     * @throws Exception
     */
    @Test
    public void testConvertPatternListToAttributeParticleList_12_duplicate_name_attributeGroup() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/PatternAttributeConverterTest/attribute_12.rng");
        PatternAttributeConverter instance = new PatternAttributeConverter(this.schema, this.rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());

        Grammar grammar = (Grammar) this.rng.getRootPattern();
        Element element = (Element) grammar.getStartPatterns().getFirst();
        HashSet<Pattern> patternsContainingAttribute = new HashSet<Pattern>(element.getPatterns());

        LinkedList<AttributeParticle> result = instance.convertPatternListToAttributeParticleList(patternsContainingAttribute);

        assertEquals(2, result.size());

        assertTrue(result.getFirst() instanceof eu.fox7.schematoolkit.xsd.om.Attribute);
        eu.fox7.schematoolkit.xsd.om.Attribute attribute1 = (eu.fox7.schematoolkit.xsd.om.Attribute) result.getFirst();

        assertEquals("{}bob", attribute1.getName());
        assertEquals(null, attribute1.getDefault());
        assertEquals(null, attribute1.getFixed());
        assertEquals(null, attribute1.getForm());
        assertTrue(attribute1.getTypeAttr());
        assertEquals(AttributeUse.Optional, attribute1.getUse());

        assertTrue(attribute1.getSimpleType() instanceof eu.fox7.schematoolkit.xsd.om.SimpleType);
        SimpleType simpleType = attribute1.getSimpleType();

        assertEquals(null, simpleType.getFinalModifiers());
        assertEquals(null, simpleType.getInheritance());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", simpleType.getName());

        assertTrue(result.getLast() instanceof eu.fox7.schematoolkit.xsd.om.Attribute);
        eu.fox7.schematoolkit.xsd.om.Attribute attribute2 = (eu.fox7.schematoolkit.xsd.om.Attribute) result.getLast();
        assertEquals("{}alice", attribute2.getName());

        assertTrue(this.schema.getAttributeGroups().isEmpty());
    }

    /**
     * Test of convertPatternListToAttributeParticleList method, of class PatternAttributeConverter.
     *
     * Test for anyAttribute nested in an attributeGroup - attributeGroup has to be resolved
     *
     * @throws Exception
     */
    @Test
    public void testConvertPatternListToAttributeParticleList_13_anyAttribute_attributeGroup() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/PatternAttributeConverterTest/attribute_13.rng");
        PatternAttributeConverter instance = new PatternAttributeConverter(this.schema, this.rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());

        Grammar grammar = (Grammar) this.rng.getRootPattern();
        Element element = (Element) grammar.getStartPatterns().getFirst();
        HashSet<Pattern> patternsContainingAttribute = new HashSet<Pattern>(element.getPatterns());

        LinkedList<AttributeParticle> result = instance.convertPatternListToAttributeParticleList(patternsContainingAttribute);

        assertEquals(2, result.size());

        assertTrue(result.getLast() instanceof eu.fox7.schematoolkit.common.AnyAttribute);
        eu.fox7.schematoolkit.common.AnyAttribute anyAttribute = (eu.fox7.schematoolkit.common.AnyAttribute) result.getLast();

        assertEquals("##any", anyAttribute.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, anyAttribute.getProcessContentsInstruction());
        eu.fox7.schematoolkit.xsd.om.Attribute attribute2 = (eu.fox7.schematoolkit.xsd.om.Attribute) result.getFirst();
        assertEquals("{}a1", attribute2.getName());
        assertTrue(this.schema.getAttributeGroups().isEmpty());
    }
}
