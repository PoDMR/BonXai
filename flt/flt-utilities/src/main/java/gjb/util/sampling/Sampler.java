/*
 * Created on Jan 27, 2006
 * Modified on $Date: 2009-10-27 14:11:40 $
 */
package gjb.util.sampling;

import java.util.List;

public interface Sampler<Type> {

    /**
     * method that selects the specified number of examples from the sample
     * such that each example has the same a priori probability; reservoir
     * sampling is used for this purpose
     * @param numberToSample
     *            int number of examples to select from the sample
     * @return List of examples
     * @throws IOException
     *             thrown if something goes wrong while reading the file
     * @throws SampleTooSmallException
     *             thrown if the size of the sample is smaller than the number of
     *             examples to select
     */
    public List<Type> sample(int numberToSample) throws SampleException;

    public Partition<Type> partition(int numberToSample)
            throws SampleException;

    /**
     * method to obtain the sample size (Note that the file has to be read entirely
     * to determine the sample size for the first time.)
     * @return int size of the sample
     */
    public int getSampleSize() throws SampleException;

}