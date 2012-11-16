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

package eu.fox7.schematoolkit.common;

public abstract class AnnotationElement implements Annotationable, Locatable, ID {

	   private Annotation annotation;
	   private Position startPosition, endPosition;

	    /**
	 * @return the startPosition
	 */
	public Position getStartPosition() {
		return startPosition;
	}

	/**
	 * @param startPosition the startPosition to set
	 */
	public void setStartPosition(Position startPosition) {
		this.startPosition = startPosition;
	}

	/**
	 * @return the endPosition
	 */
	public Position getEndPosition() {
		return endPosition;
	}

	/**
	 * @param endPosition the endPosition to set
	 */
	public void setEndPosition(Position endPosition) {
		this.endPosition = endPosition;
	}

		@Override
	    public Annotation getAnnotation() {
	        return annotation;
	    }

	    @Override
	    public void setAnnotation(Annotation annotation) {
	        this.annotation = annotation;
	    }
	    
	    // Attribute "id" represents the ID attribute type from [XML 1.0 (Second Edition)].
	    // id is used in all classes implementing Annotationable
	    private String id;
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



}
