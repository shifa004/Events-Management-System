import java.sql.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;

public class AdminDashboard {
    VBox adminLayout = new VBox(10);
    
    private Scene AdminScene;
    // private TextField usernameField = new TextField();
    // private PasswordField passwordField = new PasswordField();
    private Stage stage;
    private String username;
    
    public AdminDashboard(Stage primaryStage, String username) {
        this.stage = primaryStage;
        this.username = username;
    }

    public void initializeComponents() {
        VBox adminLayout = new VBox(10);
        adminLayout.setPadding(new Insets(10));
        adminLayout.setStyle("-fx-background-color: #ffffff;");

        HBox topButtons = new HBox(10);
        Button Add = new Button("Add Event");
        Add.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                Session session = Session.sessions.get(username);
            if (session != null) {
                session.updateLastActivity();
            } else {
                UserLogin login = new UserLogin(stage);
                login.initializeComponents();
            }
                AddStage();
            }
        });

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(event -> {
            Session session = Session.sessions.get(username);
            if (session != null) {
                session.updateLastActivity();
            } else {
                UserLogin login = new UserLogin(stage);
                login.initializeComponents();
            }
            UserLogin login = new UserLogin(stage);
            login.initializeComponents();
        });

        topButtons.getChildren().addAll(Add, logoutButton);
        topButtons.setSpacing(10);
        
        Label allEventsLabel = new Label("All Events:");
        allEventsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        adminLayout.getChildren().add(topButtons);
        adminLayout.getChildren().add(allEventsLabel);

        adminLayout.getChildren().add(getAllEventsPane());

        AdminScene = new Scene(adminLayout, 600, 400);
        stage.setTitle("Admin Dashboard");
        stage.setScene(AdminScene);

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

    private ScrollPane getAllEventsPane() {
        VBox eventsPane = new VBox(10);

        // Customize the styling as needed
        eventsPane.setStyle("-fx-background-color: #ffffff; -fx-padding: 10px;");

        ScrollPane scrollPane = new ScrollPane();
        
        // Retrieve events and add labels to the VBox
        Connection con = DBUtils.establishConnection();
        String query = "SELECT * FROM events where archive=false";
        try (Statement statement = con.createStatement();
                ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                // Process each event and add labels to the VBox
                Integer id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String date = rs.getString("date");
                String time = rs.getString("time");
                String location = rs.getString("location");
                String image = rs.getString("image");
                String category = rs.getString("category");

                VBox eventBox = new VBox(10);
                eventBox.setStyle(
                        "-fx-background-color: #ffffff; -fx-padding: 10px; -fx-border-width: 0 0 1 0; -fx-border-color: black;");

                HBox buttonBox = new HBox(10);
                Button Edit = new Button("Edit");
                Edit.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Session session = Session.sessions.get(username);
            if (session != null) {
                session.updateLastActivity();
            } else {
                UserLogin login = new UserLogin(stage);
                login.initializeComponents();
            }
                        EditStage(id, name, description, date, time, location, image, category);
                    }
                });
                Button Delete = new Button("Archive");
                Delete.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        Session session = Session.sessions.get(username);
            if (session != null) {
                session.updateLastActivity();
            } else {
                UserLogin login = new UserLogin(stage);
                login.initializeComponents();
            }
                        archiveEvent(id);
                    }
                });

                buttonBox.getChildren().addAll(Edit, Delete);
                buttonBox.setPadding(new Insets(0, 10, 0, 0));
                Label nameLabel = new Label("Event Name: ");
                Label descriptionLabel = new Label("Event Description: ");
                Label dateLabel = new Label("Event Date:");
                Label timeLabel = new Label("Event Time:");
                Label locationLabel = new Label("Event Location:");
                Label imageLabel = new Label("Event Image:");
                Label categoryLabel = new Label("Event Category:");

                Label nameValue = new Label(name);
                Label descriptionValue = new Label(description);
                Label dateValue = new Label(date);
                Label timeValue = new Label(time);
                Label locationValue = new Label(location);
                Label imageValue = new Label(image);
                Label categoryValue = new Label(category);

                // Set the font weight to bold
                nameLabel.setStyle("-fx-font-weight: bold;");
                descriptionLabel.setStyle("-fx-font-weight: bold;");
                dateLabel.setStyle("-fx-font-weight: bold;");
                timeLabel.setStyle("-fx-font-weight: bold;");
                locationLabel.setStyle("-fx-font-weight: bold;");
                imageLabel.setStyle("-fx-font-weight: bold;");
                categoryLabel.setStyle("-fx-font-weight: bold;");

               HBox combo1 = new HBox();
                combo1.getChildren().addAll(nameLabel, nameValue);

                HBox combo2 = new HBox();
                combo2.getChildren().addAll(descriptionLabel, descriptionValue);

                HBox combo3 = new HBox();
                combo3.getChildren().addAll(dateLabel, dateValue);

                HBox combo4 = new HBox();
                combo4.getChildren().addAll(timeLabel, timeValue);

                HBox combo5 = new HBox();
                combo5.getChildren().addAll(locationLabel, locationValue);

                HBox combo6 = new HBox();
                combo6.getChildren().addAll(imageLabel, imageValue);

                HBox combo7 = new HBox();
                combo7.getChildren().addAll(categoryLabel, categoryValue);
                
                eventBox.getChildren().addAll(combo1, combo2, combo3, combo4, combo5, combo6, combo7, buttonBox);

                eventsPane.getChildren().add(eventBox);
            }

            scrollPane.setContent(eventsPane);
            scrollPane.setFitToWidth(true); // To ensure the width of VBox is matched with ScrollPane

            DBUtils.closeConnection(con, statement);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to retrieve events from the database.");
        }

        return scrollPane;
    }
    

    public void AddStage() {
        
        VBox addEventLayout = new VBox(10);
        addEventLayout.setPadding(new Insets(10));
        addEventLayout.setStyle("-fx-background-color: #ffffff;");

        Label namelabel = new Label("Event Name:");
        Label desclabel = new Label("Event Description:");
        Label dateLabel = new Label("Event Date:");
        Label timeLabel = new Label("Event Time:");
        Label locationLabel = new Label("Event Location:");
        Label imageLabel = new Label("Event Image:");
        Label categoryLabel = new Label("Event Category:");

        // Set the font weight to bold for each label
        namelabel.setStyle("-fx-font-weight: bold;");
        desclabel.setStyle("-fx-font-weight: bold;");
        dateLabel.setStyle("-fx-font-weight: bold;");
        timeLabel.setStyle("-fx-font-weight: bold;");
        locationLabel.setStyle("-fx-font-weight: bold;");
        imageLabel.setStyle("-fx-font-weight: bold;");
        categoryLabel.setStyle("-fx-font-weight: bold;");

        TextField eventNameField = new TextField();
        eventNameField.getStyleClass().add("eventNameField");
        TextField eventDescriptionField = new TextField();
        TextField eventDateField = new TextField();
        TextField eventTimeField = new TextField();
        TextField eventLocationField = new TextField();
        TextField eventImageField = new TextField();
        TextField eventCategoryField = new TextField();

        //errors 
        Label nameErrorLabel = new Label();
        Label descriptionErrorLabel = new Label();
        Label dateErrorLabel = new Label();
        Label timeErrorLabel = new Label();
        Label locationErrorLabel = new Label();
        Label imageErrorLabel = new Label();
        Label categoryErrorLabel = new Label();
        //css
        nameErrorLabel.setStyle("-fx-text-fill: red;");
        descriptionErrorLabel.setStyle("-fx-text-fill: red;");
        dateErrorLabel.setStyle("-fx-text-fill: red;");
        timeErrorLabel.setStyle("-fx-text-fill: red;");
        locationErrorLabel.setStyle("-fx-text-fill: red;");
        imageErrorLabel.setStyle("-fx-text-fill: red;");
        categoryErrorLabel.setStyle("-fx-text-fill: red;");

        Button addButton = new Button("Add Event");
        addButton.getStyleClass().add("login-button");
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Session session = Session.sessions.get(username);
            if (session != null) {
                session.updateLastActivity();
            } else {
                UserLogin login = new UserLogin(stage);
                login.initializeComponents();
            }


                String name = eventNameField.getText();
                String description = eventDescriptionField.getText();
                String date = eventDateField.getText();
                String time = eventTimeField.getText();
                String location = eventLocationField.getText();
                String image = eventImageField.getText();
                String category = eventCategoryField.getText();
                Boolean valid = true;
                if (!InputValidation.checkEventName(name)) {
                    nameErrorLabel.setText("Invalid event name");
                    valid = false;
                } else {
                    System.out.println("name" + valid);
                    nameErrorLabel.setText("");
                }
                if (!InputValidation.checkEventDescription(description)) {
                    descriptionErrorLabel.setText("Maximum limit exceeded (1000)");
                    valid = false;
                    System.out.println("desc" + valid);
                } else {
                    descriptionErrorLabel.setText("");
                }
                if (!InputValidation.checkEventDate(date)) {
                    dateErrorLabel.setText("Invalid date format. Format: DD Month YYYY");
                    valid = false;
                } else {
                    dateErrorLabel.setText("");
                }
                if (!InputValidation.checkEventTime(time)) {
                    timeErrorLabel.setText("Invalid time format. Format: HH:MM am/pm");
                    valid = false;
                } else {
                    timeErrorLabel.setText("");
                }
                if (!InputValidation.checkEventLocation(location)) {
                    locationErrorLabel.setText("Invalid location format.");
                    valid = false;
                } else {
                    locationErrorLabel.setText("");
                }
                if (!InputValidation.checkEventImage(image)) {
                    imageErrorLabel.setText("Invalid image url.");
                    valid = false;
                } else {
                    imageErrorLabel.setText("");
                }
                if (!InputValidation.checkEventCategory(category)) {
                    categoryErrorLabel.setText("Exceeded max limit (50).");
                    valid = false;
                } else {
                    categoryErrorLabel.setText("");
                }
                if(!valid){
                    return;
                }else{
                    addEventToDatabase(name, description, date, time, location, image, category);
                }
               
                initializeComponents();
            }
        });

        addEventLayout.getChildren().addAll(
                new Label("Add Event:"),
                namelabel, eventNameField, nameErrorLabel,
                desclabel, eventDescriptionField, descriptionErrorLabel,
                dateLabel, eventDateField, dateErrorLabel,
                timeLabel, eventTimeField, timeErrorLabel,
                locationLabel, eventLocationField,locationErrorLabel,
                imageLabel, eventImageField,imageErrorLabel,
                categoryLabel, eventCategoryField, categoryErrorLabel,
                addButton);

                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setContent(addEventLayout);

                scrollPane.setFitToWidth(true);

                Scene addscene = new Scene(scrollPane,600,400);
                stage.hide();
                stage.setMaximized(true);
                // Set the new scene on the existing stage
                stage.setScene(addscene);
                stage.show();
                addscene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
    }

    private void EditStage(Integer id, String name, String description, String date, String time, String location, String image, String category) {
        // Stage editStage = new Stage();

        VBox editLayout = new VBox(10);
        editLayout.setPadding(new Insets(10));
        editLayout.setStyle("-fx-background-color: #ffffff;");

        TextField nameField = new TextField(name);
        TextField descriptionField = new TextField(description);
        TextField dateField = new TextField(date);
        TextField timeField = new TextField(time);
        TextField locationField = new TextField(location);
        TextField imageField = new TextField(image);
        TextField categoryField = new TextField(category);
        //errors 
        Label nameErrorLabel = new Label();
        Label descriptionErrorLabel = new Label();
        Label dateErrorLabel = new Label();
        Label timeErrorLabel = new Label();
        Label locationErrorLabel = new Label();
        Label imageErrorLabel = new Label();
        Label categoryErrorLabel = new Label();
        //css
        nameErrorLabel.setStyle("-fx-text-fill: red;");
        descriptionErrorLabel.setStyle("-fx-text-fill: red;");
        dateErrorLabel.setStyle("-fx-text-fill: red;");
        timeErrorLabel.setStyle("-fx-text-fill: red;");
        locationErrorLabel.setStyle("-fx-text-fill: red;");
        imageErrorLabel.setStyle("-fx-text-fill: red;");
        categoryErrorLabel.setStyle("-fx-text-fill: red;");
        Button saveButton = new Button("Save");
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {    
                Session session = Session.sessions.get(username);
            if (session != null) {
                session.updateLastActivity();
            } else {
                UserLogin login = new UserLogin(stage);
                login.initializeComponents();
            }
                Boolean valid = true;
                if (!InputValidation.checkEventName(nameField.getText())) {
                    nameErrorLabel.setText("Invalid event name");
                    valid = false;
                } else {
                    System.out.println("name" + valid);
                    nameErrorLabel.setText("");
                }
                if (!InputValidation.checkEventDescription(descriptionField.getText())) {
                    descriptionErrorLabel.setText("Maximum limit exceeded (1000)");
                    valid = false;
                    System.out.println("desc" + valid);
                } else {
                    descriptionErrorLabel.setText("");
                }
                if (!InputValidation.checkEventDate(dateField.getText())) {
                    dateErrorLabel.setText("Invalid date format. Format: DD Month YYYY");
                    valid = false;
                } else {
                    dateErrorLabel.setText("");
                }
                if (!InputValidation.checkEventTime(timeField.getText())) {
                    timeErrorLabel.setText("Invalid time format. Format: HH:MM am/pm");
                    valid = false;
                } else {
                    timeErrorLabel.setText("");
                }
                if (!InputValidation.checkEventLocation(locationField.getText())) {
                    locationErrorLabel.setText("Invalid location format.");
                    valid = false;
                } else {
                    locationErrorLabel.setText("");
                }
                if (!InputValidation.checkEventImage(imageField.getText())) {
                    imageErrorLabel.setText("Invalid image url.");
                    valid = false;
                } else {
                    imageErrorLabel.setText("");
                }
                if (!InputValidation.checkEventCategory(categoryField.getText())) {
                    categoryErrorLabel.setText("Exceeded max limit (50).");
                    valid = false;
                } else {
                    categoryErrorLabel.setText("");
                }
                if(!valid){
                    return;
                }else{
                    updateEventDetails(id, 
                nameField.getText(), 
                descriptionField.getText(),
                dateField.getText(),
                timeField.getText(),
                locationField.getText(),
                imageField.getText(),
                categoryField.getText());
                }            
                
            }
        });
        Label header = new Label("Edit Event: ");
        Label nameLabel = new Label("Event Name: ");
        Label descriptionLabel = new Label("Event Description: ");
        Label dateLabel = new Label("Event Date: ");
        Label timeLabel = new Label("Event Time: ");
        Label locationLabel = new Label("Event Location: ");
        Label imageLabel = new Label("Event Image: ");
        Label categoryLabel = new Label("Event Category: ");

        header.setStyle("-fx-font-weight: bold; -fx-font-size:16");
        nameLabel.setStyle("-fx-font-weight: bold;");
        descriptionLabel.setStyle("-fx-font-weight: bold;");
        dateLabel.setStyle("-fx-font-weight: bold;");
        timeLabel.setStyle("-fx-font-weight: bold;");
        locationLabel.setStyle("-fx-font-weight: bold;");
        imageLabel.setStyle("-fx-font-weight: bold;");
        categoryLabel.setStyle("-fx-font-weight: bold;");

        editLayout.getChildren().addAll(
                header,
                nameLabel, nameField, nameErrorLabel,
                descriptionLabel, descriptionField,descriptionErrorLabel,
                dateLabel, dateField, dateErrorLabel,
                timeLabel, timeField, timeErrorLabel,
                locationLabel, locationField,locationErrorLabel,
                imageLabel, imageField, imageErrorLabel,
                categoryLabel, categoryField, categoryErrorLabel,
                saveButton);

                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setContent(editLayout);

                scrollPane.setFitToWidth(true);
                
        Scene editScene = new Scene(scrollPane, 600, 400);
        stage.hide();
        stage.setMaximized(true);
        // Set the new scene on the existing stage
        stage.setScene(editScene);
        stage.show();
    }

    private void addEventToDatabase(String name, String description, String date, String time, String location, String image, String category) {
        Connection con = DBUtils.establishConnection();
        String insertQuery = "INSERT INTO events (name, description, date, time, location, image, category) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = con.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, date);
            preparedStatement.setString(4, time);
            preparedStatement.setString(5, location);
            preparedStatement.setString(6, image);
            preparedStatement.setString(7, category);

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

    private void updateEventDetails(Integer id, String name, String description, String date, String time, String location, String image, String category) {
        Connection con = DBUtils.establishConnection();
        String updateQuery = "UPDATE events SET name=?, description=?, date=?, time=?, location=?, image=?, category=? WHERE id=?";

        try (PreparedStatement preparedStatement = con.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, date);
            preparedStatement.setString(4, time);
            preparedStatement.setString(5, location);
            preparedStatement.setString(6, image);
            preparedStatement.setString(7, category);
            preparedStatement.setInt(8, id);

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
