/*
 * Created on Oct 12, 2008
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.util;

import eu.fox7.util.Timer;
import eu.fox7.util.TimerNotRunningException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class TimerTest extends TestCase {

    public static Test suite() {
        return new TestSuite(TimerTest.class);
    }

    public void testTimer() {
        long sleepTime = 1000L;
        try {
            Timer timer = new Timer();
            timer.start();
            Thread.sleep(sleepTime);
            timer.stop();
            long runTime = timer.getElapsedTime();
            assertTrue("time ok",
                       runTime < 1000000*sleepTime + 5000000 &&
                       runTime > 1000000*sleepTime - 5000000);
            double runSeconds = timer.getElapsedSeconds();
            assertTrue("seconds ok", 1.0 - 0.01 < runSeconds && runSeconds < 1.0 + 0.01);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

    public void testTimerNotStopped() {
        long sleepTime = 1000L;
        try {
            Timer timer = new Timer();
            timer.start();
            Thread.sleep(sleepTime);
            long runTime = timer.getElapsedTime();
            assertTrue("time ok",
                       runTime < 1000000*sleepTime + 5000000 &&
                       runTime > 1000000*sleepTime - 5000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
    
    public void testTimerNoStart() {
        long sleepTime = 1000L;
        try {
            Timer timer = new Timer();
            Thread.sleep(sleepTime);
            timer.stop();
            long runTime = timer.getElapsedTime();
            fail("timer not started");
            assertTrue("time ok",
                       runTime < 1000000*sleepTime + 1000000 &&
                       runTime > 1000000*sleepTime - 1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (TimerNotRunningException e) {
            return;
        }
        fail("timer not started");
    }
    
}
