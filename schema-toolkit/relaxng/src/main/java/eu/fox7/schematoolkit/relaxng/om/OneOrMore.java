package eu.fox7.schematoolkit.relaxng.om;

/**
 * Class representing the OneOrMore element of RelaxNG.
 * @author Lars Schmidt
 */
public class OneOrMore extends RelaxNGCountingPattern {
	@Override
	public int getMinOccurs() {
		return 1;
	}

	@Override
	public Integer getMaxOccurs() {
		return null;
	}
}
