package application;	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
//this is nonsense
public class Main extends Application {

	@Override
    public void start(Stage stage) throws Exception {
       Parent root = FXMLLoader.load(getClass().getResource("webview.fxml"));
        Scene scene = new Scene(root,700,600);
        stage.setTitle("TextAnalysize");
        stage.setScene(scene);
        stage.show();
    }
	public static void main(String[] args) {
		launch(args);
	}
}
