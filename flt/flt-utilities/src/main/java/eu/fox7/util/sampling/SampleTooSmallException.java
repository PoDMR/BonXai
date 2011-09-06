/*
 * Created on Mar 18, 2005
 * Modified on $Date: 2009-10-27 14:11:40 $
 */
package eu.fox7.util.sampling;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 * <p>Exception to be thrown by Sampler objects when the requested number of examples
 * exceeds the sample size
 */
public class SampleTooSmallException extends Exception {

    private static final long serialVersionUID = 1L;

    public SampleTooSmallException() {}

}
