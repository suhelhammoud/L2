package weka.filters.supervised.instance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.SupervisedFilter;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

/**
 * <!-- globalinfo-start -->
 * Produces an unbalanced random sample (with replacement)
 * Dataset must have a binary class attribute
 * If the first class ratio is equals to -1 then keep same class distribution
 * in the newly generated dataset
 * <p/>
 * <!-- globalinfo-end -->
 * <p>
 * <!-- options-start --> Valid options are:
 * <p/>
 * <pre>
 * -S &lt;num&gt;
 *  Specify the random number seed (default 1)
 * </pre>
 *
 * <pre>
 * -Z &lt;num&gt;
 *  The size of the output dataset , as a ratio of
 *  the input dataset (default 1.0)
 * </pre>
 * *
 * <pre>
 * -U &lt;num&gt;
 *  The ratio of the first class compared to the size of output dataset
 *  (default -1)
 *  If equals to -1 then keep same class distribution as in the input dataset
 * </pre>
 * <p>
 * <!-- options-end -->
 *
 * @author Suhel Hammoud (suhel.hammoud@gmail.com)
 * @version $Revision: 01 $
 */
public class ImbalanceClassSampler extends Filter implements SupervisedFilter, OptionHandler {


    static Logger logger = LoggerFactory.getLogger(ImbalanceClassSampler.class.getName());

    /**
     * for serialization.
     */
    static final long serialVersionUID = 7079001111111100686L;

    /**
     * The out data size, as a ratio to original dataset, default 1.0.
     */
    protected double m_SampleSizeRatio = 1.0;

    /**
     * The 1st class size as ratio to output dataset default -1.
     * (-1 means keep same class distribution as input dataset)
     */
    protected double m_sampleClassRatio = -1;


    /**
     * The random number generator seed.
     */
    protected int m_RandomSeed = 1;

    /**
     * Returns a string describing this filter.
     *
     * @return a description of the filter suitable for displaying in the
     * explorer/experimenter gui
     */
    public String globalInfo() {
        return "Produces an unbalanced random sample (with replacement)" +
                "Input dataset must have a binary class attribute" +
                "If the ratio of the first class is set to -1 then keep same class distribution" +
                "in the newly generated dataset";
    }

    /**
     * Returns an enumeration describing the available options.
     *
     * @return an enumeration of all the available options.
     */
    @Override
    public Enumeration<Option> listOptions() {

        Vector<Option> result = new Vector<Option>(5);
        result.addElement(new Option(
                "\tSpecify the random number seed (default 1)",
                "S", 1, "-S <num>"));
        result.addElement(new Option(
                "\tThe size of the output dataset, as a ratio of\n"
                        + "\tthe input dataset (default 1.0)",
                "Z", 1, "-Z <num>"));

        result.addElement(new Option(
                "\tRatio of first class label in output generated dataset ]0.0 ~ 1.0[ \n"
                        + "\tthe input dataset (default -1, keep same class distribution)",
                "U", 1, "-U <num>"));

        return result.elements();
    }

    /**
     * Parses a given list of options.
     * <p/>
     * <p>
     * <!-- options-start --> Valid options are:
     * <p/>
     *
     * <pre>
     * -S &lt;num&gt;
     *  Specify the random number seed (default 1)
     * </pre>
     *
     * <pre>
     * -Z &lt;num&gt;
     *  The size of the output dataset, as a ratio of
     *  the input dataset (default 1.0)
     * </pre>
     *
     * <pre>
     * -U &lt;num&gt;
     *  Ratio of first class label in output generated dataset ]0.0 ~ 1.0[
     *  (default -1, keep same distribution)
     * </pre>
     *
     * <p>
     * <!-- options-end -->
     *
     * @param options the list of options as an array of strings
     * @throws Exception if an option is not supported
     */
    @Override
    public void setOptions(String[] options) throws Exception {
        String tmpStr;

        tmpStr = Utils.getOption('S', options);
        if (tmpStr.length() != 0) {
            setRandomSeed(Integer.parseInt(tmpStr));
        } else {
            setRandomSeed(1);
        }

        tmpStr = Utils.getOption('Z', options);
        if (tmpStr.length() != 0) {
            setSampleSizeRatio(Double.parseDouble(tmpStr));
        } else {
            setSampleSizeRatio(1.0);
        }

        tmpStr = Utils.getOption('U', options);
        if (tmpStr.length() != 0) {
            setSampleClassRatio(Double.parseDouble(tmpStr));
        } else {
            setSampleClassRatio(-1);
        }

        if (getInputFormat() != null) {
            setInputFormat(getInputFormat());
        }

        Utils.checkForRemainingOptions(options);
    }

    /**
     * Gets the current settings of the filter.
     *
     * @return an array of strings suitable for passing to setOptions
     */
    @Override
    public String[] getOptions() {

        Vector<String> result = new Vector<String>();
        result.add("-S");
        result.add("" + getRandomSeed());
        result.add("-Z");
        result.add("" + getSampleSizeRatio());
        result.add("-U");
        result.add("" + getSampleClassRatio());
        return result.toArray(new String[result.size()]);
    }

    /**
     * Returns the tip text for this property.
     *
     * @return tip text for this property suitable for displaying in the
     * explorer/experimenter gui
     */
    public String randomSeedTipText() {
        return "Sets the random number seed for sampling.";
    }

    /**
     * Gets the random number seed.
     *
     * @return the random number seed.
     */
    public int getRandomSeed() {
        return m_RandomSeed;
    }

    /**
     * Sets the random number seed.
     *
     * @param newSeed the new random number seed.
     */
    public void setRandomSeed(int newSeed) {
        m_RandomSeed = newSeed;
    }

    /**
     * Returns the tip text for this property.
     *
     * @return tip text for this property suitable for displaying in the
     * explorer/experimenter gui
     */
    public String sampleSizeRatioTipText() {
        return "The sample size as a ratio of the original dataset.";
    }

    /**
     * Gets the subsample size as a ratio of the original set.
     *
     * @return the sample size ratio
     */
    public double getSampleSizeRatio() {
        return m_SampleSizeRatio;
    }

    /**
     * Sets the size of the subsample, as a ratio of the original set.
     *
     * @param sampleSizeRatio the sample set size, between 0 and 1.0.
     */
    public void setSampleSizeRatio(double sampleSizeRatio) {
        m_SampleSizeRatio = sampleSizeRatio;
    }

    /**
     * Returns the tip text for this property.
     *
     * @return tip text for this property suitable for displaying in the
     * explorer/experimenter gui
     */
    public String sampleClassRatioTipText() {
        return "The sample first class size as a ratio of the output dataset.";
    }

    /**
     * Gets the sample size as a ratio of the original set.
     *
     * @return the sample size
     */
    public double getSampleClassRatio() {
        return m_sampleClassRatio;
    }

    /**
     * Sets the size of the first class samples, as a ratio of the output dataset.
     *
     * @param sampleClassRatio the sample set size, between 0 and 1.0.
     */
    public void setSampleClassRatio(double sampleClassRatio) {
        m_sampleClassRatio = sampleClassRatio;
    }

    /**
     * Returns the Capabilities of this filter.
     *
     * @return the capabilities of this object
     * @see Capabilities
     */
    @Override
    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();
        result.disableAll();

        // attributes
        result.enableAllAttributes();
        result.enable(Capabilities.Capability.MISSING_VALUES);

        // class
        result.enable(Capabilities.Capability.BINARY_CLASS);
        return result;
    }

    /**
     * Sets the format of the input instances.
     *
     * @param instanceInfo an Instances object containing the input instance
     *                     structure (any instances contained in the object are ignored -
     *                     only the structure is required).
     * @return true if the outputFormat may be collected immediately
     * @throws Exception if the input format can't be set successfully
     */
    @Override
    public boolean setInputFormat(Instances instanceInfo) throws Exception {
        super.setInputFormat(instanceInfo);
        setOutputFormat(instanceInfo);
        return true;
    }

    /**
     * Input an instance for filtering. Filter requires all training instances be
     * read before producing output.
     *
     * @param instance the input instance
     * @return true if the filtered instance may now be collected with output().
     * @throws IllegalStateException if no input structure has been defined
     */
    @Override
    public boolean input(Instance instance) {
        if (getInputFormat() == null) {
            throw new IllegalStateException("No input instance format defined");
        }
        if (m_NewBatch) {
            resetQueue();
            m_NewBatch = false;
        }
        if (isFirstBatchDone()) {
            push(instance);
            return true;
        } else {
            bufferInput(instance);
            return false;
        }
    }

    /**
     * Signify that this batch of input to the filter is finished. If the filter
     * requires all instances prior to filtering, output() may now be called to
     * retrieve the filtered instances.
     *
     * @return true if there are instances pending output
     * @throws IllegalStateException if no input structure has been defined
     */
    @Override
    public boolean batchFinished() {
        if (getInputFormat() == null) {
            throw new IllegalStateException("No input instance format defined");
        }
        if (!isFirstBatchDone()) {
            // Do the subsample, and clear the input instances.
            createSubsample();
        }
        flushInput();
        m_NewBatch = true;
        m_FirstBatchDone = true;
        return (numPendingOutput() != 0);
    }

    /**
     * Creates a subsample of the current set of input instances. The output
     * instances are pushed onto the output queue for collection.
     */
    protected void createSubsample() {

        Instances data = getInputFormat();

        // Count num instances in each class
        int[] numInstancesPerClass = new int[2];
        for (Instance instance : data) {
            numInstancesPerClass[(int) instance.classValue()]++;
        }

        if (Arrays.stream(numInstancesPerClass).anyMatch(i -> i == 0)) {
            logger.error("Data contains less than 2 class labels");
            return;
        }

        // Collect data per class
        Instance[][] instancesPerClass = new Instance[2][];
        instancesPerClass[0] = new Instance[numInstancesPerClass[0]];
        instancesPerClass[1] = new Instance[numInstancesPerClass[1]];

        int[] counterPerClass = new int[2];
        for (Instance instance : data) {
            int classValue = (int) instance.classValue();
            instancesPerClass[classValue][counterPerClass[classValue]++] = instance;
        }

        // Determine how much data we want for each class
        int numOutSamples = (int) Math.round(data.numInstances() * m_SampleSizeRatio);

        //default ratio value is same as distribution of original dataset
        double ratio = (double) numInstancesPerClass[0] / (double) data.numInstances();
        if (m_sampleClassRatio > 0 && m_sampleClassRatio < 1.0) {
            ratio = m_sampleClassRatio;
//            logger.info("new ratio = {}", ratio);
        }
        int[] numInstancesToSample = new int[2];
        numInstancesToSample[0] = (int) (ratio * numOutSamples);
        numInstancesToSample[1] = numOutSamples - numInstancesToSample[0];

        //Now do the sampling
        Random random = new Random(m_RandomSeed);
        for (int i = 0; i < data.numClasses(); i++) {
            int numEligible = numInstancesPerClass[i];
            for (int j = 0; j < numInstancesToSample[i]; j++) {
                // Sampling with replacement
                push(instancesPerClass[i][random.nextInt(numEligible)]);
            }
        }
    }

    /**
     * Returns the revision string.
     *
     * @return the revision
     */
    @Override
    public String getRevision() {
        return RevisionUtils.extract("$Revision: 01 $");
    }

    /**
     * Main method for testing this class.
     *
     * @param argv should contain arguments to the filter: use -h for help
     */
    public static void main(String[] argv) {
        runFilter(new Resample(), argv);
    }
}
