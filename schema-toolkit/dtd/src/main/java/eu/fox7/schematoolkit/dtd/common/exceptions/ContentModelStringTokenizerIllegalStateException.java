package eu.fox7.schematoolkit.dtd.common.exceptions;

/**
 * A character is not allowed at the current position of the string tokenizer
 * @author Lars Schmidt
 */
public class ContentModelStringTokenizerIllegalStateException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContentModelStringTokenizerIllegalStateException(String token) {
        super("The following character is not allowed at the current position: " + "\"" + token + "\"");
    }

}
