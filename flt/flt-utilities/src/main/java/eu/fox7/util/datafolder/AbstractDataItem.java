/*
 * Created on Aug 5, 2008
 * Modified on $Date: 2009-11-04 15:27:36 $
 */
package eu.fox7.util.datafolder;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
abstract class AbstractDataItem {

    protected String name;
    protected String description = "";

    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean hasDescription() {
        return description != null && description.length() > 0;
    }

}
