package eu.fox7.jedit;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import eu.fox7.flt.automata.impl.sparse.AnnotatedStateNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.jedit.action.RegisterSchema;
import eu.fox7.jedit.action.ValidateXML;
import eu.fox7.jedit.textelement.BonxaiElement;
import eu.fox7.jedit.textelement.BonxaiExpression;
import eu.fox7.jedit.textelement.Linktype;
import eu.fox7.jedit.textelement.XSDElement;
import eu.fox7.jedit.textelement.XSDType;
import eu.fox7.schematoolkit.NamespaceAwareSchema;
import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.SchemaConverter;
import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaLanguage;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.bonxai.om.Bonxai;
import eu.fox7.schematoolkit.bonxai.om.Expression;
import eu.fox7.schematoolkit.bonxai.om.Locatable;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.exceptions.ConversionFailedException;
import eu.fox7.schematoolkit.xmlvalidator.AbstractXMLValidator;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.treeautomata.converter.Bonxai2ContextAutomatonConverter;
import eu.fox7.treeautomata.converter.XSD2ContextAutomatonConverter;
import eu.fox7.treeautomata.om.ExtendedContextAutomaton;

public class JEditBonxaiManager  {
	private class SchemaWrapper {
		
		@Override
		public String toString() {
			try {
				return "SchemaWrapper [namespace=" + namespace + ", buffers="
						+ Arrays.toString(buffers) + ", schemas=["
						+ ((schemas[0]==null)?"null":schemas[0].getSchemaHandler().getSchemaString()) + ", "
						+ ((schemas[1]==null)?"null":schemas[1].getSchemaHandler().getSchemaString()) + "], contextAutomaton="
						+ contextAutomaton + ", primaryLanguage=" + primaryLanguage
						+ ", correct=" + correct + "]";
			} catch (Exception e) {
				return e.getStackTrace().toString();
			} 
		}

		private final String namespace;
		private Buffer buffers[] = new Buffer[3];
		private Schema schemas[] = new Schema[3];
		private ExtendedContextAutomaton contextAutomaton;
		private SchemaLanguage primaryLanguage = null;
		private boolean correct;
	
		SchemaWrapper(String namespace) {
			this.namespace = namespace;
		}
		
		
		
		private void addSchema(Schema schema, Buffer buffer) {
			int slot;
			
			if (!(schema instanceof NamespaceAwareSchema))
				slot=2;  //DTDs go to slot 2. They are not used for validation, just as starting point for conversion.
			else
				slot=(primaryLanguage == null || schema.getSchemaLanguage()==primaryLanguage)?0:1;
			
			// workaround, as it is not yet possible to validate XMLSchema against an existing contextAutomaton
			// here we force XMLSchema to always be the primary language
			if (slot==1 && schema.getSchemaLanguage()==SchemaLanguage.XMLSCHEMA) {
				removeBuffer(buffers[0]);
				buffers[1] = buffers[0];
				schemas[1] = schemas[0];
				slot=0;
			}
			
			buffers[slot] = buffer;
			schemas[slot] = schema;
			if (slot == 0) {
				primaryLanguage = schema.getSchemaLanguage();
				contextAutomaton = computeContextAutomaton((NamespaceAwareSchema) schema, buffer);
			}
			if (schemas[1]!=null)
				verifySchema();

			//always revalidate to update links
			reValidateXML();
		}
		
		private void verifySchema() {
			correct = verifyContextAutomaton((NamespaceAwareSchema) schemas[1], contextAutomaton, buffers[1]);
			
			Collection<State> states = contextAutomaton.getStates();
			for (State state: states) {
				BonxaiExpression expression = stateExpressionMap.get(state);
				XSDType type = stateTypeMap.get(state);
				if ((expression != null) && (type != null)) {
					BonXaiPlugin.getHighlightManager().addLink(expression, type, Linktype.EXPRESSION2TYPE);
					BonXaiPlugin.getHighlightManager().addLink(type, expression, Linktype.TYPE2EXPRESSION);
				}
			}
			
			for (Entry<State, BonxaiElement> entry: stateElementMap.entrySet()) {
				State state = entry.getKey();
				BonxaiElement bonxaiElement = entry.getValue();
				XSDElement xsdElement = stateXSDElementMap.get(state);
				if (xsdElement!=null) {
					BonXaiPlugin.getHighlightManager().addLink(bonxaiElement, xsdElement, Linktype.BONXAIELEMENT2XSDELEMENT);
					BonXaiPlugin.getHighlightManager().addLink(xsdElement, bonxaiElement, Linktype.XSDELEMENT2BONXAIELEMENT);
				}
			}
		}



		public boolean containsBuffer(JEditBuffer buffer) {
			return ((buffers[0]==buffer) || (buffers[1]==buffer));
		}



		public boolean removeBuffer(JEditBuffer buffer) {
			if (buffers[1]==buffer) {
				buffers[1]=null;
				schemas[1]=null;
			}
			
			if (buffers[0]==buffer) {
				if (schemas[1]==null) 
					return true;

				buffers[0]=buffers[1];
				schemas[0]=schemas[1];
				primaryLanguage = schemas[0].getSchemaLanguage();
				buffers[1]=null;
				schemas[1]=null;
				contextAutomaton = computeContextAutomaton((NamespaceAwareSchema) schemas[0], buffers[0]);
				reValidateXML();
			}
			return false;
		}
	}
	
	private class XMLValidator extends AbstractXMLValidator {
		@Override
		public void setSchema(Schema schema) throws SchemaToolkitException {
		}

		@Override
		protected AnnotatedStateNFA<? extends StateNFA, ?> getContextAutomaton(
				String namespace) {
			SchemaWrapper wrapper = schemaMap.get(namespace);
			return (wrapper==null)?null:wrapper.contextAutomaton;
		}
	}
	
	private class SchemaMap extends HashMap<String,SchemaWrapper> {
		private static final long serialVersionUID = 1L;

		private void putSchema(Schema schema, Buffer buffer) {
			String uri = (schema instanceof NamespaceAwareSchema)?((NamespaceAwareSchema) schema).getTargetNamespace().getUri():"";

			SchemaWrapper wrapper = this.get(uri);
			if (wrapper == null) {
				wrapper = new SchemaWrapper(uri);
				this.put(wrapper.namespace, wrapper);
			}
			wrapper.addSchema(schema, buffer);
		}
		
		public void removeBuffer(JEditBuffer buffer) {
			SchemaWrapper wrapper = null;
			for (SchemaWrapper candidate: this.values())
				if (candidate.containsBuffer(buffer)) {
					wrapper = candidate;
					break;
				}
			if (wrapper!=null) {
				boolean empty = wrapper.removeBuffer(buffer);
				if (empty) {
					this.remove(wrapper.namespace);
					reValidateXML();
				}
			}
		}
	}
	
//	private Map<JEditBuffer,Map<Integer,Collection<Highlight>>> activeHighlights = new HashMap<JEditBuffer,Map<Integer,Collection<Highlight>>>();;
//	private Map<Integer,Collection<TextElement>> activeHighlightsByKey = new HashMap<Integer,Collection<TextElement>>();
	
	private Map<State,BonxaiExpression> stateExpressionMap = new HashMap<State,BonxaiExpression>();
	private Map<State,XSDType> stateTypeMap = new HashMap<State,XSDType>();
	private Map<State,BonxaiElement> stateElementMap = new HashMap<State,BonxaiElement>();
	private Map<State,XSDElement> stateXSDElementMap = new HashMap<State,XSDElement>();
	
	
	private SchemaMap schemaMap = new SchemaMap();


	private Set<JEditBuffer> xmlBuffers = new HashSet<JEditBuffer>();
	
	public XMLValidator getXMLValidator() {
		return new XMLValidator();
	}
	
	public void convertXSD(XSDSchema xmlSchema, Buffer bonxaiBuffer, View view) {
		convertSchema(xmlSchema, bonxaiBuffer, SchemaLanguage.BONXAI, view);
	}

	public void convertBonxai(Bonxai bonxai, Buffer xmlSchemaBuffer, View view) {
		convertSchema(bonxai, xmlSchemaBuffer, SchemaLanguage.XMLSCHEMA, view);
	}
	
	public void convertSchema(Buffer sourceBuffer, View view) {
		Schema sourceSchema = null;
		Buffer targetBuffer = null;
		for (SchemaWrapper schemaWrapper: schemaMap.values()) {
			if (schemaWrapper.buffers[0] == sourceBuffer) {
				sourceSchema = schemaWrapper.schemas[0];
				targetBuffer = schemaWrapper.buffers[1];
				break;
			} else if (schemaWrapper.buffers[1] == sourceBuffer) {
				sourceSchema = schemaWrapper.schemas[1];
				targetBuffer = schemaWrapper.buffers[0];
				break;
			} else if (schemaWrapper.buffers[2] == sourceBuffer) {
				sourceSchema = schemaWrapper.schemas[2];
				targetBuffer = schemaWrapper.buffers[0];
				break;
			}
		}
		if (sourceSchema!=null) {
			if (sourceSchema.getSchemaLanguage()==SchemaLanguage.DTD)
				sourceSchema = SchemaLanguage.DTD.getConverter(SchemaLanguage.XMLSCHEMA).convert(sourceSchema);
			SchemaLanguage targetLanguage = (sourceSchema.getSchemaLanguage()==SchemaLanguage.BONXAI)?SchemaLanguage.XMLSCHEMA:SchemaLanguage.BONXAI;
			this.convertSchema(sourceSchema, targetBuffer, targetLanguage, view);
		}
	}
	
	private void convertSchema(Schema sourceSchema, Buffer targetBuffer, SchemaLanguage targetLanguage, View view) {
		SchemaLanguage sourceLanguage = sourceSchema.getSchemaLanguage();
		sourceLanguage.getConverter(targetLanguage);
		SchemaConverter converter = sourceLanguage.getConverter(targetLanguage);
		if (converter==null)
			throw new RuntimeException("Schemaconverter from " + sourceLanguage + " to " + targetLanguage + " not found.");
		try {
			SchemaHandler target = converter.convert(sourceSchema.getSchemaHandler());
			if (targetBuffer==null)
				targetBuffer = jEdit.newFile(view);
			else {
				targetBuffer.remove(0, targetBuffer.getLength());
				this.removeBuffer(targetBuffer);
			}

			targetBuffer.insert(0, target.getSchemaString());
			targetBuffer.setStringProperty("eu.fox7.schemalanguage", targetLanguage.name());
		    view.getTextArea().goToBufferStart(false);
		    view.getBuffer().setMode();
		    new RegisterSchema().registerSchema(targetBuffer);
		} catch (ConversionFailedException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SchemaToolkitException e) {
			throw new RuntimeException(e);
		}

	}

	private boolean verifyContextAutomaton(NamespaceAwareSchema schema, ExtendedContextAutomaton contextAutomaton, JEditBuffer buffer) {
		BonXaiPlugin.getHighlightManager().removeBuffer(buffer);
		Boolean correct = new Boolean(false);
		if (schema.getSchemaLanguage()==SchemaLanguage.BONXAI)
			computeVerifyContextAutomaton((Bonxai) schema, buffer, contextAutomaton, correct);
		else
			computeVerifyContextAutomaton((XSDSchema) schema, buffer, contextAutomaton, correct);
		return correct;
	}

	private ExtendedContextAutomaton computeContextAutomaton(NamespaceAwareSchema schema, JEditBuffer buffer) {
		BonXaiPlugin.getHighlightManager().removeBuffer(buffer);
		if (schema.getSchemaLanguage()==SchemaLanguage.BONXAI)
			return computeVerifyContextAutomaton((Bonxai) schema, buffer, null, null);
		else
			return computeVerifyContextAutomaton((XSDSchema) schema, buffer, null, null);
	}

	
	private ExtendedContextAutomaton computeVerifyContextAutomaton(XSDSchema xmlSchema, JEditBuffer buffer, ExtendedContextAutomaton eca, Boolean correct) {
		XSD2ContextAutomatonConverter converter = new XSD2ContextAutomatonConverter(true);
		if (eca == null)
			eca = converter.convertXSD(xmlSchema);
		else
			correct = converter.verify(xmlSchema, eca);
		
		Map<QualifiedName, State> typeMap = converter.getTypeMap();
		for (Entry<QualifiedName, State> entry: typeMap.entrySet())
			if (entry.getKey().getNamespaceURI().equals(xmlSchema.getDefaultNamespace().getUri())) {
				XSDType xsdType = new XSDType(buffer, xmlSchema.getType(entry.getKey()));
				BonXaiPlugin.getHighlightManager().addTextElement(xsdType);
				this.stateTypeMap.put(entry.getValue(), xsdType);
			}

		Map<Element, State> elementStateMap = converter.getElementStateMap();
		
		for (Element element: xmlSchema.getAllElements()) {
			XSDElement xsdElement = new XSDElement(buffer, element);
			BonXaiPlugin.getHighlightManager().addTextElement(xsdElement);
			
			State state = elementStateMap.get(element);
			if (state!=null)
				stateXSDElementMap.put(state, xsdElement);
		}
		
		return eca;
	}
	
	private ExtendedContextAutomaton computeVerifyContextAutomaton(Bonxai bonxai, JEditBuffer buffer, ExtendedContextAutomaton eca, Boolean correct) {
		Bonxai2ContextAutomatonConverter converter = new Bonxai2ContextAutomatonConverter();
		if (eca == null)
			eca = converter.convert(bonxai);
		else
			correct = converter.verify(bonxai, eca);


		Map<Expression,Set<State>> expressionStateMap = converter.getExpressionStateMap();
		for (Expression expression: bonxai.getExpressions()) {
			BonxaiExpression bonxaiExpression = new BonxaiExpression(buffer, expression); 
			BonXaiPlugin.getHighlightManager().addTextElement(bonxaiExpression);

			Collection<State> states = expressionStateMap.get(expression);
			if (states==null)
				System.err.println("No state found for expression");
			else 
				for (State state: states)
					this.stateExpressionMap.put(state, bonxaiExpression);
		}
		
		Map<Locatable, Set<State>> elementStateMap = converter.getElementStateMap();
		
		for (Particle particle: bonxai.getAllElementlikeParticles()) {
			if (particle instanceof Locatable) {
				BonxaiElement bonxaiElement = new BonxaiElement(buffer, (Locatable) particle);
				BonXaiPlugin.getHighlightManager().addTextElement(bonxaiElement);
				Collection<State> states = elementStateMap.get(particle);
				if (states!=null)
					for (State state: states)
						this.stateElementMap.put(state, bonxaiElement);
			}
		}

		return eca;
	}


	public void removeBuffer(JEditBuffer buffer) {
		this.xmlBuffers.remove(buffer);
		this.schemaMap.removeBuffer(buffer);
	}

	public void addSchema(Schema schema, Buffer buffer) {
		this.schemaMap.putSchema(schema, buffer);
	}

	public BonxaiExpression getExpression(State vertical) {
		return this.stateExpressionMap.get(vertical);
	}

	public void reValidateXML() {
		for (JEditBuffer xmlBuffer: xmlBuffers )
			new ValidateXML().validateBuffer(xmlBuffer);
	}
	
	public void addXMLBuffer(JEditBuffer buffer) {
		this.xmlBuffers.add(buffer);
	}

	public SchemaMap getSchemaMap() {
		return schemaMap;
	}

	public XSDType getType(State vertical) {
		return this.stateTypeMap.get(vertical);
	}
	
	public BonxaiElement getBonxaiElement(State horizontal) {
		return this.stateElementMap.get(horizontal);
	}

	public XSDElement getXSDElement(State horizontal) {
		return this.stateXSDElementMap.get(horizontal);
	}
}
