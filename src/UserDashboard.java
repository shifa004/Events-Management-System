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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;

public class UserDashboard {
    private Scene dashScene;
    
    private Stage stage;
    private String username;
    private static final Map<Integer, Map<String, Object>> events = getEvents();
    private FlowPane homeLayout;

    public UserDashboard(Stage primaryStage, String username) {
        this.stage = primaryStage;
        this.username = username;
    }

    public void initializeComponents() {
        // Heading
        Text heading = new Text("All Events");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.getStyleClass().add("search-field");

        // Menu
        Button allEventsButton = new Button("All Events");
        Button sportsButton = new Button("Sports");
        Button entertainmentButton = new Button("Entertainment");
        Button artCultureButton = new Button("Arts and Culture");
        Button communityButton = new Button("Community");
        Button othersButton = new Button("Others");
        
        allEventsButton.setOnAction(event -> populateEvents("", null));
        sportsButton.setOnAction(event -> populateEvents("", "Sports"));
        entertainmentButton.setOnAction(event -> populateEvents("", "Entertainment"));
        artCultureButton.setOnAction(event -> populateEvents("", "Arts and Culture"));
        communityButton.setOnAction(event -> populateEvents("", "Community"));
        othersButton.setOnAction(event -> populateEvents("", "Others"));

        allEventsButton.getStyleClass().add("menu-button");
        sportsButton.getStyleClass().add("menu-button");
        entertainmentButton.getStyleClass().add("menu-button");
        artCultureButton.getStyleClass().add("menu-button");
        communityButton.getStyleClass().add("menu-button");
        othersButton.getStyleClass().add("menu-button");
        othersButton.getStyleClass().add("last-child");
        
        // Add buttons to an HBox
        HBox menuBox = new HBox(allEventsButton, sportsButton, entertainmentButton, artCultureButton, communityButton, othersButton);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setSpacing(10);

        // Profile and logout button
        Button profileButton = new Button("View Profile");
        Button logoutButton = new Button("Logout");

        profileButton.setOnAction(event -> {
            UserProfile prof = new UserProfile(stage, username);
            prof.initializeComponents();
        });
        logoutButton.setOnAction(event -> {
            UserLogin login = new UserLogin(stage);
            login.initializeComponents();
        });

        //Adding menu and profile button to HBox and setting the menu in center and profile button on the right
        HBox topBox = new HBox(menuBox, profileButton);
        topBox.setSpacing(10);
        topBox.setAlignment(Pos.CENTER);

        BorderPane topLayout = new BorderPane();
        topLayout.setCenter(menuBox); 
        topLayout.setRight(profileButton);
        topLayout.setPadding(new Insets(10, 10, 10, 10));

        HBox rightButtons = new HBox(profileButton, logoutButton);
        rightButtons.setSpacing(10); // Spacing between buttons
        topLayout.setRight(rightButtons);

        //Adding heading and search field to VBox
        VBox headSearch = new VBox(heading, searchField);
        headSearch.setSpacing(10);
        headSearch.setAlignment(Pos.CENTER);

        //Layout for Events
        homeLayout = new FlowPane();
        homeLayout.setPadding(new Insets(10));
        homeLayout.setHgap(10);
        homeLayout.setVgap(10);

        //Search Functionality
        searchField.addEventHandler(KeyEvent.KEY_RELEASED, e -> populateEvents(searchField.getText(), null));

        populateEvents("", null);

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
        stage.hide();
        stage.setMaximized(true);
        stage.show();

        dashScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
    }

    private void populateEvents(String query, String categoryFilter) {
        homeLayout.getChildren().clear();
        double imageWidth = 230;
        double imageHeight = 200;
        // Filter and display events based on search query and category
        events.values().stream()
              .filter(eventData -> {
                  String name = (String) eventData.get("name");
                  String category = (String) eventData.get("category");
                  boolean nameMatches = name.toLowerCase().contains(query.toLowerCase());
                  boolean categoryMatches = categoryFilter == null || categoryFilter.isBlank() || category.equalsIgnoreCase(categoryFilter);
                  return nameMatches && categoryMatches;
              })
              .forEach(eventData -> {
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
              });
        
        // You might need to adjust this section to correctly re-add homeLayout to the scene, if necessary
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
