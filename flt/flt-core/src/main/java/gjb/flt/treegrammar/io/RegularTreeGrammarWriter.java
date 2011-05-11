/*
 * Created on Jan 24, 2007
 * Modified on $Date: 2009-11-03 12:37:43 $
 */
package gjb.flt.treegrammar.io;

import gjb.flt.regex.Regex;
import gjb.flt.treegrammar.XMLGrammar;
import gjb.flt.treegrammar.XMLElementDefinition;
import gjb.flt.treegrammar.XMLElementNotDefinedException;
import gjb.util.tree.Node;
import gjb.util.tree.Tree;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
public class RegularTreeGrammarWriter implements GrammarWriter {

    /* (non-Javadoc)
     * @see gjb.xml.model.GrammarWriter#write(gjb.xml.model.XMLDocument, java.io.Writer)
     */
    public void write(XMLGrammar doc, Writer writer) throws IOException {
        XMLElementDefinition root = doc.getRootElement();
        writeRoot(root, writer);
        Map<String,String> ruleMap = new HashMap<String,String>();
        computeRules(doc, root, ruleMap);
        writeRules(writer, ruleMap);
    }

    public void write(XMLGrammar doc, Properties properties, Writer writer)
            throws IOException {
        if (properties != null) {
            for (String key : properties.stringPropertyNames()) {
                writer.write(XMLGrammar.COMMENT_STRING + " ");
                writer.write(key + " = " + properties.getProperty(key) + "\n");
            }
        }
        write(doc, writer);
    }

    protected void writeRoot(XMLElementDefinition root, Writer writer) throws IOException {
        writer.write(XMLGrammar.ROOT_DEF);
        writer.write(root.getQName());
        writer.write(XMLGrammar.QNAME_TYPE_SEPARATOR);
        writer.write(root.getType());
        writer.write("\n");
    }

    protected void writeRules(Writer writer, Map<String,String> rules) throws IOException {
        List<String> ruleList = new LinkedList<String>(rules.values());
        Collections.sort(ruleList, new StateNameComparator());
        for (String rule : ruleList) {
            writer.write(rule);
            writer.write("\n");
        }
    }

    protected void computeRules(XMLGrammar doc, XMLElementDefinition element,
                                Map<String, String> ruleMap) throws IOException {
        String elementName = XMLGrammar.computeElementName(element.getQName(),
                                                            element.getType());
        if (!ruleMap.containsKey(elementName)) {
            ruleMap.put(elementName, element.toString());
            if (!XMLElementDefinition.isTextType(elementName)) {
                Tree modelTree = element.getContentModel().getTree();
                for (Iterator<Node> it = modelTree.leaves(); it.hasNext(); ) {
                    String childElementName = it.next().getKey();
                    if (!XMLElementDefinition.isTextType(childElementName) &&
                            !childElementName.equals(Regex.EPSILON_SYMBOL)) {
                        try {
                            XMLElementDefinition childElement = doc.getElement(childElementName);
                            computeRules(doc, childElement, ruleMap);
                        } catch (XMLElementNotDefinedException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    protected class StateNameComparator implements Comparator<String> {

        public int compare(String name1, String name2) {
            String[] p1 = XMLGrammar.decomposeElementName(name1.substring(0, name1.indexOf(" ")));
            String[] p2 = XMLGrammar.decomposeElementName(name2.substring(0, name2.indexOf(" ")));
            int comp = p1[0].compareTo(p2[0]);
            if (comp == 0) {
                try {
                    Integer i1 = Integer.valueOf(p1[1]);
                    Integer i2 = Integer.valueOf(p2[1]);
                    return i1.compareTo(i2);
                } catch (NumberFormatException e) {
                    return p1[1].compareTo(p2[1]);
                }
            } else
                return comp;
        }
        
    }

}
