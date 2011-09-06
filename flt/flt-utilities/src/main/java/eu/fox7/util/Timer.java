/*
 * Created on Oct 12, 2008
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.util;


/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class Timer {

    protected long startTime = -1L;
    protected long stopTime = -1L;

    public void start() {
        startTime = System.nanoTime();
    }

    public void stop() {
        stopTime = System.nanoTime();
    }

    public long getElapsedTime() {
        if (startTime < 0L)
            throw new TimerNotRunningException();
        if (stopTime < 0L)
            return System.nanoTime() - startTime;
        else
            return stopTime - startTime;
    }

    public double getElapsedSeconds() {
    	return getElapsedTime()/1.0e9;
    }

    public void reset() {
        startTime = -1L;
        stopTime = -1L;
    }

}
