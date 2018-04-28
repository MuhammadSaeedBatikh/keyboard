package classifiers;

import org.apache.commons.lang3.ArrayUtils;
import weka.classifiers.Classifier;
import weka.core.*;

import java.io.FileInputStream;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Created by Muhammad on 26/07/2017.
 */
public class MyClassifier {

    static String alphabetAttributesNames;   //features excluded
    static String[] alphabetAttributesNamesArray;
    static ArrayList<Attribute> alphabetAttributes = new ArrayList<>();

    static String optionsAttributesNames;   //features excluded
    static String[] optionsAttributesNamesArray;
    static ArrayList<Attribute> optionsAttributes = new ArrayList<>();

    static String alphabetClassesString;
    static String optionsClassesString;

    static String[] alphabetClassesArray;
    static String[] optionsClassesArray;

    static Classifier alphabetClassifier;
    static Classifier optionsClassifier;

    static Instances alphabetDataSet;
    static Instances optionsDataSet;
    static boolean initialized;

    public static String classify(Classifiers flag, String record) throws Exception {
        String[] frameArr = record.split(",");
        double[] inputAtt = Arrays.stream(frameArr).map(s -> Float.parseFloat(s)).mapToDouble(Float::floatValue).toArray();
        return classify(flag, inputAtt);
    }

    public static String classify(Classifiers flag, double[] record) throws Exception {
        Instance instance = createInstance(flag, record);
        double d;
        if (flag == Classifiers.ALPHABET) {
            d = alphabetClassifier.classifyInstance(instance);
            return alphabetClassesArray[(int) d];
        } else {
            d = optionsClassifier.classifyInstance(instance);
            return optionsClassesArray[(int) d];
        }
    }


    public static String classify(Classifiers flag, double[][] recordsTable) throws Exception {
        return classify(flag, reduceToSingleRecord(recordsTable));
    }

    public static String classify(Classifiers flag, String[] inputBuffer) throws Exception {
        return classify(flag, reduceToSingleRecord(inputBuffer));
    }

    public static void initializeClassifier(String alphabetModelPath, String alphabetAttributesLabels, String alphabetClasses,
                                            String optionsModelPath, String optionsAttributesLabels, String optionsClasses) throws Exception {

        if (!initialized) {
            alphabetAttributesNames = alphabetAttributesLabels;
            optionsAttributesNames = optionsAttributesLabels;

            alphabetClassesString = alphabetClasses;
            optionsClassesString = optionsClasses;

            alphabetAttributesNamesArray = alphabetAttributesNames.split(",");
            optionsAttributesNamesArray = optionsAttributesNames.split(",");

            alphabetClassesArray = alphabetClassesString.split(",");
            optionsClassesArray = optionsClassesString.split(",");


            for (int i = 0; i < alphabetAttributesNamesArray.length; i++) {
                alphabetAttributes.add(new Attribute(alphabetAttributesNamesArray[i]));
            }

            for (int i = 0; i < optionsAttributesNamesArray.length; i++) {
                optionsAttributes.add(new Attribute(optionsAttributesNamesArray[i]));
            }

            alphabetAttributes.add(new Attribute("@@class@@", (List<String>) Arrays.asList(alphabetClassesArray)));
            optionsAttributes.add(new Attribute("@@class@@", (List<String>) Arrays.asList(optionsClassesArray)));

            alphabetClassifier = (Classifier) SerializationHelper.read(new FileInputStream(alphabetModelPath));
            optionsClassifier = (Classifier) SerializationHelper.read(new FileInputStream(optionsModelPath));
            initialized = true;
        }

    }

    public static Instance createInstance(Classifiers flag, double[] record) {
        Instance instance = null;
        if (flag == Classifiers.ALPHABET) {
            alphabetDataSet = new Instances("TextInstances", alphabetAttributes, 0);
            alphabetDataSet.setClassIndex(alphabetDataSet.numAttributes() - 1);
            instance = new DenseInstance(alphabetDataSet.numAttributes());
            alphabetDataSet.add(instance);
            instance.setDataset(alphabetDataSet);

            for (int i = 0; i < record.length; i++) {
                instance.setValue(alphabetAttributes.get(i), record[i]);
            }
        } else if (flag == Classifiers.OPTIONS) {
            optionsDataSet = new Instances("TextInstances", optionsAttributes, 0);
            optionsDataSet.setClassIndex(optionsDataSet.numAttributes() - 1);
            instance = new DenseInstance(optionsDataSet.numAttributes());
            optionsDataSet.add(instance);
            instance.setDataset(optionsDataSet);

            for (int i = 0; i < record.length; i++) {
                instance.setValue(optionsAttributes.get(i), record[i]);
            }
        }
        return instance;
    }


    public static double[] reduceToSingleRecord(String[] records) {
        double[] singleRecord = Arrays.stream(records[0].split(",")).map(s -> Float.parseFloat(s)).mapToDouble(Float::floatValue).toArray();
        double[][] recordsTable = new double[records.length][singleRecord.length];
        for (int i = 1; i < records.length; i++) {
            recordsTable[i] = Arrays.stream(records[i].split(",")).map(s -> Float.parseFloat(s)).mapToDouble(Float::floatValue).toArray();
        }
        return reduceToSingleRecord(recordsTable);
    }

    public static double[] reduceToSingleRecord(double[][] recordsTable) {
        double[] result = new double[recordsTable[0].length];
        for (int i = 0; i < recordsTable[0].length; i++) {
            for (int j = 0; j < recordsTable.length; j++) {
                result[i] += recordsTable[j][i];
            }
            result[i] /= recordsTable.length;
        }
        return result;
    }



    public static String getHighestOccurrence(String[] array) {
        List<String> list = new ArrayList<String>(Arrays.asList(array));
        int max = 0;
        int curr = 0;
        String currKey = "";
        Set<String> unique = new HashSet<String>(list);

        for (String key : unique) {
            curr = Collections.frequency(list, key);
            if (max < curr) {
                max = curr;
                currKey = key;
            }
        }

       // return ArrayUtils.indexOf(alphabetClassesArray, currKey);
        return currKey;
    }
}


