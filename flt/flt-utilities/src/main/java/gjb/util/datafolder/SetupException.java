/*
 * Created on Jun 7, 2005
 * Modified on $Date: 2009-11-04 15:27:36 $
 */
package gjb.util.datafolder;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class SetupException extends Exception {

    private static final long serialVersionUID = -5252933439491260827L;
    protected String name;

    public SetupException(String name) {
        this.name = name;
    }

    public String getMessage() {
        return "column '" + name + "' can't be added to a DataSet that already contains data";
    }

}
