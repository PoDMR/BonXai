/*
 * Created on Nov 7, 2005
 * Modified on $Date: 2009-11-12 22:18:04 $
 */
package gjb.flt.regex.io;

import gjb.flt.regex.NoRegularExpressionDefinedException;
import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.util.tree.Node;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;

/**
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
public class XMLWriter {

    public static final String DEFAULT_SYMBOL_NAME = "symbol";
    protected String symbolName = DEFAULT_SYMBOL_NAME;
    protected Writer writer;

    public XMLWriter(Writer writer) {
        this.writer = writer;
    }

    public String getSymbolName() {
        return symbolName;
    }

    public void setSymbolName(String symbolName) {
        this.symbolName = symbolName;
    }

    public void write(Regex regex) throws IOException,
            NoRegularExpressionDefinedException {
        Document doc = toXML(regex);
        OutputFormat format = OutputFormat.createPrettyPrint();
        org.dom4j.io.XMLWriter xmlWriter = new org.dom4j.io.XMLWriter(writer, format); 
        xmlWriter.write(doc);
    }

    public Document toXML(Regex regex) throws NoRegularExpressionDefinedException {
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("regex");
        if (!regex.hasTree()) throw new NoRegularExpressionDefinedException();
        toXML(root, regex.getTree().getRoot(), regex);
        return doc;
    }

    protected void toXML(Element parent, Node node, Regex regex) {
        String name = null;
        final String key = node.getKey();
        if (!node.isLeaf())
            try {
                name = regex.identifySymbol(key);
            } catch (UnknownOperatorException e) {
                throw new RuntimeException(e);
            }
        else if (regex.emptySymbol().equals(key))
            name = regex.emptySymbol().toLowerCase();
        else if (regex.epsilonSymbol().equals(key))
            name = regex.epsilonSymbol().toLowerCase();
        else
            name = symbolName;
        Element nodeElement = parent.addElement(name);
        if (!node.isLeaf() && key.matches(regex.mToNOperator())) {
            Pattern pattern = Pattern.compile(regex.mToNOperator());
            Matcher matcher = pattern.matcher(key);
            if (matcher.matches()) {
                nodeElement.addAttribute(regex.mToNMin(), matcher.group(1));
                nodeElement.addAttribute(regex.mToNMax(), matcher.group(2));
            } else {
                throw new RuntimeException("should match");
            }
        }
        if (node.isLeaf() && name.equals(symbolName)) {
            nodeElement.addText(key);
        } else {
            for (int i = 0; i < node.getNumberOfChildren(); i++) {
                toXML(nodeElement, node.child(i), regex);
            }
        }
    }

    public void close() throws IOException {
        writer.close();
    }

}
