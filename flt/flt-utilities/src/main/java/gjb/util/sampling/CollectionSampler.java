/*
 * Created on Jan 27, 2006
 * Modified on $Date: 2009-10-27 14:11:40 $
 */
package gjb.util.sampling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class CollectionSampler<Type>  implements Sampler<Type> {

    /**
     * Collection that holds the sample, it is null if inputFile is not and vice versa
     */
    protected Collection<Type> inputCollection = null;

    public CollectionSampler(Collection<Type> inputCollection) {
        this.inputCollection = inputCollection;
    }

    /**
     * method that selects the specified number of examples from the sample
     * such that each example has the same a priori probability; reservoir
     * sampling is used for this purpose
     * @param numberToSample
     *            int number of examples to select from the sample
     * @return List of examples
     * @throws SampleTooSmallException
     *             thrown if the size of the sample is smaller than the number of
     *             examples to select
     */
    public List<Type> sample(int numberToSample) throws SampleException {
        List<Type> reservoir = new ArrayList<Type>();
        Iterator<Type> collectionIt = inputCollection.iterator();
        while (collectionIt.hasNext() &&
                reservoir.size() < numberToSample) {
            reservoir.add(collectionIt.next());
        }
        if (reservoir.size() < numberToSample) {
            throw new SampleException(new SampleTooSmallException());
        } else if (!collectionIt.hasNext()) {
            return reservoir;
        }
        int nrExample = numberToSample;
        do {
            int position = RandomUtils.nextInt(nrExample + 1);
            if (position < numberToSample) {
                reservoir.set(position, collectionIt.next());
            } else {
                collectionIt.next();
            }
            nrExample++;
        } while (collectionIt.hasNext());
        return reservoir;
    }

    public Partition<Type> partition(int numberToSample)
            throws SampleException {
        List<Type> trainingList = new ArrayList<Type>();
        List<Type> validationList = new LinkedList<Type>();
        Iterator<Type> collectionIt = inputCollection.iterator();
        while (collectionIt.hasNext() && trainingList.size() < numberToSample) {
            trainingList.add(collectionIt.next());
        }
        if (trainingList.size() < numberToSample) {
            throw new SampleException(new SampleTooSmallException());
        } else if (!collectionIt.hasNext()) {
            return new Partition<Type>(trainingList, validationList);
        }
        int nrExample = numberToSample;
        do {
            int position = RandomUtils.nextInt(nrExample + 1);
            if (position < numberToSample) {
                validationList.add(trainingList.get(position));
                trainingList.set(position, collectionIt.next());
            } else {
                validationList.add(collectionIt.next());
            }
            nrExample++;
        } while (collectionIt.hasNext());
        return new Partition<Type>(trainingList, validationList);
}

    public int getSampleSize() throws SampleException {
        return inputCollection.size();
    }

}
