package de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons;

import de.tudortmund.cs.bonxai.common.AnyPattern;
import de.tudortmund.cs.bonxai.common.ChoicePattern;
import de.tudortmund.cs.bonxai.common.Particle;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.automaton.State;
import de.tudortmund.cs.bonxai.xsd.automaton.Transition;
import de.tudortmund.cs.bonxai.xsd.parser.XSDParser;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>TypeAutomatonFactory</tt> class, checks that every
 * method of this class performs properly.
 * @author Dominik Wolff
 */
public class TypeAutomatonFactoryTest extends junit.framework.TestCase {

    public TypeAutomatonFactoryTest() {
    }

    /**
     * Test of buildTypeAutomaton method, of class TypeAutomatonFactory.
     */
    @Test
    public void testBuildTypeAutomaton_Schema_HashMap() throws Exception {
        XSDParser xmlSchemaParser = new XSDParser(false, false);
        XSDSchema schema = xmlSchemaParser.parse(this.getClass().getResource("/tests/de/tudortmund/cs/bonxai/xsd/automaton/TypeAutomatons/xsds/testBuildTypeAutomaton_Schema_HashMap.xsd").getFile());
        HashMap<AnyPattern, XSDSchema> anyPatternSchemaMap = new HashMap<AnyPattern, XSDSchema>();
        anyPatternSchemaMap.put((AnyPattern) ((ChoicePattern) ((ComplexContentType) ((ComplexType) schema.getTypeSymbolTable().getReference("{A}complexTypeD").getReference()).getContent()).getParticle()).getParticles().getFirst(), schema);
        TypeAutomatonFactory instance = new TypeAutomatonFactory();

        TypeAutomaton result = instance.buildTypeAutomaton(schema, anyPatternSchemaMap);

        for (TypeState typeState: result.getStates()) {
            if (typeState.toString().equals("sink")) {
                assertTrue(typeState.getInTransitions().isEmpty());
                assertTrue(typeState.getOutTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
            }
            if (typeState.toString().equals("complexTypeA")) {
                assertTrue(typeState.getInTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().size() == 3);
                counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeB", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementB", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementB").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeC", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 3) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
            }
            if (typeState.toString().equals("complexTypeB")) {
                assertTrue(typeState.getInTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeB", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeB", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().size() == 1);
                counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
            }

            if (typeState.toString().equals("complexTypeC")) {
                assertTrue(typeState.getInTransitions().size() == 1);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementB", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementB").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeC", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().size() == 1);
                counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeB", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
            }

            if (typeState.toString().equals("complexTypeD")) {
                assertTrue(typeState.getInTransitions().size() == 3);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 3) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().size() == 2);
                counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
            }
            if (typeState.toString().equals("string")) {
                assertTrue(typeState.getInTransitions().size() == 1);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().isEmpty());
            }
        }
    }

    /**
     * Test of buildTypeAutomaton method, of class TypeAutomatonFactory.
     */
    @Test
    public void testBuildTypeAutomaton_Particle_HashMap() throws Exception {
        XSDParser xmlSchemaParser = new XSDParser(false, false);
        XSDSchema schema = xmlSchemaParser.parse(this.getClass().getResource("/tests/de/tudortmund/cs/bonxai/xsd/automaton/TypeAutomatons/xsds/testBuildTypeAutomaton_Schema_HashMap.xsd").getFile());
        HashMap<AnyPattern, XSDSchema> anyPatternSchemaMap = new HashMap<AnyPattern, XSDSchema>();
        anyPatternSchemaMap.put((AnyPattern) ((ChoicePattern) ((ComplexContentType) ((ComplexType) schema.getTypeSymbolTable().getReference("{A}complexTypeD").getReference()).getContent()).getParticle()).getParticles().getFirst(), schema);
        TypeAutomatonFactory instance = new TypeAutomatonFactory();

        Particle particle = ((ComplexContentType) ((ComplexType) schema.getTypeSymbolTable().getReference("{A}complexTypeA").getReference()).getContent()).getParticle();

        TypeAutomaton result = instance.buildTypeAutomaton(particle, anyPatternSchemaMap);

        for (TypeState typeState: result.getStates()) {
            if (typeState.toString().equals("sink")) {
                assertTrue(typeState.getInTransitions().isEmpty());
                assertTrue(typeState.getOutTransitions().size() == 3);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeB", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementB", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementB").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeC", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 3) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
            }
            if (typeState.toString().equals("complexTypeA")) {
                assertTrue(typeState.getInTransitions().size() == 1);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().size() == 3);
                counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeB", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementB", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementB").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeC", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 3) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
            }
            if (typeState.toString().equals("complexTypeB")) {
                assertTrue(typeState.getInTransitions().size() == 3);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeB", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeB", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 3) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeB", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().size() == 1);
                counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
            }
            if (typeState.toString().equals("complexTypeC")) {
                assertTrue(typeState.getInTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementB", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementB").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeC", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 3) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementB", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementB").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeC", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().size() == 1);
                counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeB", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
            }
            if (typeState.toString().equals("complexTypeD")) {
                assertTrue(typeState.getInTransitions().size() == 3);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 3) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().size() == 2);
                counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
            }
            if (typeState.toString().equals("string")) {
                assertTrue(typeState.getInTransitions().size() == 1);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().isEmpty());
            }
        }
    }

    /**
     * Test of buildProductTypeAutomaton method, of class TypeAutomatonFactory.
     */
    @Test
    public void testBuildProductTypeAutomaton() throws Exception {
        XSDParser xmlSchemaParser = new XSDParser(false, false);
        XSDSchema schema1 = xmlSchemaParser.parse(this.getClass().getResource("/tests/de/tudortmund/cs/bonxai/xsd/automaton/TypeAutomatons/xsds/testBuildTypeAutomaton_Schema_HashMap.xsd").getFile());
        XSDSchema schema2 = xmlSchemaParser.parse(this.getClass().getResource("/tests/de/tudortmund/cs/bonxai/xsd/automaton/TypeAutomatons/xsds/testBuildSubsetTypeAutomaton.xsd").getFile());
        HashMap<AnyPattern, XSDSchema> anyPatternSchemaMap = new HashMap<AnyPattern, XSDSchema>();
        anyPatternSchemaMap.put((AnyPattern) ((ChoicePattern) ((ComplexContentType) ((ComplexType) schema1.getTypeSymbolTable().getReference("{A}complexTypeD").getReference()).getContent()).getParticle()).getParticles().getFirst(), schema1);
        TypeAutomatonFactory instance = new TypeAutomatonFactory();
        TypeAutomaton typeAutomaton1 = instance.buildTypeAutomaton(schema1, anyPatternSchemaMap);
        TypeAutomaton typeAutomaton2 = instance.buildSubsetTypeAutomaton(instance.buildTypeAutomaton(schema2, anyPatternSchemaMap));
        LinkedList<TypeAutomaton> typeAutomatons = new LinkedList<TypeAutomaton>();
        typeAutomatons.add(typeAutomaton1);
        typeAutomatons.add(typeAutomaton2);

        ProductTypeAutomaton result = instance.buildProductTypeAutomaton(typeAutomatons);



        for (TypeState typeState: result.getStates()) {
            if (typeState.toString().equals("(sink,(sink))")) {
                assertTrue(typeState.getInTransitions().isEmpty());
                assertTrue(typeState.getOutTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 2);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[1]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[1]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[1]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
            }
            if (typeState.toString().equals("(complexTypeA,(complexTypeA))")) {
                assertTrue(typeState.getInTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 2);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[1]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[1]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[1]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 2);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[1]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[1]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[1]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().size() == 4);
                counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeB", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementB", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementB").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeC", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 3) {
                        assertTrue(transition.getElements().size() == 3);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[1]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[1]));
                        assertEquals("{A}complexTypeD1", ((Element) transition.getElements().toArray()[1]).getType().getName());
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[2]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[2]));
                        assertEquals("{A}complexTypeD2", ((Element) transition.getElements().toArray()[2]).getType().getName());
                    }
                    if (counter == 4) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementB", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementB").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeC", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
            }
            if (typeState.toString().equals("(complexTypeD,sink)")) {
                assertTrue(typeState.getInTransitions().size() == 3);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 3) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().size() == 2);
                counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
            }
            if (typeState.toString().equals("(complexTypeA,sink)")) {
                assertTrue(typeState.getInTransitions().size() == 1);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().size() == 3);
                counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeB", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementB", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementB").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeC", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 3) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
            }
            if (typeState.toString().equals("(complexTypeB,sink)")) {
                assertTrue(typeState.getInTransitions().size() == 3);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeB", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeB", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 3) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeB", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().size() == 1);
                counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
            }
            if (typeState.toString().equals("(complexTypeC,sink)")) {
                assertTrue(typeState.getInTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementB", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementB").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeC", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementB", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementB").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeC", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().size() == 1);
                counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeB", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
            }
            if (typeState.toString().equals("(complexTypeD,(complexTypeD1,complexTypeD2))")) {
                assertTrue(typeState.getInTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 3);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[1]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[1]));
                        assertEquals("{A}complexTypeD1", ((Element) transition.getElements().toArray()[1]).getType().getName());
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[2]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[2]));
                        assertEquals("{A}complexTypeD2", ((Element) transition.getElements().toArray()[2]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 3);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[1]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[1]));
                        assertEquals("{A}complexTypeD2", ((Element) transition.getElements().toArray()[1]).getType().getName());
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[2]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[2]));
                        assertEquals("{A}complexTypeD1", ((Element) transition.getElements().toArray()[2]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().size() == 2);
                counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 2);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[1]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[1]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[1]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 3);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD", ((Element) transition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[1]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[1]));
                        assertEquals("{A}complexTypeD2", ((Element) transition.getElements().toArray()[1]).getType().getName());
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[2]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[2]));
                        assertEquals("{A}complexTypeD1", ((Element) transition.getElements().toArray()[2]).getType().getName());
                    }
                }
            }
            if (typeState.toString().equals("(sink,(complexTypeC))")) {
                assertTrue(typeState.getInTransitions().size() == 1);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementB", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementB").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeC", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().isEmpty());
            }
            if (typeState.toString().equals("(string,sink)")) {
                assertTrue(typeState.getInTransitions().size() == 1);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().isEmpty());
            }
        }
    }

    /**
     * Test of buildSubsetTypeAutomaton method, of class TypeAutomatonFactory.
     */
    @Test
    public void testBuildSubsetTypeAutomaton() throws Exception {
        XSDParser xmlSchemaParser = new XSDParser(false, false);
        XSDSchema schema = xmlSchemaParser.parse(this.getClass().getResource("/tests/de/tudortmund/cs/bonxai/xsd/automaton/TypeAutomatons/xsds/testBuildSubsetTypeAutomaton.xsd").getFile());
        HashMap<AnyPattern, XSDSchema> anyPatternSchemaMap = new HashMap<AnyPattern, XSDSchema>();
        TypeAutomatonFactory instance = new TypeAutomatonFactory();
        TypeAutomaton typeAutomaton = instance.buildTypeAutomaton(schema, anyPatternSchemaMap);

        SubsetTypeAutomaton result = instance.buildSubsetTypeAutomaton(typeAutomaton);

        for (TypeState typeState: result.getStates()) {
            if (typeState.toString().equals("(sink)")) {
                assertTrue(typeState.getInTransitions().isEmpty());
                assertTrue(typeState.getOutTransitions().size() == 1);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
            }
            if (typeState.toString().equals("(complexTypeA)")) {
                assertTrue(typeState.getInTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().size() == 2);
                counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementB", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementB").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeC", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 2);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD1", ((Element) transition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[1]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[1]));
                        assertEquals("{A}complexTypeD2", ((Element) transition.getElements().toArray()[1]).getType().getName());
                    }
                }
            }
            if (typeState.toString().equals("(complexTypeC)")) {
                assertTrue(typeState.getInTransitions().size() == 1);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementB", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementB").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeC", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().isEmpty());
            }
            if (typeState.toString().equals("(complexTypeD1,complexTypeD2)")) {
                assertTrue(typeState.getInTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = typeState.getInTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 2);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD1", ((Element) transition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[1]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[1]));
                        assertEquals("{A}complexTypeD2", ((Element) transition.getElements().toArray()[1]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 2);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD2", ((Element) transition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[1]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[1]));
                        assertEquals("{A}complexTypeD1", ((Element) transition.getElements().toArray()[1]).getType().getName());
                    }
                }
                assertTrue(typeState.getOutTransitions().size() == 2);
                counter = 0;

                for (Iterator<Transition> it2 = typeState.getOutTransitions().iterator(); it2.hasNext();) {
                    Transition transition = it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(transition.getElements().size() == 1);
                        assertEquals("{A}elementA", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementA").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeA", ((Element) transition.getElements().toArray()[0]).getType().getName());
                    }
                    if (counter == 2) {
                        assertTrue(transition.getElements().size() == 2);
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[0]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[0]));
                        assertEquals("{A}complexTypeD2", ((Element) transition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}elementC", ((Element) transition.getElements().toArray()[1]).getName());
                        assertTrue(transition.getNameElementMap().get("{A}elementC").contains((Element) transition.getElements().toArray()[1]));
                        assertEquals("{A}complexTypeD1", ((Element) transition.getElements().toArray()[1]).getType().getName());
                    }
                }
            }
        }
    }
}