package de.tudortmund.cs.bonxai.converter.relaxng2xsd;

import de.tudortmund.cs.bonxai.relaxng.RelaxNGSchema;
import de.tudortmund.cs.bonxai.relaxng.parser.RNGParser;
import de.tudortmund.cs.bonxai.relaxng.writer.RNGWriter;
import de.tudortmund.cs.bonxai.xsd.ForeignSchema;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.tools.InterleaveHandler;
import de.tudortmund.cs.bonxai.xsd.writer.XSDWriter;
import java.util.Iterator;
import java.util.LinkedHashSet;
import org.junit.Test;

/**
 * Test of class RelaxNG2XSDConverter
 * @author Lars Schmidt
 */
public class RelaxNG2XSDConverterImplementationTest {
    private LinkedHashSet<XSDSchema> alreadySeenSchemas;

    /**
     * Test of convert method, of class RelaxNG2XSDConverter.
     * @throws Exception
     */
    @Test
    public void testConvert() throws Exception {

        this.alreadySeenSchemas = new LinkedHashSet<XSDSchema>();

//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/A.rng"); // original
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/A/A.rng"); // für oxygen/trang
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/ProblemA.rng");
//
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/new_addressbook.rng");

//        String filePath = new String("http://www.docbook.org/rng/4.5/docbook.rng");
//        String filePath = new String("http://www.docbook.org/xml/5.0/rng/docbook.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/docbook_4_5/docbook.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/docbook5.rng.xml");


//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/xhtml-strict/xhtml-strict.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/xhtml-strict/xhtml-strict_.rng");

//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/name/name4.rng");

//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/recursive_invalid.rng");

//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/name/name8.rng");

//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/name/name9.rng");

//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/recursive_valid_type_test.rng");

//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/recursive_valid_b.rng");

//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/relaxng.rng");

//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/aktueller_test.rng");

//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/aktueller_test_small.rng");

//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/aktueller_mixed_fehler.rng");

//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/aktueller_neuer_test.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/nameChoice.rng");

//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/test.rng");

        /**
         *  Simple Datatypes: conversion examples
         */
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_01.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_02.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_03.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_04.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_05.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_06.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_07.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_08.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_09.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_10.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_11.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_12.rng"); // simple version of 11
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_13.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_14.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_15.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_16.rng");
        // Nicht in XML Schema abbildbar, bzw. ich weiß noch nicht wie...?
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/datatypes/datatypes_17.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/recursive_problem.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/toplevel_duplicate_name.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/acronym_problem.rng");
//        String filePath = new String("http://www.gnosis.cx/download/relax/testSuite.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/rngs/attribute_merging.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/rngs/attribute_merging_01.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/rngs/attribute_merging_03.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/rngs/attribute_merging_04.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/rngs/attribute_merging_myExample.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/rngs/definesAsTypes.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/rngs/definesAsTypes_test_01.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/rngs/definesAsTypes_test_02.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/rngs/definesAsTypes_test_03.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/problem_with_text.rng");
        String filePath = "tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/InterleaveTest/Interleave_01.rng";

        RNGParser instance = new RNGParser(filePath, false);

        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();

//        RNGWriter rngWriter = new RNGWriter(relaxNGSchema);
//        System.out.println(rngWriter.getRNGString());

        RelaxNG2XSDConverter relaxNG2XSDConverter = new RelaxNG2XSDConverter(relaxNGSchema, "D:/test/test.xsd");
        XSDSchema xmlSchema = relaxNG2XSDConverter.convert();

//        rngWriter = new RNGWriter(relaxNG2XSDConverter.getRelaxNGSchema());
//        System.out.println(rngWriter.getRNGString());

        getSchemas(xmlSchema);

//        System.out.println("\n--------------------------------------------------------------------------\n");
//
//        this.alreadySeenSchemas = new LinkedHashSet<Schema>();
//
//
//        SchemaFileWriter schemaFileWriter = new SchemaFileWriter();
//        System.out.println(schemaFileWriter.write("D:/test/", "test.xsd", xmlSchema));
//
//        getSchemas(xmlSchema);

    }

    private void getSchemas(XSDSchema schema) {

        this.alreadySeenSchemas.add(schema);

        System.out.println("\n--------------------------------------------------------------------------\n" + schema.getSchemaLocation() + "\n--------------------------------------------------------------------------\n");

        XSDWriter xSD_Writer = new XSDWriter(schema);
        xSD_Writer = new XSDWriter(schema);
        System.out.println(xSD_Writer.getXSDString());

        // For each foreignSchema defined in the current schema call the method
        // findContentModels recursively
        if (schema.getForeignSchemas() != null && !schema.getForeignSchemas().isEmpty()) {
            for (Iterator<ForeignSchema> it = schema.getForeignSchemas().iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();
                if (!this.alreadySeenSchemas.contains(foreignSchema.getSchema())) {
                    this.getSchemas(foreignSchema.getSchema());
                }
            }
        }
    }
}
