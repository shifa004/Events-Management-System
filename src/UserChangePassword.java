import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class UserChangePassword {
    private Scene changePasswordScene;
    private PasswordField newPasswordField = new PasswordField();
    private PasswordField oldPasswordField = new PasswordField();
    private Text passwordErrorLabel = new Text();
    private Stage stage;
    private String username;

    public UserChangePassword(Stage primaryStage, String username){
        this.stage = primaryStage;
        this.username = username;
    }

    @SuppressWarnings("static-access")
    public void initializeComponents() {
        Button changePasswordButton = new Button("Change Password");
        Button cancel = new Button("Cancel");
        passwordErrorLabel.setWrappingWidth(200);
        passwordErrorLabel.setStyle("-fx-fill: red;");
        
        changePasswordButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                String oldPassword = oldPasswordField.getText();
                String newPassword = newPasswordField.getText();

                // Validate old password
                if (!validateOldPassword(oldPassword)) {
                    return;
                }
                if (!InputValidation.checkPassword(newPassword)) {
                    passwordErrorLabel.setText("Password must be 8 characters long and include an uppercase, lowercase, number, and special character");
                } else {
                    passwordErrorLabel.setText("");
                    changePassword(newPassword);
                    UserDashboard dashboard = new UserDashboard(stage, username);
                    dashboard.initializeComponents();

                }
            }
        });

        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                UserProfile profile = new UserProfile(stage, username);
                profile.initializeComponents();
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Text heading = new Text("Change Password");
        gridPane.add(heading, 0, 0);
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        gridPane.add(new Label("Old Password:"), 0, 1);
        gridPane.add(oldPasswordField, 0, 2);
        gridPane.add(new Label("New Password:"), 0, 3);
        gridPane.add(newPasswordField, 0, 4);
        gridPane.add(passwordErrorLabel, 0, 5);
        
        HBox buttons = new HBox();
        buttons.getChildren().addAll(cancel, changePasswordButton);
        buttons.setSpacing(10);
        buttons.setMargin(cancel, new Insets(0, 0, 0, 20));

        VBox all = new VBox();
        all.getChildren().addAll(gridPane, buttons);
        all.setSpacing(10);
        changePasswordScene = new Scene(all, 300, 200);
        stage.setTitle("Change Password");
        stage.setScene(changePasswordScene);
        stage.hide();
        stage.setMaximized(true);
        stage.show();
    }

    private void changePassword( String newPassword){   
        System.out.println(newPassword);    
        // Generate a random salt for the password
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        // Hash password with salt
        String hashedPassword = hashPassword(newPassword, salt);
        System.out.println(hashedPassword);

        Connection con = DBUtils.establishConnection();
        String query = "UPDATE users SET password=? WHERE username =?;";
        try{
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, hashedPassword);
            stmt.setString(2, username);
            Statement statement = con.createStatement();
            int result = stmt.executeUpdate();
            if (result == 1) {
                showAlert("Success", "Password successfully changed");
            } else {
                showAlert("Failure", "Failed to update password");
            }
            DBUtils.closeConnection(con, statement);
        }catch(Exception e){
            e.printStackTrace();
            passwordErrorLabel.setText("Invalid Old or New Password");
        }
    }

    private boolean validateOldPassword(String oldPassword) {
        Connection con = DBUtils.establishConnection();
        String query = "SELECT password, salt FROM users WHERE username = ?";
        
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            
            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                byte[] storedSalt = rs.getBytes("salt");
                String hashInput = hashPassword(oldPassword, storedSalt);
                return storedHashedPassword.equals(hashInput);
            }
            
            DBUtils.closeConnection(con, statement);
        } catch (Exception e) {
            e.printStackTrace();
            passwordErrorLabel.setText("Invalid Old or New Password");
        }
        
        return false;
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
