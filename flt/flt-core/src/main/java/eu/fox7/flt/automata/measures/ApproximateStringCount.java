/**
 * Created on Sep 16, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.measures;

import eu.fox7.flt.automata.factories.dense.GlushkovDoubleMatrixRepresentationFactory;
import eu.fox7.flt.automata.impl.dense.GlushkovMatrixRepresentation;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.math.Matrix;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class ApproximateStringCount implements LanguageMeasure<Double>,
        GlushkovMatrixMeasure<Double> {

	protected GlushkovDoubleMatrixRepresentationFactory factory = new GlushkovDoubleMatrixRepresentationFactory();

	public Double compute(StateNFA nfa) {
        GlushkovMatrixRepresentation<Double> m = factory.create(nfa);
        return compute(m);
	}

    public Double compute(StateNFA nfa, int maxLength) {
    	GlushkovMatrixRepresentation<Double> m = factory.create(nfa);
    	return compute(m, maxLength);
    }

	public Double compute(GlushkovMatrixRepresentation<Double> m) {
		int maxLength = 2*(m.getX0().getRowDimension() + 1);
        return compute(m, maxLength);
	}

	public Double compute(GlushkovMatrixRepresentation<Double> m, int maxLength) {
		double sum = m.acceptsEmptyString() ? 1.0 : 0.0;
		Jama.Matrix x0 = convert(m.getX0());
		Jama.Matrix p = convert(m.getP());
		Jama.Matrix xf = convert(m.getXf());
		if (m.isNonTrivial()) {
			if (maxLength >= 1) {
				sum += xf.times(x0).get(0, 0);
				Jama.Matrix x = (Jama.Matrix) x0.clone();
				for (int length = 2; length <= maxLength; length++) {
					x = p.times(x);
					sum += xf.times(x).get(0, 0);
				}
			}
		}
        return sum;
	}

	protected Jama.Matrix convert(Matrix<Double> m) {
		Jama.Matrix newM = new Jama.Matrix(m.getRowDimension(),
		                                   m.getColumnDimension());
		for (int row = 0; row < m.getRowDimension(); row++)
			for (int col = 0; col < m.getColumnDimension(); col++)
				newM.set(row, col, m.get(row, col).doubleValue());
		return newM;
	}

}
