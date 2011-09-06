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

import java.util.TreeSet;

import eu.fox7.bonxai.common.DualHashtable;

/**
 * Class holding the list of imports
 */
public class ImportList {

    /**
     * Hashtable holding the imports
     */
    private DualHashtable importList = new DualHashtable();

    /**
     * Adds an import to the hashtable
     * @param imporT
     */
    public void addImport(Import imporT) {
        importList.addElement(imporT);
    }

    /**
     * Returns an import parameterised by the uri
     * @param uri
     * @return import
     */
    public Import getImportByUri(String uri) {
        if (importList.containsUri(uri)) {
            return new Import(importList.getByUri(uri), uri);
        }
        return null;
    }

    /**
     * Returns an import parameterised by the url
     * @param url
     * @return import
     */
    public Import getImportByUrl(String url) {
        if (importList.containsIdentifier(url)) {
            return new Import(url, importList.getByIdentifier(url));
        }
        return null;
    }

    /**
     * Returns the list of imports
     * @return importList
     */
    public DualHashtable getImportList() {
        return importList;
    }

    /**
     * Sets the list of imports
     * @param hashtable
     */
    public void setImportList(DualHashtable dualHashtable) {
        this.importList = dualHashtable;
    }

    /**
     * Returns an ordered list of URIs inside the import lists DualHashtable.
     */
    public TreeSet<String> getUris(){
        return this.importList.getUris();
    }
}
