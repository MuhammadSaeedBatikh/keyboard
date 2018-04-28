package frameListener.states;

import com.leapmotion.leap.Hand;
import classifiers.Classifiers;
import classifiers.Sign;

/**
 * Created by Dr.Alaa on 10/7/2017.
 */
public class StatesManager {
    static Hand hand;
    static Sign sign;
    static Classifiers classifiersMode;

    public enum STATES {START, REGULAR_CLASSIFICATION, COLLECTIVE_CLASSIFIER, READY_FOR_ACTION, PREPARE_ACTION, PERFORM_ACTION, WANT_ANOTHER_ACTION}

    ;

    public static boolean inbox = false;
    public static boolean fist = false;
    public static boolean systemStart = true;
    public static STATES currentState;


    public static void executeState(Hand handInput) {
        if (systemStart) {
            currentState = STATES.START;
            systemStart = false;
        }
        hand = handInput;
        sign = new Sign(handInput);
        if (currentState != STATES.COLLECTIVE_CLASSIFIER & currentState != STATES.REGULAR_CLASSIFICATION) {
            classifiersMode = Classifiers.OPTIONS;
            fist = sign.gatherDataAndClassify(classifiersMode).equals("0");
        } else {
            classifiersMode = Classifiers.ALPHABET;
        }
        States.classifiersMode = classifiersMode;
        States.sign = sign;
        executeCorrespondingState();
    }

    public static void executeCorrespondingState() {
        if (currentState == STATES.START) States.startState();
        else if (currentState == STATES.REGULAR_CLASSIFICATION) States.normalClassifier();
        else if (currentState == STATES.COLLECTIVE_CLASSIFIER) States.collectiveClassifier();
        else if (currentState == STATES.READY_FOR_ACTION) States.readyForAction();
        else if (currentState == STATES.PREPARE_ACTION) States.prepareAction();
        else if (currentState == STATES.PERFORM_ACTION) States.performAction();
        else if (currentState == STATES.WANT_ANOTHER_ACTION) States.wantAnotherAction();
    }


    public static void setCurrentState(STATES currentState) {
        StatesManager.currentState = currentState;

    }
    

}


//==========================================================


