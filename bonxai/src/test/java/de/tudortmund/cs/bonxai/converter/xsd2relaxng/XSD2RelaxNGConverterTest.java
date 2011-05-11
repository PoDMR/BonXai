package de.tudortmund.cs.bonxai.converter.xsd2relaxng;

import de.tudortmund.cs.bonxai.relaxng.RelaxNGSchema;
import de.tudortmund.cs.bonxai.relaxng.writer.RNGWriter;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.parser.XSDParser;
import de.tudortmund.cs.bonxai.xsd.writer.XSDWriter;
import org.junit.Test;

/**
 * Test of class XSD2RelaxNGConverter
 * @author Lars Schmidt
 */
public class XSD2RelaxNGConverterTest extends junit.framework.TestCase {

    /**
     * Test of convert method, of class XSD2RelaxNGConverter.
     * @throws Exception 
     */
    @Test
    public void testConvert() throws Exception {

//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/inheritance.xsd";
//        String uri = new String("tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/Base.xsd");
//        String uri = new String("tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/substitutionGroupBlockRestriction.xsd");
//        String uri = new String("tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/substitutionGroupBlockExtension.xsd");
//        String uri = new String("tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/substitutionGroupBlockSubstitution.xsd");

//        String uri = new String("tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/substitutionGroupNested.xsd");

//        String uri = new String("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/wim/XMLSchema.xsd");

//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/type_inheritance/Type_inheritance_simpleType_complexType.xsd";

//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/type_inheritance/Type_inheritance_01.xsd";
//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/type_inheritance/Type_inheritance_02.xsd";
//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/type_inheritance/Type_inheritance_03.xsd";
//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/type_inheritance/Type_inheritance_04.xsd";
//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/type_inheritance/Type_inheritance_05.xsd";
//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/type_inheritance/Type_inheritance_06.xsd";
//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/type_inheritance/Type_inheritance_07.xsd";

//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/type_inheritance/Type_inheritance_08.xsd";
//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/type_inheritance/Type_inheritance_09.xsd";
//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/type_inheritance/Type_inheritance_10.xsd";
//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/type_inheritance/Type_inheritance_11.xsd";
        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/type_inheritance/Type_inheritance_12.xsd";

        // any
//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/any/ElementWithoutType.xsd";
//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/any/ElementWithEmptyComplexType.xsd";

//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/any/ElementWithComplexTypeSequenceAny.xsd";
//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/any/ElementWithComplexTypeAnyAttribute.xsd";
//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/any/ElementWithComplexTypeAnyAttribute2.xsd";

//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/any/ElementWithComplexTypeSequenceCounting_01.xsd";
//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/any/ElementWithComplexTypeSequenceCountingGroup.xsd";

//        String uri = new String("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/wim/xhtml1-strict.xsd");
//        String uri = new String("tests/de/tudortmund/cs/bonxai/converter/xsd2dtd/xsds/docbook.xsd");

//        String uri = new String("tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/schema.xsd");

//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/simpleTypeConverterTests/inheritance.xsd";
//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/complexTypeConverterTests/complexContentExtension.xsd";
//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/complexTypeConverterTests/complexContentRestriction.xsd";

//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/xhtml/redefineBase.xsd";

        XSDParser instance = new XSDParser(false, false);
        XSDSchema xmlSchema = instance.parse(uri);

        XSDWriter xsd_Writer = new XSDWriter(xmlSchema);
        System.out.println(xsd_Writer.getXSDString());

        XSD2RelaxNGConverter xsd2RelaxNGConverter = new XSD2RelaxNGConverter(xmlSchema, true);
        RelaxNGSchema rng = xsd2RelaxNGConverter.convert();

        RNGWriter rngWriter = new RNGWriter(rng);
        System.out.println(rngWriter.getRNGString());

    }
}
