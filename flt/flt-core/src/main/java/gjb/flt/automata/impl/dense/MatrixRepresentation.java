/*
 * Created on Jun 4, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.impl.dense;

import gjb.math.Matrix;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class MatrixRepresentation<T> {

    protected T zeroValue, oneValue;
	protected Matrix<T> x0, p, xf;
    protected boolean acceptsEmptyString;

    public MatrixRepresentation() {
		super();
	}

    public void setZeroValue(T value) {
    	zeroValue = value;
    }

    public void setOneValue(T value) {
    	oneValue = value;
    }

    public Matrix<T> getX0() {
    	return x0;
    }

	public void setX0(Matrix<T> x0) {
    	this.x0 = x0;
    }

	public Matrix<T> getP() {
    	return p;
    }

	public void setP(Matrix<T> p) {
    	this.p = p;
    }

	public Matrix<T> getXf() {
    	return xf;
    }

	public void setXf(Matrix<T> xf) {
    	this.xf = xf;
    }

	public boolean acceptsEmptyString() {
    	return acceptsEmptyString;
    }

    public void setAcceptsEmptyString(boolean accepts) {
    	this.acceptsEmptyString = accepts;
    }

    public boolean isInitial(int i) {
    	return !x0.get(i, 0).equals(zeroValue);
    }

    public void setInitialValue(int i, T value) {
    	x0.set(i, 0, value);
    }

    public boolean hasTransition(int i, int j) {
    	return !p.get(i, j).equals(zeroValue);
    }

    public void setTransitionValue(int i, int j, T value) {
    	p.set(i, j, value);
    }

    public boolean isFinal(int j) {
    	return !xf.get(0, j).equals(zeroValue);
    }

    public void setFinalValue(int j, T value) {
    	xf.set(0, j, value);
    }

    public boolean isNonTrivial() {
    	return this.x0.getRowDimension() > 0;
    }

    public T getNumberOfAcceptedStrings(int length) {
        if (length == 0) {
            return acceptsEmptyString ? oneValue : zeroValue;
        } else if (length == 1) {
        	if (isNonTrivial())
        		return xf.times(x0).get(0, 0);
        	else
        		return zeroValue;
        } else {
        	if (isNonTrivial()) {
        		Matrix<T> x = x0.clone();
        		for (int i = 2; i <= length; i++)
        			x = p.times(x);
        		return xf.times(x).get(0, 0);
        	} else
        		return zeroValue;
        }
    }
    
    public String toString() {
    	StringBuilder str = new StringBuilder();
    	str.append("x0:\n").append(x0.toString()).append("\n");
    	str.append("p:\n").append(p.toString()).append("\n");
    	str.append("xf:\n").append(xf.toString()).append("\n");
    	str.append("e: ").append(acceptsEmptyString);
    	return str.toString();
    }

}
