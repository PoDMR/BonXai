/*
 * Created on May 13, 2008
 * Modified on $Date: 2009-11-09 15:23:13 $
 */
package gjb.flt.schema.infer.ixsd;

import gjb.flt.automata.measures.MutualExclusionDistance;
import gjb.flt.treeautomata.factories.SupportContentAutomatonFactory;
import gjb.flt.treeautomata.factories.SupportContextAutomatonFactory;
import gjb.flt.treeautomata.impl.ContextAutomaton;
import gjb.util.sampling.SampleException;
import gjb.util.xml.acstring.AncestorChildrenDocumentIterator;
import gjb.util.xml.acstring.AncestorChildrenExampleParser;
import gjb.util.xml.acstring.ExampleParsingException;
import gjb.util.xml.acstring.ParseResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class XsdLearner {

    public ContextAutomaton learn(File file, int contextSize)
            throws IOException, ExampleParsingException, SampleException {
        ContextMap contextMap = computeContextMap(file, contextSize);
        ContextAutomaton contextFA = ContextMapConverter.convertToContextFA(contextMap);
        SupportContextAutomatonFactory factory = new SupportContextAutomatonFactory(new SupportContentAutomatonFactory());
        Merger friendlyMerger = new Merger(factory, contextFA);
        ContentEquivalenceRelation relation = new DirectEquivalenceRelation(0.5, new MutualExclusionDistance());
        friendlyMerger.setContentEquivalenceRelation(relation);
        friendlyMerger.merge();
        return contextFA;
    }

    public ContextMap computeContextMap(File file, int contextSize)
            throws IOException, ExampleParsingException, SampleException {
        ContextMap contextMap = new ContextMap(new SupportContentAutomatonFactory(),
                                               contextSize);
        Iterator<String> docIt = new AncestorChildrenDocumentIterator(file);
        while (docIt.hasNext()) {
            String doc = docIt.next();
            BufferedReader reader = new BufferedReader(new StringReader(doc));
            AncestorChildrenExampleParser parser = new AncestorChildrenExampleParser();
            parser.setStrippedFirst(true);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) break;
                ParseResult result = parser.parse(line);
                contextMap.add(result.getContext(), result.getContent());
            }
            reader.close();
        }
        return contextMap;
    }

}
