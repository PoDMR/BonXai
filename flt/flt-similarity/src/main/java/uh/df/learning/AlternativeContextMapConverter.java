package uh.df.learning;

import eu.fox7.flt.FLTRuntimeException;
import eu.fox7.flt.automata.NoSuchStateException;
import eu.fox7.flt.automata.NoSuchTransitionException;
import eu.fox7.flt.automata.impl.sparse.SupportNFA;
import eu.fox7.flt.regex.Glushkov;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.infer.rwr.NoOpportunityFoundException;
import eu.fox7.flt.regex.infer.rwr.RewriteEngine;
import eu.fox7.flt.regex.infer.rwr.impl.Automaton;
import eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import eu.fox7.flt.schema.infer.ixsd.ContextMap;
import eu.fox7.flt.treeautomata.factories.SupportContextAutomatonFactory;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;
import eu.fox7.flt.treeautomata.impl.ContextAutomaton;
import eu.fox7.flt.treegrammar.SyntaxException;
import eu.fox7.flt.treegrammar.XMLAttributeDefinition;
import eu.fox7.flt.treegrammar.XMLGrammar;
import gjb.util.Pair;
import gjb.util.Timer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author Dominique Fonteyn
 *
 */
public class AlternativeContextMapConverter {

	public static final String TYPE_SEP = "@";
	public static final String PADDING = "0";

	public static XMLGrammar convertToGrammar(ContextMap contextMap, RewriteEngine rewriter)
			throws NoOpportunityFoundException {
		final int contextSize = contextMap.getContextSize();
		XMLGrammar doc = new XMLGrammar();
		String rootElement = contextMap.getRootElementName();
		String[] rootContext = padContext(new String[] {}, contextSize - 1); // ContextMap.padContext
		doc.setRootElement(rootElement, formatType(rootContext));
		GraphAutomatonFactory gaFactory = new GraphAutomatonFactory();
		
		Timer timer = new Timer();
		
		for (Iterator<String> contextIt = contextMap.getContextIterator(); contextIt.hasNext();) {
			timer.start();
			String contextStr = contextIt.next();
			timer.stop();
			System.out.println("next : " + timer.getElapsedSeconds());
			
			Pair<String, String> parts = dissectType(contextStr);
			try {
				String qName = parts.getFirst();
				String type = formatType(parts.getSecond());
				List<XMLAttributeDefinition> attributes = new LinkedList<XMLAttributeDefinition>();
				SupportNFA nfa = contextMap.getContentModel(contextStr);
				Automaton automaton = gaFactory.create(nfa);
				String regexStr = rewriter.rewriteToRegex(automaton);
				String typedRegexStr = addTypeInfo(contextStr, regexStr);
				
				
				doc.addGrammarRule(qName, type, attributes, typedRegexStr);
			} catch (SyntaxException e) {
				throw new RuntimeException(e);
			}
			
			System.out.println();
			
		}
		return doc;
	}

	protected static String[] padContext(String[] context, int contextSize) {
		String[] paddedContext = new String[contextSize];
		int index = context.length - 1;
		for (int i = contextSize - 1; i >= 0; i--) {
			if (index >= 0)
				paddedContext[i] = context[index--];
			else
				paddedContext[i] = ContextMap.PADDING_SYMBOL;
		}
		return paddedContext;
	}

	public static ContextAutomaton convertToContextFA(ContextMap contextMap) {
		ContextAutomaton contextFA = new ContextAutomaton();
		contextFA.setInitialState(Glushkov.INITIAL_STATE);
		setInitialStateContentModel(contextFA, contextMap);
		for (Iterator<String> contextIt = contextMap.getContextIterator(); contextIt.hasNext();) {
			String contextStr = contextIt.next();
			Pair<String, String> parts = dissectType(contextStr);
			String stateValue = stateName(parts.getFirst(), parts.getSecond());
			ContentAutomaton contentModel = contextMap.getContentModel(contextStr);
			contextFA.addState(stateValue);
			if (acceptsEmptyString(contentModel))
				contextFA.addFinalState(stateValue);
			try {
				contextFA.annotate(stateValue, contentModel);
			} catch (NoSuchStateException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		contextFA.addTransition(contextMap.getRootElementName(), Glushkov.INITIAL_STATE, stateName(contextMap
				.getRootElementName(), contextMap.getContextSize()));
		try {
			contextFA.annotate(contextMap.getRootElementName(), Glushkov.INITIAL_STATE, stateName(contextMap
					.getRootElementName(), contextMap.getContextSize()), contextMap.getNumberOfDocuments());
		} catch (NoSuchTransitionException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		for (Iterator<String> contextIt = contextMap.getContextIterator(); contextIt.hasNext();) {
			String contextStr = contextIt.next();
			Pair<String, String> parts = dissectType(contextStr);
			String fromStateValue = stateName(parts.getFirst(), parts.getSecond());
			ContentAutomaton contentFA = contextFA.getAnnotation(fromStateValue);

			String childType = computeChildType(contextStr);
			for (String symbolValue : contentFA.getSymbolValues()) {
				String toStateValue = stateName(symbolValue, childType);
				String childContextStr = childContext(symbolValue, childType);
				if (!contextMap.hasContentModel(childContextStr))
					throw new FLTRuntimeException("no content model for " + childContextStr
							+ ", sample is most probably incomplete");
				int nrExamples = contextMap.getContentModel(childContextStr).getTotalSupport();
				contextFA.addTransition(symbolValue, fromStateValue, toStateValue);
				try {
					contextFA.annotate(symbolValue, fromStateValue, toStateValue, nrExamples);
				} catch (NoSuchTransitionException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
		return contextFA;
	}

	protected static boolean acceptsEmptyString(ContentAutomaton contentModel) {
		return contentModel.isFinalState(contentModel.getInitialState());
	}

	protected static void setInitialStateContentModel(ContextAutomaton contextFA, ContextMap contextMap) {
		String symbolValue = contextMap.getRootElementName();
		ContentAutomaton contentModel = new ContentAutomaton();
		contentModel.addTransition(symbolValue, Glushkov.INITIAL_STATE, symbolValue);
		contentModel.setFinalState(symbolValue);
		contextFA.annotate(contextFA.getInitialState(), contentModel);
	}

	protected static String childContext(String symbolValue, String childType) {
		if (childType.length() > 0)
			return childType + ContextMap.ANCESTOR_PATH_SEP + symbolValue;
		else
			return symbolValue;
	}

	protected static String stateName(String symbolValue, String type) {
		if (type.length() > 0)
			return symbolValue + SupportContextAutomatonFactory.DEFAULT_SEPARATION_CHAR + formatTypeFA(type);
		else
			return symbolValue;
	}

	protected static String stateName(String symbolValue, int contextSize) {
		String[] padding = new String[contextSize - 1];
		for (int i = 0; i < padding.length; i++)
			padding[i] = PADDING;
		return stateName(symbolValue, StringUtils.join(padding, "#"));
	}

	protected static String addTypeInfo(String contextStr, String regexStr) {
		if (regexStr.equals(Regex.LEFT_BRACKET + Regex.EPSILON_SYMBOL + Regex.RIGHT_BRACKET))
			return regexStr;
		Pattern pattern = Pattern.compile("\\w+");
		Matcher matcher = pattern.matcher(regexStr);
		String type = formatType(computeChildType(contextStr));
		StringBuffer str = new StringBuffer();
		while (matcher.find()) {
			String match = matcher.group();
			matcher.appendReplacement(str, match + XMLGrammar.QNAME_TYPE_SEPARATOR + type);
		}
		matcher.appendTail(str);
		return str.toString();
	}

	protected static String formatType(String type) {
		type = type.replaceAll(ContextMap.ANCESTOR_PATH_SEP, TYPE_SEP);
		type = type.replaceAll("\\" + ContextMap.PADDING_SYMBOL, PADDING);
		return type;
	}

	protected static String formatTypeFA(String type) {
		type = type.replaceAll(ContextMap.ANCESTOR_PATH_SEP, "#");
		type = type.replaceAll("\\" + ContextMap.PADDING_SYMBOL, PADDING);
		return type;
	}

	protected static String formatType(String[] context) {
		return formatType(StringUtils.join(context, TYPE_SEP));
	}

	protected static String computeChildType(String contextStr) {
		int pos = contextStr.indexOf(ContextMap.ANCESTOR_PATH_SEP);
		if (pos >= 0)
			return contextStr.substring(pos + 1);
		else
			return "";
	}

	protected static Pair<String, String> dissectType(String type) {
		int pos = type.lastIndexOf(ContextMap.ANCESTOR_PATH_SEP);
		String elementName = type.substring(pos + 1);
		if (pos >= 0) {
			String ancestors = type.substring(0, pos);
			return new Pair<String, String>(elementName, ancestors);
		} else
			return new Pair<String, String>(type, "");
	}
}
