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
