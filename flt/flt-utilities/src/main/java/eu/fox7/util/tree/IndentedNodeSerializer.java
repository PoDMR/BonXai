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
public interface IndentedNodeSerializer<T> extends NodeSerializer<T> {

    public void setInitialIndent(int indentNumber);

}
