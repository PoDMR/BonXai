/*
 * Created on May 30, 2007
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package gjb.flt.regex.random;

import gjb.flt.regex.matchers.Matcher;
import gjb.flt.regex.random.Sample;
import gjb.flt.regex.random.SampleReader;
import gjb.flt.regex.random.SampleWriter;
import gjb.flt.regex.random.SimpleReader;
import gjb.flt.regex.random.SimpleWriter;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class SampleTest extends TestCase {

    public static Test suite() {
        return new TestSuite(SampleTest.class);
    }

    public void test_sample_correctness1() throws Exception {
        String regexStr = "(+ (. (a) (| (b) (c) (+ (d))) (a)))";
        Sample sample = new Sample(regexStr);
        assertEquals("sample size", 0, sample.size());
        assertEquals("expression", regexStr, sample.getRegexStr());
    }
    
    public void test_sample_correctness2() throws Exception {
        String regexStr = "(+ (. (a) (| (b) (c) (+ (d))) (a)))";
        Sample sample = new Sample(regexStr, 50);
        assertEquals("sample size", 50, sample.size());
        Matcher regex = new Matcher(regexStr);
        for (String[] example : sample.getSample())
            assertTrue("match", regex.matches(example));
        assertEquals("expression", regexStr, sample.getRegexStr());
    }

    public void test_sample_correctness3() throws Exception {
        String regexStr = "(+ (. (a) (| (b) (c) (+ (d))) (a)))";
        Sample sample = new Sample(regexStr, 50);
        assertEquals("sample size", 50, sample.size());
        Matcher regex = new Matcher(regexStr);
        for (String[] example : sample.getSample())
            assertTrue("match", regex.matches(example));
        assertEquals("expression", regexStr, sample.getRegexStr());
        sample.add(30);
        assertEquals("sample size", 80, sample.size());
        for (String[] example : sample.getSample())
            assertTrue("match", regex.matches(example));
        assertEquals("expression", regexStr, sample.getRegexStr());
        sample.add(60);
        assertEquals("sample size", 140, sample.size());
        for (String[] example : sample.getSample())
            assertTrue("match", regex.matches(example));
        assertEquals("expression", regexStr, sample.getRegexStr());
    }
    
    public void test_serialization() throws Exception {
        String regexStr = "(+ (. (a) (| (b) (c) (+ (d))) (a)))";
        Sample sample = new Sample(regexStr, 50);
        Writer strWriter = new StringWriter();
        SampleWriter sampleWriter = new SimpleWriter(strWriter);
        sampleWriter.write(sample);
        String representation = strWriter.toString();
        Reader strReader = new StringReader(representation);
        SampleReader sampleReader = new SimpleReader(strReader);
        List<Sample> samples = sampleReader.read();
        assertEquals("samples", 1, samples.size());
        sample = samples.get(0);
        assertEquals("sample size", 50, sample.size());
        Matcher regex = new Matcher(regexStr);
        for (String[] example : sample.getSample())
            assertTrue("match", regex.matches(example));
        assertEquals("expression", regexStr, sample.getRegexStr());
    }

    public void test_multipleSerialization() throws Exception {
        String[] regexStrs = {
                "(+ (. (a) (| (b) (c) (+ (d))) (a)))",
                "(. (+ (. (a) (b))) (| (c) (d)))",
                "(| (. (a) (+ (b))) (. (c) (+ (b))))"
        };
        List<Sample> samples = new ArrayList<Sample>();
        for (String regexStr : regexStrs)
            samples.add(new Sample(regexStr, 50));
        Writer strWriter = new StringWriter();
        SampleWriter sampleWriter = new SimpleWriter(strWriter);
        for (Sample sample : samples)
            sampleWriter.write(sample);
        String representation = strWriter.toString();
        Reader strReader = new StringReader(representation);
        SampleReader sampleReader = new SimpleReader(strReader);
        samples = sampleReader.read();
        assertEquals("samples", regexStrs.length, samples.size());
        int i = 0;
        for (Sample sample : samples) {
            assertEquals("sample size", 50, sample.size());
            Matcher regex = new Matcher(regexStrs[i]);
            for (String[] example : sample.getSample())
                assertTrue("match", regex.matches(example));
            assertEquals("expression", regexStrs[i++], sample.getRegexStr());
        }
    }

}
