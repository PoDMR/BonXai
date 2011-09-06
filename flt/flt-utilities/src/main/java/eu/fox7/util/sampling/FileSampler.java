/*
 * Created on Mar 18, 2005
 * Modified on $Date: 2009-10-27 14:11:40 $
 */
package eu.fox7.util.sampling;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Properties;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 * <p>Class to read a specified number of examples from a file so that each
 * example has the same a priori probability to be drawn from the sample.</p>
 * <p>Example:
 * <pre>
 *   File file = new File("some_file_name.txt");
 *   Sampler sampler = new Sampler(file);
 *   List sample = sampler.sample(10);
 * </pre>
 * </p>
 */
public class FileSampler implements Sampler<String[]> {

    /**
     * Properties to be passed to the ExampleParser constructor
     */
    protected Properties properties = null;
    /**
     * File that holds the sample, it is null if inputFile is not and vice versa
     */
    protected File inputFile = null;
    /**
     * int that equals the size of the sample <strong>after</strong> the first
     * time the sample() method has been run
     */
    protected int sampleSize = -1;

    /**
     * constructor that takes a File that contains the sample and accepts Properties
     * to be passed to the ExampleParser for non-default behavior
     * @param inputFile
     *            File containing the sample
     * @param properties
     *            Properties to be passed to the ExampleParser
     */
    public FileSampler(File inputFile, Properties properties) {
        this.inputFile = inputFile;
        this.properties = properties;
    }

    /**
     * constructor that takes a File that contains the sample
     * @param inputFile
     *            File containing the sample
     */
    public FileSampler(File inputFile) {
        this(inputFile, null);
    }

    /* (non-Javadoc)
     * @see eu.fox7.ai.util.Sampler#sample(int)
     */
    public List<String[]> sample(int numberToSample)
            throws SampleException {
        try {
            return ReaderSampler.sample(new FileReader(inputFile), numberToSample);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new SampleException(e);
        }
    }

    /* (non-Javadoc)
     * @see eu.fox7.ai.util.Sampler#partition(int)
     */
    public Partition<String[]> partition(int numberToSample)
            throws SampleException {
        try {
            return ReaderSampler.partition(new FileReader(inputFile),
                                           numberToSample);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new SampleException(e);
        }
    }

    /* (non-Javadoc)
     * @see eu.fox7.ai.util.Sampler#getSampleSize()
     */
    public int getSampleSize() throws SampleException {
        if (sampleSize == -1)
            try {
                sampleSize = ReaderSampler.getSampleSize(new FileReader(inputFile));
                return sampleSize;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new SampleException(e);
            }
        else
            return sampleSize;
    }

}
