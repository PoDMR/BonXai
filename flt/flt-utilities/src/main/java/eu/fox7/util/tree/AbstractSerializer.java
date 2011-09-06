/*
 * Created on Apr 27, 2006
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.util.tree;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public abstract class AbstractSerializer<T> implements Serializer<T> {

    protected String emptyTreeString = "empty";
    protected NodeSerializer<T> nodeSerializer;

    public String getEmptyTreeString() {
        return emptyTreeString;
    }

    public void setEmptyTreeString(String emptyTreeString) {
        this.emptyTreeString = emptyTreeString;
    }

}
