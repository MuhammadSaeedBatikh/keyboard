package HomeScreen;

import autoCompeletionGUI.AutoCompletionMain;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    @FXML
    public void play(MouseEvent event) throws Exception {
        System.out.println("HomeController.play");
        ((Node) (event.getSource())).getScene().getWindow().hide();
        new AutoCompletionMain().start(new Stage());
    }

    @FXML
    public void help() {
        System.out.println("HomeController.help");
        // create a jframe
        JFrame frame = new JFrame("JOptionPane showMessageDialog example");
        // show a joptionpane dialog using showMessageDialog
        String  help ="\t\tلوحة مفاتيح الأصم\t\t" +
                "\n" +
                "\t\tإضغط إبدأ للتشغيل\t\t" +
                "\n" +
                "نفذ الحرف الذي تريد كتابته, إختر رقم من 1 إلي 5 لكتابة الكلمة كاملة على الشاشة" +
                "\n";

        JOptionPane.showMessageDialog(frame,help,
                "مساعدة",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @FXML
    public void close() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void settings(MouseEvent event) {
       createSettingsScreen(event);
    }

    public void createSettingsScreen(Event event){
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("fxml/settings.fxml"));
            Stage stage = new Stage();
            stage.setTitle("الإعدادات");
            stage.setScene(new Scene(root, 600, 500));
            stage.setResizable(false);
            ((Node) (event.getSource())).getScene().getWindow().hide();
            stage.setOnCloseRequest(event1 -> {
                try {
                    new Main().start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            stage.show();
            // Hide this current window (if this is what you want)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}