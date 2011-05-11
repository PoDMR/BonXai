/*
 * Created on Feb 14, 2007
 * Modified on $Date: 2009-11-09 11:49:45 $
 */
package gjb.util.xml.acstring;


/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public interface ExampleParser {

    public ParseResult parse(String exampleStr) throws ExampleParsingException;

}
