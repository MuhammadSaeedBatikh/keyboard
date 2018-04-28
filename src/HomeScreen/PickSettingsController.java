package HomeScreen;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * Created by Muhammad on 27/10/2017.
 */
public class PickSettingsController {
    @FXML
    ImageView enter;
    @FXML
    ImageView backspace;
    @FXML
    ImageView shift;
    @FXML
    ImageView space;

    @FXML
    public void backspaceClicked(MouseEvent event) {
        //save clicked Image
        ((Node) (event.getSource())).getScene().getWindow().hide();
        new HomeController().createSettingsScreen(event);
    }

    @FXML
    public void enterClicked(MouseEvent event) throws Exception {
        //save clicked Image
        ((Node) (event.getSource())).getScene().getWindow().hide();
        new HomeController().createSettingsScreen(event);
    }

    public void spaceBarClicked(MouseEvent event) {
        //save clicked Image
        ((Node) (event.getSource())).getScene().getWindow().hide();
        new HomeController().createSettingsScreen(event);
    }

    public void shiftClicked(MouseEvent event) {
        //save clicked Image
        ((Node) (event.getSource())).getScene().getWindow().hide();
        new HomeController().createSettingsScreen(event);
    }
}
