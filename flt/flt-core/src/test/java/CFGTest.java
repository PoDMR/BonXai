import gjb.flt.grammar.*;
import junit.framework.*;
import java.io.StringReader;
import java.util.*;

public class CFGTest extends TestCase {
  
  protected CFG cfg;
  protected String grammar =
        "S -> A B a | A b G | A K I J K | a | b | c | d | @ | $\n" +
        "A -> (\n" +
        "B -> S C\n" +
        "C -> D E\n" +
        "D -> $\n" +
        "E -> S F\n" +
        "F -> )\n" +
        "G -> S H\n" +
        "H -> I J\n" +
        "I -> .\n" +
        "J -> S F\n" +
        "K -> S L\n" +
        "L -> M F\n" +
        "M -> *";

  static public void main(String args[]) {
	junit.textui.TestRunner.run(suite());
  }

  public CFGTest(String name) {
	super(name);
  }

  public static Test suite() {
	return new TestSuite(CFGTest.class);
  }

  protected void setUp() throws Exception {
      super.setUp();
      StringReader reader = new StringReader(grammar);
      cfg = new CFG(reader);
      reader.close();
  }

  public void test_CNFcflConstructor() throws Exception {
	assertEquals("S", cfg.startSymbol());
	assertEquals(14, cfg.nonTerminals().size());
	assertEquals(10, cfg.terminals().size());
	Set<String[]> productions = cfg.productions("S");
	assertEquals(9, productions.size());
	productions = cfg.productions("B");
	assertEquals(1, productions.size());
	boolean found = false;
	for (Iterator<String[]> it = productions.iterator(); it.hasNext(); ) {
		String[] production = it.next();
		if (production.length == 2) {
			if (production[0].equals("S") && production[1].equals("C")) {
				found = true;
			}
		}
	}
	assertTrue(found);
	productions = cfg.productions("F");
	assertEquals(1, productions.size());
	found = false;
	for (Iterator<String[]> it = productions.iterator(); it.hasNext(); ) {
		String[] production = it.next();
		if (production.length == 1) {
			if (production[0].equals(")")) {
				found = true;
			}
		}
	}
	assertTrue(found);
  }

}
