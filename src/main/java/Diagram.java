

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
public class Diagram extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Brain.simulateFile(new Label(),new ProgressBar());
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        stage.setTitle("Hello World");
        stage.setScene(new Scene(root,1500,600));
        stage.show();
    }
    public static void main(String[] args){ launch(args);
    }
}
