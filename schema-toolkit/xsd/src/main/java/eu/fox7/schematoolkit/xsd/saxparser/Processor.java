package eu.fox7.schematoolkit.xsd.saxparser;

import java.util.List;

import org.xml.sax.Attributes;

import eu.fox7.schematoolkit.common.Particle;

public interface Processor {
	public Object process(List<Particle> particles, Attributes attributes);
}
