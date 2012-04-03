package eu.fox7.learning.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.dom4j.DocumentException;

import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.flt.automata.measures.MutualExclusionDistance;
import eu.fox7.flt.schema.infer.ixsd.ContentEquivalenceRelation;
import eu.fox7.flt.schema.infer.ixsd.ContextMap;
import eu.fox7.flt.schema.infer.ixsd.ContextMapConverter;
import eu.fox7.flt.schema.infer.ixsd.DirectEquivalenceRelation;
import eu.fox7.flt.schema.infer.ixsd.Merger;
import eu.fox7.flt.treeautomata.factories.SupportContentAutomatonFactory;
import eu.fox7.flt.treeautomata.factories.SupportContextAutomatonFactory;
import eu.fox7.flt.treeautomata.impl.ContextAutomaton;
import eu.fox7.treeautomata.converter.ContextAutomaton2XSDConverter;
import eu.fox7.util.sampling.SampleException;
import eu.fox7.util.xml.ConfigurationException;
import eu.fox7.util.xml.acstring.AncestorChildrenDocumentIterator;
import eu.fox7.util.xml.acstring.AncestorChildrenExampleParser;
import eu.fox7.util.xml.acstring.ExampleParsingException;
import eu.fox7.util.xml.acstring.ParseResult;
import eu.fox7.util.xml.acstring.XMLtoAncestorChildrenConverter;

public class SchemaLearner implements eu.fox7.schematoolkit.SchemaLearner {
	private StringWriter writer;
	private XMLtoAncestorChildrenConverter converter;
	private int contextSize = 1;
	
	public SchemaLearner() {
		writer = new StringWriter();
		converter = new XMLtoAncestorChildrenConverter(writer);
	}

	public SchemaLearner(int contextSize) {
		this();
		this.contextSize = contextSize;
	}

	@Override
	public void addXML(File...files) throws IOException, SchemaToolkitException {
		for (File file: files) {
			try {
				this.converter.parse(file);
				List<File> nonParsedFiles = this.converter.getNonParsedFiles();
				if (! nonParsedFiles.isEmpty())
					throw new SchemaToolkitException("File could not be parsed: " + file);
			} catch (ConfigurationException e) {
				throw new SchemaToolkitException(e);
			}
		}
	}

	@Override
	public void addXML(Reader reader) throws IOException, SchemaToolkitException {
		try {
			this.converter.parse(reader);
		} catch (ConfigurationException e) {
			throw new SchemaToolkitException(e);
		} catch (DocumentException e) {
			throw new SchemaToolkitException(e);
		}
	}

	@Override
	public void clear() {
		writer = new StringWriter();
		converter = new XMLtoAncestorChildrenConverter(writer);
	}

	@Override
	public XSDSchema learnXSD() throws SchemaToolkitException {
		try {
			ContextMap contextMap = this.computeContextMap();
			ContextAutomaton contextAutomaton = this.learnContextAutomaton(contextMap);
			ContextAutomaton2XSDConverter caConverter = new ContextAutomaton2XSDConverter();
			XSDSchema xsdSchema = caConverter.convert(contextAutomaton);
			return xsdSchema;
		} catch (ExampleParsingException e) {
			throw new SchemaToolkitException(e);
		} catch (IOException e) {
			throw new SchemaToolkitException(e);
		} catch (SampleException e) {
			throw new SchemaToolkitException(e);
		}
	}
	
	private ContextAutomaton learnContextAutomaton(ContextMap contextMap) {
        ContextAutomaton contextFA = ContextMapConverter.convertToContextFA(contextMap);
        SupportContextAutomatonFactory factory = new SupportContextAutomatonFactory(new SupportContentAutomatonFactory());
        Merger friendlyMerger = new Merger(factory, contextFA);
        ContentEquivalenceRelation relation = new DirectEquivalenceRelation(0.5, new MutualExclusionDistance());
        friendlyMerger.setContentEquivalenceRelation(relation);
        friendlyMerger.merge();
        return contextFA;
	}

	private ContextMap computeContextMap()
	throws IOException, ExampleParsingException, SampleException {
		ContextMap contextMap = new ContextMap(new SupportContentAutomatonFactory(),
				contextSize);
		StringReader stringReader = new StringReader(this.writer.toString());
		Iterator<String> docIt = new AncestorChildrenDocumentIterator(stringReader);
		while (docIt.hasNext()) {
			String doc = docIt.next();
			BufferedReader reader = new BufferedReader(new StringReader(doc));
			AncestorChildrenExampleParser parser = new AncestorChildrenExampleParser();
			parser.setStrippedFirst(true);
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.trim().length() == 0) break;
				ParseResult result = parser.parse(line);
				contextMap.add(result.getContext(), result.getContent());
			}
			reader.close();
		}
		return contextMap;
	}

	@Override
	public Schema learnBonxai() throws SchemaToolkitException {
		throw new SchemaToolkitException("BonXai learning not supported.");
	}


}
