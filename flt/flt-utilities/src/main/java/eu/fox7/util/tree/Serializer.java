/*
 * Created on Nov 8, 2005
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.util.tree;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public interface Serializer<T> {

    public T serialize(Tree tree);
    public T serialize(Tree tree, NodeSerializer<T> nodeSerializer);

}
