package frameListener;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import frameListener.states.StatesManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MyListener extends Listener {
    long prevTime = System.currentTimeMillis();
    FileWriter trainingSet;
    String outputFileName = "ranking training set.csv";


    public MyListener() throws AWTException {
    }

    public void initialize() throws IOException {
        trainingSet = new FileWriter(new File(outputFileName), true);
    }

    @Override
    public void onConnect(com.leapmotion.leap.Controller controller) {
        System.out.println("connected");
    }

    @Override
    public void onDisconnect(com.leapmotion.leap.Controller controller) {
        System.out.println("disconnected");
        JOptionPane.showMessageDialog(null, "Leap Motion Isn't connected");
    }

    @Override
    public void onFrame(Controller controller) {
        if (true) {
            Hand hand = controller.frame().hands().rightmost();
            if (controller.frame().hands().isEmpty()) {
                StatesManager.inbox = false;
                StatesManager.setCurrentState(StatesManager.STATES.START);
                StatesManager.executeState(hand);
                prevTime = System.currentTimeMillis();
                return;
            }
            if (hand.isRight()) {
                StatesManager.inbox = true;
                StatesManager.executeState(hand);
            }
        }
    }

}
