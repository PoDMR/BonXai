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

/**
 * Namespace representation for Bonxai
 */
public abstract class Namespace {

    public static final Namespace EMPTY_NAMESPACE = new DefaultNamespace("");

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Namespace other = (Namespace) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	/**
     * String containing the namespace
     */
    String uri;

    /**
     * Constructor for class namespace
     * @param uri
     */
    public Namespace(String uri) {
        this.uri = uri;
    }

    /**
     * Get namespace URI.
     */
    public String getUri() {
        return this.uri;
    }

    /**
     * Set namesapce URI.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    public abstract String getPrefix();
    
    public abstract String getIdentifier();
}

