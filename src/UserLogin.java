import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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

    // Label usernameErrorLabel = new Label();
    // Label usernamePasswordErrorLabel = new Label();

    private static final int maxAttempts = 5;
    private static final long resetTime = TimeUnit.MINUTES.toMillis(5);
    
    // Tracks login attempts and their timestamps
    private final ConcurrentHashMap<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();

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

        // usernameErrorLabel.setStyle("-fx-text-fill: red;");
        // usernamePasswordErrorLabel.setStyle("-fx-text-fill: red;");

        loginLayout.getChildren().addAll(usernameLabel, usernameField,
                passwordLabel, passwordField, 
                // usernamePasswordErrorLabel,
                loginButton,
                new Label("Don't have an account?"), signUpButton);

        //Adding layout to the scene
        loginScene = new Scene(loginLayout, 300, 300);        
        stage.setTitle("User Login");

        //Add scene to stage
        stage.setScene(loginScene);
        stage.hide();
        stage.setWidth(300);
        stage.setHeight(400);
        stage.centerOnScreen();
        stage.show();

        loginScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
    }

    @SuppressWarnings("static-access")
    private void authenticate() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        //Input Validation
        Boolean valid = true;
        // if (!InputValidation.checkUsername(username)) {
        //     usernameErrorLabel.setText("Invalid username.");
        //     valid = false;
        // } else {
        //     usernameErrorLabel.setText("");
        // }
        if (!InputValidation.checkUsername(username) || !InputValidation.checkPassword(password)) {
            showAlert("Authentication Failed", "Invalid username or password.");
            valid = false;
        }
        //  else {
        //      usernamePasswordErrorLabel.setText("");
        // }
        if(!valid){
            return;
        }

        //Check user login attempts
        if (!canAttemptLogin(username)) {
            showAlert("Login Blocked", "Too many failed attempts. Please try again later.");
            return;
        }

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

                System.out.println(storedHashedPassword);

                String hashInput = hashPassword(password, storedSalt);
                System.out.println(password);
                System.out.println(hashInput);

                if (storedHashedPassword.equals(hashInput) && inputValidation.checkUsername(username)){
                    loginAttempts.remove(username);                    
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
                        recordFailedAttempt(username);
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

    private boolean canAttemptLogin(String username) {
        LoginAttempt attempt = loginAttempts.get(username);
        if (attempt == null || attempt.canAttempt()) {
            return true;
        } else if (attempt.getAttempts() < maxAttempts) {
            return true;
        }
        return false;
    }
    
    private void recordFailedAttempt(String username) {
        loginAttempts.compute(username, (k, v) -> {
            if (v == null) {
                return new LoginAttempt();
            } else {
                v.attempt();
                return v;
            }
        });
    }

    private static class LoginAttempt {
        private int attempts;
        private long lastAttemptTimestamp;
    
        public LoginAttempt() {
            this.attempts = 1;
            this.lastAttemptTimestamp = System.currentTimeMillis();
        }
    
        public void attempt() {
            this.attempts++;
            this.lastAttemptTimestamp = System.currentTimeMillis();
        }
    
        public boolean canAttempt() {
            long now = System.currentTimeMillis();
            return now - lastAttemptTimestamp > resetTime;
        }
    
        public int getAttempts() {
            return attempts;
        }
    
        // public void reset() {
        //     this.attempts = 1;
        //     this.lastAttemptTimestamp = System.currentTimeMillis();
        // }
    }
    
}