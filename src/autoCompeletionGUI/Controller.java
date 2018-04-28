package autoCompeletionGUI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import screenWriter.ScreenWriter;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Controller {
    public ScreenWriter screenWriter = new ScreenWriter();
    public List<String> defaultList = Arrays.asList("√‰«", "„‰", "„«–«", "√‰ ", "Ì«");
    public static int index;
    @FXML
    private Button button0;

    @FXML
    private Button button1;

    @FXML
    private Button button2;

    @FXML
    private Button button3;

    @FXML
    private Button button4;
    
    public Controller() throws AWTException {
    }

    @FXML
    protected void button0Clicked(ActionEvent event) {
        String text = button0.getText();
        index = 0;
        screenWriter.printLetter(text);
    }

    @FXML
    protected void button1Clicked(ActionEvent event) {
        String text = button1.getText();
        index = 1;
        screenWriter.printLetter(text);
    }

    @FXML
    protected void button2Clicked(ActionEvent event) {
        String text = button2.getText();
        index = 2;
        screenWriter.printLetter(text);
    }

    @FXML
    protected void button3Clicked(ActionEvent event) {
        String text = button3.getText();
        index = 3;
        screenWriter.printLetter(text);
    }

    @FXML
    protected void button4Clicked(ActionEvent event) {
        String text = button4.getText();
        index = 4;
        screenWriter.printLetter(text);
    }

    public void setButtonsForAutoComplete(List<String> list) {
        Button[] buttons = {button0, button1, button2, button3, button4};

        if (list == null) {
            list = defaultList;
        }
        int limit = list.size() < buttons.length ? list.size() : buttons.length;

        for (int i = 0; i < limit; i++) {
            buttons[i].setText(list.get(i));
        }
    }
    public void clickButton(int buttonNumber){
        Button[] buttons = {button0, button1, button2, button3, button4};
        Button clickedButton = buttons[buttons.length - buttonNumber];
        clickedButton.fire();
    }


}
