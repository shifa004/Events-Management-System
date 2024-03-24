import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;

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
    InputValidation inputValidation= new InputValidation(); 

    private Stage stage;

    public UserLogin(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void initializeComponents() {
        //Layout
        VBox loginLayout = new VBox(10);
        loginLayout.getStyleClass().add("login-layout");

        //Controls
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("login-button");

        Button signUpButton = new Button("Register");
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
                loginButton,
                new Label("Don't have an account?"), signUpButton);

        //Adding layout to the scene
        loginScene = new Scene(loginLayout, 300, 300);        
        stage.setTitle("User Login");

        //Add scene to stage
        stage.setScene(loginScene);
        stage.setWidth(300);
        stage.setHeight(350);
        stage.centerOnScreen();
        stage.show();

        loginScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
    }

    private void authenticate() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        Connection con = DBUtils.establishConnection();
        String query = "SELECT * FROM users WHERE username=?";
        
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            
            if (rs.next()) {
                String user = rs.getString("username");
                String storedHashedPassword = rs.getString("password");
                byte[] storedSalt = rs.getBytes("salt");

                String hashInput = hashPassword(password, storedSalt);

                if (storedHashedPassword.equals(hashInput) && inputValidation.checkUsername(username)){                    
                    if (user.equals("admin")) {
                        // Redirect to admin dashboard
                        AdminDashboard adminDashboard = new AdminDashboard(stage);
                        adminDashboard.initializeComponents();
                    }
                    else {
                        UserDashboard dashboard = new UserDashboard(stage, username);
                        dashboard.initializeComponents();}
                    } 
                    else {
                        showAlert("Authentication Failed", "Invalid username or password.");
                    }
                }    
                else {
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
}