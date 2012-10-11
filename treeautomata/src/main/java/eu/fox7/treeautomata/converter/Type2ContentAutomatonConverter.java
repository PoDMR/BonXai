package eu.fox7.treeautomata.converter;



import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.schematoolkit.common.EmptyPattern;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.xsd.om.ComplexContentType;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.Content;
import eu.fox7.schematoolkit.xsd.om.SimpleContentType;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;

public class Type2ContentAutomatonConverter extends Particle2ContentAutomatonConverter  {
	public SparseNFA convertType(Type type) {
		Particle particle = new EmptyPattern();
		if (type instanceof SimpleType) {
			// nothing todo here
		} else if (type instanceof ComplexType) {
			ComplexType complexType = (ComplexType) type;
			Content content = complexType.getContent();
			if (content instanceof SimpleContentType) {
				//nothing todo here	
			} else if (content instanceof ComplexContentType) {
				ComplexContentType complexContent = (ComplexContentType) content;
				particle = complexContent.getParticle();
			} else if (content == null) {
				// nothing todo here
			} else {
				throw new RuntimeException("Unknown content type in complex type.");
			}
		}
		SparseNFA contentAutomaton = this.convertParticle(particle);
		
		return contentAutomaton;
	}

}
