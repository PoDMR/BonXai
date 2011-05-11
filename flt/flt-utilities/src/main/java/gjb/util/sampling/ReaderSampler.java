/*
 * Created on Jan 30, 2006
 * Modified on $Date: 2009-10-27 14:11:40 $
 */
package gjb.util.sampling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.math.RandomUtils;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class ReaderSampler {

    public static List<String[]> sample(Reader inReader, int numberToSample)
            throws SampleException {
        return sample(inReader, numberToSample, null);
    }

    public static List<String[]> sample(Reader inReader, int numberToSample,
                                        Properties properties)
            throws SampleException {
        try {
            BufferedReader reader = new BufferedReader(inReader);
            List<String[]> reservoir = new ArrayList<String[]>();
            ExampleParser parser = new ExampleParser(properties);
            String line = null;
            while ((line = reader.readLine()) != null &&
                   reservoir.size() < numberToSample) {
                String[] example = parser.parse(line);
                if (example != null) {
                    reservoir.add(example);
                }
            }
            if (reservoir.size() < numberToSample) {
                throw new SampleTooSmallException();
            } else if (line == null) {
                return reservoir;
            }
            int nrExample = numberToSample;
            do {
                String[] example = parser.parse(line);
                if (example != null) {
                    int position = RandomUtils.nextInt(nrExample + 1);
                    if (position < numberToSample) {
                        reservoir.set(position, example);
                    }
                    nrExample++;
                }
            } while ((line = reader.readLine()) != null);
            return reservoir;
        } catch (IOException e) {
            e.printStackTrace();
            throw new SampleException(e);
        } catch (SampleTooSmallException e) {
            e.printStackTrace();
            throw new SampleException(e);
        }
    }

    public static Partition<String[]> partition(Reader inReader,
                                                int numberToSample)
            throws SampleException {
        return partition(inReader, numberToSample, null);
    }

    public static Partition<String[]> partition(Reader inReader,
                                                int numberToSample,
                                                Properties properties)
            throws SampleException {
        try {
            BufferedReader reader = new BufferedReader(inReader);
            List<String[]> trainingList = new ArrayList<String[]>();
            List<String[]> validationList = new LinkedList<String[]>();
            ExampleParser parser = new ExampleParser(properties);
            String line = null;
            while ((line = reader.readLine()) != null &&
                    trainingList.size() < numberToSample) {
                String[] example = parser.parse(line);
                if (example != null) {
                    trainingList.add(example);
                }
            }
            if (trainingList.size() < numberToSample) {
                throw new SampleTooSmallException();
            } else if (line == null) {
                return new Partition<String[]>(trainingList, validationList);
            }
            int nrExample = numberToSample;
            do {
                String[] example = parser.parse(line);
                if (example != null) {
                    int position = RandomUtils.nextInt(nrExample + 1);
                    if (position < numberToSample) {
                        validationList.add(trainingList.get(position));
                        trainingList.set(position, example);
                    } else {
                        validationList.add(example);
                    }
                    nrExample++;
                }
            } while ((line = reader.readLine()) != null);
            return new Partition<String[]>(trainingList, validationList);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SampleException(e);
        } catch (SampleTooSmallException e) {
            e.printStackTrace();
            throw new SampleException(e);
        }
    }

    public static int getSampleSize(Reader inReader) throws SampleException {
        return getSampleSize(inReader, null);
    }

    public static int getSampleSize(Reader inReader, Properties properties)
            throws SampleException {
        try {
            int sampleSize = 0;
            BufferedReader reader = new BufferedReader(inReader);
            ExampleParser parser = new ExampleParser(properties);
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] example = parser.parse(line);
                if (example != null) {
                    sampleSize++;
                }
            }
            reader.close();
            return sampleSize;
        } catch (IOException e) {
            throw new SampleException(e);
        }
    }

}
