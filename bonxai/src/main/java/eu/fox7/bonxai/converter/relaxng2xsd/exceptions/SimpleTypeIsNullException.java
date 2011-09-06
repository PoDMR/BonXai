package eu.fox7.bonxai.converter.relaxng2xsd.exceptions;

import eu.fox7.bonxai.relaxng.Pattern;

/**
 * There is an unexpected null simpleType under the given element
 * @author Lars Schmidt
 */
public class SimpleTypeIsNullException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SimpleTypeIsNullException(Pattern pattern, String info) {
        super("There is an unexpected \"null\" simpleType under the given pattern: \"" + pattern + "\" (" + info + ")");
    }

}
