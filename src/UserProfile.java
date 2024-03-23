import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import javafx.stage.Stage;

public class UserProfile {
    private Scene profileScene;
    
    private Stage stage;
    private String username;
    private String firstName;
    private String lastName;

    public UserProfile(Stage primaryStage, String username)
    {
        this.stage = primaryStage;
        this.username = username;
    }

    @SuppressWarnings("static-access")
    public void initializeComponents() {
        //Retrieve User Information
        Connection con = DBUtils.establishConnection();
        String query = "SELECT * FROM users where username=?";
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                firstName = rs.getString("firstname");
                lastName = rs.getString("lastname");
                System.out.println(lastName);

            }
        }
        catch (SQLException err) {
            err.printStackTrace();
            showAlert("Database Error", "Failed to get user.");
        }    

        Text heading = new Text("My Profile");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Create labels and text fields for user information
        Label nameLabel = new Label("Username:");
        Text nameTextField = new Text(username);

        Label firstNameLabel = new Label("First Name:");
        Text firstNameTextField = new Text(firstName);

        Label lastNameLabel = new Label("Last Name:");
        Text lastNameTextField = new Text(lastName);

        Button goBack = new Button("Go Back");
        goBack.setOnAction(e -> {
            UserDashboard dash = new UserDashboard(stage, username);
            dash.initializeComponents();
        });
        
        Button editButton = new Button("Edit Profile");
        editButton.setOnAction(e -> {
            EditProfile edit = new EditProfile(stage, username, firstName, lastName);
            edit.initializeComponents();
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
        gridPane.add(lastNameLabel, 0, 2);
        gridPane.add(lastNameTextField, 1, 2);
        gridPane.add(goBack, 0, 3, 2, 1);
        gridPane.add(editButton, 1, 3, 2, 1);

        // Create the scene and set it on the stage
        VBox all = new VBox();

        //To add margin just for the heading I added the heading to HBox
        HBox head = new HBox();
        head.getChildren().add(heading);
        head.setMargin(heading, new Insets(20, 0, 0, 20));
        all.getChildren().addAll(head, gridPane);
        profileScene = new Scene(all, 400, 250);
        stage.setScene(profileScene);
        stage.setTitle("User Profile Page");
        stage.hide();
        stage.setMaximized(true);
        stage.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
