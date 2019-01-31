package attributeSelection;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.AttributeEvaluator;
import weka.core.*;
import weka.core.Capabilities.Capability;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.NumericToBinary;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * <!-- globalinfo-start --> L2AttributeEval :<br/>
 * <br/>
 * Evaluates the worth of an attribute by computing the value of the L2
 * statistic with respect to the class.<br/>
 * L2 a feature selection method called "Least Loss". It quantifies the distance between the observed and expected probabilities of features.
 * <p>
 * This code is based on ChiSquaredAttributeEval.java, Eibe Frank (eibe@cs.waikato.ac.nz), version $Revision: 10330 $
 * <p/>
 * <!-- globalinfo-end -->
 * <p>
 * <!-- technical-bibtex-start --> BibTeX:
 *
 * <pre>
 * &#64;inproceedings{todo,
 *    address = {todo},
 *    author = {Fadi, Fairuz, Suhel, Reza},
 *    journal title = { todo},
 *    pages = {xxx-xxx},
 *    publisher = {todo},
 *    title = {A New Feature Selection Method based on
 * Simplified Observed and Expected Likelihoods Distance},
 *    year = {2018}
 * }
 * </pre>
 * <p/>
 * <!-- technical-bibtex-end -->
 * <!-- options-start --> Valid options are:
 * <p/>
 * <p>
 * <pre>
 * -M
 *  treat missing values as a seperate value.
 * </pre>
 * <p>
 * <pre>
 * -B
 *  just binarize numeric attributes instead
 *  of properly discretizing them.
 * </pre>
 * <p>
 * <!-- options-end -->
 *
 * @author S. Hammoud (suhel.hammoud@gmail.com)
 * @author F. Thabtah (f.thabtah@gmail.com)
 * @version $Revision: 0.0.0.0 $
 * @see Discretize
 * @see NumericToBinary
 */
public class L2AttributeEval extends ASEvaluation implements
        AttributeEvaluator, OptionHandler {

  /**
   * for serialization
   */
  static final long serialVersionUID = -8316857822521717692L;

  /**
   * Treat missing values as a seperate value
   */
  private boolean m_missing_merge;

  /**
   * Just binarize numeric attributes
   */
  private boolean m_Binarize;

  /**
   * The L2 value for each attribute
   */
  private double[] m_L2;

  /**
   * Returns a string describing this attribute evaluator
   *
   * @return a description of the evaluator suitable for displaying in the
   * explorer/experimenter gui
   */
  public String globalInfo() {
    return "L2AttributeEval :\n\nEvaluates the worth of an attribute "
        + "by computing the value of the L2 statistic with respect to the class.\n";
  }

  /**
   * Constructor
   */
  public L2AttributeEval() {
    resetOptions();
  }

  /**
   * Returns an enumeration describing the available options
   *
   * @return an enumeration of all the available options
   **/
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> newVector = new Vector<Option>(2);
    newVector.addElement(new Option("\ttreat missing values as a seperate "
        + "value.", "M", 0, "-M"));
    newVector.addElement(new Option(
        "\tjust binarize numeric attributes instead \n"
            + "\tof properly discretizing them.", "B", 0, "-B"));
    return newVector.elements();
  }

  /**
   * Parses a given list of options.
   * <p/>
   * <p>
   * <!-- options-start --> Valid options are:
   * <p/>
   * <p>
   * <pre>
   * -M
   *  treat missing values as a seperate value.
   * </pre>
   * <p>
   * <pre>
   * -B
   *  just binarize numeric attributes instead
   *  of properly discretizing them.
   * </pre>
   * <p>
   * <!-- options-end -->
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    resetOptions();
    setMissingMerge(!(Utils.getFlag('M', options)));
    setBinarizeNumericAttributes(Utils.getFlag('B', options));

    Utils.checkForRemainingOptions(options);
  }

  /**
   * Gets the current settings.
   *
   * @return an array of strings suitable for passing to setOptions()
   */
  @Override
  public String[] getOptions() {
    Vector<String> options = new Vector<String>();
    if (!getMissingMerge()) {
      options.add("-M");
    }
    if (getBinarizeNumericAttributes()) {
      options.add("-B");
    }
    return options.toArray(new String[0]);
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   * explorer/experimenter gui
   */
  public String binarizeNumericAttributesTipText() {
    return "Just binarize numeric attributes instead of properly discretizing them.";
  }

  /**
   * Binarize numeric attributes.
   *
   * @param b true=binarize numeric attributes
   */
  public void setBinarizeNumericAttributes(boolean b) {
    m_Binarize = b;
  }

  /**
   * get whether numeric attributes are just being binarized.
   *
   * @return true if missing values are being distributed.
   */
  public boolean getBinarizeNumericAttributes() {
    return m_Binarize;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   * explorer/experimenter gui
   */
  public String missingMergeTipText() {
    return "Distribute counts for missing values. Counts are distributed "
        + "across other values in proportion to their frequency. Otherwise, "
        + "missing is treated as a separate value.";
  }

  /**
   * distribute the counts for missing values across observed values
   *
   * @param b true=distribute missing values.
   */
  public void setMissingMerge(boolean b) {
    m_missing_merge = b;
  }

  /**
   * get whether missing values are being distributed or not
   *
   * @return true if missing values are being distributed.
   */
  public boolean getMissingMerge() {
    return m_missing_merge;
  }

  /**
   * Returns the capabilities of this evaluator.
   *
   * @return the capabilities of this evaluator
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();
    result.disableAll();

    // attributes
    result.enable(Capability.NOMINAL_ATTRIBUTES);
    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.DATE_ATTRIBUTES);
    result.enable(Capability.MISSING_VALUES);

    // class
    result.enable(Capability.NOMINAL_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    return result;
  }

  /**
   * Initializes a L2 attribute evaluator. Discretizes all attributes
   * that are numeric.
   *
   * @param data set of instances serving as training dataset
   * @throws Exception if the evaluator has not been generated successfully
   */
  @Override
  public void buildEvaluator(Instances data) throws Exception {

    // can evaluator handle dataset?
    getCapabilities().testWithFail(data);

    int classIndex = data.classIndex();
    int numInstances = data.numInstances();

    if (!m_Binarize) {
      Discretize disTransform = new Discretize();
      disTransform.setUseBetterEncoding(true);
      disTransform.setInputFormat(data);
      data = Filter.useFilter(data, disTransform);
    } else {
      NumericToBinary binTransform = new NumericToBinary();
      binTransform.setInputFormat(data);
      data = Filter.useFilter(data, binTransform);
    }
    int numClasses = data.attribute(classIndex).numValues();

    // Reserve space and initialize counters
    double[][][] counts = new double[data.numAttributes()][][];
    for (int k = 0; k < data.numAttributes(); k++) {
      if (k != classIndex) {
        int numValues = data.attribute(k).numValues();
        counts[k] = new double[numValues + 1][numClasses + 1];
      }
    }

    // Initialize counters
    double[] temp = new double[numClasses + 1];
    for (int k = 0; k < numInstances; k++) {
      Instance inst = data.instance(k);
      if (inst.classIsMissing()) {
        temp[numClasses] += inst.weight();
      } else {
        temp[(int) inst.classValue()] += inst.weight();
      }
    }
    for (int k = 0; k < counts.length; k++) {
      if (k != classIndex) {
        for (int i = 0; i < temp.length; i++) {
          counts[k][0][i] = temp[i];
        }
      }
    }

    // Get counts
    for (int k = 0; k < numInstances; k++) {
      Instance inst = data.instance(k);
      for (int i = 0; i < inst.numValues(); i++) {
        if (inst.index(i) != classIndex) {
          if (inst.isMissingSparse(i) || inst.classIsMissing()) {
            if (!inst.isMissingSparse(i)) {
              counts[inst.index(i)][(int) inst.valueSparse(i)][numClasses] += inst
                  .weight();
              counts[inst.index(i)][0][numClasses] -= inst.weight();
            } else if (!inst.classIsMissing()) {
              counts[inst.index(i)][data.attribute(inst.index(i)).numValues()][(int) inst
                  .classValue()] += inst.weight();
              counts[inst.index(i)][0][(int) inst.classValue()] -= inst
                  .weight();
            } else {
              counts[inst.index(i)][data.attribute(inst.index(i)).numValues()][numClasses] += inst
                  .weight();
              counts[inst.index(i)][0][numClasses] -= inst.weight();
            }
          } else {
            counts[inst.index(i)][(int) inst.valueSparse(i)][(int) inst
                .classValue()] += inst.weight();
            counts[inst.index(i)][0][(int) inst.classValue()] -= inst.weight();
          }
        }
      }
    }
    /* print contingency tables */
//    IntStream.range(0, counts.length)
//            .filter(i -> i != classIndex)
//            .forEachOrdered(i -> {
//              System.out.println("------------------------------------");
//              System.out.println("Attribute_ " + i);
//              System.out.println(printContingencyTable(counts[i]));
//            });

    /* distribute missing counts if required */
    if (m_missing_merge) {

      for (int k = 0; k < data.numAttributes(); k++) {
        if (k != classIndex) {
          int numValues = data.attribute(k).numValues();

          // Compute marginals
          double[] rowSums = new double[numValues];
          double[] columnSums = new double[numClasses];
          double sum = 0;
          for (int i = 0; i < numValues; i++) {
            for (int j = 0; j < numClasses; j++) {
              rowSums[i] += counts[k][i][j];
              columnSums[j] += counts[k][i][j];
            }
            sum += rowSums[i];
          }

          if (Utils.gr(sum, 0)) {
            double[][] additions = new double[numValues][numClasses];

            // Compute what needs to be added to each row
            for (int i = 0; i < numValues; i++) {
              for (int j = 0; j < numClasses; j++) {
                additions[i][j] = (rowSums[i] / sum) * counts[k][numValues][j];
              }
            }

            // Compute what needs to be added to each column
            for (int i = 0; i < numClasses; i++) {
              for (int j = 0; j < numValues; j++) {
                additions[j][i] += (columnSums[i] / sum)
                    * counts[k][j][numClasses];
              }
            }

            // Compute what needs to be added to each cell
            for (int i = 0; i < numClasses; i++) {
              for (int j = 0; j < numValues; j++) {
                additions[j][i] += (counts[k][j][i] / sum)
                    * counts[k][numValues][numClasses];
              }
            }

            // Make new contingency table
            double[][] newTable = new double[numValues][numClasses];
            for (int i = 0; i < numValues; i++) {
              for (int j = 0; j < numClasses; j++) {
                newTable[i][j] = counts[k][i][j] + additions[i][j];
              }
            }
            counts[k] = newTable;
          }
        }
      }
    }


    /* Compute L2 values */
    m_L2 = new double[data.numAttributes()];
    for (int i = 0; i < data.numAttributes(); i++) {
      if (i != classIndex) {
        m_L2[i] = l2Val(ContingencyTables.reduceMatrix(counts[i]));
      }
    }
  }

  /**
   * String representation of contingency table, used for debugging
   *
   * @param ct
   * @return String representation of ct table
   */
  private String printContingencyTable(double[][] ct) {

    int[][] intCt = Arrays.stream(ct)
        .map(v -> (Arrays.stream(v).mapToInt(i -> (int) i).toArray()))
        .toArray(int[][]::new);

    return Arrays.stream(intCt)
        .map(v -> Arrays.stream(v)
            .mapToObj(String::valueOf)
            .collect(Collectors.joining(",")))
        .collect(Collectors.joining(System.lineSeparator()));
  }

  /**
   * Reset options to their default values
   */
  protected void resetOptions() {
    m_L2 = null;
    m_missing_merge = true;
    m_Binarize = false;
  }

  /**
   * Evaluates an individual attribute by measuring its L2 value.
   *
   * @param attribute the index of the attribute to be evaluated
   * @return the L2 value
   * @throws Exception if the attribute could not be evaluated
   */
  @Override
  public double evaluateAttribute(int attribute) throws Exception {

    return m_L2[attribute];
  }

  /**
   * Describe the attribute evaluator
   *
   * @return a description of the attribute evaluator as a string
   */
  @Override
  public String toString() {
    StringBuffer text = new StringBuffer();

    if (m_L2 == null) {
      text.append("L2 attribute evaluator has not been built");
    } else {
      text.append("\tL2 Ranking Filter");
      if (!m_missing_merge) {
        text.append("\n\tMissing values treated as seperate");
      }
      if (m_Binarize) {
        text.append("\n\tNumeric attributes are just binarized");
      }
    }
    text.append("\n");
    return text.toString();
  }

  /**
   * Returns the revision string.
   *
   * @return the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 0.0.0.0 $");
  }

  /**
   * Computes L2 value for one cell in a contingency table.
   *
   * @param freq     the observed frequency in the cell
   * @param expected the expected frequency in the cell
   * @return the L2 value for that cell; 0 if the expected value is
   * too close to zero
   */
  private static double l2Cell(double freq, double expected) {
    // Cell in empty row and column?
    if (expected < 1e-15) return 0;

    // Compute difference between observed and expected value
    double diff = Math.abs(freq - expected);

    // Return L2 value for the cell
    return diff * diff;
  }

  /**
   * Computes L2 statistic for a contingency table.
   *
   * @param matrix the contigency table
   * @return the value of the L2 statistic
   */
  private static double l2Val(double[][] matrix) {

    int df, nrows, ncols, row, col;
    double[] rtotal, ctotal;
    double expect = 0, l2val = 0, n = 0;

    nrows = matrix.length;
    ncols = matrix[0].length;
    rtotal = new double[nrows];
    ctotal = new double[ncols];
    for (row = 0; row < nrows; row++) {
      for (col = 0; col < ncols; col++) {
        rtotal[row] += matrix[row][col];
        ctotal[col] += matrix[row][col];
        n += matrix[row][col];
      }
    }
    df = (nrows - 1) * (ncols - 1);
    if (df <= 0) {
      return 0;
    }
    l2val = 0.0;
    for (row = 0; row < nrows; row++) {
      if (Utils.gr(rtotal[row], 0)) {
        for (col = 0; col < ncols; col++) {
          if (Utils.gr(ctotal[col], 0)) {
            /* another suggested calculatino reduction, gives almost indentical results
             * expect = (ctotal[col] * rtotal[row]) / n ; //only frequecny
             * l2val += l2Cell(matrix[row][col] , expect);//only frequency
             * */
            expect = (ctotal[col] * rtotal[row]) / n / n;
            l2val += l2Cell(matrix[row][col] / n, expect);
          }
        }
      }
    }
    return l2val;
  }

  /**
   * Main method.
   *
   * @param args the options
   */
  public static void main(String[] args) {
    runEvaluator(new L2AttributeEval(), args);
  }
}
