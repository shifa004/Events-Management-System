import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
    InputValidation inputValidation = new InputValidation();

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

        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UserLogin userLogin = new UserLogin(stage);
                userLogin.initializeComponents();
            }
        });

        Label usernameErrorLabel = new Label();
        Label passwordErrorLabel = new Label();
        Label firstnameErrorLabel = new Label();
        Label lastnameErrorLabel = new Label();
        // css
        usernameErrorLabel.setStyle("-fx-text-fill: red;");
        passwordErrorLabel.setStyle("-fx-text-fill: red;");
        firstnameErrorLabel.setStyle("-fx-text-fill: red;");
        lastnameErrorLabel.setStyle("-fx-text-fill: red;");
        registerLayout.getChildren().addAll(
                createLabelWithStyle("Username:"), usernameField, usernameErrorLabel,
                createLabelWithStyle("Password:"), passwordField, passwordErrorLabel,
                createLabelWithStyle("First Name:"), firstNameField, firstnameErrorLabel,
                createLabelWithStyle("Last Name"), lastNameField, lastnameErrorLabel,
                registerButton,
                new Label("Already have an account?"),
                loginButton);

        registerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String username = usernameField.getText();

                String password = passwordField.getText();
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                Boolean valid = true;

                if (!InputValidation.checkUsername(username)) {
                    usernameErrorLabel.setText("Invalid username.");
                    valid = false;
                } else {
                    usernameErrorLabel.setText("");
                }
                if (!InputValidation.checkPassword(password)) {
                    passwordErrorLabel.setText("Invalid password");
                    valid = false;
                } else {
                    passwordErrorLabel.setText("");
                }
                if (!InputValidation.checkFirstName(firstName)) {
                    firstnameErrorLabel.setText("Field cannot be empty.");
                    valid = false;
                } else {
                    firstnameErrorLabel.setText("");
                }
                if (!InputValidation.checkLastName(lastName)) {
                    lastnameErrorLabel.setText("Field cannot be empty.");
                    valid = false;
                } else {
                    lastnameErrorLabel.setText("");
                }
                if(!valid){
                    return;
                }
                addUser(username,password,firstName,lastName);
            }
        });
        registerScene = new Scene(registerLayout, 450, 500);
        registerScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setTitle("User Registration");
        stage.setScene(registerScene);
        stage.setHeight(560);
        stage.centerOnScreen();
        stage.show();
    }

    private Label createLabelWithStyle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("register-label");
        return label;
    }

    private void addUser(String username, String password, String firstName, String lastName) {
        if (checkUsernameExists(username)) {
            showAlert("Registration Failed", "Username already exists.");
            return;
        }

        // Generate a random salt for the password
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        // Hash password with salt
        String hashedPassword = hashPassword(password, salt);

        Connection con = DBUtils.establishConnection();
        String query = "INSERT INTO users (username, password, salt, firstname, lastname) values (?, ?, ?, ?, ? );";
        PreparedStatement statement = null;

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            statement.setBytes(3, salt);
            statement.setString(4, firstName);
            statement.setString(5, lastName);

            int rs = statement.executeUpdate();

            if (rs == 1) {
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
            // This handles any hashing errors
            e.printStackTrace();
            return null;
        }
    }

    private boolean checkUsernameExists(String username) {
        Connection con = DBUtils.establishConnection();
        String query = "SELECT * FROM users WHERE username = ?";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                // Username exists
                DBUtils.closeConnection(con, statement);
                return true;
            }
            DBUtils.closeConnection(con, statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
