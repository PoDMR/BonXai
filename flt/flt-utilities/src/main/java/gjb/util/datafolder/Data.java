/*
 * Created on Jun 7, 2005
 * Modified on $Date: 2009-11-04 15:27:36 $
 */
package gjb.util.datafolder;

import java.util.Date;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class Data extends AbstractDataCollection {

    protected String creator;
    protected Date date;
    public Data(String name, String creator) {
        super();
        this.name = name;
        this.creator = creator;
        this.date = new Date();
    }

    /**
     * @return Returns the creator.
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator The creator to set.
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * @return Returns the date.
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date The date to set.
     */
    public void setDate(Date date) {
        this.date = date;
    }

}
