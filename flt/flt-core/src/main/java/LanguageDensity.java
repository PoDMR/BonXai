import gjb.flt.automata.factories.sparse.CFGApproximationFactory;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.matchers.NFAMatcher;
import gjb.flt.grammar.CFG;
import gjb.util.SequenceSet;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class LanguageDensity {

	public static final int DEFAULTSIZE = 5;
	public static final int DEFAULTDEPTH = 2;

	@SuppressWarnings("unchecked")
    public static void main(String[] argv) throws Exception {
		String fileName = null;
		int size = 0;
		int depth = 0;
		boolean onlyCorrect = false;
		boolean readingFileName = false;
		boolean readingSize = false;
		boolean readingDepth = false;
		for (int i = 0; i < argv.length; i++) {
			if (readingFileName) {
				fileName = argv[i];
				readingFileName = false;
			} else if (readingSize) {
				size = Integer.parseInt(argv[i]);
				readingSize = false;
			} else if (readingDepth) {
				depth = Integer.parseInt(argv[i]);
				readingDepth = false;
			}
			if (argv[i].equals("-f")) {
				readingFileName = true;
			} else if (argv[i].equals("-s")) {
				readingSize = true;
			} else if (argv[i].equals("-d")) {
				readingDepth = true;
			} else if (argv[i].equals("-o")) {
				onlyCorrect = true;
			}
		}
		if (size <= 0) size = DEFAULTSIZE;
		if (depth <= 0) depth = DEFAULTDEPTH;
		Reader reader = null;
		if (fileName != null) {
			reader = new FileReader(fileName);
		} else {
			reader = new InputStreamReader(System.in);
		}
		CFG cfg = new CFG(reader);
		CFGApproximationFactory cfgApprox = new CFGApproximationFactory(cfg, depth);
		SparseNFA nfa = cfgApprox.nfa();
		NFAMatcher matcher = new NFAMatcher(nfa);
		Set alphabet = new TreeSet(cfg.terminals());
		SequenceSet seq = new SequenceSet(new LinkedList(alphabet));
		for (int i = 1; i <= size; i++) {
			for (Iterator it = seq.iterator(i); it.hasNext(); ) {
				List input = (List) it.next();
				String[] strs = (String[]) input.toArray(new String[0]);
				StringBuffer str = new StringBuffer();
				for(int j = 0; j < strs.length; j++) {
					str.append(strs[j]);
				}
				boolean inLanguage = matcher.matches(strs);
				if (!onlyCorrect || inLanguage) {
					System.out.print(str.toString());
					if (matcher.matches(strs)) {
						System.out.println("\t1");
					} else {
						System.out.println("\t0");
					}
				}
			}
		}
	}

}
