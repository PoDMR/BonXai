/**
 * Created on Sep 24, 2009
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class TimedRunnerTest extends TestCase {

    public static Test suite() {
        return new TestSuite(TimedRunnerTest.class);
    }

	public void testInfiniteLoop() {
		try {
			Looper loop = new Looper();
			Integer parameter = Integer.valueOf(-1);
	        TimedRunner<Integer> runner = new TimedRunner<Integer>(loop, "count", parameter.getClass());
			Integer result = runner.run(1, parameter);
	        assertTrue("no result", result == null);
        } catch (SecurityException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        } catch (NoSuchMethodException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        } catch (InterruptedException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        }
	}

	public void testFiniteLoop() {
		try {
			Looper loop = new Looper();
			Integer parameter = Integer.valueOf(10000);
			TimedRunner<Integer> runner = new TimedRunner<Integer>(loop, "count", parameter.getClass());
			Integer result = runner.run(2, parameter);
			assertTrue("no result", result != null);
		} catch (SecurityException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}
	
	public void testFiniteLoopPrimitives() {
		try {
			Looper loop = new Looper();
			TimedRunner<Integer> runner = new TimedRunner<Integer>(loop,
					"count2",
					int.class);
			Integer result = runner.run(2, 100000);
			assertTrue("no result", result != null);
		} catch (SecurityException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}
	
	public void testInfiniteLoopStaticCall() {
		try {
			Looper loop = new Looper();
			Integer parameter = Integer.valueOf(-1);
	        Integer result = TimedRunner.run(1, loop, "count", parameter);
	        assertTrue("no result", result == null);
        } catch (SecurityException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        } catch (NoSuchMethodException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        } catch (InterruptedException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        }
	}

	public void testFiniteLoopStaticCall() {
		try {
			Looper loop = new Looper();
			Integer parameter = Integer.valueOf(10000);
			Integer result = TimedRunner.run(2, loop, "count", parameter);
			assertTrue("no result", result != null);
		} catch (SecurityException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}
	
	public static class Looper {

		public Integer count(Integer limit) {
			int counter = 0;
			for (int i = 0; i > limit; i++)
	            counter = (counter + 13) % 123;
			return counter;
		}

		public int count2(int limit) {
			int counter = 0;
			for (int i = 0; i > limit; i++)
	            counter = (counter + 13) % 123;
			return counter;
		}
	}

}
