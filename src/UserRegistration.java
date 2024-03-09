import java.sql.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
// import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserRegistration {
    private Scene registerScene;
    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private TextField firstNameField = new TextField();
    private TextField lastNameField = new TextField();
    InputValidation inputValidation= new InputValidation();

    private Stage stage;

    public UserRegistration(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void initializeComponents() {
        VBox registerLayout = new VBox(10);
        registerLayout.getStyleClass().add("register-layout");
        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("button");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("button");        

        registerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addUser();
            }
        });

        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UserLogin userLogin = new UserLogin(stage);
                userLogin.initializeComponents();
            }
        });

        registerLayout.getChildren().addAll(
                createLabelWithStyle("Username:"), usernameField,
                createLabelWithStyle("Password:"), passwordField, 
                createLabelWithStyle("First Name:"), firstNameField, 
                createLabelWithStyle("Last Name"), lastNameField,
                registerButton,
                createLabelWithStyle("Already have an account?"),
                loginButton);


        registerScene = new Scene(registerLayout, 300, 450);
        registerScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setTitle("User Registration");
        stage.setScene(registerScene);
        stage.show();
    }

    private Label createLabelWithStyle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("register-label");
        return label;
    }

    private void addUser() {
        String username = usernameField.getText();
        inputValidation.checkUsername(username);
        String password = passwordField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        Connection con = DBUtils.establishConnection();
        String query = "insert into users values (?, ?,?, ? );";
        PreparedStatement statement = null;
        
        try {
            statement = con.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, firstName);
            statement.setString(4, lastName);

            int rs = statement.executeUpdate();
            
            if (rs==1) {
                UserChangePassword changePassword = new UserChangePassword(stage, username);
                changePassword.initializeComponents();
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
