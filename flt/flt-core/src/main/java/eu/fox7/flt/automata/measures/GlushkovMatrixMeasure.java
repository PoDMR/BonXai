/**
 * Created on Jun 15, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.measures;

import eu.fox7.flt.automata.impl.dense.GlushkovMatrixRepresentation;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public interface GlushkovMatrixMeasure<T> {

    public T compute(GlushkovMatrixRepresentation<T> m);

}
