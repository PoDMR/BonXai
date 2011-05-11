/*
 * Created on Feb 14, 2007
 * Modified on $Date: 2009-11-09 13:14:09 $
 */
package gjb.util.xml.acstring;

import java.io.IOException;

import gjb.flt.FLTException;

/**
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
public class ExampleParsingException extends FLTException {

	private static final long serialVersionUID = 2013950802566467018L;

	public ExampleParsingException(String msg) {
		this.msg = msg;
	}

	public ExampleParsingException(IOException e) {
	    this.exception = e;
    }
	
}
