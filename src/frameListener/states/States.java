package frameListener.states;

import autoCompeletionGUI.AutoCompletionMain;
import classifiers.Classifiers;
import classifiers.MyClassifier;
import classifiers.Sign;
import screenWriter.ScreenWriter;
import spellChecker.InputAnalyzerAPI;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dr.Alaa on 10/7/2017.
 */
public class States {
    static ScreenWriter screenWriter;
    static StringBuilder writtenPart = new StringBuilder("");
    public static Sign sign;
    public static String[] optionClassifiedFrames = new String[10];
    public static String[] alphaClassifiedFrames = new String[10];
    static List<String> suggestedList = new ArrayList<>();
    public static Classifiers classifiersMode;
    private static int currFrame = 0;
    private static int performCurrFrame = 0;
    private static boolean printAnotherWord;

    public States() throws AWTException {
    }

    public static void initializeStates() throws AWTException {
        System.out.println("done");
        screenWriter = new ScreenWriter();
    }

    public static void startState() {
        if (StatesManager.inbox) {
            StatesManager.setCurrentState(StatesManager.STATES.REGULAR_CLASSIFICATION);
        } else {
            return;
        }
    }

    public static void normalClassifier() {
        sign.gatherDataAndClassify(Classifiers.ALPHABET);
        String classifiedSign = sign.gatherDataAndClassify(Classifiers.ALPHABET);
        alphaClassifiedFrames[currFrame] = classifiedSign;
        currFrame++;
        if (currFrame == 10) {
            StatesManager.setCurrentState(StatesManager.STATES.COLLECTIVE_CLASSIFIER);
        }
    }

    public static void collectiveClassifier() {
        String maxClassifiedLetter = MyClassifier.getHighestOccurrence(alphaClassifiedFrames);
        currFrame = 0;
        StatesManager.setCurrentState(StatesManager.STATES.READY_FOR_ACTION);
        writtenPart.append(screenWriter.lettersMap.get(maxClassifiedLetter));
        suggestedList = InputAnalyzerAPI.inputAnalyzer(writtenPart.toString());
        screenWriter.printLetter(maxClassifiedLetter);
        System.out.println("class " + writtenPart);
        AutoCompletionMain.suggestedList = suggestedList;
        AutoCompletionMain.changeList = true;
        //spellchecker here
        //list of suggestions
        //change GUI
    }

    public static void readyForAction() {
        if (StatesManager.inbox) {
            String classifiedSign = sign.gatherDataAndClassify(Classifiers.OPTIONS);
            if (classifiedSign.equals("0")) {
                StatesManager.setCurrentState(StatesManager.STATES.PREPARE_ACTION);
            }
        } else {
            StatesManager.setCurrentState(StatesManager.STATES.START);
        }
    }

    public static void prepareAction() {
        if (!StatesManager.fist) {
            if (StatesManager.inbox) {
                StatesManager.setCurrentState(StatesManager.STATES.PERFORM_ACTION);
            } else if (!StatesManager.inbox) {
                StatesManager.setCurrentState(StatesManager.STATES.START);
            }
        }
    }

    public static void performAction() {
        if (!StatesManager.inbox) {
            StatesManager.setCurrentState(StatesManager.STATES.START);
            printAnotherWord = false;
        } else {
            String classified = sign.gatherDataAndClassify(classifiersMode);
            if (!classified.equals("0")) {
                optionClassifiedFrames[performCurrFrame] = classified;
                performCurrFrame++;
                if (performCurrFrame == 10) {
                    String maxClassifiedLetter = MyClassifier.getHighestOccurrence(optionClassifiedFrames);
                    System.out.println("maxClassifiedLetter = " + maxClassifiedLetter);
                    performCurrFrame = 0;
                    StatesManager.setCurrentState(StatesManager.STATES.WANT_ANOTHER_ACTION);
                    boolean isNumber = screenWriter.getAction(maxClassifiedLetter) == null;
                    System.out.println("get action " + screenWriter.getAction(maxClassifiedLetter) + "  action: " + maxClassifiedLetter);
                    System.out.println("isNumber = " + isNumber);
                    boolean deleteAction = false;
                    if (!isNumber) {
                        deleteAction = screenWriter.getAction(maxClassifiedLetter).equalsIgnoreCase("delete");
                    }
                    if (isNumber) {
                        int buttonNumber = Integer.valueOf(maxClassifiedLetter);
                        String chosenWord = suggestedList.get(5 - buttonNumber);
                        String[] chunck = InputAnalyzerAPI.chunkInput(writtenPart);
                        String prefix = chunck[1];
                        int prefixLength = prefix == null ? 0 : prefix.length();
                        if (prefix == chosenWord.substring(0, prefixLength)) {
                            writtenPart.append(chosenWord.substring(prefixLength) + " ");
                        } else {
                            writtenPart.setLength(writtenPart.length() - prefixLength);
                            writtenPart = writtenPart.append(chosenWord + " ");
                        }
                        String[] split = writtenPart.toString().split(" ");
                        if (split.length > 1) {
                            String chosWrord = split[split.length - 1];
                            String previousWord = split[split.length - 2];
                            try {
                                InputAnalyzerAPI.userChooseWord(chosWrord, previousWord);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        screenWriter.deleteMultipleLetters(prefixLength);
                        System.out.println("chosenWord = " + chosenWord);
                        AutoCompletionMain.clickedButton = buttonNumber;
                        AutoCompletionMain.performAction = true;
                        System.out.println("writtenPart = " + writtenPart);
                    } else if (deleteAction) {
                        screenWriter.deleteLetter();
                        writtenPart.setLength(writtenPart.length() - 1);
                        System.out.println("writtenPart after deletion = " + writtenPart);
                        suggestedList = InputAnalyzerAPI.inputAnalyzer(writtenPart.toString());
                        AutoCompletionMain.suggestedList = suggestedList;
                        AutoCompletionMain.changeList = true;
                    } else {
                        screenWriter.performKeyboardAction(maxClassifiedLetter);
                        String action = screenWriter.getAction(maxClassifiedLetter);
                        if (action.equalsIgnoreCase("enter")||action.equalsIgnoreCase("space")){
                            suggestedList = InputAnalyzerAPI.inputAnalyzer(writtenPart.toString());
                            AutoCompletionMain.suggestedList = suggestedList;
                            AutoCompletionMain.changeList = true;
                        }
                        if (action.equalsIgnoreCase("shift")){
                            screenWriter.clickShift();
                        }
                    }
                }
            }
        }

    }

    public static void wantAnotherAction() {
        if (StatesManager.fist)
            StatesManager.setCurrentState(StatesManager.STATES.PREPARE_ACTION);
        else if (!StatesManager.inbox)
            StatesManager.setCurrentState(StatesManager.STATES.START);

    }

    public static void flush() {
        writtenPart = new StringBuilder("");
        optionClassifiedFrames = new String[10];
        alphaClassifiedFrames = new String[10];
        suggestedList = new ArrayList<>();
        currFrame = 0;
        performCurrFrame = 0;
        printAnotherWord = false;
    }
}

