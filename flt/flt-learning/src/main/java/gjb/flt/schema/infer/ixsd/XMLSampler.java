/*
 * Created on Oct 23, 2006
 * Modified on $Date: 2009-11-09 13:12:26 $
 */
package gjb.flt.schema.infer.ixsd;

import gjb.flt.treeautomata.factories.SupportContentAutomatonFactory;
import gjb.flt.treeautomata.factories.SupportContextAutomatonFactory;
import gjb.flt.treeautomata.impl.ContextAutomaton;
import gjb.util.xml.AbstractXMLSampler;
import gjb.util.xml.ConfigurationException;

import java.io.IOException;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class XMLSampler extends AbstractXMLSampler {

    SupportContextAutomatonFactory factory = new SupportContextAutomatonFactory(new SupportContentAutomatonFactory());
    protected int maxDepth = 0;

    public XMLSampler() {
        super();
    }

    public SupportContextAutomatonFactory getContextFAFactory() {
        return factory;
    }

    public ContextAutomaton getContextFA() {
        return factory.getAutomaton();
    }

    protected void add(Document document) throws ConfigurationException {
        factory.add(new String[0],
                    new String[] {document.getRootElement().getQualifiedName()});
        add(document.getRootElement());
        try {
            if (isVerbose()) {
                writer.write("states,depth: " + factory.getAutomaton().getNumberOfStates() + "," + maxDepth + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConfigurationException(e);
        }
    }

    protected void add(Element element) throws ConfigurationException {
        ancestorPath.push(element);
        int depth = getAncestorString().length;
        if (depth > maxDepth)
            maxDepth = depth;
        factory.add(getAncestorString(), getChildString(element));
        for (Iterator<?> elemIt = element.elementIterator(); elemIt.hasNext(); )
            add((Element) elemIt.next());
        ancestorPath.pop();
    }

}
