package eu.fox7.jedit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import eu.fox7.flt.automata.impl.sparse.AnnotatedStateNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.jedit.action.ValidateXML;
import eu.fox7.jedit.textelement.BonxaiExpression;
import eu.fox7.jedit.textelement.XSDType;
import eu.fox7.schematoolkit.NamespaceAwareSchema;
import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.SchemaLanguage;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.bonxai.om.Bonxai;
import eu.fox7.schematoolkit.bonxai.om.Expression;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xmlvalidator.AbstractXMLValidator;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.treeautomata.converter.Bonxai2ContextAutomatonConverter;
import eu.fox7.treeautomata.converter.ExtendedContextAutomaton;
import eu.fox7.treeautomata.converter.XSD2ContextAutomatonConverter;

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
		private JEditBuffer buffers[] = new JEditBuffer[2];
		private NamespaceAwareSchema schemas[] = new NamespaceAwareSchema[2];
		private ExtendedContextAutomaton contextAutomaton;
		private SchemaLanguage primaryLanguage = null;
		private boolean correct;
	
		SchemaWrapper(String namespace) {
			this.namespace = namespace;
		}
		
		
		
		private void addSchema(NamespaceAwareSchema schema, JEditBuffer buffer) {
			int slot=(primaryLanguage == null || schema.getSchemaLanguage()==primaryLanguage)?0:1;
						
			buffers[slot] = buffer;
			schemas[slot] = schema;
			if (slot == 0) {
				primaryLanguage = schema.getSchemaLanguage();
				contextAutomaton = computeContextAutomaton(schema, buffer);
				reValidateXML();
			}
			if (schemas[1]!=null)
				correct = verifySchema();
		}
		
		private boolean verifySchema() {
			return false; //verifyContextAutomaton(schemas[1], contextAutomaton, buffers[1]);
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
				contextAutomaton = computeContextAutomaton(schemas[0], buffers[0]);
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

		private void putSchema(NamespaceAwareSchema schema, JEditBuffer buffer) {
			SchemaWrapper wrapper = this.get(schema.getTargetNamespace().getUri());
			if (wrapper == null) {
				wrapper = new SchemaWrapper(schema.getTargetNamespace().getUri());
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
			boolean empty = wrapper.removeBuffer(buffer);
			if (empty) {
				this.remove(wrapper.namespace);
				reValidateXML();
			}
		}
	}
	
//	private Map<JEditBuffer,Map<Integer,Collection<Highlight>>> activeHighlights = new HashMap<JEditBuffer,Map<Integer,Collection<Highlight>>>();;
//	private Map<Integer,Collection<TextElement>> activeHighlightsByKey = new HashMap<Integer,Collection<TextElement>>();
	
	private Map<State,BonxaiExpression> stateExpressionMap = new HashMap<State,BonxaiExpression>();
	private Map<State,XSDType> stateTypeMap = new HashMap<State,XSDType>();
	
	
	private SchemaMap schemaMap = new SchemaMap();


	private Set<JEditBuffer> xmlBuffers = new HashSet<JEditBuffer>();
	
	public XMLValidator getXMLValidator() {
		return new XMLValidator();
	}
	
	public void convertXSD(XSDSchema xmlSchema, JEditBuffer bonxaiBuffer) {
		// TODO Auto-generated method stub
		
	}

	public void convertBonxai(Bonxai bonxai, JEditBuffer xmlSchemaBuffer) {
		// TODO Auto-generated method stub
		
	}

	

	private boolean verifyContextAutomaton(XSDSchema xmlSchema,
			ExtendedContextAutomaton contextAutomaton, JEditBuffer buffer) {
		// TODO Auto-generated method stub
		return false;
	}

	private ExtendedContextAutomaton computeContextAutomaton(NamespaceAwareSchema schema, JEditBuffer buffer) {
		BonXaiPlugin.getHighlightManager().removeBuffer(buffer);
		if (schema.getSchemaLanguage()==SchemaLanguage.BONXAI)
			return computeContextAutomaton((Bonxai) schema, buffer);
		else
			return computeContextAutomaton((XSDSchema) schema, buffer);
	}

	
	private ExtendedContextAutomaton computeContextAutomaton(XSDSchema xmlSchema, JEditBuffer buffer) {
		XSD2ContextAutomatonConverter converter = new XSD2ContextAutomatonConverter(true);
		ExtendedContextAutomaton eca = converter.convertXSD(xmlSchema);
		Map<QualifiedName, State> typeMap = converter.getTypeMap();
		for (Entry<QualifiedName, State> entry: typeMap.entrySet())
			if (entry.getKey().getNamespaceURI().equals(xmlSchema.getDefaultNamespace().getUri())) {
				XSDType xsdType = new XSDType(buffer, xmlSchema.getType(entry.getKey()));
				BonXaiPlugin.getHighlightManager().addTextElement(xsdType);
				this.stateTypeMap.put(entry.getValue(), xsdType);
			}
				
		return eca;
	}
	
	private ExtendedContextAutomaton computeContextAutomaton(Bonxai bonxai, JEditBuffer buffer) {
		Bonxai2ContextAutomatonConverter converter = new Bonxai2ContextAutomatonConverter();
		ExtendedContextAutomaton eca = converter.convert(bonxai);
		Map<Expression,Set<State>> expressionStateMap = converter.getExpressionStateMap();
		for (Expression expression: bonxai.getExpressions()) {
			BonxaiExpression bonxaiExpression = new BonxaiExpression(buffer, expression); 
			BonXaiPlugin.getHighlightManager().addTextElement(bonxaiExpression);
			for (State state: expressionStateMap.get(expression))
				this.stateExpressionMap.put(state, bonxaiExpression);
		}

		return eca;
	}

	private boolean verifyContextAutomaton(Bonxai bonxai,
			ExtendedContextAutomaton contextAutomaton, JEditBuffer buffer) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeBuffer(JEditBuffer buffer) {
		this.xmlBuffers.remove(buffer);
		this.schemaMap.removeBuffer(buffer);
	}

	public void addSchema(NamespaceAwareSchema schema, JEditBuffer buffer) {
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
}
