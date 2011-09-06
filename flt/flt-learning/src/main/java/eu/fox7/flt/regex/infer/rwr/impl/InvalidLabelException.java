/*
 * Created on Sep 4, 2008
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package eu.fox7.flt.regex.infer.rwr.impl;

import eu.fox7.flt.regex.infer.InferenceException;

/**
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public class InvalidLabelException extends InferenceException {

    private static final long serialVersionUID = 415627072273830087L;

    public InvalidLabelException(String msg) {
        super(msg);
    }

}
