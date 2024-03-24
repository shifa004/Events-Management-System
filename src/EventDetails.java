// import java.sql.*;
// import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class EventDetails {
    private Scene detailsScene;
    
    private Stage stage;
    private String username;

    public EventDetails(Stage primaryStage, String username) {
        this.stage = primaryStage;
        this.username = username;
    }

    public void initializeComponents(Map<String, Object> event) {
        System.out.println("Container clicked for event: " + event);

        // Heading
        Text heading = new Text("Event Details");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        VBox detailsLayout = new VBox(10);
        detailsLayout.setAlignment(Pos.CENTER);

        String name1 = (String) event.get("name");
        String date1 = (String) event.get("date");
        String location1 = (String) event.get("location");
        String time1 = (String) event.get("time");
        String image1 = (String) event.get("image");
        
        double imageWidth = 300;
        double imageHeight = 300;
        
        Text name = new Text(name1);
        name.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));


        HBox d = new HBox();
        Text date = new Text("Date: ");
        Text date2 = new Text(date1);
        d.getChildren().addAll(date, date2);
        d.setAlignment(Pos.CENTER);

        HBox t = new HBox();
        Text time = new Text("TIme: ");
        Text time2 = new Text(time1);
        t.getChildren().addAll(time, time2);
        t.setAlignment(Pos.CENTER);

        HBox l = new HBox();
        Text location = new Text("Venue: ");
        Text location2 = new Text(location1);
        l.getChildren().addAll(location, location2);
        l.setAlignment(Pos.CENTER);

        // Set font weight to bold for "Date:", "Venue:", and "Time:"
        date.setStyle("-fx-font-weight: bold");
        location.setStyle("-fx-font-weight: bold");
        time.setStyle("-fx-font-weight: bold");

        ImageView imageView = new ImageView(new Image(image1));
        imageView.setFitWidth(imageWidth);
        imageView.setFitHeight(imageHeight);

        Button goBack = new Button("Go Back");
        goBack.setOnAction(e -> {
            UserDashboard dash = new UserDashboard(stage, username);
            dash.initializeComponents();
        });

        detailsLayout.getChildren().addAll(name, imageView, d, l, t);
        VBox all = new VBox();
        all.setSpacing(20);
        all.setAlignment(Pos.CENTER);
        all.getChildren().addAll(heading, detailsLayout, goBack);
        //Adding layout to the scene
        detailsScene = new Scene(all, 300, 300);        
        stage.setTitle("User Login");

        //Add scene to stage
        stage.setScene(detailsScene);
        stage.hide();
        stage.setMaximized(true);
        stage.show();
    }
}