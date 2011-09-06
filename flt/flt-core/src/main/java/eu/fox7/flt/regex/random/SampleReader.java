/*
 * Created on May 30, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.random;

import java.util.List;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public interface SampleReader {

    public List<Sample> read() throws SampleReadException;

}
