package eu.fox7.schematoolkit.xmlvalidator;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import eu.fox7.flt.automata.impl.sparse.AnnotatedStateNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.schematoolkit.common.AbstractAttribute;
import eu.fox7.schematoolkit.common.AttributeUse;
import eu.fox7.schematoolkit.common.Locatable;
import eu.fox7.schematoolkit.common.Position;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xmlvalidator.AbstractXMLValidator.ContextAutomatonProvider;
import eu.fox7.treeautomata.om.ExtendedContextAutomaton;
import eu.fox7.util.Pair;

public class XMLParser extends DefaultHandler {
	private static final State INITIAL = new State();
    private Locator locator;
    private Stack<Pair<State,State>> caState;
    private Stack<Pair<Integer,Integer>> locations;
    private Stack<String> namespaces;
	private Stack<List<XMLAttribute>> attributeStack;
    private AbstractXMLValidator.ContextAutomatonProvider contextAutomatonProvider;
    private boolean isValid;
    private Collection<ParsedElement> elements = new LinkedList<ParsedElement>();
    
    public static enum Status {
    	VALID, INVALID, ERROR, UNCONSTRAINED, EXTERNAL, UNCHECKED
    }

    private static class XMLAttribute {
    	public XMLAttribute(String uri, String localName, String value) {
			this.uri = uri;
    		this.localName = localName;
			this.value = value;
		}
    	
    	public String uri;
		public String localName;
    	public String value;
    }
    
    public static class ParsedElement implements Locatable {
    	public String getErrorString() {
			return errorString;
		}

		private int startLine, startColumn, endLine, endColumn;
    	private State horizontal, vertical;
    	private Status status;
		private String errorString;

		public ParsedElement(State vertical, State horizontal, int startLine,
				int startColumn, int endLine, int endColumn, Status status, String errorString) {
			this.vertical = vertical;
			this.horizontal = horizontal;
			this.startLine = startLine;
			this.startColumn = startColumn;
			this.endLine = endLine;
			this.endColumn = endColumn;
			this.status = status;
			this.errorString = errorString;
		}
		
		public Status getStatus() {
			return status;
		}

		public int getStartLine() {
			return startLine;
		}

		public int getStartColumn() {
			return startColumn;
		}

		public int getEndLine() {
			return endLine;
		}

		public int getEndColumn() {
			return endColumn;
		}

		public State getHorizontal() {
			return horizontal;
		}

		public State getVertical() {
			return vertical;
		}

		@Override
		public Position getStartPosition() {
			return new Position(startLine, startColumn);
		}

		@Override
		public Position getEndPosition() {
			return new Position(endLine, endColumn);
		}

		@Override
		public void setStartPosition(Position position) {
			throw new RuntimeException("not implemented!");
		}

		@Override
		public void setEndPosition(Position position) {
			throw new RuntimeException("not implemented!");
		}
		
		@Override
		public String toString() {
			return "XMLElement: ("+startLine+','+startColumn+") - ("+endLine+','+endColumn+") Status: " + status.toString();
		}
    }
    
    public XMLParser(ContextAutomatonProvider contextAutomatonProvider) {
    	this.contextAutomatonProvider = contextAutomatonProvider;
    }
    
    public boolean isValid() {
    	return isValid;
    }
    
    public Collection<ParsedElement> getElements() {
    	return elements;
    }
    
    // this will be called when XML-parser starts reading
    // XML-data; here we save reference to current position in XML:
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }
    
    @Override public void startDocument() {
    	this.caState = new Stack<Pair<State,State>>();
    	this.caState.push(new Pair<State,State>(INITIAL, null));
    	this.isValid = true;
    	this.locations = new Stack<Pair<Integer,Integer>>();
    	this.namespaces = new Stack<String>();
    	this.attributeStack = new Stack<List<XMLAttribute>>();
    }
    
    @Override
    public void startElement(String uri, String localName,
    		String qName, Attributes attrs) throws SAXException {
    	if (locator != null)
    		locations.push(new Pair<Integer,Integer>(locator.getLineNumber(), locator.getColumnNumber()));
    	else
    		locations.push(new Pair<Integer,Integer>(0,0));
    	
    	AnnotatedStateNFA<? extends StateNFA,?> contextAutomaton = contextAutomatonProvider.getContextAutomaton(uri);
    	
		Pair<State,State> newState = new Pair<State,State>(null, null); 
    	State vertical = caState.peek().getFirst();
    	State horizontal = null;
    	String fqName = "{" + uri + "}" + localName;
    	Symbol symbol = Symbol.create(fqName);
//    	System.err.println("Name: "+ fqName);
    	
    	if (contextAutomaton==null)
    		vertical = null;
    	else if ((vertical == INITIAL) || (! uri.equals(namespaces.peek()))) 
    		vertical = contextAutomaton.getInitialState();
    	
    	if (vertical != null) {
    		Set<State> states = contextAutomaton.getNextStates(symbol, vertical);
    		if (states.size()>1)
    			throw new SAXException("ContextAutomaton is not deterministic!");
    		if (states.size() == 1) {
    			vertical = states.iterator().next();
    			StateNFA nfa = contextAutomaton.getAnnotation(vertical);
    			if (nfa != null)
    				horizontal = nfa.getInitialState();
    			newState = new Pair<State,State>(vertical, horizontal);   			
    		} 
    	}
    	caState.push(newState);
    	namespaces.push(uri);

		List<XMLAttribute> attributeList = new LinkedList<XMLAttribute>();
		this.attributeStack.push(attributeList);

		for (int i=0; i<attrs.getLength(); ++i) {
			attributeList.add(new XMLAttribute(attrs.getURI(i), attrs.getLocalName(i), attrs.getValue(i)));
		}

    
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException  {
    	namespaces.pop();
    	String fqName = "{" + uri + "}" + localName;
    	Symbol symbol = Symbol.create(fqName);
    	
    	Pair<State,State> currentState = caState.pop();
    	Pair<State,State> parentState = caState.pop();
    	
    	State vertical = currentState.getFirst();
    	State horizontal = currentState.getSecond();
    	State parentVertical = parentState.getFirst();
    	State parentHorizontal = parentState.getSecond();
    	
    	Status status = Status.VALID;

    	String errorString = null;
    	
    	AnnotatedStateNFA<? extends StateNFA,?> parentContextAutomaton = contextAutomatonProvider.getContextAutomaton(uri);

    	if ((vertical != null) && (horizontal != null)) {
			StateNFA nfa = parentContextAutomaton.getAnnotation(vertical);
			if (! nfa.isFinalState(horizontal)) {
//				System.err.println("No final state reached at " + getLocation());
				isValid = false;
				status = Status.INVALID;
				errorString = "Expected one of:";
				for (Transition transition: nfa.getOutgoingTransitions(horizontal))
					errorString+= " " + QualifiedName.getLocalNameFromFQN(transition.getSymbol().toString());
			}
    	}
    	
    	boolean external = (parentVertical == INITIAL) || (! uri.equals(namespaces.peek()));
    	
    	if (vertical == null)
    		if (parentVertical == null)
    			status = Status.UNCHECKED;
    		else if (external)
    			status = Status.EXTERNAL;
    		else
    			status = Status.UNCONSTRAINED;
    	else if (horizontal == null) {
    		isValid = false;
    		status = Status.INVALID;
    		errorString = "Element content not valid!";
    	}
    	
		if (parentHorizontal != null) {
	    	if (external) 
	    		parentContextAutomaton = contextAutomatonProvider.getContextAutomaton(namespaces.peek()); 
			StateNFA nfa = parentContextAutomaton.getAnnotation(parentVertical);
			Set<State> states = nfa.getNextStates(symbol, parentHorizontal);
			if (states.size()>1)
				throw new SAXException("ContentAutomaton is not deterministic!");
			if (states.size() == 1) {
				parentState = new Pair<State,State>(parentVertical, states.iterator().next());   			
			} else {
//				System.err.println("Element " + fqName + " not allowed in content model at " + getLocation());
				isValid = false;
				status = Status.ERROR;
				errorString = "Expected one of:";
				if (nfa.isFinalState(parentHorizontal))
					errorString+=" ε";
				for (Transition transition: nfa.getOutgoingTransitions(parentHorizontal))
					errorString+= " " + QualifiedName.getLocalNameFromFQN(transition.getSymbol().toString());
				parentState = new Pair<State,State>(parentVertical, null);   			
			}
		}
		
		
		
		// check Attributes
		if (parentContextAutomaton instanceof ExtendedContextAutomaton) {
			ExtendedContextAutomaton eca = (ExtendedContextAutomaton) parentContextAutomaton;
			Set<AbstractAttribute> declaredAttributes = eca.getAttributes(vertical);

			List<XMLAttribute> xmlAttributes = this.attributeStack.pop();
			for (XMLAttribute attribute: xmlAttributes) {
				System.err.println("Checking attribute " + attribute.localName);
				QualifiedName attributeName = new QualifiedName(attribute.uri, attribute.localName);
				attributeName.setAttribute(true);
				boolean found = false;
				if (declaredAttributes!=null)
					for (AbstractAttribute declaredAttribute: declaredAttributes)
						if (declaredAttribute.getName().equals(attributeName)) {
							found = true;
							// TODO: fixed value?
						}
				if (!found) {
					if (errorString==null)
						errorString="";
					errorString+="Attribute " + attributeName.getName() + " not allowed!";
					isValid = false;
					status = Status.INVALID;
				}
			}
			
			if (declaredAttributes!=null)
				for (AbstractAttribute declaredAttribute: declaredAttributes) {
					if (declaredAttribute.getUse()==AttributeUse.required) {
						System.err.println("Checking presence of attribute " + declaredAttribute.getName().getName());
						boolean found = false;
						for (XMLAttribute attribute: xmlAttributes) {
							QualifiedName attributeName = new QualifiedName(attribute.uri, attribute.localName);
							attributeName.setAttribute(true);
							if (declaredAttribute.getName().equals(attributeName))
								found = true;
						}
						if (!found) {
							if (errorString==null)
								errorString="";
							errorString+="Attribute " + declaredAttribute.getName().getName() + " not found!";
							isValid = false;
							status = Status.INVALID;
						}
					}
				}
		}
    	
    	Pair<Integer,Integer> pos = locations.pop();
    	ParsedElement element = new ParsedElement(vertical, parentState.getSecond(), pos.getFirst(), pos.getSecond(), locator.getLineNumber(), locator.getColumnNumber(), status, errorString);
    	elements.add(element);
    	caState.push(parentState);
    }

    private String getLocation() {
    	// compose a text with location of error-case:
    	String location = "";
    	if (locator != null) {
    		location = " line " + locator.getLineNumber();
    		location += ", column " + locator.getColumnNumber();
    		location += ": ";
    	}
    	
    	return location;
    }
}
