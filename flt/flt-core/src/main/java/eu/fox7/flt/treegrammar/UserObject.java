/*
 * Created on Jun 24, 2005
 * Modified on $Date: 2009-11-03 12:36:00 $
 */
package eu.fox7.flt.treegrammar;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>A UserObject is a wrapper/parser for DataGenerators and
 * ProbabilityDistributions.  The syntax for such an object is:
 * <pre>
 * type: name = class(param_1 = value_1, ..., param_n = value_n)
 * </pre>
 * e.g.
 * <pre>
 * distribution: skewed = eu.fox7.math.UserDefinedDistribution(0 = 0.1, 1 = 0.9)
 * </pre>
 * or
 * <pre>
 * generator: CDATA = eu.fox7.xml.model.StringGenerator(min = 3, max = 30)
 * </pre>
 * The probabilities used in the example shown in eu.fox7.xml.model.XMLDocument can
 * be defined as:
 * <pre>
 * distribution: skewed = eu.fox7.math.UserDefinedDistribution(0 = 0.1, 1 = 0.9)
 * generator: CDATA = eu.fox7.xml.model.StringGenerator()
 * generator: PCDATA = eu.fox7.xml.model.StringGenerator(max = 30)
 * distribution: depth = eu.fox7.math.GaussianIntegerDistribution(mean = 10.0, variance = 2.0)
 * </pre>
 * <p> Currently, two types of user objects are supported, 'generator' and
 * 'distribution'.  Classes to be used as generators or distributions should
 * implement the eu.fox7.xml.model.DataGenerator and 
 * eu.fox7.math.ProbabilityDistribution respectively. </p>
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public class UserObject {

    /**
     * constants that define the textual format for the UserObject
     */
    protected static final String PARAM_SEP_PATTERN = "\\s*,\\s*";
    protected static final String PARAM_DEF_SEP_PATTERN = "\\s*=\\s*";
    protected static final String PARAM_DEF_PATTERN = "\\w+" + PARAM_DEF_SEP_PATTERN + "[A-Za-z0-9._\\-]+";
    protected static final String PARAMETER_PATTERN = PARAM_DEF_PATTERN +
                                                      "(?:" + PARAM_SEP_PATTERN +
                                                      PARAM_DEF_PATTERN + ")*";
    protected static final String USER_OBJECT_PATTERN = "^(\\w+)\\s*:\\s*(\\w+)\\s*=\\s*(\\w+(?:\\.\\w+)*)\\((" + PARAMETER_PATTERN + ")?\\)$";
    /**
     * Pattern to interprete the UserObject's definition
     */
    protected static Pattern userObjectPattern = Pattern.compile(USER_OBJECT_PATTERN);
    /**
     * UserObject's name, i.e. the identifier used in the grammar's decoration
     */
    protected String name;
    /**
     * The UserObject's type, can be 'generator' or 'distribution'
     */
    protected String type;
    /**
     * The name of the class to instantiate the object from
     */
    protected String className;
    /**
     * The object, either a DataGenerator or a ProbabilityDistribution
     */
    protected Object object;
    /**
     * map between the type and the Java interfaces corresponding to them
     */
    protected static Map<String,String> typeMap = new HashMap<String,String>();

    /**
     * Constructor to be called internally to do some initialization
     *
     */
    protected UserObject() {
        if (typeMap.size() == 0) {
            typeMap.put(XMLGrammar.DISTRIBUTION_ID,
                        XMLGrammar.DISTRIBUTION_INTERFACE);
            typeMap.put(XMLGrammar.GENERATOR_ID,
                        XMLGrammar.GENERATOR_INTERFACE);
        }
    }

    /**
     * Constructor that parses the definition string to construct the appropriate
     * object that can be retrieved via the getObject() method
     * @param defStr
     *            String that defines the UserObject
     * @throws SyntaxException
     *            thrown if the input format is incorrect
     */
    public UserObject(String defStr) throws SyntaxException {
        this();
        Matcher matcher = userObjectPattern.matcher(defStr);
        if (matcher.matches()) {
            try {
                this.type = typeMap.get(matcher.group(1));
                this.name = matcher.group(2);
                this.className = matcher.group(3);
                Class<?> objectClass = Class.forName(className);
                /* all user objects should have a constructor that takes only
                 * java.util.Properties as an argument
                 */
                Properties parameters = computeParameterProperties(matcher.group(4));
                Class<?>[] parameterTypes = {Class.forName("java.util.Properties")};
                Constructor<?> constructor = objectClass.getConstructor(parameterTypes);
                Object[] parameterValues = {parameters};
                this.object = constructor.newInstance(parameterValues);
            } catch (Exception e) {
                e.printStackTrace();
                throw new SyntaxException("invalid user object definition: '" +
                                          defStr + "'", e);
            }
        } else {
            throw new SyntaxException("invalid user object definition: '" +
                                      defStr + "'");
        }
    }

    /**
     * Method to retrieve the name as it occurs in a decorated grammar
     * @return String name.
     */
    public String getName() {
        return name;
    }

    /**
     * Method to retrieve the constructed UserObject, can either be a DataGenerator
     * or a ProbabilityDistribution, so a type cast has to be done in the calling
     * context based on the type.
     * @return Object object.
     */
    public Object getObject() {
        return object;
    }

    /**
     * Method to retrieve the type, can either be 'generator' or 'distribution',
     * necessary to know which type cast to do.
     * @return String type.
     */
    public String getType() {
        return type;
    }

    /**
     * Convenience method to parse the parameter string and represent it as a set
     * of key/value pairs in a Properties object
     * @param paramStr
     *            String representing the name/value pair as arguments for
     *            the object's constructor
     * @return Properties representing the parameters
     */
    protected Properties computeParameterProperties(String paramStr) {
        Properties properties = new Properties();
        if (paramStr != null) {
            paramStr = paramStr.trim();
            if (paramStr.length() > 0) {
                String[] params = paramStr.split(PARAM_SEP_PATTERN);
                for (int i = 0; i < params.length; i++) {
                    String[] parts = params[i].split(PARAM_DEF_SEP_PATTERN, 2);
                    properties.setProperty(parts[0], parts[1]);
                }
            }
        }
        return properties;
    }
}
