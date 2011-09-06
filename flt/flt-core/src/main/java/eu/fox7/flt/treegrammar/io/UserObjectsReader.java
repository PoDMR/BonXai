/**
 * Created on Nov 2, 2009
 * Modified on $Date: 2009-11-03 12:36:59 $
 */
package eu.fox7.flt.treegrammar.io;

import eu.fox7.flt.treegrammar.SyntaxException;
import eu.fox7.flt.treegrammar.UserObject;
import eu.fox7.flt.treegrammar.data.DataGenerator;
import eu.fox7.flt.treegrammar.generators.XMLGenerator;
import eu.fox7.math.ProbabilityDistribution;

import java.io.BufferedReader;
import java.io.Reader;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class UserObjectsReader {

    public static final String COMMENT_STRING = "#";
    protected static final String COMMENT_PATTERN = "^\\s*" + COMMENT_STRING + ".*$";
    protected static final String DISTRIBUTION_ID = "distribution";
    protected static final String GENERATOR_ID = "generator";
    protected static final String DEFINITION_PATTERN = "^\\s*(?:" +
                                                       DISTRIBUTION_ID + "|" +
                                                       GENERATOR_ID + ")\\s*:.+$";
    protected static final String MAX_DEPTH_DEF = "max-depth";
    protected static final String DISTRIBUTION_INTERFACE = "eu.fox7.math.ProbabilityDistribution";
    protected static final String GENERATOR_INTERFACE = "eu.fox7.xml.model.data.DataGenerator";
    protected XMLGenerator xmlGenerator;

    public UserObjectsReader(XMLGenerator xmlGenerator) {
    	this.xmlGenerator = xmlGenerator;
    }

    /**
     * Method that parses the specification of the UserObjects
     * @param userObjectReader
     *            Reader for the user objects specification
     * @throws SyntaxException
     *            thrown if the grammar's syntax is incorrect
     */
    public void readUserObjects(Reader userObjectsReader) throws SyntaxException {
        BufferedReader bReader = new BufferedReader(userObjectsReader);
        int lineNumber = 0;
        String line = null;
        try {
            while ((line = bReader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.matches(COMMENT_PATTERN) || line.length() == 0) {
                    continue;
                } else if (line.matches(DEFINITION_PATTERN)) {
                    UserObject userObject = new UserObject(line);
                    if (userObject.getType().equals(DISTRIBUTION_INTERFACE)) {
                        xmlGenerator.addDistribution(userObject.getName(),
                                                     (ProbabilityDistribution) userObject.getObject());
                    } else if (userObject.getType().equals(GENERATOR_INTERFACE)) {
                        xmlGenerator.addGenerator(userObject.getName(),
                                                  (DataGenerator) userObject.getObject());
                    } else {
                        throw new SyntaxException("unknown UserObject type '"
                                + userObject.getType() + "' for '"
                                + userObject.getName() + "'");
                    }
                } else if (line.toLowerCase().startsWith(MAX_DEPTH_DEF)) {
                    String depthStr = line.substring(line.indexOf("=") + 1).trim();
                    xmlGenerator.setMaxDepth(Integer.valueOf(depthStr).intValue());
                } else {
                    throw new SyntaxException("syntax error in line '" + line + "'");
                }
            }
        } catch (SyntaxException e) {
            throw e;
        } catch (Exception e) {
            throw new SyntaxException("exception while reading userobjects at line " +
                                      lineNumber, e);
        }
    }

}
