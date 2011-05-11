/**
 * This file is part of BonXai.
 *
 * BonXai is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BonXai is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.tudortmund.cs.bonxai.converter.xsd2bonxai.old;

import java.util.*;

/**
 * Class representing a list of ancestor trees.
 *
 * If an ancestor-path in an ancestor tree specifies a certain ComplexType
 * accurately the last transition of this ancestor-path is not marked, so that no
 * outgoing transitions are generated for the node, which is the destination of
 * this transition.
 */
public class AncestorTreeList {

    /**
     * List of ancestor trees.
     */
    private LinkedList<AncestorTree> treeList;
    /**
     * Hashmap containing marked current element names.
     *
     * If it returns true for a certain element name this means there are more
     * than one transition annotated with this element name in all
     * currentTransitionLayers.
     */
    private HashMap<String, Boolean> currentMarks;
    /**
     * Hashmap containing marked next element names.
     *
     * If it returns true for a certain element name this means there are more
     * than one transition annotated with this element name in all
     * nextTransitionLayers.
     */
    private HashMap<String, Boolean> nextMarks;

    /**
     * Constructor of the AncestorTreeList, which obtains no parameter.
     */
    public AncestorTreeList() {
        treeList = new LinkedList<AncestorTree>();
        currentMarks = new HashMap<String, Boolean>();
        nextMarks = new HashMap<String, Boolean>();
    }

    /**
     * Returns the list of ancestor trees for this AncestorTreeList.
     *
     * @return list of ancestor trees
     */
    public LinkedList<AncestorTree> getTreeList() {
        return treeList;
    }

    /**
     * Returns the hashmap of currently marked element names.
     *
     * @return hashmap, which contains marked element names
     */
    public HashMap<String, Boolean> getCurrentMarks() {
        return currentMarks;
    }

    /**
     * Adds the element name to the hashmap currentMarks, if it is the second
     * time the element is, boolean is set to true.
     *
     * @param elementName   name of an element
     */
    public void setCurrentMark(String elementName) {
        if (currentMarks.containsKey(elementName)) {
            currentMarks.put(elementName, true);
        } else {
            currentMarks.put(elementName, false);
        }
    }

    /**
     * Returns the hashmap of next marked element names.
     *
     * @return hashmap, which contains marked element names
     */
    public HashMap<String, Boolean> getNextMarks() {
        return nextMarks;
    }

    /**
     * Adds the element name to the hashmap nextMarks, if it is the second time
     * the element is, boolean is set to true.
     *
     * @param elementName   name of an element
     */
    public void setNextMark(String elementName) {
        if (nextMarks.containsKey(elementName)) {
            nextMarks.put(elementName, true);
        } else {
            nextMarks.put(elementName, false);
        }
    }

    /**
     * Swaps the hashmap of next marked element names with the current one and
     * replaces the hashmap of next marked element names with an empty one.
     */
    public void swapMarks() {
        currentMarks = nextMarks;
        nextMarks = new HashMap<String, Boolean>();
    }

    /**
     * Sets each of the next marked element names to false.
     */
    public void setAllNextMarkToFalse() {
        Set<String> keys = nextMarks.keySet();
        for (Iterator<String> it = keys.iterator(); it.hasNext();) {
            nextMarks.put(it.next(), false);
        }
    }
}
