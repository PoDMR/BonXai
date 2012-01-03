package eu.fox7.schematoolkit.relaxng.om;

/**
 * An enumeration of possible values for the Relax NG combine method of start or
 * define elements.
 *
 * It is important, that all those elements with the same name share the same
 * combine method.
 *
 * @author Lars Schmidt
 */
public enum CombineMethod {

    /**
     * All start or define elements with the same name are combined in the
     * form of a choice.
     */
    choice,
    /**
     * All start or define elements with the same name are combined with the
     * interleaving operator.
     */
    interleave,
}
