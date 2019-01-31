package attributeSelection;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.AttributeEvaluator;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.NumericToBinary;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;
import java.util.stream.IntStream;

/**
 * <!-- globalinfo-start --> Va AttributeEval :<br/>
 * <br/>
 * Evaluates the worth of an attribute by measuring the va value with
 * respect to the class.<br/>
 * <br/>
 * Va formulas : <br/>
 * <p/>
 * <!-- globalinfo-end -->
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
 * <!-- technical-bibtex-start --> BibTeX:
 *
 * <pre>
 * &#64;inproceedings{todo,
 *    address = {todo},
 *    author = {Fadi, Fairuz, Suhel, Reza},
 *    journal title = { todo},
 *    pages = {xxx-xxx},
 *    publisher = {todo},
 *    title = {TODO},
 *    year = {2018}
 * }
 * </pre>
 * <p/>
 * <!-- technical-bibtex-end -->
 * <!-- options-end -->
 *
 * @author Fadi  (f.thabtah@gmail.com)
 * @author Suhel (suhel.hammoud@gmail.com)
 * @version $Revision: 00007777 $
 * @see Discretize
 * @see NumericToBinary
 */

enum MA_VA_FORMULA {
  FIRUZ, SUHEL;

  public static SelectedTag selectedTag(String value) {
    return new SelectedTag(value, toTags());
  }

  public static Tag[] toTags() {
    MA_VA_FORMULA[] formulas = values();
    Tag[] result = new Tag[formulas.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = new Tag(i, formulas[i].name(), formulas[i].name());
    }
    return result;
  }


};

public class Va extends ASEvaluation implements
        AttributeEvaluator, OptionHandler {

  /**
   * for serialization
   */
  static final long serialVersionUID = -3049819495125892189L;

  /**
   * Treat missing values as a seperate value
   */
  private boolean m_missing_merge;

  /**
   * Just binarize numeric attributes
   */
  private boolean m_Binarize;


  /**
   * The Va value for each attribute (suhel)
   */
  private double[] m_Va;

  public double[] getAttributesRanks() {
    return Arrays.copyOf(m_Va, m_Va.length);
  }

  /**
   * The formula used for Va
   */
  protected String m_vaFormula = MA_VA_FORMULA.FIRUZ.name();

  /**
   * Returns a string describing this attribute evaluator
   *
   * @return a description of the evaluator suitable for displaying in the
   * explorer/experimenter gui
   */
  public String globalInfo() {
    return "Va AttributeEval :\n\nEvaluates the worth of an attribute "
        + "by computing the value of the |Va| with respect to the class.\n" +
        "\n more info on :\nhttps://gitlab.com/suhel.hammoud/weka.3.8.1/blob/master/src/weka/attributeSelection";
  }

  /**
   * Constructor
   */
  public Va() {
    resetOptions();
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   **/
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> newVector = new Vector<Option>(3);
    newVector.addElement(new Option("\ttreat missing values as a separate "
        + "value.", "M", 0, "-M"));
    newVector.addElement(new Option(
        "\tjust binarize numeric attributes instead \n"
            + "\tof properly discretizing them.", "B", 0, "-B"));
    newVector.addElement(new Option(
        "\tWether to use 0=Firuz/1=Suhel formula. (default 0=Firuz)",
        "F", 1, "-F < Firuz | Suhel >"));

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
   *  treat missing values as a separate value.
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

    final String fIndex = Utils.getOption('F', options);
    m_vaFormula = MA_VA_FORMULA.valueOf(fIndex).name();
    Utils.checkForRemainingOptions(options); //only in chi, TODO: check this later
  }

  /**
   * Va Gets the current settings.
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

    options.add("-F");
    options.add("" + m_vaFormula);


    return options.toArray(new String[0]);
  }

  public String vaFormulaAttributesTipText() {
    return "vaFormula tip text";
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

  public void setVaFormula(SelectedTag newValue) {
    m_vaFormula = newValue.getSelectedTag().toString();
  }

  public SelectedTag getVaFormula() {
    return MA_VA_FORMULA.selectedTag(m_vaFormula);
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
    result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
    result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
    result.enable(Capabilities.Capability.MISSING_VALUES);

    // class
    result.enable(Capabilities.Capability.NOMINAL_CLASS);
    result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
//        result.enable(Capabilities.Capability.NUMERIC_CLASS);
//        result.enable(Capabilities.Capability.DATE_CLASS);

    return result;
  }

  /**
   * Initializes a chi-squared attribute evaluator, a InfoGain attribute evaluator,
   * and the Va Attribute evaluator all in one place. Discretizes all attributes
   * that are numeric.
   * Make new contingency table once and then use it to calculate IG, CHI, and Va
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

    // distribute missing counts if required
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

    /** ** IG ** **/
    // Compute info gains
    double[] m_InfoGains = new double[data.numAttributes()];
    for (int i = 0; i < data.numAttributes(); i++) {
      if (i != classIndex) {
        m_InfoGains[i] = (ContingencyTables.entropyOverColumns(counts[i]) - ContingencyTables
            .entropyConditionedOnRows(counts[i]));
      }
    }

    /*** Chi ***/
    // Compute chi-squared values
    double[] m_ChiSquareds = new double[data.numAttributes()];
    for (int i = 0; i < data.numAttributes(); i++) {
      if (i != classIndex) {
        m_ChiSquareds[i] = ContingencyTables.chiVal(
            ContingencyTables.reduceMatrix(counts[i]), false);
      }
    }

    /** Va (suhel) **/
    switch (MA_VA_FORMULA.valueOf(m_vaFormula)) {
      case SUHEL:
        m_Va = vaSuhel(m_InfoGains, m_ChiSquareds);
        break;
      case FIRUZ:
        m_Va = vaFiruz(m_InfoGains, m_ChiSquareds);
        break;
    }


    if (m_Va.length != m_ChiSquareds.length ||
        m_Va.length != m_InfoGains.length)
      throw new AssertionError("m_va length != m_InfoGains.length != m_ChiSquareds.lenght");
  }

  public static double[] normalizedVectorSuhel(double[] values) {
    double sqrtSumSquares = Math.sqrt(
        Arrays.stream(values)
            .map(value -> value * value)
            .sum());
    return Arrays.stream(values)
        .map(value -> value / sqrtSumSquares)
        .toArray();
  }

  /**
   * use Firuz formula to normalize the value vector
   *
   * @param values
   * @return
   */
  private static double[] normalizedVectorFiruz(double[] values) {
    double maxValue = Arrays.stream(values).max().getAsDouble(); //TODO check zero
    return Arrays.stream(values).map(v -> v / maxValue).toArray();
  }

  private static double[] vaFiruz(double[] ig, double[] chi) {

    double[] igF = normalizedVectorFiruz(ig);
    double[] chiF = normalizedVectorFiruz(chi);

    return IntStream.range(0, igF.length)
        .mapToDouble(i -> Math.sqrt(igF[i] * igF[i]
            + chiF[i] * chiF[i]))
        .toArray();
  }

  private static double[] vaSuhel(double[] ig, double[] chi) {

    double[] igF = normalizedVectorSuhel(ig);
    double[] chiF = normalizedVectorSuhel(chi);

    double[] vaTmp = IntStream.range(0, igF.length)
        .mapToDouble(i -> Math.sqrt(igF[i] * igF[i]
            + chiF[i] * chiF[i]))
        .toArray();
    return normalizedVectorSuhel(vaTmp);
  }

  /**
   * Reset options to their default values
   */
  protected void resetOptions() {
    m_Va = null;          //Va
    m_missing_merge = true;
    m_Binarize = false;
    m_vaFormula = MA_VA_FORMULA.FIRUZ.name();
  }

  /**
   * evaluates an individual attribute by measuring Its Va values
   *
   * @param attribute the index of the attribute to be evaluated
   * @return the Va value
   * @throws Exception if the attribute could not be evaluated
   */
  @Override
  public double evaluateAttribute(int attribute) throws Exception {
    return m_Va[attribute];
  }

  /**
   * Describe the attribute evaluator
   *
   * @return a description of the attribute evaluator as a string
   */
  @Override
  public String toString() {
    StringBuffer text = new StringBuffer();
    if (m_Va == null) {  //Va
      text.append("Va attribute evaluator has not been built");
    } else { //All OK
      text.append("\tVa Ranking Filter");
      if (!m_missing_merge) {
        text.append("\n\tMissing values treated as separate");
      }
      if (m_Binarize) {
        text.append("\n\tNumeric attributes are just binarized");
      }
    }
    text.append("\n\tVa Formula: " + m_vaFormula);

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
    return RevisionUtils.extract("$Revision: 00007777 $"); //arbitrary number
  }

  // ============
  // Test method.
  // ============

  /**
   * Main method for testing this class.
   *
   * @param args the options
   */
  public static void main(String[] args) {
    runEvaluator(new Va(), args);

  }


}
