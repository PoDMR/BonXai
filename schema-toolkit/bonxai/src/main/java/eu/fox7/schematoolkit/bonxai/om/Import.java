/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.bonxai.om;

/**
 * Class representing an import statement in Bonxai
 */
public class Import {

    /**
     * uri of the import
     */
    private String uri;
    /**
     * url of the import
     */
    private String url;

    /**
     * Constructor for the class Import with only uri parameter
     * @param uri
     */
    public Import(String uri) {
        this.uri = uri;
    }

    /**
     * Constructor for the class Import with both uri and url  parameter
     * @param url
     * @param uri
     */
    public Import(String url, String uri) {
        this.uri = uri;
        this.url = url;
    }

    /**
     * Returns the uri of the import
     * @return uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Returns the url of the import
     * @return url
     */
    protected String getUrl() {
        return url;
    }

    /**
     * Implementation of the getIdentifier-Method from DualHashtableElement-Interface
     * This returns the url of the import like the method getUrl()
     * @return url
     */
    public String getIdentifier() {
        return this.getUrl();
    }

}

