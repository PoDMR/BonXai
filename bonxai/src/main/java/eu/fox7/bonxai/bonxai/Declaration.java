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
package eu.fox7.bonxai.bonxai;

import eu.fox7.bonxai.common.NamespaceList;

/**
 * Class representing the header of the Bonxai schema
 */
public class Declaration {

    /**
     * List of imports
     */
    private ImportList importList;
    /**
     * List of data-types
     */
    private DataTypeList dataTypeList;
    /**
     * List of namespaces
     */
    private NamespaceList namespaceList;

    /**
     * Constructor for the class Declaration
     * @param importList
     * @param dataTypeList
     * @param namespaceList
     */
    public Declaration(ImportList importList, DataTypeList dataTypeList, NamespaceList namespaceList) {
        this.importList = importList;
        this.dataTypeList = dataTypeList;
        this.namespaceList = namespaceList;
    }

    /**
     * Returns a list of imports
     * @return importList
     */
    public ImportList getImportList() {
        return importList;
    }

    /**
     * Returns a list of data-types
     * @return dataTypeList
     */
    public DataTypeList getDataTypeList() {
        return dataTypeList;
    }

    /**
     * Returns a list of namespaces
     * @return namespaceList
     */
    public NamespaceList getNamespaceList() {
        return namespaceList;
    }
}
