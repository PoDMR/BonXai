/**
 * Created on Sep 24, 2009
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.util;

import java.lang.reflect.Method;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 * TimedRunner can be used to run a class method for a specified maximal
 * duration.
 */
public class TimedRunner<T> {

	protected Job job;

	public TimedRunner() {
	    super();
	}

	public TimedRunner(Object object, String methodName, Class<?>...parameterTypes)
	        throws SecurityException, NoSuchMethodException {
		this();
		this.job = new Job(object, methodName, parameterTypes);
	}

	@SuppressWarnings({ "unchecked" })
    public T run(long duration, Object...parameters) throws InterruptedException {
		job.setParameters(parameters);
		execute(duration, job);
		return (T) job.getResult();
	}

	@SuppressWarnings("unchecked")
    public static<T> T run(long duration, Object object, String methodName, Object...parameters)
	        throws SecurityException, NoSuchMethodException, InterruptedException {
		Class<?>[] parameterTypes = new Class<?>[parameters.length];
		for (int i = 0; i < parameters.length; i++)
			parameterTypes[i] = parameters[i].getClass();
		Job job = new Job(object, methodName, parameterTypes);
		job.setParameters(parameters);
		execute(duration, job);
		return (T) job.getResult();
	}

	protected static void execute(long duration, Job job) throws InterruptedException {
        Thread thread = new Thread(job, "timed job");
    	thread.start();
    	thread.join(1000*duration);
    }

	public static class Job implements Runnable {

		protected Class<?> c;
		protected String methodName;
		protected Class<?>[] parameterTypes;
		protected Method method;
		protected Object[] parameters;
		protected Object object;
		protected Object result;

		public Job(Object object, String methodName, Class<?>...parameterTypes)
		        throws SecurityException, NoSuchMethodException {
			this.object = object;
			this.c = object.getClass();
			this.methodName = methodName;
			this.parameterTypes = parameterTypes;
			this.method = c.getMethod(methodName, parameterTypes);
		}

		public Object[] getParameters() {
	    	return parameters;
	    }

		public void setParameters(Object[] parameters) {
	    	this.parameters = parameters;
	    }

		public void run() {
			setResult(null);
			try {
		        setResult(this.method.invoke(object, getParameters()));
	        } catch (Exception e) {
		        throw new RunException(e);
	        }
		}

		public boolean hasResult() {
			return result != null;
		}

		public Object getResult() {
	    	return result;
	    }

		protected void setResult(Object result) {
	    	this.result = result;
	    }

	}

}
