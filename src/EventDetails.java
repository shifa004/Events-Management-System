// import java.sql.*;
// import java.util.HashMap;
import java.util.Map;

// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
import javafx.scene.Scene;
// import javafx.scene.control.Alert;
// import javafx.scene.control.Label;
// import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
// import javafx.scene.image.Image;
// import javafx.scene.image.ImageView;
// import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
// import javafx.scene.control.TextField;
// import javafx.scene.text.Font;
// import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
// import javafx.scene.control.Menu;
// import javafx.scene.control.MenuBar;
// import javafx.scene.layout.BorderPane;
// import javafx.scene.layout.HBox;
// import javafx.scene.control.Button;

public class EventDetails {
    private Scene detailsScene;
    
    private Stage stage;

    public EventDetails(Stage primaryStage, String username) {
        this.stage = primaryStage;
    }

    public void initializeComponents(Map<String, Object> event) {
        VBox detailsLayout = new VBox(10);
        String name1 = (String) event.get("name");

        Text name = new Text(name1);

        detailsLayout.getChildren().addAll(name);

        //Adding layout to the scene
        detailsScene = new Scene(detailsLayout, 300, 300);        
        stage.setTitle("User Login");

        //Add scene to stage
        stage.setScene(detailsScene);
        stage.show();

    }
}