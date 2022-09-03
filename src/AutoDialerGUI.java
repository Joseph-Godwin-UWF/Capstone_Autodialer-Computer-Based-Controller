
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AutoDialerGUI extends Application {

    public static Stage stage;
    @Override
    public void start(Stage stage) throws Exception {
        AutoDialerGUI.stage = stage; // initialize value of stage.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
