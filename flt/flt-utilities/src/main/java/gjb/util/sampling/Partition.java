package gjb.util.sampling;

import java.util.List;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 *
 * <p>Class that represents a partition of an example list into a training and
 * a validation list.  An object of this type is returned by Sampler's partition()
 * method and can be used as follows:
 * <pre>
 *   Partition partition = sampler.partition(100);
 *   List trainingList = partition.getTrainingList();
 *   List validationList = partition.getValidationList();
 * </pre>
 * </p>
 */
public class Partition<Type> {

    /**
     * Lists to hold the examples
     */
    protected List<Type> trainingList, validationList;

    /**
     * constructor only to be called from the enclosing type
     * @param trainingList
     * @param validationList
     */
    protected Partition(List<Type> trainingList, List<Type> validationList) {
        this.trainingList = trainingList;
        this.validationList = validationList;
    }

    /**
     * method that returns the training list
     * @return List with the training examples
     */
    public List<Type> getValidationList() {
        return validationList;
    }

    /**
     * method that returns the validation list
     * @return List with the validation examples
     */
    public List<Type> getTrainingList() {
        return trainingList;
    }

}