package eu.fox7.schematoolkit.relaxng.om;

/**
 * Class representing the optional element of RelaxNG.
 * @author Lars Schmidt
 */
public class Optional extends RelaxNGCountingPattern {
	@Override
	public int getMinOccurs() {
		return 0;
	}

	@Override
	public Integer getMaxOccurs() {
		return 1;
	}

}
