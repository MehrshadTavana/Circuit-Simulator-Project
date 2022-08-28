import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.stage.*;
import javafx.stage.Window;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;


public class Main extends Application {
    static String path="";/*System.getProperty("user.dir")+"\\input.txt"; //default*/
    static String outputPath=System.getProperty("user.dir")+"\\output.txt"; //default
    static Stage window,w0;
    static Scene mainScene;
    static VBox layout = new VBox();

    public Main() throws FileNotFoundException {
    }

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception  {
        w0=new Stage();
        w0.initStyle(StageStyle.UNDECORATED);
        BorderPane borderPane = new BorderPane();

        VBox vBox=new VBox();
        Scene scene=new Scene(vBox,500,316);

        w0.setScene(scene);
        w0.show();
       /* for (int i=0;i<=8;++i){
            FileInputStream input
             = new FileInputStream(System.getProperty("user.dir")+"\\loadingImgs\\loading"+i+".jpg");
            Image image=new Image(input);
            BackgroundImage backgroundimage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
            Background background = new Background(backgroundimage);
            vBox.setBackground(background);
            try {
                TimeUnit.MILLISECONDS.sleep(250);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }*/

        FileInputStream input = new FileInputStream(System.getProperty("user.dir")+"\\loadingImgs\\loading"+".jpg");
        Image image=new Image(input);
        BackgroundImage backgroundimage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        Background background = new Background(backgroundimage);
        vBox.setBackground(background);
       // w0.show();
        w0.setAlwaysOnTop(true);
        scene.setOnMouseMoved(mouseHandler);


        window=stage;
        Parent root = FXMLLoader.load(getClass().getResource("sampleMainPage.fxml"));
        stage.setTitle("AZA");
        stage.setScene(new Scene(root, 1000, 600));

        FileInputStream input1 = new FileInputStream(System.getProperty("user.dir")+"\\AZAlogo"+".jpg");
        Image image1=new Image(input1);

        stage.getIcons().add(image1);
        w0.getIcons().add(image1);

        //stage.show();

        //w0.hide();
    }

    public static void ErrorBox(String title,String message){
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.initModality(Modality.APPLICATION_MODAL);
        a.setContentText(message);
        a.setTitle(title);
        a.showAndWait();
    }

    EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent mouseEvent) {
/*            System.out.println(mouseEvent.getEventType() + "\n"
                    + "X : Y - " + mouseEvent.getX() + " : " + mouseEvent.getY() + "\n"
                    + "SceneX : SceneY - " + mouseEvent.getSceneX() + " : " + mouseEvent.getSceneY() + "\n"
                    + "ScreenX : ScreenY - " + mouseEvent.getScreenX() + " : " + mouseEvent.getScreenY());
*/
            try {
                TimeUnit.MILLISECONDS.sleep(250);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            window.show();
            w0.hide();
        }

    };
}
