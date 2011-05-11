/*
 * Created on Nov 7, 2005
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.io;

import gjb.flt.regex.InvalidXMLException;
import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.util.tree.SExpressionParseException;

import java.io.Reader;
import java.util.Iterator;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class XMLReader {

    public static final String DEFAULT_SYMBOL_NAME = XMLWriter.DEFAULT_SYMBOL_NAME;
    protected String symbolName = DEFAULT_SYMBOL_NAME;
    protected Reader reader;
    protected Properties properties;

    public XMLReader(Reader reader) {
        this(reader, null);
    }

    public XMLReader(Reader reader, Properties properties) {
        this.reader = reader;
        this.properties = properties;
    }

    public String getSymbolName() {
        return symbolName;
    }

    public void setSymbolName(String symbolName) {
        this.symbolName = symbolName;
    }

    public Regex read() throws DocumentException, SExpressionParseException,
            UnknownOperatorException, InvalidXMLException {
        return fromXML((new SAXReader()).read(reader));
    }

    public Regex fromXML(Document doc) throws InvalidXMLException,
            UnknownOperatorException {
        Regex regex = new Regex(properties);
        StringBuffer str = new StringBuffer();
        Element element = doc.getRootElement();
        fromXML(str, element, regex);
        try {
	        return new Regex(str.toString(), properties);
        } catch (SExpressionParseException e) {
        	throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected void fromXML(StringBuffer str, Element element, Regex regex)
            throws InvalidXMLException {
        if (element.getName().equals("regex")) {
            int counter = 0;
            for (Iterator it = element.elementIterator(); it.hasNext(); ) {
                if (++counter > 1) throw new InvalidXMLException("multiple elements under root");
                fromXML(str, (Element) it.next(), regex);
            }
            if (counter == 0) throw new InvalidXMLException("no elements under root");
        } else {
            if (regex.isSymbolName(element.getName())) {
                str.append(Regex.LEFT_BRACKET);
                if (element.getName().equals("mToN")) {
                    str.append(regex.mToNLeftBracket());
                    str.append(element.attributeValue(regex.mToNMin()));
                    str.append(regex.mToNSeparator());
                    str.append(element.attributeValue(regex.mToNMax()));
                    str.append(regex.mToNRightBracket());
                } else {
                    try {
                        String symbol = regex.getSymbol(element.getName());
                        if (regex.isOperatorSymbol(symbol))
                            str.append(symbol);
                        else if (regex.isEmptySymbol(symbol))
                            str.append(regex.emptySymbol());
                        else if (regex.isEpsilonSymbol(symbol))
                            str.append(regex.epsilonSymbol());
                        else
                            throw new InvalidXMLException("'" + symbol + "' is not an operator"); 
                    } catch (UnknownOperatorException e) {}
                }
                for (Iterator it = element.elementIterator(); it.hasNext(); ) {
                    str.append(" ");
                    fromXML(str, (Element) it.next(), regex);
                }
                str.append(Regex.RIGHT_BRACKET);
            } else if (element.getName().equals(symbolName)){
                str.append(Regex.LEFT_BRACKET);
                str.append(element.getTextTrim());
                str.append(regex.rightBracket());
            } else
                throw new InvalidXMLException("element '" + element.getName() + "' undefined");
        }
    }

}
