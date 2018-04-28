package autoCompeletionGUI;

import HomeScreen.Main;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import classifiers.MyClassifier;
import frameListener.MyListener;
import spellChecker.InputAnalyzerAPI;
import frameListener.states.States;

import java.util.*;

public class AutoCompletionMain extends Application {
    public static MyListener myListener;
    static com.leapmotion.leap.Controller leapController;
    public static boolean changeList;
    public static boolean performAction;
    public static int clickedButton;
    public static List<String> suggestedList = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AutoCompletionMain.class.getResource("autoComplete.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        primaryStage.setTitle("Deaf Keyboard");
        primaryStage.setAlwaysOnTop(true);
        Scene scene = new Scene(root, 528, 20);
        scene.getStylesheets().add(getClass().getResource("ButtonStyle.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> {
            try {
                stopListener();
                new Main().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        initializeListener();
        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (true) {
                    Thread.sleep(20);
                    if (changeList | performAction) {
                        Platform.runLater(() -> {
                            if (performAction) {
                                controller.clickButton(clickedButton);
                                performAction = false;
                            } else {
                                controller.setButtonsForAutoComplete(suggestedList);
                                changeList = false;
                            }
                        });
                    }
                }
            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        primaryStage.show();
    }


    public static List<String> randomStrings() {
        List<String> arr = new ArrayList<>();
        for (int i = 0; i < Math.random() * 10; i++) {
            double rand = Math.random() * 500;
            arr.add(String.valueOf(rand));
        }
        return arr;
    }

    public static void initializeListener() throws Exception {
        myListener = new MyListener();
        leapController = new com.leapmotion.leap.Controller();
        myListener.initialize();
        String alphabetAttributesNames =
                "F0distalX,F0distalY,F0distalZ,F0IntermediateX,F0IntermediateY,F0IntermediateZ,F0ProximalX,F0ProximalY,F0ProximalZ,F0TipDistanceToPalm," +
                        "F1distalX,F1distalY,F1distalZ,F1IntermediateX,F1IntermediateY,F1IntermediateZ,F1ProximalX,F1ProximalY,F1ProximalZ,F1TipDistanceToPalm," +
                        "F2distalX,F2distalY,F2distalZ,F2IntermediateX,F2IntermediateY,F2IntermediateZ,F2ProximalX,F2ProximalY,F2ProximalZ,F2TipDistanceToPalm," +
                        "F3distalX,F3distalY,F3distalZ,F3IntermediateX,F3IntermediateY,F3IntermediateZ,F3ProximalX,F3ProximalY,F3ProximalZ,F3TipDistanceToPalm," +
                        "F4distalX,F4distalY,F4distalZ,F4IntermediateX,F4IntermediateY,F4IntermediateZ,F4ProximalX,F4ProximalY,F4ProximalZ,F4TipDistanceToPalm," +
                        "grabAngle,pinchDistance,armPitch,armYaw,armRoll,wristPositionX,wristPositionY,wristPositionZ,sphereCenterX,sphereCenterY,sphereCenterZ," +
                        "sphereRadius";

        String optionsAttributesNames =
                "F0distalX,F0distalY,F0distalZ,F0IntermediateX,F0IntermediateY,F0IntermediateZ,F0ProximalX,F0ProximalY,F0ProximalZ,F0TipDistanceToPalm," +
                        "F1distalX,F1distalY,F1distalZ,F1IntermediateX,F1IntermediateY,F1IntermediateZ,F1ProximalX,F1ProximalY,F1ProximalZ,F1TipDistanceToPalm," +
                        "F2distalX,F2distalY,F2distalZ,F2IntermediateX,F2IntermediateY,F2IntermediateZ,F2ProximalX,F2ProximalY,F2ProximalZ,F2TipDistanceToPalm," +
                        "F3distalX,F3distalY,F3distalZ,F3IntermediateX,F3IntermediateY,F3IntermediateZ,F3ProximalX,F3ProximalY,F3ProximalZ,F3TipDistanceToPalm," +
                        "F4distalX,F4distalY,F4distalZ,F4IntermediateX,F4IntermediateY,F4IntermediateZ,F4ProximalX,F4ProximalY,F4ProximalZ,F4TipDistanceToPalm," +
                        "grabAngle,pinchDistance,armPitch,armYaw,armRoll";

        //                 1    2   3    4   5     6    7    8    9    10  11  12    13    14   15   16   17  18         19         20  21   22   23   24   25  26  27  28
        String alphabetClasses = "alph,baa,taa,thaa,geem,hhaa,khaa,daal,tzaal,raa,zaay,seen,sheen,saad,daad,ttaa,zaa,ayin,faa,qaaf,kaaf,laam,meem,noon,haa,wau,yaa,gheenToBeDeleted";


        String optionsClasses = "1,2,0,3,4,5,horns,OK,gun,yaa_horns";
        String alphabetModelPath = "alphabet-svm-cross12.model";
        String optionsModelPath = "options SMO.model";

        MyClassifier.initializeClassifier(alphabetModelPath, alphabetAttributesNames, alphabetClasses,
                optionsModelPath, optionsAttributesNames, optionsClasses
        );
        InputAnalyzerAPI.initialize();
        States.initializeStates();
        leapController.addListener(myListener);
    }

    public static void stopListener(){
        leapController.removeListener(myListener);
        States.flush();
    }
}
