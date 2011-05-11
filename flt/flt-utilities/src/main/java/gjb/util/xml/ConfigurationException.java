/*
 * Created on Jul 14, 2006
 * Modified on $Date: 2009-11-09 13:13:50 $
 */
package gjb.util.xml;

import java.io.IOException;

import gjb.flt.FLTException;

/**
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
public class ConfigurationException extends FLTException {

	private static final long serialVersionUID = -233955054590836254L;

	public ConfigurationException(IOException e) {
		this.exception = e;
	}
	
}
