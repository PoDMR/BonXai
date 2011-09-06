/*
 * Created on Mar 5, 2009
 * Modified on $Date: 2009-10-26 18:37:40 $
 */
package eu.fox7.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.fox7.math.Binner;
import eu.fox7.math.EquidensityBinner;
import eu.fox7.math.EquidistantBinner;
import eu.fox7.math.InvalidBinDefinitionException;
import eu.fox7.math.InvalidDataException;
import eu.fox7.math.LogEquidensityBinner;
import eu.fox7.math.LogEquidistantBinner;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class BinnerTest extends TestCase {

    public static Test suite() {
        return new TestSuite(BinnerTest.class);
    }

    public void testEquidistantBinner1() {
        final double min = 2.0, max = 4.0;
        final int nrBins = 6;
        final double epsilon = 1.0e-5;
        final double[] expected = {2.0, 2.5, 3.0, 3.5, 4.0};
        try {
            Binner binner = new EquidistantBinner(min, max, nrBins);
            double[] bins = binner.getBinBounds();
            assertEquals("nr bins", nrBins - 1, bins.length);
            assertEquals("min", min, bins[0]);
            assertEquals("max", max, bins[bins.length - 1]);
            for (int i = 0; i < bins.length; i++) {
                assertTrue("value " + i, expected[i] - epsilon < bins[i] &&
                           bins[i] < expected[i] + epsilon);
            }
        } catch (InvalidBinDefinitionException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
    
    public void testEquidistantBinner2() {
        final double min = 2.4, max = 7.8;
        final int nrBins = 7;
        final double epsilon = 1.0e-5;
        try {
            Binner binner = new EquidistantBinner(min, max, nrBins);
            double[] bins = binner.getBinBounds();
            assertEquals("nr bins", nrBins - 1, bins.length);
            assertEquals("min", min, bins[0]);
            assertEquals("max", max, bins[bins.length - 1]);
            for (int i = 0; i < bins.length; i++) {
                double value = min + i*(max - min)/(nrBins - 2);
                assertTrue("value " + i, value - epsilon < bins[i] &&
                                             bins[i] < value + epsilon);
            }
        } catch (InvalidBinDefinitionException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

    public void testEquidistantBinner3() {
        final double min = 2.0, max = 2.0;
        final int nrBins = 2;
        final double epsilon = 1.0e-5;
        final double[] expected = {2.0};
        try {
            Binner binner = new EquidistantBinner(min, max, nrBins);
            double[] bins = binner.getBinBounds();
            assertEquals("nr bins", nrBins - 1, bins.length);
            assertEquals("min", min, bins[0]);
            assertEquals("max", max, bins[bins.length - 1]);
            for (int i = 0; i < bins.length; i++) {
                assertTrue("value " + i, expected[i] - epsilon < bins[i] &&
                           bins[i] < expected[i] + epsilon);
            }
        } catch (InvalidBinDefinitionException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
    
    public void testEquidistantBinner4() {
        final double min = 2.0, max = 2.0;
        final int nrBins = 1;
        try {
            new EquidistantBinner(min, max, nrBins);
            fail("expected exception");
        } catch (InvalidBinDefinitionException e) {}
    }
    
    public void testEquidistantBinner5() {
        final double min = 2.0, max = 1.0;
        final int nrBins = 10;
        try {
            new EquidistantBinner(min, max, nrBins);
            fail("expected exception");
        } catch (InvalidBinDefinitionException e) {}
    }

    public void testBinning1() {
        final double min = 2.0, max = 8.0;
        final int nrBins = 5;
        final double[] data = {2.0, 12.1, 2.1, 0.1, 0.3, 0.7, 1.9,
        		               3.9, 1.3, 1.5, 5.0, 4.1};
        final double[][] expected = {
        		{0.1, 0.3, 0.7, 1.9, 1.3, 1.5},
        		{2.0, 2.1, 3.9},
        		{5.0, 4.1},
        		{},
        		{12.1}
        };
        try {
            Binner binner = new EquidistantBinner(min, max, nrBins);
            List<List<Double>> bins = binner.computeBins(data);
            long[] counts = binner.computeBinCounts(data);
            assertEquals("nr bins", nrBins, bins.size());
            for (int i = 0; i < nrBins; i++) {
            	assertEquals("bin content size " + i,
            			     expected[i].length, bins.get(i).size());
            	assertEquals("bin count " + i,
            			expected[i].length, counts[i]);
            	for (int j = 0; j < expected[i].length; j++)
            		assertEquals("bin " + i + " content " + j,
            				     expected[i][j], bins.get(i).get(j));
            }
        } catch (InvalidBinDefinitionException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

    public void testBinning2() {
    	final double min = 2.0, max = 8.0;
    	final int nrBins = 5;
    	final double[] data = {2.1, 3.2, 4.5, 5.7, 2.9, 2.1, 3.2};
    	final double[][] expected = {
    			{},
    			{2.1, 3.2, 2.9, 2.1, 3.2},
    			{4.5, 5.7},
    			{},
    			{}
    	};
    	try {
    		Binner binner = new EquidistantBinner(min, max, nrBins);
    		List<List<Double>> bins = binner.computeBins(data);
    		long[] counts = binner.computeBinCounts(data);
    		assertEquals("nr bins", nrBins, bins.size());
    		for (int i = 0; i < nrBins; i++) {
    			assertEquals("bin content size " + i,
    					expected[i].length, bins.get(i).size());
    			assertEquals("bin count " + i,
    					expected[i].length, counts[i]);
    			for (int j = 0; j < expected[i].length; j++)
    				assertEquals("bin " + i + " content " + j,
    						expected[i][j], bins.get(i).get(j));
    		}
    	} catch (InvalidBinDefinitionException e) {
    		e.printStackTrace();
    		fail("unexpected exception");
    	}
    }
    
    public void testLogEquidistantBinner1() {
        final double min = 1.0e-1, max = 1.0e2;
        final int nrBins = 5;
        final double epsilon = 1.0e-5;
        final double[] expected = {1.0e-1, 1.0, 10.0, 100.0};
        try {
            Binner binner = new LogEquidistantBinner(min, max, nrBins);
            double[] bins = binner.getBinBounds();
            assertEquals("nr bins", nrBins - 1, bins.length);
            assertEquals("min", min, bins[0]);
            assertEquals("max", max, bins[bins.length - 1]);
            for (int i = 0; i < bins.length; i++) {
                assertTrue("value " + i, expected[i] - epsilon < bins[i] &&
                           bins[i] < expected[i] + epsilon);
            }
        } catch (InvalidBinDefinitionException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

    public void testEquidensityBinner1() {
    	double[] data = new double[20];
    	for (int i = 0; i < data.length; i++)
    		data[i] = i;
    	final double[] expected = {4.5, 9.5, 14.5};
    	try {
			Binner binner = new EquidensityBinner(data, 4);
			double[] bins = binner.getBinBounds();
			assertEquals("nr of bins", expected.length, bins.length);
			for (int i = 0; i < bins.length; i++)
				assertEquals("bin " + i, expected[i], bins[i]);
		} catch (InvalidBinDefinitionException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (InvalidDataException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
    }

    public void testEquidensityBinner2() {
    	double[] data = {1.0, 2.0};
    	try {
			new EquidensityBinner(data, 5);
			fail("exception expected");
		} catch (InvalidBinDefinitionException e) {
	    } catch (InvalidDataException e) {}
    }

    public void testEquidensityBinner3() {
    	double[] data = new double[8];
    	List<Double> dataList = new ArrayList<Double>();
    	for (int i = 0; i < data.length; i++) {
    		dataList.add(2.0*i);
    	}
    	Collections.shuffle(dataList);
    	for (int i = 0; i < data.length; i++)
    		data[i] = dataList.get(i);
    	final double[] expected = {3.0, 7.0, 11.0};
    	try {
    		Binner binner = new EquidensityBinner(data, 4);
    		double[] bins = binner.getBinBounds();
    		assertEquals("nr of bins", expected.length, bins.length);
    		for (int i = 0; i < bins.length; i++)
    			assertEquals("bin " + i, expected[i], bins[i]);
    	} catch (InvalidBinDefinitionException e) {
    		e.printStackTrace();
    		fail("unexpected exception");
		} catch (InvalidDataException e) {
			e.printStackTrace();
			fail("unexpected exception");
    	}
    }

    public void testLogEquidensityBinner1() {
    	double[] data = new double[8];
    	List<Double> dataList = new ArrayList<Double>();
    	double x = 1.0;
    	for (int i = 0; i < data.length; i++) {
    		dataList.add(x);
    		x *= 100.0;
    	}
    	Collections.shuffle(dataList);
    	for (int i = 0; i < data.length; i++)
    		data[i] = dataList.get(i);
    	final double[] expected = {1.0e3, 1.0e7, 1.0e11};
    	try {
    		Binner binner = new LogEquidensityBinner(data, 4);
    		double[] bins = binner.getBinBounds();
    		assertEquals("nr of bins", expected.length, bins.length);
    		for (int i = 0; i < bins.length; i++)
    			assertEquals("bin " + i, expected[i], bins[i]);
    	} catch (InvalidBinDefinitionException e) {
    		e.printStackTrace();
    		fail("unexpected exception");
		} catch (InvalidDataException e) {
			e.printStackTrace();
			fail("unexpected exception");
    	}
    }
    
    public void testLogEquidensityBinner2() {
    	double[] data = {1.0, 2.0};
    	try {
    		new LogEquidensityBinner(data, 5);
    		fail("exception expected");
    	} catch (InvalidBinDefinitionException e) {
    	} catch (InvalidDataException e) {}
    }
    
    public void testLogEquidensityBinner3() {
    	double[] data = {3.0, 11.0, -2.0, -5.3};
    	try {
			new LogEquidensityBinner(data, 2);
    		fail("exception expected");
		} catch (InvalidBinDefinitionException e) {
			e.printStackTrace();
    		fail("unexpected exception");
		} catch (InvalidDataException e) {}
    }

}
