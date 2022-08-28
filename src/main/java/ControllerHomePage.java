import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerHomePage implements Initializable {
    @FXML public Text textfield1,textfield2,textfield3,textfield4;
    public void btn(){
        textfield1.setText("In order to start with your first simulation as the first\n step you have to open a txt file which contains the information of\ncircuit and its elements.");
        textfield2.setText("To run a circuit simulation, first you have to make sure that\nyou don't need any other changes in the properties of the circuit.\nThen, you can press the Draw button to draw the diagrams of the\nwanted properties or you can press the Run button to draw the circuit.\nThe Output button will show the output file for you as well.");
        textfield3.setText("To save the picuter of your diagrams you can simply press the\nFile menu and choose Save As item and then choose your directories\nfor saving the picture.");
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btn();
    }
}
