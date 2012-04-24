package uh.df.xsd.analysis;

import gjb.xml.xsdanalyser.TypeNameAnalysis;

import org.apache.commons.lang.StringUtils;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;

/**
 * 
 * @author domi
 * 
 */
public class AdaptedTypeNameAnalysis extends TypeNameAnalysis {

	/**
	 * 
	 * @param xsd
	 */
	public AdaptedTypeNameAnalysis(XSDSchema xsd) {
		super(xsd);
	}

	/**
	 * 
	 */
	@Override
	public String getTypeName(XSDTypeDefinition typeDef) {
		return typeNames.get(typeDef).replaceAll("#", "");
	}

	/**
	 * 
	 */
	@Override
	protected String typeName() {
		return StringUtils.join(namePath.iterator(), "_");
	}

}
