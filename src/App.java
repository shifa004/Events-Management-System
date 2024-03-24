import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        //this.primaryStage = primaryStage;
        UserLogin login = new UserLogin(primaryStage);
        login.initializeComponents();
        // String user = "";
        // UserDashboard dash = new UserDashboard(primaryStage, user);
        // dash.initializeComponents();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
