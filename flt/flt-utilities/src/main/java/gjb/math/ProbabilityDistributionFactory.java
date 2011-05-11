/*
 * Created on Feb 26, 2007
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.math;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class ProbabilityDistributionFactory {

    /**
     * constants that define the textual format for the ProbabilityDistribution
     */
    protected static final String PARAM_SEP_PATTERN = "\\s*,\\s*";
    protected static final String PARAM_DEF_SEP_PATTERN = "\\s*=\\s*";
    protected static final String PARAM_DEF_PATTERN = "\\w+" + PARAM_DEF_SEP_PATTERN + "[A-Za-z0-9._\\-]+";

    public ProbabilityDistribution create(String distrStr)
            throws IllDefinedDistributionException {
        return create(extractClassName(distrStr), extractParamStr(distrStr));
    }

    public ProbabilityDistribution create(String className, String paramStr)
            throws IllDefinedDistributionException {
        return create(className, computeParameterProperties(paramStr));
    }

    @SuppressWarnings("unchecked")
    public ProbabilityDistribution create(String className, Properties properties)
            throws IllDefinedDistributionException {
        try {
            Class objectClass = Class.forName(className);
            /* all probability distributions should have a constructor that takes only
             * java.util.Properties as an argument
             */
            Class[] parameterTypes = {properties.getClass()};
            Constructor constructor = objectClass.getConstructor(parameterTypes);
            Object[] parameterValues = {properties};
            return (ProbabilityDistribution) constructor.newInstance(parameterValues);
        } catch (IllegalArgumentException e) {
            throw new IllDefinedDistributionException(e);
        } catch (InstantiationException e) {
            throw new IllDefinedDistributionException(e);
        } catch (IllegalAccessException e) {
            throw new IllDefinedDistributionException(e);
        } catch (InvocationTargetException e) {
            System.err.println(className + ":\n" + properties.toString());
            e.printStackTrace();
            throw new IllDefinedDistributionException(e);
        } catch (SecurityException e) {
            throw new IllDefinedDistributionException(e);
        } catch (NoSuchMethodException e) {
            throw new IllDefinedDistributionException(e);
        } catch (ClassNotFoundException e) {
            throw new IllDefinedDistributionException(e);
        }
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

    protected static String extractClassName(String distrStr) {
        return distrStr.substring(0, distrStr.indexOf("(")).trim();
    }

    protected static String extractParamStr(String distrStr) {
        String paramStr = distrStr.substring(distrStr.indexOf("(") + 1).trim();
        return paramStr.substring(0, paramStr.lastIndexOf(")")).trim();
    }

}
