/*
 * Created on Dec 20, 2005
 * Modified on $Date: 2009-10-27 14:14:00 $
 */
package eu.fox7.flt.automata;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class NoSuchStateException extends Exception {

    private static final long serialVersionUID = 1L;
    protected String stateValue;
    
    public NoSuchStateException(String stateValue) {
        this.stateValue = stateValue;
    }

    @Override
    public String getMessage() {
        return "no state '" + stateValue + "' defined";
    }

}
