package de.tudortmund.cs.bonxai.xsd.tools;

import de.tudortmund.cs.bonxai.xsd.*;
import java.util.*;

/**
 * In order to intersect SimpleTypes it is needed to know all restrictions of this
 * simpleType. SimpleTypes are resticted through so called facets. These are saved
 * in this class as well as information about list inheritance.
 * @author Dominik Wolff
 */
public class SimpleTypeInheritanceContainer {

    // Facets of simpleType restrictions
    private boolean hasSimpleTypeRestriction;
    private SimpleContentFixableRestrictionProperty<String> simpleTypeMinExclusive;
    private SimpleContentFixableRestrictionProperty<String> simpleTypeMaxExclusive;
    private SimpleContentFixableRestrictionProperty<String> simpleTypeMinInclusive;
    private SimpleContentFixableRestrictionProperty<String> simpleTypeMaxInclusive;
    private SimpleContentFixableRestrictionProperty<Integer> simpleTypeTotalDigits;
    private SimpleContentFixableRestrictionProperty<Integer> simpleTypeFractionDigits;
    private SimpleContentFixableRestrictionProperty<Integer> simpleTypeLength;
    private SimpleContentFixableRestrictionProperty<Integer> simpleTypeMinLength;
    private SimpleContentFixableRestrictionProperty<Integer> simpleTypeMaxLength;
    private LinkedList<SimpleContentRestrictionProperty<String>> simpleTypePatternList = new LinkedList<SimpleContentRestrictionProperty<String>>();
    private SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace> simpleTypeWhitespace;
    private LinkedList<String> simpleTypeEnumeration = new LinkedList<String>();

    // Facets of list restrictions
    private boolean hasList;
    private LinkedHashSet<SimpleContentList> simpleContentLists = new LinkedHashSet<SimpleContentList>();
    private SimpleContentFixableRestrictionProperty<Integer> listLength;
    private SimpleContentFixableRestrictionProperty<Integer> listMinLength;
    private SimpleContentFixableRestrictionProperty<Integer> listMaxLength;
    private LinkedList<String> listEnumeration = new LinkedList<String>();
    private LinkedList<SimpleContentRestrictionProperty<String>> listPatternList = new LinkedList<SimpleContentRestrictionProperty<String>>();
    private SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace> listWhitespace;

    // Base type of this path
    private SimpleType base;

    /**
     * Add pattern to pattern list of the list restrictions.
     * @param pattern SimpleContentRestrictionProperty representing a pattern.
     */
    public void addListPattern(SimpleContentRestrictionProperty<String> pattern) {
        listPatternList.add(pattern);
    }

    /**
     * Add list to list list of the list restrictions.
     * @param simpleContentList SimpleContentList of the container.
     */
    public void addSimpleContentList(SimpleContentList simpleContentList) {
        simpleContentLists.add(simpleContentList);
    }

    /**
     * Add pattern to pattern list of the simpleType restrictions.
     * @param pattern SimpleContentRestrictionProperty representing a pattern.
     */
    public void addSimpleTypePattern(SimpleContentRestrictionProperty<String> pattern) {
        simpleTypePatternList.add(pattern);
    }

    /**
     * Get the base type.
     * @return Base type of the container.
     */
    public SimpleType getBase() {
        return base;
    }

    /**
     * Get simpleContentLists.
     * @return SimpleContentLists field of the container.
     */
    public LinkedHashSet<SimpleContentList> getSimpleContentLists() {
        return simpleContentLists;
    }

    /**
     * Set new simpleContentLists set.
     * @param simpleContentLists New simpleContentList set.
     */
    public void setSimpleContentLists(LinkedHashSet<SimpleContentList> simpleContentLists) {
        this.simpleContentLists = simpleContentLists;
    }

    /**
     * Set new base type .
     * @param base New base type.
     */
    public void setBase(SimpleType base) {
        this.base = base;
    }

    /**
     * Check if container has list.
     * @return <tt>true</tt> if the container contains list.
     */
    public boolean isHasList() {
        return hasList;
    }

    /**
     * Set new boolean for field hasList.
     * @param hasList Boolean which specifies if a list is contained.
     */
    public void setHasList(boolean hasList) {
        this.hasList = hasList;
    }

    /**
     * Check if contains simpleType restriction.
     * @return <tt>true</tt> if the container contains simpleType restriction.
     */
    public boolean isHasSimpleTypeRestriction() {
        return hasSimpleTypeRestriction;
    }

    /**
     * Set new boolean for field hasSimpleTypeRestriction.
     * @param hasSimpleTypeRestriction Boolean which specifies if a restriction
     * is contained.
     */
    public void setHasSimpleTypeRestriction(boolean hasSimpleTypeRestriction) {
        this.hasSimpleTypeRestriction = hasSimpleTypeRestriction;
    }

    /**
     * Get enumeration of the list restriction.
     * @return ListEnumeration field of the container.
     */
    public LinkedList<String> getListEnumeration() {
        return listEnumeration;
    }

    /**
     * Set new SimpleContentFixableRestrictionProperty for the enumeration facet
     * of the list type.
     * @param listEnumeration SimpleContentFixableRestrictionProperty for
     * enumeration.
     */
    public void setListEnumeration(LinkedList<String> listEnumeration) {
        this.listEnumeration = listEnumeration;
    }

    /**
     * Get length of the list restriction.
     * @return ListLength field of the container.
     */
    public SimpleContentFixableRestrictionProperty<Integer> getListLength() {
        return listLength;
    }

    /**
     * Set new SimpleContentFixableRestrictionProperty for the length facet
     * of the list type.
     * @param listLength SimpleContentFixableRestrictionProperty for
     * length.
     */
    public void setListLength(SimpleContentFixableRestrictionProperty<Integer> listLength) {
        this.listLength = listLength;
    }

    /**
     * Get max length of the list restriction.
     * @return ListMaxLength field of the container.
     */
    public SimpleContentFixableRestrictionProperty<Integer> getListMaxLength() {
        return listMaxLength;
    }

    /**
     * Set new SimpleContentFixableRestrictionProperty for the max length facet
     * of the list type.
     * @param listMaxLength SimpleContentFixableRestrictionProperty for
     * max length.
     */
    public void setListMaxLength(SimpleContentFixableRestrictionProperty<Integer> listMaxLength) {
        this.listMaxLength = listMaxLength;
    }

    /**
     * Get min length of the list restriction.
     * @return ListMinLength field of the container.
     */
    public SimpleContentFixableRestrictionProperty<Integer> getListMinLength() {
        return listMinLength;
    }

    /**
     * Set new SimpleContentFixableRestrictionProperty for the min length facet
     * of the list type.
     * @param listMinLength SimpleContentFixableRestrictionProperty for
     * min length.
     */
    public void setListMinLength(SimpleContentFixableRestrictionProperty<Integer> listMinLength) {
        this.listMinLength = listMinLength;
    }

    /**
     * Get pattern list of the list restriction.
     * @return ListPatternList field of the container.
     */
    public LinkedList<SimpleContentRestrictionProperty<String>> getListPatternList() {
        return listPatternList;
    }

    /**
     * Set new SimpleContentFixableRestrictionProperty for the pattern list facet
     * of the list type.
     * @param listPatternList SimpleContentFixableRestrictionProperty for
     * pattern list.
     */
    public void setListPatternList(LinkedList<SimpleContentRestrictionProperty<String>> listPatternList) {
        this.listPatternList = listPatternList;
    }

    /**
     * Get whitespace of the list restriction.
     * @return ListWhitespace field of the container.
     */
    public SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace> getListWhitespace() {
        return listWhitespace;
    }

    /**
     * Set new SimpleContentFixableRestrictionProperty for the whitespace facet
     * of the list type.
     * @param listWhitespace SimpleContentFixableRestrictionProperty for
     * whitespace.
     */
    public void setListWhitespace(SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace> listWhitespace) {
        this.listWhitespace = listWhitespace;
    }

    /**
     * Get enumeration of the simpleType restriction.
     * @return SimpleTypeEnumeration field of the container.
     */
    public LinkedList<String> getSimpleTypeEnumeration() {
        return simpleTypeEnumeration;
    }

    /**
     * Set new SimpleContentFixableRestrictionProperty for the enumeration facet
     * of the simpleType.
     * @param simpleTypeEnumeration SimpleContentFixableRestrictionProperty for
     * enumeration.
     */
    public void setSimpleTypeEnumeration(LinkedList<String> simpleTypeEnumeration) {
        this.simpleTypeEnumeration = simpleTypeEnumeration;
    }

    /**
     * Get fraction digits of the simpleType restriction.
     * @return SimpleTypeFractionDigits field of the container.
     */
    public SimpleContentFixableRestrictionProperty<Integer> getSimpleTypeFractionDigits() {
        return simpleTypeFractionDigits;
    }

    /**
     * Set new SimpleContentFixableRestrictionProperty for the fraction digits facet
     * of the simpleType.
     * @param simpleTypeFractionDigits SimpleContentFixableRestrictionProperty for
     * fraction digits.
     */
    public void setSimpleTypeFractionDigits(SimpleContentFixableRestrictionProperty<Integer> simpleTypeFractionDigits) {
        this.simpleTypeFractionDigits = simpleTypeFractionDigits;
    }

    /**
     * Get length of the simpleType restriction.
     * @return SimpleTypeLength field of the container.
     */
    public SimpleContentFixableRestrictionProperty<Integer> getSimpleTypeLength() {
        return simpleTypeLength;
    }

    /**
     * Set new SimpleContentFixableRestrictionProperty for the length facet of
     * the simpleType.
     * @param simpleTypeLength SimpleContentFixableRestrictionProperty for
     * length.
     */
    public void setSimpleTypeLength(SimpleContentFixableRestrictionProperty<Integer> simpleTypeLength) {
        this.simpleTypeLength = simpleTypeLength;
    }

    /**
     * Get max exclusive of the simpleType restriction.
     * @return SimpleTypeMaxExclusive field of the container.
     */
    public SimpleContentFixableRestrictionProperty<String> getSimpleTypeMaxExclusive() {
        return simpleTypeMaxExclusive;
    }

    /**
     * Set new SimpleContentFixableRestrictionProperty for the max exclusive facet
     * of the simpleType.
     * @param simpleTypeMaxExclusive SimpleContentFixableRestrictionProperty for
     * max exclusive.
     */
    public void setSimpleTypeMaxExclusive(SimpleContentFixableRestrictionProperty<String> simpleTypeMaxExclusive) {
        this.simpleTypeMaxExclusive = simpleTypeMaxExclusive;
    }

    /**
     * Get max inclusive of the simpleType restriction.
     * @return SimpleTypeMaxInclusive field of the container.
     */
    public SimpleContentFixableRestrictionProperty<String> getSimpleTypeMaxInclusive() {
        return simpleTypeMaxInclusive;
    }

    /**
     * Set new SimpleContentFixableRestrictionProperty for the max inclusive facet
     * of the simpleType.
     * @param simpleTypeMaxInclusive SimpleContentFixableRestrictionProperty for
     * max inclusive.
     */
    public void setSimpleTypeMaxInclusive(SimpleContentFixableRestrictionProperty<String> simpleTypeMaxInclusive) {
        this.simpleTypeMaxInclusive = simpleTypeMaxInclusive;
    }

    /**
     * Get max length of the simpleType restriction.
     * @return SimpleTypeMaxLength field of the container.
     */
    public SimpleContentFixableRestrictionProperty<Integer> getSimpleTypeMaxLength() {
        return simpleTypeMaxLength;
    }

    /**
     * Set new SimpleContentFixableRestrictionProperty for the max length facet
     * of the simpleType.
     * @param simpleTypeMaxLength SimpleContentFixableRestrictionProperty for
     * max length.
     */
    public void setSimpleTypeMaxLength(SimpleContentFixableRestrictionProperty<Integer> simpleTypeMaxLength) {
        this.simpleTypeMaxLength = simpleTypeMaxLength;
    }

    /**
     * Get min exclusive of the simpleType restriction.
     * @return SimpleTypeMinExclusive field of the container.
     */
    public SimpleContentFixableRestrictionProperty<String> getSimpleTypeMinExclusive() {
        return simpleTypeMinExclusive;
    }

    /**
     * Set new SimpleContentFixableRestrictionProperty for the min exclusive facet
     * of the simpleType.
     * @param simpleTypeMinExclusive SimpleContentFixableRestrictionProperty for
     * min exclusive.
     */
    public void setSimpleTypeMinExclusive(SimpleContentFixableRestrictionProperty<String> simpleTypeMinExclusive) {
        this.simpleTypeMinExclusive = simpleTypeMinExclusive;
    }

    /**
     * Get min inclusive of the simpleType restriction.
     * @return SimpleTypeMinInclusive field of the container.
     */
    public SimpleContentFixableRestrictionProperty<String> getSimpleTypeMinInclusive() {
        return simpleTypeMinInclusive;
    }

    /**
     * Set new SimpleContentFixableRestrictionProperty for the min inclusive facet
     * of the simpleType.
     * @param simpleTypeMinInclusive SimpleContentFixableRestrictionProperty for
     * min inclusive.
     */
    public void setSimpleTypeMinInclusive(SimpleContentFixableRestrictionProperty<String> simpleTypeMinInclusive) {
        this.simpleTypeMinInclusive = simpleTypeMinInclusive;
    }

    /**
     * Get min length of the simpleType restriction.
     * @return SimpleTypeMinLength field of the container.
     */
    public SimpleContentFixableRestrictionProperty<Integer> getSimpleTypeMinLength() {
        return simpleTypeMinLength;
    }

    /**
     * Set new SimpleContentFixableRestrictionProperty for the min length facet
     * of the simpleType.
     * @param simpleTypeMinLength SimpleContentFixableRestrictionProperty for
     * min length.
     */
    public void setSimpleTypeMinLength(SimpleContentFixableRestrictionProperty<Integer> simpleTypeMinLength) {
        this.simpleTypeMinLength = simpleTypeMinLength;
    }

    /**
     * Get pattern list of the simpleType restriction.
     * @return SimpleTypePatternList field of the container.
     */
    public LinkedList<SimpleContentRestrictionProperty<String>> getSimpleTypePatternList() {
        return simpleTypePatternList;
    }

    /**
     * Set new SimpleContentFixableRestrictionProperty for the  pattern list facet
     * of the simpleType.
     * @param simpleTypePatternList SimpleContentFixableRestrictionProperty for
     * pattern list.
     */
    public void setSimpleTypePatternList(LinkedList<SimpleContentRestrictionProperty<String>> simpleTypePatternList) {
        this.simpleTypePatternList = simpleTypePatternList;
    }

    /**
     * Get total digits of the simpleType restriction.
     * @return SimpleTypeTotalDigits field of the container.
     */
    public SimpleContentFixableRestrictionProperty<Integer> getSimpleTypeTotalDigits() {
        return simpleTypeTotalDigits;
    }

    /**
     * Set new SimpleContentFixableRestrictionProperty for the total digits facet
     * of the simpleType.
     * @param simpleTypeTotalDigits SimpleContentFixableRestrictionProperty for
     * total digits.
     */
    public void setSimpleTypeTotalDigits(SimpleContentFixableRestrictionProperty<Integer> simpleTypeTotalDigits) {
        this.simpleTypeTotalDigits = simpleTypeTotalDigits;
    }

    /**
     * Get whitespace of the simpleType restriction.
     * @return SimpleTypeWhitespace field of the container.
     */
    public SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace> getSimpleTypeWhitespace() {
        return simpleTypeWhitespace;
    }

    /**
     * Set new SimpleContentFixableRestrictionProperty for the whitespace facet
     * of the simpleType.
     * @param simpleTypeWhitespace SimpleContentFixableRestrictionProperty for
     * whitespace.
     */
    public void setSimpleTypeWhitespace(SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace> simpleTypeWhitespace) {
        this.simpleTypeWhitespace = simpleTypeWhitespace;
    }
}
