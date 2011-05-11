/**
 * Created on Nov 2, 2009
 * Modified on $Date: 2009-11-04 09:00:36 $
 */
package gjb.flt.treegrammar.io;

import gjb.flt.regex.generators.LanguageGenerator;
import gjb.flt.treegrammar.SyntaxException;
import gjb.flt.treegrammar.UserObjectNotDefinedException;
import gjb.flt.treegrammar.XMLAttributeDefinition;
import gjb.flt.treegrammar.XMLElementDefinition;
import gjb.flt.treegrammar.XMLGrammar;
import gjb.flt.treegrammar.generators.XMLGenerator;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lucg5005
 * @version $Revision: 1.3 $
 *
 */
public class RegularTreeGrammarReader {

    public static final String COMMENT_STRING = "#";
    protected static final String COMMENT_PATTERN = "^\\s*" + COMMENT_STRING + ".*$";
    protected static final String ROOT_DEF_PATTERN = "^\\s*root\\s*=.*$";
    public static final String QNAME_TYPE_SEPARATOR = "#";
    protected static final String LHS_RHS_RULE_SEP_PATTERN = "\\s*->\\s*";
    protected static final String ATTR_CONTENT_MODEL_SEP_PATTERN = "\\s*;\\s*";
    protected static final String ATTR_DEF_SEP_PATTERN = "\\s*,\\s*";
    protected static final String ATTR_DEF_SEP = ", ";
    protected static final String ATTR_DISTR_PATTERN = "^(\\w+)(?:(\\?)(?:\\[([^\\]]+)\\])?)?$";
    protected static Pattern attrDistrPattern = Pattern.compile(ATTR_DISTR_PATTERN);
	protected XMLGenerator xmlGenerator;

	public RegularTreeGrammarReader() {
		this(new XMLGenerator());
	}

	public RegularTreeGrammarReader(XMLGenerator treeGenerator) {
    	this.xmlGenerator = treeGenerator;
    }

    public XMLGrammar readGrammar(Reader grammarReader)
            throws SyntaxException {
    	XMLGrammar xmlGrammar = new XMLGrammar();
        BufferedReader bReader = new BufferedReader(grammarReader);
        int lineNumber = 0;
        String line = null;
        try {
            while ((line = bReader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.matches(COMMENT_PATTERN) || line.length() == 0) {
                    continue;
                } else if (line.matches(ROOT_DEF_PATTERN)) {
                    line = line.substring(line.indexOf("=") + 1).trim();
                    String[] parts = line.split(QNAME_TYPE_SEPARATOR, 2);
                    String qName = parts[0];
                    String type = parts[1];
                    int i = -1;
                    if ((i = type.indexOf("[")) >= 0) {
                        String distrName = type.substring(i + 1, type.length() - 1).trim();
                        type = type.substring(0, i).trim();
                        xmlGenerator.setDepthDistribution(distrName);
                    }
                    xmlGrammar.setRootElement(qName, type);
                } else {
                    String[] parts = line.split(LHS_RHS_RULE_SEP_PATTERN, 2);
                    String lhs = parts[0];
                    String rhs = parts[1];
                    parts = lhs.split(QNAME_TYPE_SEPARATOR, 2);
                    String qName = parts[0];
                    String type = parts[1];
                    parts = rhs.split(ATTR_CONTENT_MODEL_SEP_PATTERN, 2);
                    String attrStr = parts[0];
                    String contentModelStr = parts[1];
                    List<XMLAttributeDefinition> attributes = computeAttributes(xmlGrammar, attrStr);
                    XMLElementDefinition elemDef = xmlGrammar.addGrammarRule(qName, type, attributes, contentModelStr);
                    
                    LanguageGenerator contentModel = xmlGenerator.addContentGenerator(elemDef,
                                                                                      contentModelStr);
                    for (String distrName : xmlGenerator.getDistributionNames())
                    	contentModel.addDistribution(distrName, xmlGenerator.getDistribution(distrName));
                }
            }
        } catch (Exception e) {
            throw new SyntaxException("exception while reading grammar at line " +
                                      lineNumber + ": " + e.getMessage(), e);
        }
        return xmlGrammar;
    }

    /**
     * Method to compute the List of XMLAttributes from its textual representation 
     * @param attrStr
     *            String representation of the attributes
     * @return List with XMLAttributes
     * @throws UserObjectNotDefinedException
     *            thrown if a generator or distribution is unknown
     */
     protected List<XMLAttributeDefinition> computeAttributes(XMLGrammar xmlGrammar,
                                                              String attrStr)
             throws UserObjectNotDefinedException {
         List<XMLAttributeDefinition> list = new LinkedList<XMLAttributeDefinition>();
         if (attrStr.trim().length() == 0) return list;
         String[] attrDefs = attrStr.split(ATTR_DEF_SEP_PATTERN);
         for (int i = 0; i < attrDefs.length; i++) {
             String[] attrDef = attrDefs[i].split(QNAME_TYPE_SEPARATOR, 2);
             String qName = attrDef[0];
             String type = null;
             boolean isOptional = false;
             String distrName = null;
             Matcher matcher = attrDistrPattern.matcher(attrDef[1]);
             if (matcher.matches()) {
                 type = matcher.group(1);
                 if (matcher.group(2) != null) {
                     isOptional = true;
                     distrName = "default-zeroOrOne";
                 }
                 if (matcher.group(3) != null) {
                     distrName = matcher.group(3);
                 }
             } else {}
             XMLAttributeDefinition attr = new XMLAttributeDefinition(xmlGrammar, qName, type,
                                                                      isOptional);
             if (distrName != null)
            	 xmlGenerator.addContentDistribution(attr, xmlGenerator.getDistribution(distrName));
             xmlGenerator.addContentGenerator(attr, xmlGenerator.getGenerator(type));
             list.add(attr);
         }
         return list;
     }

}
