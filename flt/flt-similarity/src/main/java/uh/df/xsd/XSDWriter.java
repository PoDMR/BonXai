package uh.df.xsd;

import eu.fox7.flt.treegrammar.XMLElementDefinition;
import eu.fox7.flt.treegrammar.XMLElementNotDefinedException;
import eu.fox7.flt.treegrammar.XMLGrammar;
import gjb.util.tree.Node;
import gjb.util.tree.Tree;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 * 
 * @author Dominique Fonteyn
 *
 */
public class XSDWriter {
	
	/**
	 * Translates the grammar to a XSD, and writes it with the writer.
	 * @param writer the writer to output the XSD
	 * @param grammar the grammar that needs to be translated to a XSD
	 * @throws XMLElementNotDefinedException 
	 * @throws IOException 
	 */
	public void writeXSD(Writer writer, XMLGrammar grammar, String namespace) throws XMLElementNotDefinedException, IOException {
		writer.write("<?xml version=\"1.0\"?>\n");
		writer.write("<xs:schema xmlns:xs=\"" + namespace + "\">\n\n");
		
		// root has to go first
		XMLElementDefinition element = grammar.getRootElement();		
		writer.write("<xs:element name=\"" + element.getQName() + "\" type=\"" + getTypeName(element) + "\" />\n");
		
		// iteratore over each type
		for(Iterator<String> it = grammar.getElementNameIterator(); it.hasNext(); ) {
			element = grammar.getElement(it.next());
			if (element.getContentModelString().toLowerCase().contains("epsilon")) {
				writer.write(outputSimpleType(element) + "\n");
			}
			else {
				writer.write(outputComplexType(element) + "\n");
			}			
		}		
		
		writer.write("</xs:schema>\n");		
		writer.flush();
	}
	
	protected String outputSimpleType(XMLElementDefinition element) {
		return ("<xs:simpleType name=\"" + getTypeName(element) + "\">\n\t<xs:restriction base=\"xs:string\" />\n</xs:simpleType>\n");
	}

	protected String outputComplexType(XMLElementDefinition element) {
		StringBuilder str = new StringBuilder();
		str.append("<xs:complexType name=\"" + getTypeName(element) + "\" mixed=\"true\">\n");		
		Tree tree = element.getContentModel().getTree();
		str.append(parseNode(tree.getRoot(), 1, 1));
		str.append("</xs:complexType>\n");
		return str.toString();
	}

	protected String getTypeName(XMLElementDefinition element) {
		return (element.getQName() + element.getType() + "Type");
	}
	
	protected String getTypeName(String nodeKey) {
		return (nodeKey.replace(XMLGrammar.QNAME_TYPE_SEPARATOR, "") + "Type");
	}

	protected String getLabel(String nodeKey) {
		return nodeKey.substring(0, nodeKey.indexOf(XMLGrammar.QNAME_TYPE_SEPARATOR));
	}

	protected String parseNode(Node node, int minoccur, int maxoccur) {
		StringBuilder str = new StringBuilder();

		if (node.getKey().equals(".")) {
			str.append("<xs:sequence>\n");
			for (Node child : node.getChildren())
				str.append(parseNode(child, 1, 1));
			str.append("</xs:sequence>\n");
		} else if (node.getKey().equals("+")) {
			for (Node child : node.getChildren())
				str.append(parseNode(child, 1, -999));
		} else if (node.getKey().equals("*")) {
			for (Node child : node.getChildren())
				str.append(parseNode(child, 0, -999));
		} else if (node.getKey().equals("?")) {
			for (Node child : node.getChildren())
				str.append(parseNode(child, 0, 1));
		} else if (node.getKey().equals("|")) {
			str.append("<xs:choice>\n");
			for (Node child : node.getChildren())
				str.append(parseNode(child, 1, 1));
			str.append("</xs:choice>\n");
		} else {
			str.append("<xs:element name=\"" + getLabel(node.getKey()) + "\" type=\"" + getTypeName(node.getKey())
					+ "\" "); //  
			str.append("minOccurs=\"" + minoccur + "\" ");
			if (maxoccur > 0)
				str.append("maxOccurs=\"" + maxoccur + "\" ");
			else
				str.append("maxOccurs=\"unbounded\" ");
			str.append("/>\n");
		}
		return str.toString();
	}	

}
