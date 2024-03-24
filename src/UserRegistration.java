import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;

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
        registerButton.getStyleClass().add("register-button");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("signin-button");        

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
                new Label("Already have an account?"),
                loginButton);


        registerScene = new Scene(registerLayout, 450, 500);
        registerScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setTitle("User Registration");
        stage.setScene(registerScene);
        stage.setHeight(450);
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

        //Generate a random salt for the password
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        // Hash password with salt
        String hashedPassword = hashPassword(password, salt);

        Connection con = DBUtils.establishConnection();
        String query = "INSERT INTO users (username, password, salt, first_name, last_name) values (?, ?, ?, ?, ? );";
        PreparedStatement statement = null;
        
        try {
            statement = con.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            statement.setBytes(3, salt);
            statement.setString(4, firstName);
            statement.setString(5, lastName);
            
            int rs = statement.executeUpdate();
            
            if (rs==1) {
                // UserDashboard dashboard = new UserDashboard(stage, username);
                // dashboard.initializeComponents();
                UserLogin login = new UserLogin(stage);
                login.initializeComponents();
            } else {
                showAlert("Authentication Failed", "Invalid username or password.");
            }
            DBUtils.closeConnection(con, statement);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to connect to the database.");
        }
    }

    private String hashPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            String passwordToHash = password + Base64.getEncoder().encodeToString(salt);
            byte[] hashedPassword = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            //This handles any hashing errors
            e.printStackTrace();
            return null;
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
