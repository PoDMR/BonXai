/*
 * Created on Feb 14, 2007
 * Modified on $Date: 2009-11-09 11:49:45 $
 */
package eu.fox7.util.xml.acstring;


import org.apache.commons.lang.StringUtils;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class AncestorChildrenExampleParser implements ExampleParser {

    public static final String DEFAULT_ANCESTOR_CHILDREN_SEP = "::-";
    protected String ancestorChildrenSep = DEFAULT_ANCESTOR_CHILDREN_SEP;
    public static final String DEFAULT_ANCESTOR_SEP = "/";
    protected String ancestorSep = DEFAULT_ANCESTOR_SEP;
    public static final String DEFAULT_CHILDREN_SEP = " ";
    protected String childrenSep = DEFAULT_CHILDREN_SEP;
    protected boolean isStrippedFirst = false;
    protected boolean isRemovingNamespaces = false;

    public String getAncestorChildrenSep() {
        return ancestorChildrenSep;
    }

    public void setAncestorChildrenSep(String ancestorChildrenSep) {
        this.ancestorChildrenSep = ancestorChildrenSep;
    }

    public String getAncestorSep() {
        return ancestorSep;
    }

    public void setAncestorSep(String ancestorSep) {
        this.ancestorSep = ancestorSep;
    }

    public String getChildrenSep() {
        return childrenSep;
    }

    public void setChildrenSep(String childrenSep) {
        this.childrenSep = childrenSep;
    }

    public boolean isStrippedFirst() {
        return isStrippedFirst;
    }

    public void setStrippedFirst(boolean isStrippedFirst) {
        this.isStrippedFirst = isStrippedFirst;
    }

    public boolean isRemovingNamespaces() {
        return isRemovingNamespaces;
    }

    public void setRemovingNamespaces(boolean isRemovingNamespaces) {
        this.isRemovingNamespaces = isRemovingNamespaces;
    }

    /* (non-Javadoc)
     * @see eu.fox7.util.schemax.ExampleParser#parse(java.lang.String)
     */
    public ParseResult parse(String exampleStr) throws ExampleParsingException {
        if (exampleStr.trim().length() == 0)
            throw new ExampleParsingException("empty lines can't occur as input");
        if (!exampleStr.contains(getAncestorChildrenSep()))
            throw new ExampleParsingException("no separator in '" + exampleStr + "'");
        String[] parts = exampleStr.split(getAncestorChildrenSep(), 2);
        String[] ancestors = null;
        if (parts[0].trim().length() != 0) {
            if (isStrippedFirst() && parts[0].startsWith(getAncestorSep()))
                parts[0] = parts[0].substring(getAncestorSep().length());
            ancestors = parts[0].trim().split(getAncestorSep());
        } else
            ancestors = new String[0];
        String[] children = null;
        if (parts[1].trim().length() != 0)
            children = parts[1].trim().split(getChildrenSep());
        else
            children = new String[0];
        if (isRemovingNamespaces()) {
            removeNamespaces(ancestors);
            removeNamespaces(children);
        }
        toLowerCase(ancestors);
        toLowerCase(children);
        return new AncestorChildrenParseResult(ancestors, children);
    }

    protected void toLowerCase(String[] elements) {
        for (int i = 0; i < elements.length; i++)
            elements[i] = elements[i].toLowerCase();
    }

    protected void removeNamespaces(String[] elements) {
        for (int i = 0; i < elements.length; i++)
            elements[i] = removeNamespaces(elements[i]);
    }

    protected String removeNamespaces(String element) {
        int pos = element.indexOf(':');
        if (pos >= 0)
            return element.substring(pos + 1);
        else
            return element;
    }

    public static class AncestorChildrenParseResult implements ParseResult {

        protected String[] contextStr;
        protected String[] contentStr;

        public AncestorChildrenParseResult(String[] contextStr,
                                           String[] contentStr) {
            super();
            this.contextStr = contextStr;
            this.contentStr = contentStr;
        }

        public String[] getContent() {
            return contentStr;
        }

        public String[] getContext() {
            return contextStr;
        }
        
        @Override
        public String toString() {
        	return StringUtils.join(getContext(), "/") + ", " +
        	    StringUtils.join(getContent(), " ");
        }

    }

}
