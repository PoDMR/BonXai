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

import de.tudortmund.cs.bonxai.common.DualHashtable;
import java.util.TreeSet;

/**
 * List of data-types
 */
public class DataTypeList {

    /**
     * Hashtable holding data-types
     */
    private DualHashtable dataTypes = new DualHashtable();

    /**
     * Adds a data-type to the hashtable
     * @param dataType
     */
    public void addDataType(DataType dataType) {
        dataTypes.addElement(dataType);
    }

    /**
     * Returns data-type with parametrised identifier if exists in hashtable
     * @param identifier
     * @return dataType
     */
    public DataType getDataTypeByIdentifier(String identifier) {
        if (identifier != null) {
            return new DataType(identifier, dataTypes.getByIdentifier(identifier));
        }
        return null;
    }

    /**
     * Returns data-type with parametrised uri if exists in hashtable
     * @param uri
     * @return dataType
     */
    public DataType getDataTypeByUri(String uri) {
        if (uri != null) {
            return new DataType(dataTypes.getByUri(uri), uri);
        }
        return null;
    }

    /**
     * Returns an ordered list of URIs inside the dataTypes DualHashtable.
     */
    public TreeSet<String> getUris(){
        return this.dataTypes.getUris();
    }

}
