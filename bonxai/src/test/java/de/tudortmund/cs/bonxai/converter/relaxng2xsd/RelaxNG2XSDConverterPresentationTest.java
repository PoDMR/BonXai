package de.tudortmund.cs.bonxai.converter.relaxng2xsd;

import de.tudortmund.cs.bonxai.relaxng.RelaxNGSchema;
import de.tudortmund.cs.bonxai.relaxng.parser.RNGParser;
import de.tudortmund.cs.bonxai.relaxng.writer.RNGWriter;
import de.tudortmund.cs.bonxai.xsd.ForeignSchema;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.writer.XSDWriter;
import java.util.Iterator;
import org.junit.Test;

/**
 * Test of class RelaxNG2XSDConverter
 * @author Lars Schmidt
 */
public class RelaxNG2XSDConverterPresentationTest {

    /**
     * Test of convert method, of class RelaxNG2XSDConverter.
     * @throws Exception
     */
    @Test
    public void testConvert() throws Exception {
        String filePath = "";

        // Easy recursive example (select possible RECURSION_MODE_COMPLEXTYPE, RECURSION_MODE_ELEMENT_SET_ALL_ELEMENTS_TOPLEVEL settings)
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/recursive_valid_b.rng");
        
        // EDC is broken (different names for same element => same type...)
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/nameChoice.rng");

        // Start with interleave (select possible INTERLEAVE_APPROXIMATION_OFF settings)
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/start_interleave.rng");
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/interleave_01.rng");

        // Generated groups
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/group_example_1.rng");
        // Start element union necessary (for RECURSION_MODE_COMPLEXTYPE, too)
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/group_example_2.rng");

        // EDC is broken (different names for same element => same type...)
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/nameChoice.rng");

        // DocBook
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/docbook_4_5/docbook.rng");
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/docbook5.rng.xml");

        // XHTML-strict
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/xhtml-strict/xhtml-strict.rng");

        /**
         *  Simple Datatypes: conversion examples
         */
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_01.rng");
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_02.rng");
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_03.rng");
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_04.rng");
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_05.rng");
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_06.rng");
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_07.rng");
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_08.rng");
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_09.rng");
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_10.rng");
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_11.rng");
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_12.rng"); // simple version of 11
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_13.rng"); // attribute merging
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_14.rng");
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_15.rng");
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_16.rng");
//        
//        // Are the semantics of the following RELAX NG schema possible in XML Schema?
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_17.rng");
//

        // Defines as type
//        filePath = new String("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/rngs/definesAsTypes.rng");
//        filePath = new String("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/rngs/notAllowed.rng");

        // RELAX NG
//        filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/relaxng.rng");


//        filePath = "tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/rngs/interleave.rng";
        
        filePath = "tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/rngs/edc_01_.rng";


        RNGParser instance = new RNGParser(filePath, false);

        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();

        RelaxNG2XSDConverter relaxNG2XSDConverter = new RelaxNG2XSDConverter(relaxNGSchema, "test.xsd");
        XSDSchema xmlSchema = relaxNG2XSDConverter.convert();

        XSDWriter xsd_Writer = new XSDWriter(xmlSchema);
        System.out.println("\nConversion result:\n--------------------------------------------------------------------------\n");
        System.out.println(xsd_Writer.getXSDString());

        if (!xmlSchema.getForeignSchemas().isEmpty()) {
            for (Iterator<ForeignSchema> it = xmlSchema.getForeignSchemas().iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();
                System.out.println("\n--------------------------------------------------------------------------\n" + foreignSchema.getClass().getName() + " - " + foreignSchema.getSchemaLocation() + "\n--------------------------------------------------------------------------\n");
                xsd_Writer = new XSDWriter(foreignSchema.getSchema());
                System.out.println(xsd_Writer.getXSDString());
            }
        }
    }
}
