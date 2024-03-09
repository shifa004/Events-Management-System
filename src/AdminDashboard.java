import java.sql.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AdminDashboard {
    VBox adminLayout = new VBox(10);

    private Scene AdminScene;
    // private TextField usernameField = new TextField();
    // private PasswordField passwordField = new PasswordField();
    private Stage stage;

    
    public AdminDashboard(Stage primaryStage) {
        this.stage = primaryStage;
        
    }

    public void initializeComponents() {
        VBox adminLayout = new VBox(10);
        adminLayout.setPadding(new Insets(10));
        adminLayout.setStyle("-fx-background-color: #ffffff;");

        Button Add = new Button("Add Event");
                Add.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        AddStage();
                    }
                });

        Label allEventsLabel = new Label("All Events:");
        allEventsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        adminLayout.getChildren().add(Add);

        adminLayout.getChildren().add(allEventsLabel);

        adminLayout.getChildren().add(getAllEventsPane());



        AdminScene = new Scene(adminLayout, 600, 400);
        stage.setTitle("Admin Dashboard");
        stage.setScene(AdminScene);

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

    private VBox getAllEventsPane() {
        VBox eventsPane = new VBox(10);

        // Customize the styling as needed
        eventsPane.setStyle("-fx-background-color: #ffffff; -fx-padding: 10px;");

        // Retrieve events and add labels to the VBox
        Connection con = DBUtils.establishConnection();
        String query = "SELECT * FROM events";
        try (Statement statement = con.createStatement();
                ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                // Process each event and add labels to the VBox
                Integer id = rs.getInt("id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                String description = rs.getString("description");

                VBox eventBox = new VBox(10);
                eventBox.setStyle(
                        "-fx-background-color: #ffffff; -fx-padding: 10px; -fx-border-width: 0 0 1 0; -fx-border-color: black;");

                HBox buttonBox = new HBox(10);
                Button Edit = new Button("Edit");
                Edit.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        EditStage(id, name, type, description);
                    }
                });
                Button Delete = new Button("Archive");
                Delete.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        archiveEvent(id);
                    }
                });

                buttonBox.getChildren().addAll(Edit, Delete);
                buttonBox.setPadding(new Insets(0, 10, 0, 0));
                Label nameLabel = new Label("Event Name: ");
                Label typeLabel = new Label("Event Type: ");
                Label descriptionLabel = new Label("Event Description: ");

                Label nameValue = new Label(name);
                Label typeValue = new Label(type);
                Label descriptionValue = new Label(description);

                // Set the font weight to bold
                nameLabel.setStyle("-fx-font-weight: bold;");
                typeLabel.setStyle("-fx-font-weight: bold;");
                descriptionLabel.setStyle("-fx-font-weight: bold;");

                HBox combo1 = new HBox();
                combo1.getChildren().addAll(nameLabel, nameValue);

                HBox combo2 = new HBox();
                combo2.getChildren().addAll(typeLabel, typeValue);

                HBox combo3 = new HBox();
                combo3.getChildren().addAll(descriptionLabel, descriptionValue);

                eventBox.getChildren().addAll(combo1, combo2, combo3, buttonBox);

                eventsPane.getChildren().add(eventBox);
            }

            DBUtils.closeConnection(con, statement);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to retrieve events from the database.");
        }

        return eventsPane;
    }

    public void AddStage() {

        VBox addEventLayout = new VBox(10);
        addEventLayout.setPadding(new Insets(10));
        addEventLayout.setStyle("-fx-background-color: #ffffff;");


        Label namelabel = new Label("Event Name:");
        Label typelabel = new Label("Event Type:");
        Label desclabel = new Label("Event Description:");

        // Set the font weight to bold for each label
        namelabel.setStyle("-fx-font-weight: bold;");
        typelabel.setStyle("-fx-font-weight: bold;");
        desclabel.setStyle("-fx-font-weight: bold;");

        TextField eventNameField = new TextField();
        TextField eventTypeField = new TextField();
        TextField eventDescriptionField = new TextField();

        Button addButton = new Button("Add Event");
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Handle adding the event to the database
                String name = eventNameField.getText();
                String type = eventTypeField.getText();
                String description = eventDescriptionField.getText();
                addEventToDatabase(name, type, description);
                initializeComponents();
            }
        });

        addEventLayout.getChildren().addAll(
                new Label("Add Event:"),
                namelabel, eventNameField,
                typelabel, eventTypeField,
                desclabel, eventDescriptionField,
                addButton);

                Scene addscene = new Scene(addEventLayout,600,400);
                stage.setMaximized(true);
                // Set the new scene on the existing stage
                stage.setScene(addscene);
    }

    private void EditStage(Integer id, String name, String type, String description) {
        // Stage editStage = new Stage();

        VBox editLayout = new VBox(10);
        editLayout.setPadding(new Insets(10));
        editLayout.setStyle("-fx-background-color: #ffffff;");

        TextField nameField = new TextField(name);
        TextField typeField = new TextField(type);
        TextField descriptionField = new TextField(description);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                
                updateEventDetails(id, nameField.getText(), typeField.getText(),
                descriptionField.getText());

            }
        });
        Label header = new Label("Edit Event: ");
        Label nameLabel = new Label("Event Name: ");
        Label typeLabel = new Label("Event Type: ");
        Label descriptionLabel = new Label("Event Description: ");

        header.setStyle("-fx-font-weight: bold; -fx-font-size:16");
        nameLabel.setStyle("-fx-font-weight: bold;");
        typeLabel.setStyle("-fx-font-weight: bold;");
        descriptionLabel.setStyle("-fx-font-weight: bold;");

        editLayout.getChildren().addAll(
                header,
                nameLabel, nameField,
                typeLabel, typeField,
                descriptionLabel, descriptionField,
                saveButton);

        Scene editScene = new Scene(editLayout, 600, 400);
        stage.setMaximized(true);
        // Set the new scene on the existing stage
        stage.setScene(editScene);
    }

    private void addEventToDatabase(String name, String type, String description) {
        Connection con = DBUtils.establishConnection();
        String insertQuery = "INSERT INTO events (name, type, description) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = con.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, type);
            preparedStatement.setString(3, description);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Add Event Successful", "Event added successfully.");
            } else {
                showAlert("Add Event Failed", "Failed to add event.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to add event to the database.");
        } finally {
            //DBUtils.closeConnection(con);
        }
    }


    private void updateEventDetails(Integer id, String name, String type, String description) {
        Connection con = DBUtils.establishConnection();
        String updateQuery = "UPDATE events SET name=?, type=?, description=? WHERE id=?";

        try (PreparedStatement preparedStatement = con.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, type);
            preparedStatement.setString(3, description);
            preparedStatement.setInt(4, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Update Successful", "Event details updated successfully.");
                initializeComponents();
            } else {
                showAlert("Update Failed", "Failed to update event details.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to update event details.");
        } finally {
            //
        }
    }
    public void archiveEvent(Integer id) {
        Connection con = DBUtils.establishConnection();
        String updateQuery = "UPDATE events SET archive=true WHERE id=?";
    
        try (PreparedStatement preparedStatement = con.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, id);
    
            int rowsAffected = preparedStatement.executeUpdate();
    
            if (rowsAffected > 0) {
                showAlert("archive Successful", "Event archive successfully.");
                initializeComponents();
            } else {
                showAlert("archive Failed", "Failed to archive event.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to archive event.");
        } finally {
            //DBUtils.closeConnection(con);
        }
}}
