package HomeScreen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML
    ImageView obliqueOK;
    @FXML
    ImageView horns;
    @FXML
    ImageView sword;
    @FXML
    ImageView claw;

    @FXML
    Button obliqueOKChangeButton;
    @FXML
    Button hornsChangeButton;
    @FXML
    Button swordChangeButton;
    @FXML
    Button clawChangeButton;
    @FXML
    Button save;
    @FXML
    Button close;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void obliqueOKChange(ActionEvent actionEvent) {
        showSettingsPicker("", actionEvent);
    }

    @FXML
    public void swordChange(ActionEvent actionEvent) throws Exception {
        showSettingsPicker("", actionEvent);
    }

    public void hornsChange(ActionEvent actionEvent) {
        showSettingsPicker("", actionEvent);
    }

    public void clawChange(ActionEvent actionEvent) {
        showSettingsPicker("", actionEvent);
    }

    @FXML
    public void save(ActionEvent actionEvent) throws Exception {
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        new Main().start(new Stage());
    }

    @FXML
    public void closeClicked(ActionEvent actionEvent) throws Exception {
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        new Main().start(new Stage());
    }

    public void showSettingsPicker(String parentGesture, ActionEvent event) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("fxml/pickSettings.fxml"));
            Stage stage = new Stage();
            // stage.setTitle("ÊÛíÑÇáÅÚÏÇÏÇÊ");
            stage.setScene(new Scene(root, 570, 90));
            stage.setResizable(false);
            ((Node) (event.getSource())).getScene().getWindow().hide();
            stage.setOnCloseRequest(event1 -> {
                try {
                    new HomeController().createSettingsScreen(event);
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
