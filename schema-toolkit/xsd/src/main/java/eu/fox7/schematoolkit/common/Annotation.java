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
package eu.fox7.schematoolkit.common;

import java.util.LinkedList;

import eu.fox7.schematoolkit.xsd.om.AppInfo;
import eu.fox7.schematoolkit.xsd.om.Documentation;

/**
 * Class representing a XSD-annotation-tag.
 *
 * All classes implementing the annotationable-interface should hold one of this
 * annotation-objects as a private attribute.
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class Annotation {

    LinkedList<AppInfo> appInfos;
    LinkedList<Documentation> documentations;

    // Attribute "id" represents the ID attribute type from [XML 1.0 (Second Edition)].
    private String id;

    /**
     * Constructor for the class Annotation
     */
    public Annotation() {
        appInfos = new LinkedList<AppInfo>();
        documentations = new LinkedList<Documentation>();
    }


    /**
     * Getter for the attribute id
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for the attribute id
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for the appended AppInfo children of this annotation-object.
     * @return
     */
    public LinkedList<AppInfo> getAppInfos() {
        return appInfos;
    }

    /**
     * Method for adding an AppInfo to this annotation.
     * @param appInfo
     */
    public void addAppInfos(AppInfo appInfo) {
        this.appInfos.add(appInfo);
    }

    /**
     * Getter for the appended Documentations children of this annotation-object.
     * @return
     */
    public LinkedList<Documentation> getDocumentations() {
        return documentations;
    }

    /**
     * Method for adding a Documentation to this annotation.
     * @param documentation
     */
    public void addDocumentations(Documentation documentation) {
        this.documentations.add(documentation);
    }
}
