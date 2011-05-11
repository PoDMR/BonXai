package de.tudortmund.cs.bonxai.xsd.automaton;

import de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons.*;
import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.*;

/**
 * @author Dominik Wolff
 */
public class AutomatonTestSuite {

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();

        // Automaton package
        suite.addTestSuite(AutomatonTest.class);
        suite.addTestSuite(StateTest.class);
        suite.addTestSuite(TransitionTest.class);

        // Type automaton package
        suite.addTestSuite(TypeStateTest.class);
        suite.addTestSuite(SubsetTypeStateTest.class);
        suite.addTestSuite(ProductTypeStateTest.class);

        suite.addTestSuite(TypeAutomatonTest.class);
        suite.addTestSuite(SubsetTypeAutomatonTest.class);
        suite.addTestSuite(ProductTypeAutomatonTest.class);

        suite.addTestSuite(TypeAutomatonFactoryTest.class);

        // Particle automaton package
        suite.addTestSuite(ParticleStateTest.class);
        suite.addTestSuite(SubsetParticleStateTest.class);
        suite.addTestSuite(ProductParticleStateTest.class);

        suite.addTestSuite(ParticleAutomatonTest.class);
        suite.addTestSuite(SubsetParticleAutomatonTest.class);
        suite.addTestSuite(ProductParticleAutomatonTest.class);

        suite.addTestSuite(ParticleTransitionTest.class);
        suite.addTestSuite(ParticleAutomatonFactoryTest.class);
        suite.addTestSuite(ParticleBuilderTest.class);

        return suite;
    }
}