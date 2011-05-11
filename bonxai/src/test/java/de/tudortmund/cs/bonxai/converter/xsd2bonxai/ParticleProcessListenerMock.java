package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import java.util.Vector;

import de.tudortmund.cs.bonxai.common.Particle;

class ParticleProcessListenerMock implements ParticleProcessListener {

    public Vector<Particle> notifiedParticles = new Vector<Particle>();

    public int notifyCount = 0;

    public void notify(de.tudortmund.cs.bonxai.common.Particle particle) {
        notifyCount++;
        notifiedParticles.add(particle);
    }
}
