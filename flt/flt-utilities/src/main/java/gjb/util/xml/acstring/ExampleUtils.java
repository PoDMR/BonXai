/*
 * Created on Nov 22, 2005
 * Modified on $Date: 2009-11-09 13:13:38 $
 */
package gjb.util.xml.acstring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class ExampleUtils {

    public static final String EXAMPLE_SEP = "\\s+";
    public static final String COMMENT = "#"; 
    protected static int maximumAttempts = 10;
    protected static int numberOfExamples = 0;

    public static int getMaximumAttempts() {
        return maximumAttempts;
    }

    public static void setMaximumAttempts(int maximumAttempts) {
        ExampleUtils.maximumAttempts = maximumAttempts;
    }

    public static int getNumberOfExamples() {
        return numberOfExamples;
    }

    public static Reader diluteExamples(Reader inReader,
                                        double fractionToDelete)
            throws DilutionFactorNotReachedException, IOException {
        BufferedReader bReader = new BufferedReader(inReader);
        List<String> lines = new LinkedList<String>();
        String line = null;
        while ((line = bReader.readLine()) != null) {
            lines.add(line);
        }
        boolean isValid = false;
        int attempts = 0;
        StringBuilder str = null;
        while (!isValid && ++attempts <= maximumAttempts) {
            numberOfExamples = 0;
            Set<String> alphabet = new HashSet<String>();
            Set<String> dilutedAlphabet = new HashSet<String>();
            str = new StringBuilder();
            for (String l : lines) {
                String[] tokens = l.trim().split(EXAMPLE_SEP);
                addTokensToAlphabet(tokens, alphabet);
                if (Math.random() > fractionToDelete) {
                    str.append(l).append("\n");
                    addTokensToAlphabet(tokens, dilutedAlphabet);
                    numberOfExamples++;
                }
            }
            isValid = alphabet.equals(dilutedAlphabet);
        }
        if (attempts > maximumAttempts || str.length() == 0)
            throw new DilutionFactorNotReachedException();
        else 
            return new StringReader(str.toString());
    }

    public static List<String[]> diluteExamples(List<String[]> allExamples,
                                                double fractionToDelete)
            throws DilutionFactorNotReachedException {
        boolean isValid = false;
        int attempts = 0;
        List<String[]> examples = new LinkedList<String[]>();
        while (!isValid && ++attempts <= maximumAttempts) {
            numberOfExamples = 0;
            Set<String> alphabet = new HashSet<String>();
            Set<String> dilutedAlphabet = new HashSet<String>();
            examples.clear();
            for (String[] example : allExamples) {
                addTokensToAlphabet(example, alphabet);
                if (Math.random() > fractionToDelete) {
                    examples.add(example);
                    addTokensToAlphabet(example, dilutedAlphabet);
                    numberOfExamples++;
                }
            }
            isValid = alphabet.equals(dilutedAlphabet);
        }
        if (attempts > maximumAttempts || examples.size() == 0)
            throw new DilutionFactorNotReachedException();
        else
            return examples;
    }

    protected static String exampleToString(String[] example) {
        StringBuilder str = new StringBuilder();
        if (example.length > 0)
            str.append(example[0]);
        for (int i = 1; i < example.length; i++)
            str.append(" ").append(example[i]);
        return str.toString();
    }

    protected static boolean containsSignature(String[] example,
                                               Set<String> signatures) {
        for (int i = 1; i < example.length; i++)
            if (signatures.contains(createSignature(example[i-1], example[i])))
                return true;
        return false;
            
    }

    public static List<String[]> parseExamples(Reader reader) throws IOException {
        List<String[]> examples = new LinkedList<String[]>();
        BufferedReader bReader = new BufferedReader(reader);
        String line = null;
        while ((line = bReader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith(COMMENT)) continue;
            if (line.length() == 0) {
                examples.add(new String[0]);
            } else {
                examples.add(line.split(EXAMPLE_SEP));
            }
        }
        return examples;
    }

    protected static String createSignature(String fromStr, String toStr) {
        return fromStr + "#" + toStr;
    }

    protected static void addTokensToAlphabet(String[] tokens,
                                              Set<String> alphabet) {
        for (int i = 0; i < tokens.length; i++) {
            alphabet.add(tokens[i]);
        }
    }

}
