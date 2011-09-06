/**
 * 
 */
package eu.fox7.math;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 * 
 * This class is based on EquidensityBinner, but the bin bounds are computed
 * using the geometric, rather than the arithmetic mean.  This Binner is better
 * suited for data values spanning seveal orders of magnitude.  
 */
public class LogEquidensityBinner extends EquidensityBinner {

	public LogEquidensityBinner(double[] data, int nrBins)
			throws InvalidBinDefinitionException, InvalidDataException {
		super(data, nrBins);
	}

	@Override
	protected double computeBin(double data1, double data2) {
		return Math.sqrt(data1)*Math.sqrt(data2);
	}

	@Override
	protected boolean isValid(double[] data) {
		for (double x : data)
			if (x < 0.0)
				return false;
		return true;
	}

}
