import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;

public class UserDashboard {
    private Scene dashScene;
    
    private Stage stage;
    private static final Map<Integer, Map<String, Object>> events = getEvents();

    public UserDashboard(Stage primaryStage, String username) {
        this.stage = primaryStage;
    }

    public void initializeComponents() {
        // Heading
        Text heading = new Text("All Events");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.getStyleClass().add("search-field"); // Apply the CSS style

        // Menu
        Menu allEventsItem = new Menu("All Events");
        Menu sports = new Menu("Sports");
        Menu entertainment = new Menu("Entertainment");
        Menu artCulture = new Menu("Arts and Culture");
        Menu community = new Menu("Community");
        Menu others = new Menu("Others");
        MenuBar menuBar = new MenuBar(allEventsItem, sports, entertainment, artCulture, community, others);

        // Profile button
        Button profileButton = new Button("Profile");

        //Adding menu and profile button to HBox and setting the menu in center and profile button on the right
        HBox topBox = new HBox(menuBar, profileButton);
        topBox.setSpacing(10);
        topBox.setAlignment(Pos.CENTER);

        BorderPane topLayout = new BorderPane();
        topLayout.setCenter(menuBar); 
        topLayout.setRight(profileButton);
        topLayout.setPadding(new Insets(10, 10, 10, 10));

        //Adding heading and search field to VBox
        VBox headSearch = new VBox(heading, searchField);
        headSearch.setSpacing(10);
        headSearch.setAlignment(Pos.CENTER);

        //Layout for Events
        FlowPane homeLayout = new FlowPane();
        homeLayout.setPadding(new Insets(10));
        homeLayout.setHgap(10);
        homeLayout.setVgap(10);
        // homeLayout.getStyleClass().add("login-layout");

        double imageWidth = 230;
        double imageHeight = 200;
        //Controls
        for (Map<String, Object> eventData : events.values()) {
            String name = (String) eventData.get("name");
            String imageUrl = (String) eventData.get("image");

            ImageView imageView = new ImageView(new Image(imageUrl));
            imageView.setFitWidth(imageWidth);
            imageView.setFitHeight(imageHeight);

            Label nameLabel = new Label(name);
            nameLabel.setMaxWidth(imageWidth);
            nameLabel.setWrapText(true);
            nameLabel.setAlignment(Pos.CENTER);

            VBox container = new VBox(imageView, nameLabel);
            container.setMaxWidth(imageWidth);
            container.setMaxHeight(imageHeight);
            container.setAlignment(Pos.CENTER);

            // Set eventData as a property of the container
            container.getProperties().put("eventData", eventData);

            container.setOnMouseClicked(event -> {
                // Retrieve eventData from the container when clicked
                @SuppressWarnings("unchecked")
                Map<String, Object> clickedEventData = (Map<String, Object>) container.getProperties().get("eventData");
                
                EventDetails eventDetails = new EventDetails(stage, name);
                eventDetails.initializeComponents(clickedEventData);

                // Your action here using clickedEventData
                System.out.println("Container clicked for event: " + clickedEventData);
                // You can access other properties of clickedEventData as well
            });        
    

            homeLayout.getChildren().add(container);
        }
        homeLayout.setAlignment(Pos.CENTER);
        ScrollPane scrollPane = new ScrollPane(homeLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        VBox mainLayout = new VBox();
        mainLayout.setSpacing(10);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(headSearch, scrollPane);

        //Adding layout to the scene
        BorderPane root = new BorderPane(mainLayout);
        root.setTop(topLayout);
        
        dashScene = new Scene(root, 800, 800);        
        stage.setTitle("Home Page");

        //Add scene to stage
        stage.setScene(dashScene);
        stage.setMaximized(true);
        stage.show();

        dashScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
    }

    private static Map<Integer, Map<String, Object>> getEvents() {
        Map<Integer, Map<String, Object>> resultMap = new HashMap<>();
        int rowNum = 0;
        Connection con = DBUtils.establishConnection();
        String query = "SELECT * FROM events;";
        
        try {
            PreparedStatement statement = con.prepareStatement(query);            
            ResultSet rs = statement.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> rowData = new HashMap<>();
                String name = rs.getString("name");
                String description = rs.getString("description");
                String date = rs.getString("date");
                String time = rs.getString("time");
                String location = rs.getString("location");
                String image = rs.getString("image");
                String category = rs.getString("category");

                rowData.put("name", name);
                rowData.put("description", description);
                rowData.put("date", date);
                rowData.put("time", time);
                rowData.put("location", location);
                rowData.put("image", image);
                rowData.put("category", category);
            
                resultMap.put(rowNum, rowData);
                rowNum++;    
            } 
            DBUtils.closeConnection(con, statement);
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to connect to the database.");
        }
        return resultMap;
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
