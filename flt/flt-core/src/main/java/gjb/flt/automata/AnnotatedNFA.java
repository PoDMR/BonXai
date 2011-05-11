package gjb.flt.automata;

public interface AnnotatedNFA<StateT,TransitionT> extends NFA {

	public void annotate(String stateValue, StateT annotation) 
	        throws NoSuchStateException;

	public StateT getAnnotation(String stateValue);

	public boolean hasAnnotation(String stateValue);

	public void annotate(String symbolValue, String fromStateValue,
	                     String toStateValue, TransitionT annotation)
	        throws NoSuchTransitionException;

	public TransitionT getAnnotation(String symbolValue, String fromStateValue,
	                                 String toStateValue);

	public boolean hasAnnotation(String symbolValue, String fromStateValue,
	                             String toStateValue);

}
