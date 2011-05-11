/**
 * This file is part of BonXai.
 *
 * BonXai is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BonXai is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.tudortmund.cs.bonxai.utils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.utils.exceptions.RecursiveContentModelException;
import de.tudortmund.cs.bonxai.utils.exceptions.UPAViolationException;
import de.tudortmund.cs.bonxai.xsd.ElementRef;

/**
 *  This class can be used to check the Unique Particle Attribution constraint
 *  for a single content model (particle).
 *
 *  After a constructor call with a particle, performing checkDeterminism()
 *  will throw a UPAViolationException if the content model is ambiguous.
 *
 *  The constructor call itself will throw a RecursiveContentModelException
 *  if there is a group which contains a group reference to itself.
 */
public class ContentModelValidator {

    /**
     * A Transition consists of a from and to integer representing states and
     * a transition label (either a element name or a any element namespace
     * plus a position suffix.
     */
    protected class Transition {
        int from, to;
        String label;

        public Transition(int from, String s, int to) {
            this.from = from;
            label = s;
            this.to = to;
        }

        public int getFrom() {
            return from;
        }

        public int getTo() {
            return to;
        }

        public String getLabel() {
            return label;
        }
    }

    /**
     * lastID shows the ID of the last created state.
     */
    private int lastID;

    /**
     * Represents the automaton created by translateParticleToFSA().
     */
    private LinkedList<Transition> transitions;

    /**
     * Initial state of the automaton created by translateParticleToFSA().
     */
    private int start;

    /**
     * Constructor with particle. Throws a RecursiveContentModelException if
     * there is a group defintion which contains a group reference to itself.
     *
     * @param particle
     * @throws RecursiveContentModelException
     */
    public ContentModelValidator(Particle particle) throws RecursiveContentModelException {
        lastID = 0;
        lastNumber = 0;
        transitions = new LinkedList<Transition>();
        start = translateParticleToFSA(++lastID, particle, new LinkedList<String>());
        determinize();
    }

    /**
     * Translates a particle to a FSA ending at state s. Returns the initial
     * state of this FSA.
     *
     * @param s
     * @param particle
     * @return
     * @throws RecursiveContentModelException
     */
    protected int translateParticleToFSA(int s, Particle particle, LinkedList<String> foundGroups) throws RecursiveContentModelException {
        lastNumber++;
        int b = 0;
        int min;
        int max;

        if (particle instanceof CountingPattern) {
            CountingPattern countingPattern = (CountingPattern) particle;
            min = countingPattern.getMin();

            // maxOccurs = unbounded
            if (countingPattern.getMax() == null) {
                int t = ++lastID;
                b = translateTermToFSA(t, countingPattern.getParticles().getFirst(), foundGroups);
                transitions.add(new Transition(t, "epsilon", b));
                transitions.add(new Transition(b, "epsilon", s));
                s = b;

                // maxOccurs != unbounded
            } else {
                max = countingPattern.getMax();
                for (int i = 0; i < max-min; i++) {
                    b = translateTermToFSA(s, countingPattern.getParticles().getFirst(), foundGroups);
                    transitions.add(new Transition(b, "epsilon", s));
                    s = b;
                }
            }

            for (int i = 0; i < min; i++) {
                b = translateTermToFSA(s, countingPattern.getParticles().getFirst(), foundGroups);
                s = b;
            }

            // !(particle instanceof CountingPattern)
        } else {
            b = translateTermToFSA(s, particle, foundGroups);
        }

        return b;
    }

    /**
     * Last position number used for a transition suffix.
     */
    private int lastNumber;

    /**
     * Translates a term (AnyPattern, AllPattern, Element, ElementRef, ChoicePattern or
     * SequencePattern to a FSA ending at state s. Returns the initial state
     * of this FSA.
     *
     * @param s
     * @param particle
     * @return
     * @throws RecursiveContentModelException
     */
    protected int translateTermToFSA(int s, Particle particle, LinkedList<String> fG) throws RecursiveContentModelException {
        int b = ++lastID;
        LinkedList<String> foundGroups = new  LinkedList<String>();
        foundGroups.addAll(fG);

        if (particle instanceof AnyPattern) {
            AnyPattern anyPattern = (AnyPattern) particle;
            String label = anyPattern.getNamespace() + "_" + lastNumber;
            transitions.add(new Transition(b, label, s));

        } else  if (particle instanceof Element) {
            Element element = (Element) particle;
            String label = element.getName() + "_" + lastNumber;
            transitions.add(new Transition(b, label, s));

        } else  if (particle instanceof ElementRef) {
            ElementRef elementRef = (ElementRef) particle;
            String label = elementRef.getElement().getName() + "_" + lastNumber;
            transitions.add(new Transition(b, label, s));

        } else  if (particle instanceof ChoicePattern) {
            ChoicePattern choicePattern = (ChoicePattern) particle;
            LinkedList<Particle> pl = choicePattern.getParticles();
            while (!pl.isEmpty())
            {
                transitions.add(new Transition(b, "epsilon", translateParticleToFSA(s, pl.poll(), foundGroups)));
            }

        } else  if (particle instanceof SequencePattern) {
            SequencePattern sequencePattern = (SequencePattern) particle;
            LinkedList<Particle> pl = sequencePattern.getParticles();
            int intermediate = 0;
            while (!pl.isEmpty())
            {
                intermediate = translateParticleToFSA(s, pl.pollLast(), foundGroups);

                s = intermediate;
            }
            if (intermediate!=0) transitions.add(new Transition(b, "epsilon", intermediate));

        } else  if (particle instanceof AllPattern) {
            AllPattern allPattern = (AllPattern) particle;
            ChoicePattern choicePattern = resolveAllPattern(allPattern);
            LinkedList<Particle> pl = choicePattern.getParticles();
            while (!pl.isEmpty())
            {
                transitions.add(new Transition(b, "epsilon", translateParticleToFSA(s, pl.poll(), foundGroups)));
            }

        } else  if (particle instanceof GroupRef) {
            GroupRef groupRef = (GroupRef) particle;
            Group group = groupRef.getGroup();
            if (!foundGroups.contains(group.getName())) {
                foundGroups.add(group.getName());
                if (group.getParticleContainer() != null) translateParticleToFSA(s, group.getParticleContainer(), foundGroups);
            } else if (group instanceof de.tudortmund.cs.bonxai.xsd.Group) {
                de.tudortmund.cs.bonxai.xsd.Group g = (de.tudortmund.cs.bonxai.xsd.Group) group;
                throw new RecursiveContentModelException("\"" + g.getNamespace() + "\":" + g.getLocalName());
            } else throw new RecursiveContentModelException(group.getName());
        }

        return b;
    }

    /**
     * Transforms all pattern into a semantically equivalent choice pattern.
     *
     * @param allPattern
     * @return
     */
    protected ChoicePattern resolveAllPattern(AllPattern allPattern) {
        LinkedList<Particle> l = new LinkedList<Particle>();
        resolveAllPatternHelper(null, allPattern.getParticles(), l);
        ChoicePattern choicePattern = new ChoicePattern();
        while (!l.isEmpty()) {
            choicePattern.addParticle(l.pollFirst());
        }
        return choicePattern;
    }

    protected void resolveAllPatternHelper(LinkedList<Particle> parent, LinkedList<Particle> r, LinkedList<Particle> l) {
        if (r.size() == 2) {
            LinkedList<Particle> l1 = new LinkedList<Particle>();
            if (parent!=null) l1.addAll(parent);
            l1.addAll(r);
            LinkedList<Particle> l2 = new LinkedList<Particle>();
            SequencePattern r1 = new SequencePattern();
            while (!l1.isEmpty()) {
                r1.addParticle(l1.pollFirst());
            }
            if (parent!=null) l2.addAll(parent);
            l2.add(r.getLast());
            l2.add(r.getFirst());
            SequencePattern r2 = new SequencePattern();
            while (!l2.isEmpty()) {
                r2.addParticle(l2.pollFirst());
            }
            l.add( r1 );
            l.add( r2 );
        } else {
            for (int i=0; i<r.size(); i++) {
                LinkedList<Particle> l1 = new LinkedList<Particle>();
                if (parent!=null) l1.addAll(parent);
                l1.add(r.get(i));
                LinkedList<Particle> l2 = new LinkedList<Particle>();
                l2.addAll(r);
                l2.remove(i);
                resolveAllPatternHelper(l1,l2, l);
            }
        }
    }

    /**
     * Returns the epsilon closure of state s.
     *
     * @param s
     * @return
     */
    protected TreeSet<Integer> getEpsilonClosure(int s) {
        TreeSet<Integer> ecStates = new TreeSet<Integer>();
        ecStates.add(s);
        Iterator<Transition> it = transitions.iterator();
        while (it.hasNext()) {
            Transition t = it.next();
            if (t.from == s && t.label == "epsilon") {
                ecStates.addAll(getEpsilonClosure(t.to));
            }
        }
        return ecStates;
    }

    /**
     * Returns the epsilon closure of states in tree set sl.
     *
     * @param sl
     * @return
     */
    protected TreeSet<Integer> getEpsilonClosure(TreeSet<Integer> sl) {
        TreeSet<Integer> ecStates = new TreeSet<Integer>();
        Iterator<Integer> it = sl.iterator();
        while (it.hasNext()) {
            ecStates.addAll(getEpsilonClosure(it.next()));
        }
        return ecStates;
    }

    /**
     * Returns the outgoing transition labels (!= null) from state s.
     * @param s
     * @return
     */
    protected LinkedList<String> getTransitions(TreeSet<Integer> s) {
        LinkedList<String> tl = new LinkedList<String>();
        Iterator<Transition> it = transitions.iterator();
        while (it.hasNext()) {
            Transition t = it.next();
            if (s.contains(t.from) && t.label != "epsilon" && !tl.contains(t.label)) {
                tl.add(t.label);
            }
        }
        return tl;
    }

    /**
     * Represents the automaton created by collapseTransitions().
     */
    protected LinkedList<Transition> newTransitions;

    /**
     * Stores the mapping from sets of states in "transitions" to states in "newTransitions".
     */
    private HashMap<TreeSet<Integer>, Integer> idMap;

    /**
     * ID of the initial state of the determinized automaton.
     */
    private int newStart = 0;

    /**
     * Takes the automaton represented by "transitions" and determinizes it.
     * This new automaton is stored in "newTransitions".
     */
    protected void determinize() {
        newTransitions = new LinkedList<Transition>();
        newStart = ++lastID;
        idMap = new HashMap<TreeSet<Integer>, Integer>();
        TreeSet<Integer> ecFromStart = getEpsilonClosure(start);
        idMap.put(ecFromStart, newStart);
        determinizeHelper(ecFromStart);
    }

    protected void determinizeHelper(TreeSet<Integer> unmarked) {
        Iterator<String> it = getTransitions(unmarked).iterator();
        while (it.hasNext()) {
            String p = it.next();
            Iterator<Transition> it2 = transitions.iterator();
            TreeSet<Integer> pClosure = new TreeSet<Integer>();
            while (it2.hasNext()) {
                Transition t = it2.next();
                if (unmarked.contains(t.from) && t.label == p) {
                    pClosure.addAll(getEpsilonClosure(t.to));
                }
            }
            Integer u = idMap.get(pClosure);
            boolean b = false;
            if (u == null) {
                b = true;
                u = ++lastID;
                idMap.put(pClosure, u);
            }
            newTransitions.add(new Transition(idMap.get(unmarked), p, u));
            if (b) determinizeHelper(pClosure);

        }
    }

    /**
     * For easier debugging. Returns all transitions before and after
     * collapsing epsilon transitions in the form "<from> --<label>-> <to>".
     *
     * @return
     */
    public String transitionsToString() {
        String s = "";
        s+="Before (s0="+ start + "):\n";
        Iterator<Transition> it = transitions.iterator();
        while (it.hasNext()) {
            Transition t = it.next();
            s+=t.from + " --" + t.label + "-> " + t.to + "\n";
        }

        s+="After (s0="+ newStart + "):\n";
        Iterator<Transition> it2 = newTransitions.iterator();
        while (it2.hasNext()) {
            Transition t = it2.next();
            s+=t.from + " --" + t.label + "-> " + t.to + "\n";
        }
        return s;
    }

    /**
     * Throws an exception if the particle passed in the constructor violates
     * the Unique Particle Constraint.
     *
     * The determinized automaton is checked for states with two or more
     * identical outgoing transitions, or a transition and a wildcard
     * transition (any) with the same namespace, or two wildcard transitions
     * with the same namespace. The created position suffix is ignored in
     * these comparisons.
     *
     * @throws UPAViolationException
     */
    public void checkDeterminism() throws UPAViolationException {
        while (!newTransitions.isEmpty()) {
            Transition t = newTransitions.poll();
            Iterator<Transition> it = newTransitions.iterator();
            while (it.hasNext()) {
                Transition u = it.next();
                if (t.from == u.from) {
                    String a = t.label.substring(0, t.label.lastIndexOf("_"));
                    String b = u.label.substring(0, u.label.lastIndexOf("_"));
                    if (!a.startsWith("{") && !b.startsWith("{")) {
                        if (a.equals(b)) throw new UPAViolationException(("Wildcard for namespace \"" + a + "\""), ("wildcard for namespace \"" + b + "\""));
                    } else if (a.startsWith("{") && b.startsWith("{")) {
                        if (a.equals(b)) throw new UPAViolationException(("Element \""+ a.substring(1, a.lastIndexOf("}")) + "\":" + a.substring(a.lastIndexOf("}") + 1)), ("element \""+ b.substring(1, b.lastIndexOf("}")) + "\":" + b.substring(b.lastIndexOf("}") + 1)));
                    } else if (!a.startsWith("{") && b.startsWith("{")) {
                        String namespaceB = b.substring(1, b.lastIndexOf("}"));
                        if (a.equals(namespaceB)) throw new UPAViolationException(("Element \""+ namespaceB + "\":" + b.substring(b.lastIndexOf("}") + 1)), ("wildcard for namespace \"" + a + "\""));
                    } else if (a.startsWith("{") && !b.startsWith("{")) {
                        String namespaceA = a.substring(1, a.lastIndexOf("}"));
                        if (b.equals(namespaceA)) throw new UPAViolationException(("Element \""+ namespaceA + "\":" + a.substring(a.lastIndexOf("}") + 1)), ("wildcard for namespace \"" + b + "\""));
                    }
                }
            }
        }
    }
}
