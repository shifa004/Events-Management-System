import java.sql.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserLogin {
    private Scene loginScene;
    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();

    private Stage stage;

    public UserLogin(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void initializeComponents() {
        //Layout
        VBox loginLayout = new VBox(10);
        loginLayout.getStyleClass().add("login-layout");

        //Controls
        Button loginButton = new Button("Sign In");
        loginButton.getStyleClass().add("login-button");

        Button signUpButton = new Button("Sign Up");
        signUpButton.getStyleClass().add("signup-button");
        
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                authenticate();
            }
        });

        signUpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UserRegistration userRegister = new UserRegistration(stage);
                userRegister.initializeComponents();
            }
        });

        //Adding controls to the layout
        Label usernameLabel = new Label("Username: ");
        usernameLabel.getStyleClass().add("login-label");

        Label passwordLabel = new Label("Password:");
        passwordLabel.getStyleClass().add("login-label");

        loginLayout.getChildren().addAll(usernameLabel, usernameField,
                passwordLabel, passwordField,
                loginButton);

        loginLayout.getChildren().addAll(new Label("or"), signUpButton);

        //Adding layout to the scene
        loginScene = new Scene(loginLayout, 300, 300);        
        stage.setTitle("User Login");

        //Add scene to stage
        stage.setScene(loginScene);
        stage.show();

        loginScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
    }

    private void authenticate() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Connection con = DBUtils.establishConnection();
        String query = "SELECT * FROM users WHERE username=? AND password=?;";
        
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            
           if (rs.next()) {
                String user = rs.getString("username");
                if (user.matches("admin")) {
                // Redirect to admin dashboard
                AdminDashboard adminDashboard = new AdminDashboard(stage);
                adminDashboard.initializeComponents();
            } else {
                UserDashboard dashboard = new UserDashboard(stage, username);
                dashboard.initializeComponents();}
            } else {
                showAlert("Authentication Failed", "Invalid username or password.");
            }
            DBUtils.closeConnection(con, statement);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to connect to the database.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
