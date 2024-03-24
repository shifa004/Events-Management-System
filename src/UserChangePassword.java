import java.sql.*;

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
        changePasswordButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                changePassword();
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
        
        gridPane.add( new Label("New Password:"), 0, 1);
        gridPane.add(newPasswordField, 0, 2);

        HBox buttons = new HBox();
        buttons.getChildren().addAll(cancel, changePasswordButton);
        buttons.setSpacing(10);
        buttons.setMargin(cancel, new Insets(0, 0, 0, 20));

        VBox all = new VBox();
        all.getChildren().addAll(gridPane, buttons);
        changePasswordScene = new Scene(all, 300, 200);
        stage.setTitle("Change Password");
        stage.setScene(changePasswordScene);
        stage.hide();
        stage.setMaximized(true);
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
