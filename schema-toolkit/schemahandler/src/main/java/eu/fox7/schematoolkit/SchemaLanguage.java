/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit;

import java.util.EnumMap;
import java.util.Map;

public enum SchemaLanguage {
	XMLSCHEMA("XML Schema", "eu.fox7.schematoolkit.xsd.XSDSchemaHandler"), 
	RELAXNG("RelaxNG", "eu.fox7.schematoolkit.relaxng.RelaxNGSchemaHandler"), 
	BONXAI("BonXai", "eu.fox7.schematoolkit.bonxai.BonxaiSchemaHandler"), 
	DTD("DTD", "eu.fox7.schematoolkit.dtd.DTDSchemaHandler"), 
	TYPEAUTOMATON("TYPEAUTOMATON", "eu.fox7.schematoolkit.typeautomaton.TypeAutomatonSchemaHandler");
	
	private String name;
	private Class<SchemaHandler> handlerClass;
	private Map<SchemaLanguage, Class<SchemaConverter>> converterClasses;

	static {
		XMLSCHEMA.trySetConverter(BONXAI, "eu.fox7.schematoolkit.converter.xsd2bonxai.XSD2BonxaiConverter");
		XMLSCHEMA.trySetConverter(RELAXNG, "eu.fox7.schematoolkit.converter.xsd2relaxng.XSD2RelaxNGConverter");
		XMLSCHEMA.trySetConverter(DTD, "eu.fox7.schematoolkit.converter.xsd2dtd.XSD2DTDConverter");
		XMLSCHEMA.trySetConverter(TYPEAUTOMATON, "eu.fox7.schematoolkit.typeautomaton.factories.XSDTypeAutomatonFactory");
		BONXAI.trySetConverter(XMLSCHEMA, "eu.fox7.schematoolkit.converter.bonxai2xsd.Bonxai2XSDConverter");
		RELAXNG.trySetConverter(XMLSCHEMA, "eu.fox7.schematoolkit.converter.relaxng2xsd.RelaxNG2XSDConverter");
		DTD.trySetConverter(XMLSCHEMA, "eu.fox7.schematoolkit.converter.dtd2xsd.DTD2XSDConverter");
	}
	
	SchemaLanguage(String name, String handlerClass) {
		this.name = name;
		try {
			this.setSchemaHandler(handlerClass);
		} catch (ClassNotFoundException e) {
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setConverter(SchemaLanguage targetLanguage, String converterClassname) throws ClassNotFoundException {
		if (converterClasses == null)
			 converterClasses = new EnumMap<SchemaLanguage, Class<SchemaConverter>>(SchemaLanguage.class);
		Class<?> converterClass = this.getClass().getClassLoader().loadClass(converterClassname);
		if (!SchemaConverter.class.isAssignableFrom(converterClass))
			throw new ClassCastException(converterClass.getCanonicalName() + " cannot be cast to " + SchemaConverter.class.getCanonicalName());
		
		this.converterClasses.put(targetLanguage, (Class<SchemaConverter>) converterClass);
	}

	public void trySetConverter(SchemaLanguage targetLanguage, String converterClassname) {
		try {
			setConverter(targetLanguage, converterClassname);
		} catch (ClassNotFoundException e) {
		}
	}
	

	@SuppressWarnings("unchecked")
	public void setSchemaHandler(String handlerClassname) throws ClassNotFoundException {
		Class<?> handlerClass = this.getClass().getClassLoader().loadClass(handlerClassname);
		if (!SchemaHandler.class.isAssignableFrom(handlerClass))
			throw new ClassCastException(handlerClass.getCanonicalName() + " cannot be cast to " + SchemaHandler.class.getCanonicalName());
		this.handlerClass = (Class<SchemaHandler>) handlerClass;
	}
	
	public String toString() {
		return this.name;
	}
	
	public SchemaHandler getSchemaHandler() {
		if (this.handlerClass==null)
			return null;
		else
			try {
				return this.handlerClass.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} 
	}
	
	public SchemaConverter getConverter(SchemaLanguage targetLanguage) {
		Class<SchemaConverter> converter = this.converterClasses.get(targetLanguage); 
		if (converter==null)
			return null;
		else
			try {
				return converter.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
	}
}
