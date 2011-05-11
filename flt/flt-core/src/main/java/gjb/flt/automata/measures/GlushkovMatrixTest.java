/**
 * Created on Jul 9, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.measures;

import gjb.flt.automata.impl.dense.GlushkovMatrixRepresentation;


/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public interface GlushkovMatrixTest<T> {

    public boolean test(GlushkovMatrixRepresentation<T> m);

}
