/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.bonxai.converter.xsd2dtd;

import java.math.BigInteger;

/**
 * Class PermutationTool
 *
 * Calculate all permutations of a numeric set with a given length
 *
 * Example: (parameter: length = 4)
 * Result:
 * {0, 1, 2, 3}
 * {0, 1, 3, 2}
 * {0, 2, 1, 3}
 * {0, 2, 3, 1}
 * {0, 3, 1, 2}
 * {0, 3, 2, 1}
 * {1, 0, 2, 3}
 * {1, 0, 3, 2}
 * {1, 2, 0, 3}
 * {1, 2, 3, 0}
 * {1, 3, 0, 2}
 * {1, 3, 2, 0}
 * {2, 0, 1, 3}
 * {2, 0, 3, 1}
 * {2, 1, 0, 3}
 * {2, 1, 3, 0}
 * {2, 3, 0, 1}
 * {2, 3, 1, 0}
 * {3, 0, 1, 2}
 * {3, 0, 2, 1}
 * {3, 1, 0, 2}
 * {3, 1, 2, 0}
 * {3, 2, 0, 1}
 * {3, 2, 1, 0}
 *
 */
public class PermutationTool {

    private int[] resultIntArray;
    private BigInteger numberOfPermutationsLeft, numberOfTotalPermutations;

    /**
     * Constructor of class PermutationTool
     * @param length
     */
    public PermutationTool(int length) {
        resultIntArray = new int[length];
        numberOfTotalPermutations = calculateTotalPermutationCount(length);
        for (int i = 0; i < resultIntArray.length; i++) {
            resultIntArray[i] = i;
        }
        numberOfPermutationsLeft = new BigInteger(numberOfTotalPermutations.toString());
    }

    /**
     * Getter for the status of the current permutation generation
     * @return boolean      returns true, if there are more permutations left for calculation
     */
    public boolean morePermutationsLeft() {
        return numberOfPermutationsLeft.compareTo(BigInteger.ZERO) == 1;
    }

    private static BigInteger calculateTotalPermutationCount(int length) {
        BigInteger result = BigInteger.ONE;
        for (int i = length; i > 1; i--) {
            result = result.multiply(new BigInteger(Integer.toString(i)));
        }
        return result;
    }

    /**
     * Calculate the next permutation regarding the current status of the PermutationTool
     * @return
     */
    public int[] calculateNextPermutation() {
        if (numberOfPermutationsLeft.equals(numberOfTotalPermutations)) {
            numberOfPermutationsLeft = numberOfPermutationsLeft.subtract(BigInteger.ONE);
            return resultIntArray;
        } else {
            int temporaryInt;
            int a = resultIntArray.length - 2;
            while (resultIntArray[a] > resultIntArray[a + 1]) {
                a--;
            }
            int b = resultIntArray.length - 1;
            while (resultIntArray[a] > resultIntArray[b]) {
                b--;
            }
            temporaryInt = resultIntArray[b];
            resultIntArray[b] = resultIntArray[a];
            resultIntArray[a] = temporaryInt;
            int c = resultIntArray.length - 1;
            int d = a + 1;
            while (c > d) {
                temporaryInt = resultIntArray[d];
                resultIntArray[d] = resultIntArray[c];
                resultIntArray[c] = temporaryInt;
                c--;
                d++;
            }
            numberOfPermutationsLeft = numberOfPermutationsLeft.subtract(BigInteger.ONE);
            return resultIntArray;
        }
    }

    /**
     * Print out the current permutation as a String (for debug purpose)
     * @return String       the current permutation (a comma seperated String)
     */
    public String printResult() {
        String resultString = "";
        for (int i = 0; i < resultIntArray.length; i++) {
            resultString += resultIntArray[i] + ", ";
        }
        if (!resultString.equals("")) {
            resultString = resultString.substring(0, (resultString.length() - 2));
        }
        return resultString;
    }
}
