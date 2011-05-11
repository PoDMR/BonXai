package gjb.util.tree;
import gjb.util.tree.DefaultTreeComparator;
import gjb.util.tree.NodeOrderTreeComparator;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ComparatorTest extends TestCase {
  
	protected final int N = 7;
	protected Tree[] trees = new Tree[7];
	protected String[] strs = {"(a (b (d)) (c (e) (f)))",
							   "(a (a (d)) (c (e) (f)))",
							   "(a (b (e)) (c (e) (f)))",
							   "(a (b (d)) (c (e) (g)))",
							   "(a)",
							   "()",
							   "(a (b (d)))"};
	protected int[] order = {5, 4, 1, 6, 0, 3, 2};
	protected int[] order2 = {5, 4, 2, 6, 3, 0, 1};
	protected String[] nodeOrder = {"g", "f", "e", "d", "c", "b", "a"};

	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public ComparatorTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(ComparatorTest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
		try {
			for (int i = 0; i < N; i++) {
				trees[i] = Tree.parse(strs[i]);
			}
		} catch (SExpressionParseException e) {
			System.err.println(e.getMessage());
		}
	}

	public void test_compare() {
		Comparator<Tree> cmp = new DefaultTreeComparator();
		assertEquals(0, cmp.compare(trees[0], trees[0]));
		assertTrue(cmp.compare(trees[1], trees[0]) < 0);
		assertTrue(cmp.compare(trees[0], trees[1]) > 0);
		assertTrue(cmp.compare(trees[5], trees[4]) < 0);
		assertTrue(cmp.compare(trees[4], trees[1]) < 0);
		assertTrue(cmp.compare(trees[1], trees[6]) < 0);
		assertTrue(cmp.compare(trees[6], trees[0]) < 0);
		assertTrue(cmp.compare(trees[0], trees[3]) < 0);
		assertTrue(cmp.compare(trees[3], trees[2]) < 0);
	}

	public void test_sort() {
		List<Tree> toSort = new ArrayList<Tree>();
		List<Tree> target = new ArrayList<Tree>();
		for (int i = 0; i < N; i++) {
			toSort.add(trees[i]);
			target.add(trees[order[i]]);
		}
		Collections.sort(toSort, new DefaultTreeComparator());
		assertEquals(target, toSort);
	}

	public void test_sortRepetition() {
		List<Tree> toSort = new ArrayList<Tree>();
		List<Tree> target = new ArrayList<Tree>();
		toSort.add(trees[4]);
		toSort.add(trees[5]);
		toSort.add(trees[5]);
		toSort.add(trees[2]);
		toSort.add(trees[4]);
		target.add(trees[5]);
		target.add(trees[5]);
		target.add(trees[4]);
		target.add(trees[4]);
		target.add(trees[2]);
		Collections.sort(toSort, new DefaultTreeComparator());
		assertEquals(target, toSort);
	}

	public void test_sortNodeOrder() {
		List<Tree> toSort = new ArrayList<Tree>();
		List<Tree> target = new ArrayList<Tree>();
		for (int i = 0; i < N; i++) {
			toSort.add(trees[i]);
			target.add(trees[order2[i]]);
		}
		Collections.sort(toSort, new NodeOrderTreeComparator(Arrays.asList(nodeOrder)));
		assertEquals(target, toSort);
	}

	public void test_sortNodeOrderRepetition() {
		List<Tree> toSort = new ArrayList<Tree>();
		List<Tree> target = new ArrayList<Tree>();
		//	protected int[] order2 = {5, 4, 2, 6, 3, 0, 1};
		toSort.add(trees[4]);
		toSort.add(trees[5]);
		toSort.add(trees[5]);
		toSort.add(trees[2]);
		toSort.add(trees[4]);
		target.add(trees[5]);
		target.add(trees[5]);
		target.add(trees[4]);
		target.add(trees[4]);
		target.add(trees[2]);
		Collections.sort(toSort,
						 new NodeOrderTreeComparator(Arrays.asList(nodeOrder)));
		assertEquals(target, toSort);
	}

}
