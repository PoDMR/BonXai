import eu.fox7.flt.grammar.*;
import junit.framework.*;
import java.io.StringReader;
import java.util.*;

public class CNFcflTest extends TestCase {
  
  protected CNFcfl cfl;
  protected String regexGrammar =
        "S -> A B | A G | A K | a | b | c | d | @ | $\n" +
        "A -> (\n" +
        "B -> S C\n" +
        "C -> D E\n" +
        "D -> |\n" +
        "E -> S F\n" +
        "F -> )\n" +
        "G -> S H\n" +
        "H -> I J\n" +
        "I -> .\n" +
        "J -> S F\n" +
        "K -> S L\n" +
        "L -> M F\n" +
        "M -> *";
  protected String grammarError1 =
        "S -> A B | A G | A K | a | b | c | d\n" +
        "A -> (\n" +
        "B -> S C\n" +
        "D -> |\n" +
        "E -> S F\n" +
        "F -> )\n" +
        "G -> S H\n" +
        "H -> I J\n" +
        "I -> .\n" +
        "J -> S F\n" +
        "K -> S L\n" +
        "L -> M F\n" +
        "M -> *";
  protected String grammarError2 =
        "S -> A B | A G | A K | a | b | c | d\n" +
        "A -> (\n" +
        "B -> S C\n" +
        "C -> D E H\n" +
        "D -> |\n" +
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

  public CNFcflTest(String name) {
	super(name);
  }

  public static Test suite() {
	return new TestSuite(CNFcflTest.class);
  }

  protected void setUp() throws Exception {
      super.setUp();
      StringReader reader = new StringReader(regexGrammar);
      cfl = new CNFcfl(reader);
      reader.close();
  }



  public void test_CNFcflConstructorExceptions() throws Exception {
	StringReader reader = new StringReader(grammarError1);
	try {
	  new CNFcfl(reader);
	  reader.close();
	  fail("exception ProductionNotDefinedException expected");
	} catch(ProductionNotDefinedException e) {}
	reader.close();
	reader = new StringReader(grammarError2);
	try {
	  new CNFcfl(reader);
	  reader.close();
	  fail("exception RuleNotInCNFException expected");
	} catch(RuleNotInCNFException e) {}
	reader.close();
	assertTrue(cfl.isInputLexicallyCorrect("(a|b)*.c.d"));
	assertTrue(cfl.isInputLexicallyCorrect("(a|b)* . c . d"));
	assertTrue(!cfl.isInputLexicallyCorrect("(a|e)* . c . d"));
	assertTrue(!cfl.isInputLexicallyCorrect("[a|b]* . c . d"));
  }

  public void test_CNFcflTree() throws Exception {
	cfl.cykTree("a");
	cfl.cykTree("(a.b)");
	cfl.cykTree("((a.b).c)");
	cfl.cykTree("((a|b).c)");
	cfl.cykTree("(((a.b)*).c)");
	cfl.cykTree("(((a.((b|c)*))*).c)");
	cfl.cykTree("((d.((a.b)*)).c)");
	try {
	    cfl.cykTree("(a)");
	    fail("SyntaxErrorException should be thrown");
	} catch(SyntaxErrorException e) {}
	try {
	  cfl.cykTree("((a.b)*.c)");
	  fail("SyntaxErrorException should be thrown");
	} catch(SyntaxErrorException e) {}
	try {
	  cfl.cykTree("((a.b*.c)");
	  fail("SyntaxErrorException should be thrown");
	} catch(SyntaxErrorException e) {}
	
  }

  @SuppressWarnings("unchecked")
protected static Object extractSingleton(Set set) {
	if (set.size() == 1) {
	  Object[] array = set.toArray();
	  return array[0];
	} else {
	  return null;
	}
  }

  protected static String matrixToString(boolean[][] m) {
	StringBuffer str = new StringBuffer();
	for (int i = 0; i < m.length; i++) {
	  for (int j = 0; j < m[i].length; j++) {
		if (m[i][j]) {
		  str.append("1 ");
		} else {
		  str.append("0 ");
		}
	  }
	  str.append("\n");
	}
	return str.toString();
  }

}
