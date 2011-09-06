/*
 * Created on May 15, 2005
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package eu.fox7.flt.regex.infer.crx.impl;


import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Base class that implements the ParsingElement interface and does the main
 * work by building the pre-order relation, the parial order as well as the
 * attributes.</p>
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class StructureElement implements ParsingElement {

    /**
     * Number of Examples for this element
     */
    protected int count = 0;
    /**
     * Set of the tuples in the relation between child items, used to construct
     * the equivalence relation afterwards; each call to addToCurrentExample()
     * updates this relation if necessary; must be set to null for each new example
     */
    protected Set<String> relationSet = new HashSet<String>();
    /**
     * Set that holds the alphabet of the Element's content model
     */
    protected Set<String> alphabet = new HashSet<String>();

    /**
     * Method that adds an Example for the element, it updates the alphabet and the
     * pre-order relation as well as the attribute information.
     * @param example
     *            Example to add to the element
     */
    public void addExample(Example example) {
        count++;
        String previousContentItem = null;
        for (Iterator<String> it = example.getContentIterator(); it.hasNext(); ) {
            String contentItem = it.next();
            addDigram(previousContentItem, contentItem);
            previousContentItem = contentItem;
        }
    }

    public void addDigram(String previousContentItem, String contentItem) {
        if (previousContentItem != null) {
            relationSet.add(previousContentItem + " " + contentItem);
            alphabet.add(previousContentItem);
        }
        relationSet.add(contentItem + " " + contentItem);
        alphabet.add(contentItem);
    }

    public Set<String> getAlphabet() {
        return alphabet;
    }

    /**
     * Method that returns the number of examples for this Element's content model
     * @return int number of examples
     */
    public int getExampleCount() {
        return count;
    }

    /**
     * Method that computes and returns the pre-order relation on the 
     * @return Relation
     */
    protected Relation getRelation() {
        return new Relation();
    }

    /**
     * Method that computes and returns the partial order derived from the sample
     * @return PartialOrder
     */
    protected PartialOrder getPartialOrder() {
        return new PartialOrder();
    }

    public String getRegex() {
        StringBuilder str = new StringBuilder();
        if (!alphabet.isEmpty()) {
            List<Set<String>> equivalenceClasses = getPartialOrder().getTopologicalSort();
            if (equivalenceClasses.size() > 1)
                str.append("(. ");
            for (Iterator<Set<String>> it = equivalenceClasses.iterator(); it.hasNext();) {
                Set<String> eClass = it.next();
                String multiplicityStr = computeMultiplicity(eClass);
                String classStr = equivalenceClassToRegex(eClass);
                if (multiplicityStr.length() != 0 && !classStr.equals("#PCDATA"))
                    str.append("(").append(multiplicityStr).append(" ");
                str.append(classStr);
                if (multiplicityStr.length() != 0 && !classStr.equals("#PCDATA"))
                    str.append(")");
                if (it.hasNext()) str.append(" ");
            }
            if (equivalenceClasses.size() > 1)
                str.append(")");
        } else {
            str.append("(EPSILON)");
        }
        return str.toString();
    }

    /**
     * Method that computes the multiplicity of an equivalence class
     * @param eClass
     *            Set equivalence class to determine the multiplicity for
     * @return String multiplicity character
     */
    protected String computeMultiplicity(Set<String> eClass) {
        return "";
    }

    /**
     * Convenience method that represents an equivalence class as a DTD string
     * @param eClass
     *            Set of qNames that forms an equivalence class in the partial order
     * @return String representation
     */
    protected static String equivalenceClassToString(Set<String> eClass) {
        StringBuffer str = new StringBuffer();
        if (eClass.size() > 1) str.append("(");
        List<String> list = new LinkedList<String>(eClass);
        Collections.sort(list);
        for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
            str.append(it.next());
            if (it.hasNext()) str.append(" | ");
        }
        if (eClass.size() > 1) str.append(")");
        return str.toString();
    }

    /**
     * Convenience method that represents an equivalence class as a DTD string
     * @param eClass
     *            Set of qNames that forms an equivalence class in the partial order
     * @return String representation
     */
    protected static String equivalenceClassToRegex(Set<String> eClass) {
        StringBuffer str = new StringBuffer();
        if (eClass.size() > 1) str.append("(| ");
        List<String> list = new LinkedList<String>(eClass);
        Collections.sort(list);
        for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
            str.append("(").append(it.next()).append(")");
            if (it.hasNext()) str.append(" ");
        }
        if (eClass.size() > 1) str.append(")");
        return str.toString();
    }

    /**
     * Method for debugging purposes
     */
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("\trelation:\n");
        for (String tupleStr : relationSet)
            str.append("\t\t").append(tupleStr).append("\n");
        return str.toString();
    }

    /*
     * <p>Class that represents the pre-order relation on the element's content.</p>
     * @author eu.fox7
     */
    public class Relation {

        /**
         * Map that associates an element with the set of elements that come
         * immediately after it in the example strings
         */
        protected Map<String,Set<String>> afterRelationMap = new HashMap<String,Set<String>>();
        /**
         * Map that associates an element with the set of elements that come after it
         * in the example strings, this is the transitive closure of afterRelationMap.
         */
        protected Map<String,Set<String>> afterClosureMap;
        protected Set<Set<String>> equivalenceSets;

        /**
         * Constructor that computes the relations necessary to build the partial
         * order from.
         */
        public Relation() {
            for (String tupleStr : relationSet) {
                String[] str = tupleStr.split(" ");
                if (!afterRelationMap.containsKey(str[0])) {
                    afterRelationMap.put(str[0], new HashSet<String>());
                }
                Set<String> afterSet = afterRelationMap.get(str[0]);
                afterSet.add(str[1]);
            }
            afterClosureMap = computeTransitiveClosure(afterRelationMap);
            equivalenceSets = computeEquivalenceSets();
        }

        /**
         * Method that computes the transitive closre of the given relation using
         * the Warshall algorithm
         * @param relation
         * @return
         */
        protected Map<String,Set<String>> computeTransitiveClosure(Map<String,Set<String>> relation) {
            /* first, copy the given relation so that the original remains intact */
            Map<String,Set<String>> closure = new HashMap<String,Set<String>>();
            for (String element : relation.keySet()) {
                Set<String> set = relation.get(element);
                closure.put(element, new HashSet<String>(set));
            }
            /* now perform Warshall on the copy */
            for (String iElement : closure.keySet()) {
                for (String jElement : closure.keySet()) {
                    Set<String> jSet = closure.get(jElement);
                    if (jSet.contains(iElement))
                        jSet.addAll(closure.get(iElement));
                }
            }
            return closure;
        }

        /**
         * Method to compute the equivalence classes induced by the pre-order
         * relation on the symbols in the sample's examples
         * @return Set of equivalence classes to compute the strict before relation
         *         for
         */
        protected Set<Set<String>> computeEquivalenceSets() {
            Set<Set<String>> equivalenceClasses = new HashSet<Set<String>>();
            Set<String> symbolsDone = new HashSet<String>();
            for (Iterator<String> iIt = this.getAlphabetIterator(); iIt.hasNext(); ) {
                String iElement = iIt.next();
                if (!symbolsDone.contains(iElement)) {
                    Set<String> equivalenceClass = new HashSet<String>();
                    for (Iterator<String> jIt = this.getAlphabetIterator(); jIt.hasNext(); ) {
                        String jElement = jIt.next();
                        if (this.isAfter(iElement, jElement) &&
                                this.isAfter(jElement, iElement)) {
                            equivalenceClass.add(jElement);
                            symbolsDone.add(jElement);
                        }
                    }
                    equivalenceClasses.add(equivalenceClass);
                }
            }
            return equivalenceClasses;
        }

        /**
         * Method that returns an Iterator over all symbols in the relation
         * @return Iterator over the symbols in the relation
         */
        public Iterator<String> getAlphabetIterator() {
            return afterRelationMap.keySet().iterator();
        }

        /**
         * Method that checks whether qName2 comes after qName1 according to the
         * afterClosureMap relation
         * @param qName1
         *            String first qName
         * @param qName2
         *            String second qName
         * @return boolean true if qName2 comes after qName1, false otherwise
         */
        public boolean isAfter(String qName1, String qName2) {
            return afterClosureMap.get(qName1).contains(qName2);
        }
 
        /**
         * Method that checks whether qName2 comes directly after qName1 according
         * to the afterRelationMap relation
         * @param qName1
         *            String first qName
         * @param qName2
         *            String second qName
         * @return boolean true if qName2 comes immediatelhy after qName1,
         *                 false otherwise
         */
        public boolean isImmediatelyAfter(String qName1, String qName2) {
            return afterRelationMap.get(qName1).contains(qName2);
        }

        public Set<Set<String>> getEquivalenceClasses() {
            return equivalenceSets;
        }

        /**
         * Method for debugging purposes
         */
        public String toString() {
            return toDot();
        }

        /**
         * Method for debugging purposes
         */
        public String toDot() {
            return toDot(false);
        }

        /**
         * Method for debugging purposes
         */
        public String toDot(boolean immeditalyAfter) {
            StringBuffer str = new StringBuffer();
            str.append("digraph g\n").append("{\n\n").append("\trankdir=\"LR\"\n\n");
            str.append("\t/* list of nodes */\n");
            for (Iterator<String> it = getAlphabetIterator(); it.hasNext(); ) {
                String element = it.next();
                str.append("\tn").append(element).append(" [shape=\"ellipse\", ");
                str.append("label=\"").append(element).append("\"];\n");
            }
            str.append("\n\t/* list of edges */\n");
            for (Iterator<String> iIt = getAlphabetIterator(); iIt.hasNext(); ) {
                String iElement = iIt.next();
                for (Iterator<String> jIt = getAlphabetIterator(); jIt.hasNext(); ) {
                    String jElement = jIt.next();
                    if (immeditalyAfter) {
                        if (isImmediatelyAfter(iElement, jElement)) {
                            str.append("\tn").append(iElement).append(" -> ");
                            str.append("n").append(jElement).append(";\n");
                        }
                    } else {
                        if (isAfter(iElement, jElement)) {
                            str.append("\tn").append(iElement).append(" -> ");
                            str.append("n").append(jElement).append(";\n");
                        }
                    }
                }
            }
            str.append("}\n");
            return str.toString();
        }

    }

    /**
     * <p> Class to represent the partial order on the equivalence classes derived
     * for the element from the sample during the parsing process </p> 
     * @author eu.fox7
     *
     */
    public class PartialOrder {

        /**
         * Map that encodes the strict after relation by mapping an equivalence class
         * to its immediate successors in the Hasse diagram
         */
        protected Map<Set<String>,Set<Set<String>>> strictAfterRelationMap;
        /**
         * Map that encodes the strict before relation by mapping an equivalence class
         * to its immediate predecessors in the Hasse diagram
         */
        protected Map<Set<String>,Set<Set<String>>> strictBeforeRelationMap;
        /**
         * Set of equivalence classes that have no predecessors in the Hasse diagram
         */
        protected Set<Set<String>> rootClasses;
        /**
         * Set of equivalence classes
         */
        protected Set<Set<String>> equivalenceClasses;
 
        /**
         * Constructor that computes the partial order for the element's content model
         * as derived from the sample's examples
         *
         */
        public PartialOrder() {
            Relation relation = getRelation();
            Set<Set<String>> preOrderEquivalenceClasses = relation.getEquivalenceClasses();
            strictAfterRelationMap = computeStrictAfterRelation(relation,
                                                                preOrderEquivalenceClasses);
            strictBeforeRelationMap = computeStrictBeforeRelation(relation,
                                                                  preOrderEquivalenceClasses);
            equivalenceClasses = computeFolding(preOrderEquivalenceClasses);
            strictAfterRelationMap = computeStrictAfterRelation(relation,
                                                                equivalenceClasses);
            strictBeforeRelationMap = computeStrictBeforeRelation(relation,
                                                                  equivalenceClasses);
            rootClasses = computeRootClasses(equivalenceClasses,
                                             strictBeforeRelationMap);
        }

        /**
         * Method that determines whether the second Set comes strictly after the
         * first according to the relation on the symbols.  A set comes strictly
         * immediately after another if the former contains an element that
         * isImmediatelyAfter an element of the latter. 
         * @param relation
         *            Relation that expresses the pre-order of the symbols in
         *            the examples
         * @param set1
         *            first Set to compare 
         * @param set2
         *            second Set to compare 
         * @return boolean true if set2 comes immediately after set1, false otherwise
         */
        protected boolean isImmediatelyAfter(Relation relation,
                                             Set<String> set1, Set<String> set2) {
            for (String iElement : set1)
                for (String jElement : set2)
                    if (relation.isImmediatelyAfter(iElement, jElement) &&
                            !iElement.equals(jElement)) {
                        return true;
                    }
            return false;
        }

        /**
         * Method to compute the strict after relation between equivalence classes,
         * i.e. the Hasse diagram
         * @param relation
         *            Relation that expresses the pre-order of the symbols in
         *            the examples
         * @param equivalenceClasses
         *            Set of equivalence classes to compute the strict after relation
         *            for     
         * @return Map that maps an equivalence class (Set) to the Set of
         *         equivalence classes that come strictly after it as defined by
         *         the isImmediatelyAfter method
         */
        protected Map<Set<String>,Set<Set<String>>> computeStrictAfterRelation(Relation relation,
                                                                               Set<Set<String>> equivalenceClasses) {
            Map<Set<String>,Set<Set<String>>> strictAfterRelationMap = new HashMap<Set<String>,Set<Set<String>>>();
            for (Set<String> iEquivalenceClass : equivalenceClasses) {
                Set<Set<String>> afterSet = new HashSet<Set<String>>();
                strictAfterRelationMap.put(iEquivalenceClass, afterSet);
                for (Set<String> jEquivalenceClass : equivalenceClasses) {
                    if (isImmediatelyAfter(relation, iEquivalenceClass,
                                           jEquivalenceClass) &&
                        !iEquivalenceClass.equals(jEquivalenceClass)) {
                        afterSet.add(jEquivalenceClass);
                    }
                }
            }
            return strictAfterRelationMap;
        }

        /**
         * Method to compute the strict before relation between equivalence classes,
         * i.e. the Hasse diagram
         * @param relation
         *            Relation that expresses the pre-order of the symbols in
         *            the examples
         * @param equivalenceClasses
         *            Set of equivalence classes to compute the strict before relation
         *            for     
         * @return Map that maps an equivalence class (Set) to the Set of
         *         equivalence classes that come strictly before it as defined by
         *         the isImmediatelyAfter method
         */
        protected Map<Set<String>,Set<Set<String>>> computeStrictBeforeRelation(Relation relation,
                                                                                Set<Set<String>> equivalenceClasses) {
            Map<Set<String>,Set<Set<String>>> strictBeforeRelationMap = new HashMap<Set<String>,Set<Set<String>>>();
            for (Set<String> iEquivalenceClass : equivalenceClasses) {
                Set<Set<String>> beforeSet = new HashSet<Set<String>>();
                strictBeforeRelationMap.put(iEquivalenceClass, beforeSet);
                for (Set<String> jEquivalenceClass : equivalenceClasses) {
                    if (isImmediatelyAfter(relation, jEquivalenceClass,
                                           iEquivalenceClass) &&
                        !iEquivalenceClass.equals(jEquivalenceClass)) {
                        beforeSet.add(jEquivalenceClass);
                    }
                }
            }
            return strictBeforeRelationMap;
        }

        /**
         * Method that computes the sett of root classes, i.e. those classes that
         * have no predecessors in the Hasse diagram
         * @param equivalenceClasses
         *            Set of equivalence classes to compute the strict before relation
         *            for
         * @param strictBeforeRelation
         *            Map that maps an equivalence class (Set) to the Set of
         *            equivalence classes that come strictly before it as defined by
         *            the isImmediatelyAfter method             
         * @return Set of equivalence classes that have no predecessors in the Hasse
         *         diagram
         */
        protected Set<Set<String>> computeRootClasses(Set<Set<String>> equivalenceClasses,
                                                      Map<Set<String>,Set<Set<String>>> strictBeforeRelationMap) {
            Set<Set<String>> rootClasses = new HashSet<Set<String>>();
            for (Set<String> equivalenceClass : equivalenceClasses)
                if (strictBeforeRelationMap.get(equivalenceClass).isEmpty())
                    rootClasses.add(equivalenceClass);
            return rootClasses;
        }

        /**
         * Method that computes the folding for the original set of equivalence
         * classes.  Classes will be folded together when they have the same set of
         * successors and predecessors.
         * @param equivalenceClasses
         *            Set of equivalence classes to fold
         * @return Set of equivalence classes
         */
        protected Set<Set<String>> computeFolding(Set<Set<String>> equivalenceClasses) {
            Set<Set<String>> foldedClasses = new HashSet<Set<String>>();
            Set<Set<String>> classesDone = new HashSet<Set<String>>();
            for (Set<String> iClass : equivalenceClasses) {
                if (!classesDone.contains(iClass)) {
                    Set<String> newClass = new HashSet<String>(iClass);
                    foldedClasses.add(newClass);
                    for (Set<String> jClass : equivalenceClasses) {
                        if (getPredecessorSet(iClass).equals(getPredecessorSet(jClass)) &&
                                getSuccessorSet(iClass).equals(getSuccessorSet(jClass))) {
                            newClass.addAll(jClass);
                            classesDone.add(jClass);
                        }
                    }
                }
            }
            return foldedClasses;
        }

        /**
         * Method to access the root equivalence classes of the partial order
         * @return Iterator over the set of root classes (Sets)
         */
        public Iterator<Set<String>> getRootClassIterator() {
            return rootClasses.iterator();
        }

        /**
         * Method that returns an Iterator over the equivalence classes in the
         * PartialOrder
         * @return Iterator over the equivalence classes
         */
        protected Iterator<Set<String>> getClassIterator() {
            return equivalenceClasses.iterator();
        }

        /**
         * Method that returns the Set of successors of the given equivalence class
         * @param equivalenceClass
         *            Set equivalence class
         * @return Set conttaining the equivalence classes that come immediately after
         *         the one specified
         */
        public Set<Set<String>> getSuccessorSet(Set<String> equivalenceClass) {
            return strictAfterRelationMap.get(equivalenceClass);
        }

        /**
         * Method that returns the Set of predecessors of the given equivalence class
         * @param equivalenceClass
         *            Set equivalence class
         * @return Set conttaining the equivalence classes that come immediately before
         *         the one specified
         */
        public Set<Set<String>> getPredecessorSet(Set<String> equivalenceClass) {
            return strictBeforeRelationMap.get(equivalenceClass);
        }

        /**
         * Method that computes the topological sort for the partial order.
         * @return List equivalence classes ordered according to the topological sort
         */
        public List<Set<String>> getTopologicalSort() {
            List<Set<String>> sortedList = new LinkedList<Set<String>>();
            LinkedList<Set<String>> toDoList = new LinkedList<Set<String>>();
            Set<Set<String>> doneSet = new HashSet<Set<String>>();
            for (Iterator<Set<String>> it = getRootClassIterator(); it.hasNext(); ) {
                toDoList.addLast(it.next());
            }
            while (!toDoList.isEmpty()) {
                Set<String> eClass = toDoList.removeLast();
                if (!doneSet.contains(eClass)) {
                    sortedList.add(eClass);
                    doneSet.add(eClass);
                    for (Set<String> succEClass : getSuccessorSet(eClass)) {
                        if (!doneSet.contains(succEClass)) {
                            if (getPredecessorSet(succEClass).size() == 1) {
                                toDoList.addLast(succEClass);
                            } else {
                                toDoList.addFirst(succEClass);
                            }
                        }
                    }
                }
            }
            return sortedList;
        }

        /**
         * Method returning the dot representation of the partial order for debugging
         * purposes
         * @return String dot representation
         */
        public String toDot() {
            StringBuffer str = new StringBuffer();
            Map<Set<String>,Integer> classIdMap = new HashMap<Set<String>,Integer>();
            int id = 0;
            str.append("digraph g\n").append("{\n\n").append("\trankdir=\"LR\"\n\n");
            str.append("\t/* list of nodes */\n");
            for (Set<String> equivalenceClass : strictAfterRelationMap.keySet()) {
                classIdMap.put(equivalenceClass, ++id);
                str.append("\tn").append(id).append(" [shape=\"ellipse\", ");
                str.append("label=\"").append(equivalenceClass.toString()).append("\"];\n");
            }
            str.append("\n\t/* list of edges */\n");
            for (Set<String> iEquivalenceClass : strictAfterRelationMap.keySet()) {
                Set<Set<String>> afterSet = strictAfterRelationMap.get(iEquivalenceClass);
                for (Set<String> jEquivalenceClass : afterSet) {
                    str.append("\tn").append(classIdMap.get(iEquivalenceClass).toString());
                    str.append(" -> ");
                    str.append("n").append(classIdMap.get(jEquivalenceClass).toString());
                    str.append(";\n");
                }
            }
            str.append("}\n");
            return str.toString();            
        }

    }

}
