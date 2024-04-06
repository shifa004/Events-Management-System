import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditProfile {
    private Scene profileScene;
    
    private Stage stage;
    private String username;
    private String firstName;
    private String lastName;

    public EditProfile(Stage primaryStage, String username, String firstName, String lastName)
    {
        this.stage = primaryStage;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @SuppressWarnings("static-access")
    public void initializeComponents() {
        Text heading = new Text("Edit Profile");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        Label firstnameErrorLabel = new Label();
        Label lastnameErrorLabel = new Label();
        firstnameErrorLabel.setStyle("-fx-text-fill: red;");
        lastnameErrorLabel.setStyle("-fx-text-fill: red;");

        // Create labels and text fields for user information
        Label nameLabel = new Label("Username:");
        TextField nameTextField = new TextField(username);
        nameTextField.setDisable(true);

        Label firstNameLabel = new Label("First Name:");
        TextField firstNameTextField = new TextField(firstName);

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameTextField = new TextField(lastName);

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> {
            UserProfile profile = new UserProfile(stage, username);
            profile.initializeComponents();
        });

        Button editPassword = new Button("Edit Password");
        editPassword.setOnAction(e -> {
            UserChangePassword cPassword = new UserChangePassword(stage, username);
            cPassword.initializeComponents();
        });
        
        Button save = new Button("Save Changes");
        save.setOnAction(e -> {
            Boolean valid = true;
            firstName = firstNameTextField.getText();
            lastName = lastNameTextField.getText();
            if (!InputValidation.checkFirstName(firstName)) {
                firstnameErrorLabel.setText("Invalid First Name.");
                valid = false;
            } else {
                firstnameErrorLabel.setText("");
            }
            if (!InputValidation.checkLastName(lastName)) {
                lastnameErrorLabel.setText("Invalid Last Name");
                valid = false;
            } else {
                lastnameErrorLabel.setText("");
            }
            if(!valid){
                return;
            }
            updateUserDetails();
            UserProfile profile = new UserProfile(stage, username);
            profile.initializeComponents();
        });     

        // Create a grid pane to arrange components
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Add components to the grid pane
        gridPane.add(nameLabel, 0, 0);
        gridPane.add(nameTextField, 1, 0);
        gridPane.add(firstNameLabel, 0, 1);
        gridPane.add(firstNameTextField, 1, 1);
        gridPane.add(firstnameErrorLabel, 2, 1);
        gridPane.add(lastNameLabel, 0, 2);
        gridPane.add(lastNameTextField, 1, 2);
        gridPane.add(lastnameErrorLabel, 2, 2);

        HBox buttons = new HBox();
        buttons.getChildren().addAll(cancel, editPassword, save);
        buttons.setSpacing(10);
        buttons.setMargin(cancel, new Insets(0, 0, 0, 20));

        // Create the scene and set it on the stage
        VBox all = new VBox();

        //To add margin just for the heading I added the heading to HBox
        HBox head = new HBox();
        head.getChildren().add(heading);
        head.setMargin(heading, new Insets(20, 0, 0, 20));
        all.getChildren().addAll(head, gridPane, buttons);
        profileScene = new Scene(all, 400, 250);
        stage.setScene(profileScene);
        stage.setTitle("User Profile Page");
        stage.hide();
        stage.setMaximized(true);
        stage.show();
    }

    private void updateUserDetails(){
        Connection con = DBUtils.establishConnection();
        String updateQuery = "UPDATE users SET firstname=?, lastname=? WHERE username=?";

        try (PreparedStatement preparedStatement = con.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, username);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Update Successful", "Updated successfully.");
            } else {
                showAlert("Update Failed", "Failed to update details.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to update event details.");
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
