package eu.fox7.schematoolkit.relaxng.om;

/**
 * Class representing the ZeroOrMore element of RelaxNG.
 * @author Lars Schmidt
 */
public class ZeroOrMore extends RelaxNGCountingPattern {
	@Override
	public int getMinOccurs() {
		return 0;
	}

	@Override
	public Integer getMaxOccurs() {
		return null;
	}
}
