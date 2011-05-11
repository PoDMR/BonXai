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
package de.tudortmund.cs.bonxai.bonxai;

import java.util.Vector;

/**
 * List of groups
 */
public class GroupList {

    /**
     * List containing groups
     */
    private Vector<GroupElement> groupElements = new Vector<GroupElement>();

    /**
     * Default, empty constructor, use setter to fill.
     */
    public GroupList() {
    }

    /**
     * Constructor with pre-filled groupElements.
     */
    public GroupList(Vector<GroupElement> groupElements) {
        this.groupElements = groupElements;
    }

    /**
     * Returns the list of groups
     * @return groupElements
     */
    public Vector<GroupElement> getGroupElements() {
        return groupElements;
    }

    /**
     * Adds a group to the list of groups
     * @param groupElement
     */
    public void addGroupElement(GroupElement groupElement) {
        this.groupElements.add(groupElement);
    }
}

