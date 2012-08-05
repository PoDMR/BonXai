package eu.fox7.schematoolkit.xsd.saxparser;

import java.util.List;

import org.xml.sax.Attributes;

import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ParticleContainer;

public class ParticleContainerProcessor implements Processor {
	protected ParticleContainer particleContainer;
	
	@Override
	public Object process(List<Particle> particles, Attributes attributes) {
		for (Particle particle: particles)
			this.particleContainer.addParticle(particle);
		return particleContainer;
	}

}
