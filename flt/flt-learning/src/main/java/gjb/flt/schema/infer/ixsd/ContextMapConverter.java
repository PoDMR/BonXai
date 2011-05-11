/*
 * Created on Mar 7, 2007
 * Modified on $Date: 2009-11-05 14:01:56 $
 */
package gjb.flt.schema.infer.ixsd;

import gjb.flt.FLTRuntimeException;
import gjb.flt.automata.NoSuchStateException;
import gjb.flt.automata.NoSuchTransitionException;
import gjb.flt.automata.impl.sparse.SupportNFA;
import gjb.flt.regex.Glushkov;
import gjb.flt.regex.Regex;
import gjb.flt.regex.infer.rwr.NoOpportunityFoundException;
import gjb.flt.regex.infer.rwr.RewriteEngine;
import gjb.flt.regex.infer.rwr.impl.Automaton;
import gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import gjb.flt.treeautomata.factories.SupportContextAutomatonFactory;
import gjb.flt.treeautomata.impl.ContentAutomaton;
import gjb.flt.treeautomata.impl.ContextAutomaton;
import gjb.flt.treegrammar.SyntaxException;
import gjb.flt.treegrammar.XMLAttributeDefinition;
import gjb.flt.treegrammar.XMLGrammar;
import gjb.util.Pair;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class ContextMapConverter {

    public static final String TYPE_SEP = "_";
    public static final String PADDING = "0";

    public static XMLGrammar convertToGrammar(ContextMap contextMap,
                                              RewriteEngine rewriter)
            throws NoOpportunityFoundException {
        final int contextSize = contextMap.getContextSize();
        XMLGrammar doc = new XMLGrammar();
        String rootElement = contextMap.getRootElementName();
        String[] rootContext = ContextMap.padContext(new String[] {},
                                                     contextSize - 1);
        doc.setRootElement(rootElement, formatType(rootContext));
        GraphAutomatonFactory gaFactory = new GraphAutomatonFactory();
        for (Iterator<String> contextIt = contextMap.getContextIterator(); contextIt.hasNext(); ) {
            String contextStr = contextIt.next();
            Pair<String,String> parts = dissectType(contextStr);
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
        }
        return doc;
    }

    public static ContextAutomaton convertToContextFA(ContextMap contextMap) {
    	ContextAutomaton contextFA = new ContextAutomaton();
    	contextFA.setInitialState(Glushkov.INITIAL_STATE);
        setInitialStateContentModel(contextFA, contextMap);
        for (Iterator<String> contextIt = contextMap.getContextIterator(); contextIt.hasNext(); ) {
            String contextStr = contextIt.next();
            Pair<String,String> parts = dissectType(contextStr);
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
        contextFA.addTransition(contextMap.getRootElementName(),
                                Glushkov.INITIAL_STATE,
                                stateName(contextMap.getRootElementName(),
                                          contextMap.getContextSize()));
        try {
            contextFA.annotate(contextMap.getRootElementName(),
                               Glushkov.INITIAL_STATE,
                               stateName(contextMap.getRootElementName(),
                                         contextMap.getContextSize()),
                               contextMap.getNumberOfDocuments());
        } catch (NoSuchTransitionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        for (Iterator<String> contextIt = contextMap.getContextIterator(); contextIt.hasNext(); ) {
            String contextStr = contextIt.next();
            Pair<String,String> parts = dissectType(contextStr);
            String fromStateValue = stateName(parts.getFirst(), parts.getSecond());
            ContentAutomaton contentFA = contextFA.getAnnotation(fromStateValue);
            
            String childType = computeChildType(contextStr);
            for (String symbolValue : contentFA.getSymbolValues()) {
            	String toStateValue = stateName(symbolValue, childType);
                String childContextStr = childContext(symbolValue, childType);
                if (!contextMap.hasContentModel(childContextStr))
                	throw new FLTRuntimeException("no content model for " + childContextStr +
                	                              ", sample is most probably incomplete");
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

	protected static void setInitialStateContentModel(ContextAutomaton contextFA,
                                                      ContextMap contextMap) {
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
            return symbolValue +
                SupportContextAutomatonFactory.DEFAULT_SEPARATION_CHAR +
                formatTypeFA(type);
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

    protected static Pair<String,String> dissectType(String type) {
        int pos = type.lastIndexOf(ContextMap.ANCESTOR_PATH_SEP);
        String elementName = type.substring(pos + 1);
        if (pos >= 0) {
            String ancestors = type.substring(0, pos);
            return new Pair<String,String>(elementName, ancestors);
        } else
            return new Pair<String,String>(type, "");
    }

}
