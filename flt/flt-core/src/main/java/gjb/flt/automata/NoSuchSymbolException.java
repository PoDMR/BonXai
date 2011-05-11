/**
 * Created on Oct 14, 2009
 * Modified on $Date: 2009-10-27 14:14:00 $
 */
package gjb.flt.automata;

import gjb.flt.automata.impl.sparse.Symbol;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class NoSuchSymbolException extends NFAException {

    private static final long serialVersionUID = 798248427184244236L;
	protected String symbolValue;

	public NoSuchSymbolException(Symbol symbol) {
		this.symbolValue = symbol.toString();
	}

	public NoSuchSymbolException(String symbolValue) {
		this.symbolValue = symbolValue;
	}

	@Override
	public String getMessage() {
		return "symbol '" + symbolValue + "' is undefined";
	}

}
