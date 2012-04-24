package uh.df.maple.exceptions;

@SuppressWarnings("serial")
public class StreamNotOpenException extends Exception {
	
	public StreamNotOpenException() {
		super("MapleStream is not open. Call open() to open the stream.");
	}

}
