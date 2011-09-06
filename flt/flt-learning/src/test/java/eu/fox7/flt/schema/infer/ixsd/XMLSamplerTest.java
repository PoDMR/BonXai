/*
 * Created on Feb 9, 2007
 * Modified on $Date: 2009-11-10 10:02:53 $
 */
package eu.fox7.flt.schema.infer.ixsd;

import eu.fox7.flt.automata.measures.EquivalenceTest;
import eu.fox7.flt.automata.measures.RelativeLanguageMeasure;
import eu.fox7.flt.automata.measures.SOAWeightedEditDistance;
import eu.fox7.flt.schema.infer.ixsd.XMLSampler;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;
import eu.fox7.flt.treeautomata.impl.ContextAutomaton;

import java.io.File;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class XMLSamplerTest extends TestCase {

    public static Test suite() {
        return new TestSuite(XMLSamplerTest.class);
    }

    public void test_fileListConsistency() throws Exception {
        final int nrSamplers = 3;
        final int nrFiles = 20;
        final String dirName = this.getClass().getResource("/test-data/multi").getFile();
        XMLSampler[] samplers = new XMLSampler[nrSamplers];
        for (int i = 0; i < samplers.length; i++) {
            samplers[i] = new XMLSampler();
            samplers[i].setMaxFiles(nrFiles);
            samplers[i].parse(dirName);
        }
        for (int i = 1; i < samplers.length; i++) {
            List<File> list1 = samplers[i-1].getParsedFiles();
            List<File> list2 = samplers[i].getParsedFiles();
            assertEquals("sample sizes", list1.size(), list2.size());
            for (int j = 0; j < list1.size(); j++)
                assertEquals("file", list1.get(j), list2.get(j));
        }
    }

    public void test_contextFAConsistency() throws Exception {
        final int nrSamplers = 3;
        final int nrFiles = 20;
        final String dirName = this.getClass().getResource("/test-data/multi").getFile();
        XMLSampler[] samplers = new XMLSampler[nrSamplers];
        for (int i = 0; i < samplers.length; i++) {
            samplers[i] = new XMLSampler();
            samplers[i].setMaxFiles(nrFiles);
            samplers[i].parse(dirName);
        }
        for (int i = 1; i < samplers.length; i++) {
            ContextAutomaton nfa1 = samplers[i-1].getContextFA();
            ContextAutomaton nfa2 = samplers[i].getContextFA();
            assertTrue("context FAs", EquivalenceTest.areEquivalent(nfa1, nfa2));
        }
    }

    public void test_contentModelConsistency() throws Exception {
        final int nrSamplers = 3;
        final int nrFiles = 20;
        final String dirName = this.getClass().getResource("/test-data/multi").getFile();
        XMLSampler[] samplers = new XMLSampler[nrSamplers];
        for (int i = 0; i < samplers.length; i++) {
            samplers[i] = new XMLSampler();
            samplers[i].setMaxFiles(nrFiles);
            samplers[i].parse(dirName);
        }
        for (int i = 1; i < samplers.length; i++) {
            ContextAutomaton nfa1 = samplers[i-1].getContextFA();
            ContextAutomaton nfa2 = samplers[i].getContextFA();
            for (String stateValue : nfa1.getStateValues()) {
                ContentAutomaton model1 = nfa1.getAnnotation(stateValue);
                ContentAutomaton model2 = nfa2.getAnnotation(stateValue);
                RelativeLanguageMeasure<Double> measure = new SOAWeightedEditDistance();
                assertEquals("models for " + stateValue,
                             0.0,
                             measure.compute(model1, model2));
            }
        }
    }
    
}
