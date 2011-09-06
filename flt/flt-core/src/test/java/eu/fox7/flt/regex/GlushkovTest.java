package eu.fox7.flt.regex;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.matchers.NFAMatcher;
import eu.fox7.flt.regex.Glushkov;
import eu.fox7.flt.regex.measures.AmbiguityTest;
import eu.fox7.flt.regex.measures.LanguageTest;
import eu.fox7.util.tree.Tree;

import java.io.StringReader;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GlushkovTest extends TestCase {
  
	protected Glushkov glushkov;

	public GlushkovTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(GlushkovTest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
		glushkov = new Glushkov();
	}

	public void test_markTree() {
		try {
			Tree tree = new Tree(new StringReader("(| (a) (. (b) (? (c)) (a) (* (a))))"));
			Tree markedTree = glushkov.mark(tree);
			assertEquals("(| (a_1) (. (b_2) (? (c_3)) (a_4) (* (a_5))))",
						 markedTree.toSExpression());
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	@SuppressWarnings("unchecked")
    public void test_symbols() {
		try {
			Tree tree = new Tree(new StringReader("(| (a) (. (b) (? (c)) (a) (* (a))))"));
			Set symbols = glushkov.symbols(tree);
			assertEquals(3, symbols.size());
			assertTrue(symbols.contains("a") && symbols.contains("b") &&
					   symbols.contains("c"));
			Tree markedTree = glushkov.mark(tree);
			symbols = glushkov.symbols(markedTree);
			assertEquals(5, symbols.size());
			assertTrue(symbols.contains("a_1") && symbols.contains("b_2") &&
					   symbols.contains("c_3") && symbols.contains("a_4") &&
					   symbols.contains("a_5"));
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	@SuppressWarnings("unchecked")
    public void test_first1() {
		try {
			String treeRepr = "(| (a_1) (. (b_2) (? (c_3)) (a_4) (* (a_5))))";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.first(tree);
			assertEquals(2, set.size());
			assertTrue(set.contains("a_1") && set.contains("b_2"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

	@SuppressWarnings("unchecked")
    public void test_first2() {
		try {
			String treeRepr = "(. (| (a_1) (b_2) (a_3)) (c_4))";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.first(tree);
			assertEquals(3, set.size());
			assertTrue(set.contains("a_1") && set.contains("b_2") &&
					   set.contains("a_3"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

	@SuppressWarnings("unchecked")
    public void test_first3() {
		try {
			String treeRepr = "(a_1)";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.first(tree);
			assertEquals(1, set.size());
			assertTrue(set.contains("a_1"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

	@SuppressWarnings("unchecked")
    public void test_first4() {
		try {
			String treeRepr = "()";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.first(tree);
			assertEquals(0, set.size());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

	@SuppressWarnings("unchecked")
    public void test_first5() {
		try {
			String treeRepr = "(| (a_1) (. (? (b_2)) (? (c_3)) (a_4) (* (a_5))))";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.first(tree);
			assertEquals(4, set.size());
			assertTrue(set.contains("a_1") && set.contains("b_2") &&
					   set.contains("c_3") && set.contains("a_4"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

	@SuppressWarnings("unchecked")
    public void test_first6() {
		try {
			String treeRepr = "(? (. (? (a_1)) (b_2)))";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.first(tree);
			assertEquals(2, set.size());
			assertTrue(set.contains("a_1") && set.contains("b_2"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

	@SuppressWarnings("unchecked")
    public void test_last1() {
		try {
			String treeRepr = "(| (a_1) (. (b_2) (? (c_3)) (a_4) (* (a_5))))";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.last(tree);
			assertEquals(3, set.size());
			assertTrue(set.contains("a_1") && set.contains("a_4") &&
					   set.contains("a_5"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

	@SuppressWarnings("unchecked")
    public void test_last2() {
		try {
			String treeRepr = "(. (| (a_1) (b_2) (a_3)) (c_4))";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.last(tree);
			assertEquals(1, set.size());
			assertTrue(set.contains("c_4"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

	@SuppressWarnings("unchecked")
    public void test_last3() {
		try {
			String treeRepr = "(a_1)";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.last(tree);
			assertEquals(1, set.size());
			assertTrue(set.contains("a_1"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

	@SuppressWarnings("unchecked")
    public void test_last4() {
		try {
			String treeRepr = "()";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.last(tree);
			assertEquals(0, set.size());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

    @SuppressWarnings("unchecked")
	public void test_last5() {
		try {
			String treeRepr = "(| (a_1) (. (? (b_2)) (+ (c_3)) (? (a_4)) (* (a_5))))";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.last(tree);
			assertEquals(4, set.size());
			assertTrue(set.contains("a_1") && set.contains("a_5") &&
					   set.contains("a_4") && set.contains("c_3"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

    @SuppressWarnings("unchecked")
	public void test_last6() {
		try {
			String treeRepr = "(? (. (? (a_1)) (b_2)))";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.last(tree);
			assertEquals(1, set.size());
			assertTrue(set.contains("b_2"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

    @SuppressWarnings("unchecked")
	public void test_follow1() {
		try {
			String treeRepr = "(| (a_1) (. (b_2) (? (c_3)) (a_4) (* (a_5))))";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.follow(tree, "b_2");
			assertEquals(2, set.size());
			assertTrue(set.contains("c_3") && set.contains("a_4"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

    @SuppressWarnings("unchecked")
	public void test_follow2() {
		try {
			String treeRepr = "(. (| (a_1) (b_2) (a_3)) (c_4))";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.follow(tree, "b_2");
			assertEquals(1, set.size());
			assertTrue(set.contains("c_4"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

    @SuppressWarnings("unchecked")
	public void test_follow3() {
		try {
			String treeRepr = "(. (* (| (a_1) (b_2) (a_3))) (c_4))";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.follow(tree, "b_2");
			assertEquals(4, set.size());
			assertTrue(set.contains("c_4") && set.contains("a_1") &&
					   set.contains("b_2") && set.contains("a_3"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

    @SuppressWarnings("unchecked")
	public void test_follow4() {
		try {
			String treeRepr = "(. (* (| (a_1) (b_2) (a_3))) (c_4))";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.follow(tree, "c_4");
			assertEquals(0, set.size());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

    @SuppressWarnings("unchecked")
	public void test_follow5() {
		try {
			String treeRepr = "(a_1)";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.follow(tree, "a_1");
			assertEquals(0, set.size());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

    @SuppressWarnings("unchecked")
	public void test_follow6() {
		try {
			String treeRepr = "()";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.follow(tree, "a_1");
			assertTrue(set == null);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

    @SuppressWarnings("unchecked")
	public void test_follow7() {
		try {
			String treeRepr = "(| (a_1) (. (? (b_2)) (+ (c_3)) (? (a_4)) (* (a_5))))";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.follow(tree, "a_1");
			assertEquals(0, set.size());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

    @SuppressWarnings("unchecked")
	public void test_follow8() {
		try {
			String treeRepr = "(| (a_1) (. (? (b_2)) (+ (c_3)) (? (a_4)) (* (a_5))))";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.follow(tree, "c_3");
			assertEquals(3, set.size());
			assertTrue(set.contains("c_3") && set.contains("a_4") &&
					   set.contains("a_5"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

    @SuppressWarnings("unchecked")
	public void test_follow9() {
		try {
			String treeRepr = "(* (. (a_1) (| (a_2) (a_3))))";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.follow(tree, "a_1");
			assertEquals(2, set.size());
			assertTrue(set.contains("a_2") && set.contains("a_3"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

    @SuppressWarnings("unchecked")
	public void test_follow10() {
		try {
			String treeRepr = "(* (. (a_1) (| (a_2) (a_3))))";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.follow(tree, "a_2");
			assertEquals(1, set.size());
			assertTrue(set.contains("a_1"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

    @SuppressWarnings("unchecked")
	public void test_follow11() {
		try {
			String treeRepr = "(* (. (a_1) (| (a_2) (a_3))))";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.follow(tree, "a_3");
			assertEquals(1, set.size());
			assertTrue(set.contains("a_1"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

    @SuppressWarnings("unchecked")
	public void test_follow12() {
		try {
			String treeRepr = "(. (* (. (a_1) (? (a_2)))) (a_3))";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.follow(tree, "a_1");
			assertEquals(3, set.size());
			assertTrue(set.contains("a_1") && set.contains("a_2") &&
					   set.contains("a_3"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

    @SuppressWarnings("unchecked")
	public void test_follow13() {
		try {
			String treeRepr = "(. (. (. (. (a_1) (? (a_2))) (? (a_3))) (a_4)) (? (a_5)))";
			Tree tree = new Tree(new StringReader(treeRepr));
			Set set = glushkov.follow(tree, "a_1");
			assertEquals(3, set.size());
			assertTrue(set.contains("a_4") && set.contains("a_2") &&
					   set.contains("a_3"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}		

    @SuppressWarnings("unchecked")
	public void test_ambiguity1() {
		try {
			String regex = "(? (. (* (| (a) (b))) (a)))";
			assertTrue(glushkov.isAmbiguous(regex));
			Tree tree = new Tree(new StringReader(regex));
			Tree markedTree = glushkov.mark(tree);
			Set set = glushkov.first(markedTree);
			assertEquals(3, set.size());
			assertTrue(set.contains("a_1") && set.contains("b_2") &&
					   set.contains("a_3"));
			set = glushkov.last(markedTree);
			assertEquals(1, set.size());
			assertTrue(set.contains("a_3"));
			set = glushkov.follow(markedTree, "a_1");
			assertEquals(3, set.size());
			assertTrue(set.contains("a_1") && set.contains("b_2") &&
					   set.contains("a_3"));
			set = glushkov.follow(markedTree, "b_2");
			assertEquals(3, set.size());
			assertTrue(set.contains("a_1") && set.contains("b_2") &&
					   set.contains("a_3"));
			set = glushkov.follow(markedTree, "a_3");
			assertEquals(0, set.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

    @SuppressWarnings("unchecked")
	public void test_ambiguity2() {
		try {
			String regex = "(* (. (* (b)) (a)))";
			assertTrue(!glushkov.isAmbiguous(regex));
			Tree tree = new Tree(new StringReader(regex));
			Tree markedTree = glushkov.mark(tree);
			Set set = glushkov.first(markedTree);
			assertEquals(2, set.size());
			assertTrue(set.contains("b_1") && set.contains("a_2"));
			set = glushkov.last(markedTree);
			assertEquals(1, set.size());
			assertTrue(set.contains("a_2"));
			set = glushkov.follow(markedTree, "b_1");
			assertEquals(2, set.size());
			assertTrue(set.contains("b_1") && set.contains("a_2"));
			set = glushkov.follow(markedTree, "a_2");
			assertEquals(2, set.size());
			assertTrue(set.contains("b_1") && set.contains("a_2"));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

    @SuppressWarnings("unchecked")
	public void test_ambiguity3() {
		try {
			String regex = "(* (| (a) (b) (c)))";
			assertTrue(!glushkov.isAmbiguous(regex));
			Tree tree = new Tree(new StringReader(regex));
			LanguageTest ambiguityTest = new AmbiguityTest();
			assertTrue(!ambiguityTest.test(regex));
			Tree markedTree = glushkov.mark(tree);
			Set set = glushkov.first(markedTree);
			assertEquals(3, set.size());
			assertTrue(set.contains("a_1") && set.contains("b_2") &&
					   set.contains("c_3"));
			set = glushkov.last(markedTree);
			assertEquals(3, set.size());
			assertTrue(set.contains("a_1") && set.contains("b_2") &&
					   set.contains("c_3"));
			set = glushkov.follow(markedTree, "a_1");
			assertEquals(3, set.size());
			assertTrue(set.contains("a_1") && set.contains("b_2") &&
					   set.contains("c_3"));
			set = glushkov.follow(markedTree, "b_2");
			assertEquals(3, set.size());
			assertTrue(set.contains("a_1") && set.contains("b_2") &&
					   set.contains("c_3"));
			set = glushkov.follow(markedTree, "c_3");
			assertEquals(3, set.size());
			assertTrue(set.contains("a_1") && set.contains("b_2") &&
					   set.contains("c_3"));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test_ambiguity4a() {
		try {
			String regex = "(| (? (a)) (+ (b)) (? (c)))";
			LanguageTest ambiguityTest = new AmbiguityTest();
			assertTrue(!ambiguityTest.test(regex));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test_ambiguity4b() {
		try {
			String regex = "(| (? (abc)) (+ (bcd)) (? (cde)))";
			LanguageTest ambiguityTest = new AmbiguityTest();
			assertTrue(!ambiguityTest.test(regex));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test_ambiguity6() {
		try {
			String regex = "(| (? (instrument)) (? (div)) (? (division)) (? (repeat)) (? (upbeat)) (? (notation)) (+ (bar)) (? (text)) (? (primitives)) (? (core)) (? (preeffect)) (? (soundset)) (? (posteffect)) (? (texture)) (? (textureeffect)) (? (instrument)) (? (instrumenteffect)) (? (band)) (? (bandeffect)))";
			LanguageTest ambiguityTest = new AmbiguityTest();
			assertTrue(ambiguityTest.test(regex));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

    public void test_ambiguity8() {
        try {
            String regex = "(| (. (a) (b)) (. (a) (c)))";
			LanguageTest ambiguityTest = new AmbiguityTest();
            assertTrue(ambiguityTest.test(regex));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void test_ambiguity9() {
        try {
            String regex = "(| (. (a) (b)) (. (? (c)) (a) (c)))";
			LanguageTest ambiguityTest = new AmbiguityTest();
            assertTrue(ambiguityTest.test(regex));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    public void test_ambiguity10() {
        try {
            String regex = "(| (. (| (* (? (. (? (+ (. (| (+ (e)) (d)) (b)))) (a) (c) (d)))) (b) (e) (a)) (b) (e) (c) (a)) (d) (c))";
			LanguageTest ambiguityTest = new AmbiguityTest();
            assertTrue(ambiguityTest.test(regex));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
	public void test_acceptsEmptyString() throws Exception {
	    String regex = "(EPSILON)";
	    GlushkovFactory glushkov = new GlushkovFactory();
	    SparseNFA nfa = glushkov.create(regex);
	    NFAMatcher matcher = new NFAMatcher(nfa);
	    assertTrue(matcher.matches(new String[0]));
	}
	
}
