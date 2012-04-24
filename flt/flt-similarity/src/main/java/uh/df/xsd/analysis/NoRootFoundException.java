package uh.df.xsd.analysis;

@SuppressWarnings("serial")
public class NoRootFoundException extends Exception {
	
	public NoRootFoundException() {
		super("No root element found in the XSD!");
	}

}
