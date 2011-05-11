/*
 * Created on May 20, 2005
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package gjb.flt.regex.infer.crx.impl;


import java.util.Set;

/**
 * <p>Interface to be implemented by element classes that are instantiated during
 * the parse process.</p>
 * @author gjb
 * @version $Revision: 1.1 $
 */
public interface ParsingElement {

    /**
     * Method to be called from within endElement() to add the Example to the
     * element that was just read.
     * @param example
     *            Example that was just completed
     */
    public void addExample(Example example);

    /**
     * Method that returns the Element's alphabet
     * @return Set of Strings representing the alphabet
     */
    public Set<String> getAlphabet();

}