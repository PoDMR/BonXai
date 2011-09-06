/*
 * Created on Mar 6, 2007
 * Modified on $Date: 2009-11-05 14:01:56 $
 */
package eu.fox7.flt.schema.infer.ixsd;

import eu.fox7.flt.automata.factories.sparse.IncrementalAutomatonFactory;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;
import eu.fox7.util.sampling.SampleException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class ContextMap {

    public static final String ANCESTOR_PATH_SEP = "/";
    public static final String PADDING_SYMBOL = ".";
    protected int contextSize;
    protected Map<String,IncrementalAutomatonFactory<ContentAutomaton>> map = new HashMap<String,IncrementalAutomatonFactory<ContentAutomaton>>();
    protected String rootElementName = null;
    protected int numberOfDocuments = 0;
	protected IncrementalAutomatonFactory<ContentAutomaton> factoryTemplate;

    public ContextMap(IncrementalAutomatonFactory<ContentAutomaton> factory, int contextSize) {
        this.contextSize = contextSize;
        this.factoryTemplate = factory;
    }

    public void add(String[] context, String[] content) throws SampleException {
        if (context.length == 0) {
            rootElementName = content[0];
        } else {
            if (context.length == 1) {
                if (getRootElementName() == null) {
                    rootElementName = context[0];
                }
                if (getContextSize() > 1 && !getRootElementName().equals(context[0])) {
                    throw new SampleException("sample contains documents with disparate root elements: '" + context[0] + "' vs. '" + getRootElementName() + "'");
                }
                numberOfDocuments++;
            }
            String contextStr = computeContextString(context);
            if (!map.containsKey(contextStr))
                map.put(contextStr, factoryTemplate.newInstance());
            getContentModelFactory(contextStr).add(content);
        }
    }

	protected String computeContextString(String[] context) {
        return StringUtils.join(padContext(context, getContextSize()),
                                ANCESTOR_PATH_SEP);
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

    public int getContextSize() {
        return contextSize;
    }

    public int getNumberOfDocuments() {
        return numberOfDocuments;
    }

    public String getRootElementName() {
        return rootElementName;
    }

    public int getNumberOfContexts() {
        return map.size();
    }

    public Iterator<String> getContextIterator() {
        return map.keySet().iterator();
    }

    public boolean hasContentModel(String contextStr) {
        return map.containsKey(contextStr);
    }

	public ContentAutomaton getContentModel(String contextStr) {
        return getContentModelFactory(contextStr).getAutomaton();
    }

	public IncrementalAutomatonFactory<ContentAutomaton> getContentModelFactory(String contextStr) {
	    return map.get(contextStr);
    }

    public ContentAutomaton getContentModel(String[] context) {
        return getContentModel(computeContextString(context));
    }

    public static String[] splitContext(String contextStr) {
    	return contextStr.split(ANCESTOR_PATH_SEP);
    }

    public static String extractElementName(String[] context) {
        return context.length > 0 ? context[context.length-1] : PADDING_SYMBOL;
    }

    public static String extractElementName(String contextStr) {
    	return extractElementName(splitContext(contextStr));
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("root = ").append(getRootElementName()).append("\n");
        for (String contextStr : map.keySet()) {
            str.append(contextStr).append(" -> [");
            str.append(getContentModel(contextStr).toString());
            str.append("]\n");
        }
        return str.toString();
    }

}
