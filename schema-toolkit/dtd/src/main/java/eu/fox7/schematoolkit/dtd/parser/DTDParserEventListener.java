package eu.fox7.schematoolkit.dtd.parser;

import java.util.ArrayDeque;
import java.util.Deque;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.xml.dtdparser.DTDEventListener;
import com.sun.xml.dtdparser.InputEntity;

import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.ElementRef;
import eu.fox7.schematoolkit.common.EmptyPattern;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ParticleContainer;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.common.SequencePattern;
import eu.fox7.schematoolkit.dtd.om.Attribute;
import eu.fox7.schematoolkit.dtd.om.Attribute.AttributeDefaultPresence;
import eu.fox7.schematoolkit.dtd.om.AttributeType;
import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;
import eu.fox7.schematoolkit.dtd.om.Element;

public class DTDParserEventListener implements DTDEventListener {
	protected class DummyParticle extends Particle {}
	
	protected DocumentTypeDefinition dtd;
	protected Element element;
	protected ParticleContainer mixedContainer;
	protected Particle particle;
	protected Deque<Particle> stack;
	
	@Override
	public void setDocumentLocator(Locator arg0) {
		System.err.println("setDocumentLocator()");
	}

	@Override
	public void startDTD(InputEntity in) throws SAXException {
		this.dtd = new DocumentTypeDefinition();
	}

	@Override
	public void endDTD() throws SAXException {  //nothing todo
	}

	@Override
	public void startContentModel(String name, short type) throws SAXException {
		this.stack = new ArrayDeque<>();
		element = new Element(new QualifiedName(Namespace.EMPTY_NAMESPACE, name));
		particle = new DummyParticle();
		switch (type) {
		case DTDEventListener.CONTENT_MODEL_EMPTY: 
			particle = new EmptyPattern();
			break;
		case DTDEventListener.CONTENT_MODEL_MIXED:
			element.setMixed(true);
			mixedContainer = new ChoicePattern();
			particle = new CountingPattern(mixedContainer,0,null);
			break;
		case DTDEventListener.CONTENT_MODEL_ANY:
			particle = new AnyPattern();
			break;
		case DTDEventListener.CONTENT_MODEL_CHILDREN:
			break; //nothing todo (default case)
		default:
			throw new SAXException("Invalid content model type: " + type);
		}
	}

	@Override
	public void endContentModel(String arg0, short arg1) throws SAXException {
		this.element.setParticle(particle);
		this.dtd.addElement(element);
	}

	@Override
	public void mixedElement(String name) throws SAXException {
		mixedContainer.addParticle(new ElementRef(new QualifiedName(Namespace.EMPTY_NAMESPACE, name)));
	}
	
	@Override
	public void attributeDecl(String eName, String aName, String aType, String[] enumeration, short use, String value)
			throws SAXException {
		AttributeDefaultPresence adp = null;
		switch (use) {
		case DTDEventListener.USE_FIXED:
			adp = AttributeDefaultPresence.FIXED;
			break;
		case DTDEventListener.USE_IMPLIED:
			adp = AttributeDefaultPresence.IMPLIED;
			break;
		case DTDEventListener.USE_REQUIRED:
			adp = AttributeDefaultPresence.REQUIRED;
			break;
		case DTDEventListener.USE_NORMAL: 
			//BUG in DTDParser: implied is reported as normal
			if (value==null)
				adp = AttributeDefaultPresence.IMPLIED;
			break;
		}
		Attribute attribute = new Attribute(new QualifiedName(Namespace.EMPTY_NAMESPACE,aName), adp, value);
		attribute.setType(AttributeType.valueOf(aType));
		//TODO Enumeration
		this.dtd.getElement(new QualifiedName(Namespace.EMPTY_NAMESPACE,eName)).addAttribute(attribute);

	}

	@Override
	public void childElement(String name, short occurence) throws SAXException {
		Particle child =  this.createCountingPattern(new ElementRef(new QualifiedName(Namespace.EMPTY_NAMESPACE, name)), occurence);
		if (particle instanceof DummyParticle)
			particle = child;
		else if (particle instanceof ParticleContainer)
			((ParticleContainer) particle).addParticle(child); 
		else
			throw new SAXException("particle is neither null nor container");
	}

	@Override
	public void comment(String arg0) throws SAXException { // ignore comments
	}

	@Override
	public void connector(short type) throws SAXException {
		if (!(particle instanceof ParticleContainer)) {
			Particle child = particle;
			switch (type) {
			case DTDEventListener.CHOICE:
				particle = new ChoicePattern();
				break;
			case DTDEventListener.SEQUENCE:
				particle = new SequencePattern();
				break;
			default:
				throw new SAXException("Illegal connector: " + type);
			}
			((ParticleContainer) particle).addParticle(child);
		}
	}

	@Override
	public void startModelGroup() throws SAXException {
		stack.push(particle);
		particle = new DummyParticle();
	}

	@Override
	public void endModelGroup(short occurence) throws SAXException {
		Particle child = this.createCountingPattern(particle, occurence);
		if (! stack.isEmpty()) {
			particle = stack.pop();
			if (particle instanceof DummyParticle)
				particle = child;
			else
				((ParticleContainer) particle).addParticle(child);
		} 
	}
	
	
	
	@Override
	public void endCDATA() throws SAXException {
		System.err.println("DTDParserEventListener endCDATA()");
	}

	@Override
	public void externalGeneralEntityDecl(String arg0, String arg1, String arg2) throws SAXException {
		System.err.println("DTDParserEventListener externalGeneralEntityDecl()");
	}

	@Override
	public void externalParameterEntityDecl(String arg0, String arg1, String arg2) throws SAXException {
		System.err.println("DTDParserEventListener externalParameterEntityDecl()");
	}

	@Override
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
	}

	@Override
	public void internalGeneralEntityDecl(String arg0, String arg1) throws SAXException {
		System.err.println("DTDParserEventListener internalGeneralEntityDecl()");
	}

	@Override
	public void internalParameterEntityDecl(String arg0, String arg1) throws SAXException {
		System.err.println("DTDParserEventListener internalParameterEntityDecl()");
	}


	@Override
	public void notationDecl(String arg0, String arg1, String arg2) throws SAXException {
		System.err.println("DTDParserEventListener notationDecl()");
	}

	@Override
	public void processingInstruction(String arg0, String arg1) throws SAXException {
		System.err.println("DTDParserEventListener processingInstructions()");
	}

	@Override
	public void startCDATA() throws SAXException {
		System.err.println("DTDParserEventListener startCDATA()");
	}

	@Override
	public void unparsedEntityDecl(String arg0, String arg1, String arg2, String arg3) throws SAXException {
		System.err.println("DTDParserEventListener unparsedEntityDecl()");
	}

	@Override
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
		System.err.println("DTDParserEventListener characters()");
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {
		throw e;
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		throw e;
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		throw e;
	}
	
	protected Particle createCountingPattern(Particle particle, short occurence) throws SAXException {
		switch (occurence) {
		case DTDEventListener.OCCURENCE_ONCE: //default
			break;
		case DTDEventListener.OCCURENCE_ONE_OR_MORE: 
			particle = new CountingPattern(particle,1,null);
			break;
		case DTDEventListener.OCCURENCE_ZERO_OR_MORE: 
			particle = new CountingPattern(particle,0,null);
			break;
		case DTDEventListener.OCCURENCE_ZERO_OR_ONE: 
			particle = new CountingPattern(particle,0,1);
			break;
		default: 
			throw new SAXException("Invalid occurence indicator: " + occurence);
		}
		
		return particle;
	}

	public DocumentTypeDefinition getDTD() {
		return this.dtd;
	}
}
