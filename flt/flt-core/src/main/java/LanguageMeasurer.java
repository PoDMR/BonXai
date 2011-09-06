import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.ModifiableNFA;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.measures.LanguageMeasure;
import eu.fox7.flt.automata.measures.LanguageTest;
import eu.fox7.flt.automata.measures.RelativeLanguageMeasure;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.converters.Converter;
import eu.fox7.util.cli.Application;
import eu.fox7.util.tree.SExpressionParseException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.cli.CommandLine;

/*
 * Created on Jul 9, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class LanguageMeasurer extends Application {

    public static final String DEFAULT_MEASURE_FORMAT = "%s: %.4f";
    public static final String DEFAULT_TEST_FORMAT= "%s: %b";
    public static final String LIST_SEP = ";";

    /**
     * @param args
     * @throws JSAPException 
     */
    public static void main(String[] args) {
		try {
			Application appl = new LanguageMeasurer();
			appl.run(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-Errors.RUNTIME_ERROR.ordinal());
		}
    }

    @Override
    protected void action(CommandLine cl) throws Exception {
        String regexStr = cl.getOptionValue('r');
        GlushkovFactory glushkov = new GlushkovFactory();
        try {
            SparseNFA nfa = glushkov.create(regexStr);
            if (cl.hasOption('m')) {
                String[] measureNames = splitOptionValues(cl.getOptionValue('m'));
                for (String measureName : measureNames) {
                	String format = cl.hasOption('M') ? cl.getOptionValue('M') : DEFAULT_MEASURE_FORMAT;
                    if (cl.hasOption('s')) {
                        ModifiableNFA cmpNFA = glushkov.create(cl.getOptionValue('s'));
                        System.out.println(String.format(format, measureName,
                                                         compute(measureName,
                                                                 nfa, cmpNFA),
                                                         regexStr));
                    } else {
                        System.out.println(String.format(format, measureName,
                                                         compute(measureName, nfa),
                                                         regexStr));
                    }
                }
            }
            if (cl.hasOption('t')) {
                String[] testNames = splitOptionValues(cl.getOptionValue('t'));
            	String format = cl.hasOption('T') ? cl.getOptionValue('T') : DEFAULT_TEST_FORMAT;
                for (String testName : testNames) {
                    System.out.println(String.format(format, testName,
                                                     test(testName, nfa),
                                                     regexStr));
                }                
            }
            if (cl.hasOption('c')) {
                Regex regex = new Regex(regexStr);
                String converterName = cl.getOptionValue('c');
                System.out.println(convert(converterName, regex));
            }
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            System.err.println("Error: invalid regular expression");
            System.exit(2);
        } catch (SecurityException e) {
            e.printStackTrace();
            System.exit(3);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.exit(3);
        } catch (UnknownOperatorException e) {
            e.printStackTrace();
            System.err.println("Error: invalid regular expression");
            System.exit(2);
        } catch (FeatureNotSupportedException e) {
            e.printStackTrace();
            System.err.println("Error: invalid regular expression");
            System.exit(2);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(2);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.exit(2);
        } catch (InstantiationException e) {
            e.printStackTrace();
            System.exit(2);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.exit(2);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

	@Override
    protected void defineParameters() {
    	addParameter("r", "regex", true, true,
    	             "regular expression to parse (prefix format)");
    	addParameter("s", "regex2", true, false,
    	             "regular expression to compare the first one to (prefix format)");
    	addParameter("m", "measures", true, false,
    	             "list of measures to calculate, separated by '" + LIST_SEP + "'");
    	addParameter("t", "tests", true, false,
    	             "list of tests to be performed, separated by '" + LIST_SEP + "'");
    	addParameter("M", "measure-format", true, false,
    	             "format string to display the measure result in, default '" + DEFAULT_MEASURE_FORMAT + "'");
    	addParameter("T", "test-format", true, false,
    	             "format string to display the measure result in, default '" + DEFAULT_MEASURE_FORMAT + "'");
    	addParameter("c", "converter", true, false,
    	             "converter to use on the regular expression" + DEFAULT_MEASURE_FORMAT + "'");
    }

	protected static String convert(String converterName, Regex regex)
            throws ClassNotFoundException, SecurityException,
                   NoSuchMethodException, IllegalArgumentException,
                   InstantiationException, IllegalAccessException,
                   InvocationTargetException {
        Class<?> c = Class.forName(converterName);
        Constructor<?> constructor = c.getConstructor();
        Converter converter = (Converter) constructor.newInstance();
        return converter.convert(regex);
    }

    protected static boolean test(String testName, SparseNFA nfa)
            throws ClassNotFoundException, SecurityException,
                   NoSuchMethodException, IllegalArgumentException,
                   InstantiationException, IllegalAccessException,
                   InvocationTargetException {
        Class<?> c = Class.forName(testName);
        Constructor<?> constructor = c.getConstructor();
        LanguageTest test = (LanguageTest) constructor.newInstance();
        return test.test(nfa);
    }

    protected static Double compute(String measureName, ModifiableNFA nfa, ModifiableNFA cmpNFA)
            throws ClassNotFoundException, SecurityException,
                   NoSuchMethodException, IllegalArgumentException,
                   InstantiationException, IllegalAccessException,
                   InvocationTargetException {
        Class<?> c = Class.forName(measureName);
        Constructor<?> constructor = c.getConstructor();
        RelativeLanguageMeasure<?> measure = (RelativeLanguageMeasure<?>) constructor.newInstance();
        Method computeMethod = measure.getClass().getMethod("compute",
                                                            new Class[] {SparseNFA.class,
        		                                                         SparseNFA.class});
        Class<?> returnClass = computeMethod.getReturnType();
        Method conversionMethod = returnClass.getMethod("doubleValue", new Class[0]);
        Object result = computeMethod.invoke(measure, new Object[] {nfa, cmpNFA});
        return (Double) conversionMethod.invoke(result, new Object[0]);
    }

    protected static double compute(String measureName, ModifiableNFA nfa)
            throws ClassNotFoundException, SecurityException,
                   NoSuchMethodException, IllegalArgumentException,
                   InstantiationException, IllegalAccessException,
                   InvocationTargetException {
        Class<?> c = Class.forName(measureName);
        Constructor<?> constructor = c.getConstructor();
        LanguageMeasure<?> measure = (LanguageMeasure<?>) constructor.newInstance();
        Method computeMethod = measure.getClass().getMethod("compute",
                                                            new Class[] {SparseNFA.class});
        Class<?> returnClass = computeMethod.getReturnType();
        Method conversionMethod = returnClass.getMethod("doubleValue", new Class[0]);
        Object result = computeMethod.invoke(measure, new Object[] {nfa});
        return (Double) conversionMethod.invoke(result, new Object[0]);
    }

    protected static String[] splitOptionValues(String arg) {
    	return arg.split("\\s*" + LIST_SEP + "\\s*");
    }

}
