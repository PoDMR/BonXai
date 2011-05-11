package de.tudortmund.cs.bonxai.xsd.parser.exceptions.type;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class EmptySimpleContentUnionMemberTypesException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmptySimpleContentUnionMemberTypesException(String simpleContentUnionString) {
        super("Following SimpleContentUnion has an no memberTypes or content-simpleTypes: " + simpleContentUnionString);
    }
}
