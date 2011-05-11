/*
 * Created on Aug 5, 2008
 * Modified on $Date: 2009-11-04 15:27:36 $
 */
package gjb.util.datafolder;

import java.io.IOException;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public interface DataWriter {

    public void write(Data data) throws IOException;
    public void close() throws IOException;

}
