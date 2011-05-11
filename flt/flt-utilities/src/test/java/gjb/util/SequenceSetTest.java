package gjb.util;
import gjb.util.SequenceSet;
import junit.framework.*;
import java.util.*;

public class SequenceSetTest extends TestCase {
  

  static public void main(String args[]) {
	junit.textui.TestRunner.run(suite());
  }

  public SequenceSetTest(String name) {
	super(name);
  }

  public static Test suite() {
	return new TestSuite(SequenceSetTest.class);
  }

  public void test_SequenceSet2outOf3() throws Exception {
	  Set<String> set = new TreeSet<String>();
	  set.add("b");
	  set.add("c");
	  set.add("a");
	  SequenceSet seq = new SequenceSet<String>(new LinkedList<String>(set));
	  SequenceSet.Iterator it;
	  Set<List> newSet = new HashSet<List>();
	  for (it = seq.iterator(1); it.hasNext(); ) {
		  newSet.add(it.next());
	  }
	  assertEquals(3, newSet.size());
	  newSet.clear();
	  for (it = seq.iterator(1); it.hasNext(); ) {
		  newSet.add(it.next());
	  }
	  assertEquals(3, newSet.size());
	  newSet.clear();
	  for (it = seq.iterator(1); it.hasNext(); ) {
		  newSet.add(it.next());
	  }
	  assertEquals(3, newSet.size());
	  newSet.clear();
  }

}
