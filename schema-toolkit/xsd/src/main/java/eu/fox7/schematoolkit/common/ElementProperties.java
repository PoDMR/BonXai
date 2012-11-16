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

package eu.fox7.schematoolkit.common;

import eu.fox7.schematoolkit.xsd.om.Element;

public class ElementProperties {
	private boolean nillable;
	private String fixedValue;
	private String defaultValue;

	public ElementProperties() {
		
	}
	
	public ElementProperties(Element element) {
		fixedValue = element.getFixed();
		defaultValue = element.getDefault();
		nillable = element.getNillable();
	}
	
	public ElementProperties(String defaultValue, String fixedValue, boolean nillable) {
		this.fixedValue = fixedValue;
		this.defaultValue = defaultValue;
		this.nillable = nillable;
	}
	
	public boolean isNillable() {
		return nillable;
	}
	public void setNillable(boolean nillable) {
		this.nillable = nillable;
	}
	public String getFixedValue() {
		return fixedValue;
	}
	public void setFixedValue(String fixedValue) {
		this.fixedValue = fixedValue;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
