package uh.df.maple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import uh.df.maple.exceptions.StreamNotOpenException;


/**
 * 
 * @author Dominique Fonteyn
 *
 */
public class MapleStream {
	protected static final String EOC = "___EOC___";
	protected static final String MAPLEOPTIONS = " -q";
	protected String locationOfMapleDir;
	protected Process mapleInstance;
	protected OutputStream streamToMaple;
	protected BufferedReader streamFromMaple;

	/**
	 * 
	 * @param locationOfMapleDir
	 */
	public MapleStream(String locationOfMapleDir) {
		this.locationOfMapleDir = locationOfMapleDir;
	}

	/**
	 * 
	 * @throws IOException
	 */
	public void open() throws IOException {
		mapleInstance = Runtime.getRuntime().exec(locationOfMapleDir + MAPLEOPTIONS);
		streamToMaple = mapleInstance.getOutputStream();
		streamFromMaple = new BufferedReader(new InputStreamReader(mapleInstance.getInputStream()));
	}

	/**
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (mapleInstance != null) {
			String closeCommand = "quit\n";
			streamToMaple.write(closeCommand.getBytes());
			streamToMaple.flush();
			try {
				mapleInstance.waitFor();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		}
	}

	/**
	 * 
	 * @param command
	 * @throws StreamNotOpenException
	 * @throws IOException
	 */
	public void send(String command) throws StreamNotOpenException, IOException {
		if (isOpen()) {
			// send command
			streamToMaple.write(command.getBytes());
			streamToMaple.flush();

			// send EOC
			streamToMaple.write(("printf(`" + EOC + "\\n`):\n").getBytes());
			streamToMaple.flush();
		} else
			throw new StreamNotOpenException();
	}

	/**
	 * 
	 * @return
	 * @throws StreamNotOpenException
	 * @throws IOException
	 */
	public String receive() throws StreamNotOpenException, IOException {
		if (isOpen()) {
			String line = null;
			String result = "";

			// keep reading lines until we reach the EOC
			while (true) {
				line = streamFromMaple.readLine();
				if (line.indexOf(EOC) != -1)
					break;
				if (line.trim().length() != 0)
					result += line.trim();
			}
			return result.trim();
		} else
			throw new StreamNotOpenException();
	}

	/**
	 * 
	 * @return
	 */
	protected boolean isOpen() {
		if (mapleInstance == null)
			return false;
		return true;
	}

}