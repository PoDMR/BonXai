/**
 * Created on Jun 9, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.measures;

import java.math.BigInteger;

import gjb.flt.automata.factories.dense.GlushkovBigIntegerMatrixRepresentationFactory;
import gjb.flt.automata.impl.dense.GlushkovMatrixRepresentation;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.math.Matrix;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class StringCount implements LanguageMeasure<BigInteger>,
        GlushkovMatrixMeasure<BigInteger> {

	protected GlushkovBigIntegerMatrixRepresentationFactory factory = new GlushkovBigIntegerMatrixRepresentationFactory();

    public BigInteger compute(StateNFA nfa) {
        GlushkovMatrixRepresentation<BigInteger> m = factory.create(nfa);
        return compute(m);
    }

    public BigInteger compute(StateNFA nfa, int maxLength) {
    	GlushkovMatrixRepresentation<BigInteger> m = factory.create(nfa);
    	return compute(m, maxLength);
    }

    public BigInteger compute(GlushkovMatrixRepresentation<BigInteger> m) {
		int maxLength = 2*(m.getX0().getRowDimension() + 1);
        return compute(m, maxLength);
	}

	public BigInteger compute(GlushkovMatrixRepresentation<BigInteger> m,
	                          int maxLength) {
		BigInteger sum = m.acceptsEmptyString() ? BigInteger.ONE : BigInteger.ZERO;
		if (m.isNonTrivial()) {
			if (maxLength >= 1) {
				sum = sum.add(m.getXf().times(m.getX0()).get(0, 0));
				Matrix<BigInteger> x = m.getX0().clone();
				for (int length = 2; length <= maxLength; length++) {
					x = m.getP().times(x);
					sum = sum.add(m.getXf().times(x).get(0, 0));
				}
			}
		}
        return sum;
	}

}
