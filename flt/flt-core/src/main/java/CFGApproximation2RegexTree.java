import eu.fox7.flt.automata.converters.Minimizer;
import eu.fox7.flt.automata.converters.NFAMinimizer;
import eu.fox7.flt.automata.factories.sparse.CFGApproximationFactory;
import eu.fox7.flt.automata.factories.sparse.Determinizer;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.StateDFA;
import eu.fox7.flt.grammar.CFG;
import eu.fox7.flt.regex.factories.StateEliminationFactory;
import eu.fox7.util.tree.Handle;
import eu.fox7.util.tree.Tree;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public class CFGApproximation2RegexTree {

	public static final int DEFAULTDEPTH = 1;

	public static void main(String[] argv) throws Exception {
		String fileName = null;
		String outFileName = null;
		int depth = 0;
		boolean readingFileName = false;
		boolean readingOutFileName = false;
		boolean readingDepth = false;
		for (int i = 0; i < argv.length; i++) {
			if (readingFileName) {
				fileName = argv[i];
				readingFileName = false;
			} else if (readingOutFileName) {
				outFileName = argv[i];
				readingOutFileName = false;
			} else if (readingDepth) {
				depth = Integer.parseInt(argv[i]);
				readingDepth = false;
			}
			if (argv[i].equals("-f")) {
				readingFileName = true;
			} else if (argv[i].equals("-o")) {
				readingOutFileName = true;
			} else if (argv[i].equals("-d")) {
				readingDepth = true;
			}
		}
		if (depth <= 0) depth = DEFAULTDEPTH;
		Reader reader = null;
		if (fileName != null) {
			reader = new FileReader(fileName);
		} else {
			reader = new InputStreamReader(System.in);
		}
		Writer writer = null;
		if (outFileName != null) {
			writer = new FileWriter(outFileName);
		} else {
			writer = new OutputStreamWriter(System.out);
		}
		CFG cfg = new CFG(reader);
		CFGApproximationFactory cfgApprox = new CFGApproximationFactory(cfg, depth);
		SparseNFA nfa = cfgApprox.nfa();
		writer.write("/* " + toRegex(nfa) + " */\n\n");
		Handle handle = new Handle(toRegexTree(nfa));
		writer.write(handle.toDot());
		writer.close();
	}

    protected static String toRegex(SparseNFA nfa) {
    	StateDFA dfa = Determinizer.dfa(nfa);
        StateEliminationFactory factory = new StateEliminationFactory();
        return factory.create(dfa, false).toString();
    }

    public static Tree toRegexTree(SparseNFA nfa) {
    	Minimizer minimizer = new NFAMinimizer();
        SparseNFA dfa = Determinizer.dfa(nfa);
        minimizer.minimize(dfa);
		StateEliminationFactory graph = new StateEliminationFactory();
        return graph.create(dfa, false).getTree();
    }

}
