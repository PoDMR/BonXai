package eu.fox7.util;

import eu.fox7.util.TimerNotRunningException;

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

    public void reset() {
        startTime = -1L;
        stopTime = -1L;
    }

}