/*
 * Created on May 26, 2005
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package eu.fox7.flt.regex.infer.crx.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>Class that models an example for the attributes and content of an element
 * in a DTD.  It is used during the XML parsing process to hold the information
 * gathered in startElement(), characters() and endElement() during a SAX2 parse.</p>
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class Example {

    /**
     * Example's content, list of Strings
     */
    protected List<String> contentList = new LinkedList<String>();

    /**
     * Method that yields an Iterator over the Example's content.
     * @return Iterator over the Example's content
     */
    public Iterator<String> getContentIterator() {
        return contentList.iterator();
    }

    /**
     * Method to compute the occurrence Map for the Example, i.e. a mapping from
     * the content items to the number of times they occur in the Example
     * @return Map from content items to Integer
     */
    public Map<String,Integer> getOccurrenceMap() {
        Map<String,Integer> map = new HashMap<String,Integer>();
        for (String contentItem : contentList) {
            if (!map.containsKey(contentItem))
                map.put(contentItem, 0);
            map.put(contentItem, map.get(contentItem) + 1);
        }
        return map;
    }

    /**
     * Method that adds a content item to the Example's List of content items.
     * @param item
     *            String content item
     */
    public void addContentItem(String item) {
        contentList.add(item);
    }

    /**
     * Method for debugging purposes
     */
    public String toString() {
        return contentList.toString();
    }

}
