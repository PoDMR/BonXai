/*
 * Created on Aug 5, 2008
 * Modified on $Date: 2009-11-04 15:27:36 $
 */
package eu.fox7.util.datafolder;

import java.io.IOException;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public interface DataWriter {

    public void write(Data data) throws IOException;
    public void close() throws IOException;

}
