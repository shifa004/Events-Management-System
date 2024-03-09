import java.sql.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserChangePassword {
    private Scene changePasswordScene;
    private PasswordField newPasswordField = new PasswordField();
    private Stage stage;
    private String username;

    public UserChangePassword(Stage primaryStage, String username){
        this.stage = primaryStage;
        this.username = username;
    }

    public void initializeComponents() {
        VBox changePasswordLayout = new VBox(10);
        changePasswordLayout.setPadding(new Insets(10));
        Button changePasswordButton = new Button("Change Password");
        Button logoutButton = new Button("Logout");
        changePasswordButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                //changePassword();
                changePassword();
            }
        });

        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                UserLogin userLogin = new UserLogin(stage);
                userLogin.initializeComponents();
            }
        });

        changePasswordLayout.getChildren().addAll(new Label("Welcome " + username), new Label("New Password:"), newPasswordField, changePasswordButton, logoutButton);

        changePasswordScene = new Scene(changePasswordLayout, 300, 200);
        stage.setTitle("Change Password");
        stage.setScene(changePasswordScene);
        stage.show();
    }

    private void changePassword(){
        String newPassword = newPasswordField.getText();
        Connection con = DBUtils.establishConnection();
        String query = "UPDATE users SET password=? WHERE username =?;";
        try{
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, newPassword);
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
            showAlert("Database Error", "Failed to connect to the database.");
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
