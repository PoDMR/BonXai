/*
 * Created on Jan 27, 2006
 * Modified on $Date: 2009-10-27 14:11:40 $
 */
package gjb.util.sampler;

import gjb.util.sampling.CliSampler;
import gjb.util.sampling.ExampleParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ExampleParserTest extends TestCase {

    protected final String data =
        "a b\n" +
        "\n" +
        "a a b\n" +
        "a b\n";
    protected BufferedReader reader;
    protected ExampleParser parser;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ExampleParserTest.class);
    }

    public static Test suite() {
        return new TestSuite(ExampleParserTest.class);
      }

    public void setUp() throws Exception {
        super.setUp();
        reader = new BufferedReader(new StringReader(data));
        parser = new ExampleParser();
    }

    public void test_parser() {
        try {
            List<String[]> sample = parser.parse(reader);
            assertEquals("sample size", 4, sample.size());
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        }
    }

    public void test_iterator() {
        try {
            StringBuilder str = new StringBuilder();
            for (Iterator<String[]> it = parser.iterator(reader); it.hasNext(); ) {
                str.append(StringUtils.join(it.next(), " ")).append("\n");
            }
            assertEquals("iterator reconstruction", data, str.toString());
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        }
    }

    public void test_emptyExample() {
        String data =
            "\n";
        try {
            List<String[]> sample = parser.parse(new BufferedReader(new StringReader(data)));
            assertEquals("sample size", 1, sample.size());
            String[] example = sample.get(0);
            assertEquals("example size", 0, example.length);
        } catch (IOException e) {
            e.printStackTrace();
            fail("parse exception");
        }
    }

//    public void test_Cli() {
//        final String outputFileName = "test-data/test-out.txt";
//        String sampleSizeStr = "4";
//        for (int i = 0; i < 5; i++) {
//            try {
//                CliSampler.main(new String[] {"-n", sampleSizeStr,
//                        "-p", "test-data/test.properties",
//                        "-i", "test-data/test-in.txt",
//                        "-o", outputFileName});
//                BufferedReader reader = new BufferedReader(new FileReader(outputFileName));
//                int counter = 0;
//                while ((reader.readLine()) != null) {
//                    counter++;
//                }
//                reader.close();
//                assertEquals("sample size",
//                             Integer.valueOf(sampleSizeStr).intValue(), counter);
//                File file = new File(outputFileName);
//                assertTrue("file delete", file.delete());
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//                fail("unexpected exception");
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                fail("unexpected exception");
//            } catch (IOException e) {
//                e.printStackTrace();
//                fail("unexpected exception");
//            }
//        }
//    }
//
//    public void test_Cli2() {
//        final String outputFileName = "test-data/test-out-2.txt";
//        String sampleSizeStr = "3";
//        final int nrColumns = 3;
//        for (int i = 0; i < 5; i++) {
//            try {
//                CliSampler.main(new String[] {"-n", sampleSizeStr,
//                        "-p", "test-data/test-2.properties",
//                        "-i", "test-data/test-in-2.txt",
//                        "-o", outputFileName});
//                BufferedReader reader = new BufferedReader(new FileReader(outputFileName));
//                int counter = 0;
//                String line = null;
//                while ((line = reader.readLine()) != null) {
//                    counter++;
//                    String[] parts = line.split("\\t");
//                    assertEquals("columns", nrColumns, parts.length);
//                }
//                reader.close();
//                assertEquals("sample size",
//                             Integer.valueOf(sampleSizeStr).intValue(), counter);
//                File file = new File(outputFileName);
//                assertTrue("file delete", file.delete());
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//                fail("unexpected exception");
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                fail("unexpected exception");
//            } catch (IOException e) {
//                e.printStackTrace();
//                fail("unexpected exception");
//            }
//        }
//    }
    
}
