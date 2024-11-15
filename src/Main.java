/*
Author: Charles Carter
Date: 11/14/2024

This is the main class for my final project for CSCI 1112 at STECH.
The program is a game that uses Open JavaFX and simulates exploring a haunted house using pixel art style graphics.
 */


import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private BorderPane borderPane;
    private Pane gameWindow;
    private TextArea textAreaStory;
    private ComboBox<String> exitsComboBox;
    private ImageView backgroundView;

    private List<Room> rooms;

    @Override
    public void start(Stage primaryStage) {
        try {
            String audioPath = getClass().getResource("/resources/media/Music.mp3").toExternalForm();
            Media backgroundMusic = new Media(audioPath);
            MediaPlayer mediaPlayer = new MediaPlayer(backgroundMusic);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop the audio continuously
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace(); // Prints the exception if there is an issue with the media file
        }


        // Initialize UI Components
        borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        gameWindow = new Pane();
        backgroundView = new ImageView();
        gameWindow.getChildren().add(backgroundView);

        textAreaStory = new TextArea();
        textAreaStory.setWrapText(true);
        textAreaStory.setEditable(false);
        textAreaStory.setPrefHeight(130);
        textAreaStory.setStyle("-fx-control-inner-background: black; -fx-text-fill: yellow;");

        ScrollPane scrollPane = new ScrollPane(textAreaStory);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.setStyle("-fx-background: black;");

        exitsComboBox = new ComboBox<>();
        exitsComboBox.setVisibleRowCount(3);
        exitsComboBox.setOnAction(e -> {
            String selectedExit = exitsComboBox.getValue();
            Room nextRoom = findRoomByName(selectedExit);
            if (nextRoom != null) {
                updateRoom(nextRoom);
            }
        });

        Label exitsLabel = new Label("EXITS", exitsComboBox);
        exitsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        exitsLabel.setContentDisplay(ContentDisplay.BOTTOM);
        HBox.setMargin(exitsLabel, new Insets(-70, 0, 0, 20));

        HBox optionsTab = new HBox();
        optionsTab.setAlignment(Pos.CENTER);
        optionsTab.getChildren().add(scrollPane);
        optionsTab.getChildren().add(exitsLabel);

        borderPane.setTop(gameWindow);
        borderPane.setBottom(optionsTab);

        Scene scene = new Scene(borderPane, 1320, 900);
        primaryStage.setTitle("Haunted House Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Initialize Rooms
        initializeRooms();
        updateRoom(findRoomByName("Front Yard"));
    }



    private Room findRoomByName(String roomName) {
        for (Room room : rooms) {
            if (room.getName().equalsIgnoreCase(roomName)) {
                return room;
            }
        }
        return null;
    }

    private void updateRoom(Room room) {
        if (room == null) return;

        // Update the text area with the room's description
        textAreaStory.setText(room.getDescription());

        // Update the image in the game window
        Image roomImage = new Image(room.getImagePath());
        backgroundView.setImage(roomImage);

        // Create a fade transition for the room image
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), backgroundView);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();

        // Update the exits in the combo box
        exitsComboBox.getItems().clear();
        exitsComboBox.getItems().addAll(room.getExits());
        exitsComboBox.setValue(null); // Clear any existing selection

        if (room.getName().equalsIgnoreCase("Kitchen")) {
            animateLightsFlicker(); // Specific animation for the Kitchen
        }
        else if (room.getName().equalsIgnoreCase("Bathroom") ||
                 room.getName().equalsIgnoreCase("Upstairs")
                ){
            showGhost(); // Call the ghost method
        }
    }

    // Method to animate the lights flickering in the Kitchen
    private void animateLightsFlicker() {
        FadeTransition lightsFlicker = new FadeTransition(Duration.seconds(0.1), backgroundView);
        lightsFlicker.setFromValue(1.0);
        lightsFlicker.setToValue(0.2);
        lightsFlicker.setCycleCount(4); // Flicker five times
        lightsFlicker.setAutoReverse(true);
        lightsFlicker.play();
    }

    private void showGhost() {
        // Load the ghost image
        Image ghostImage = new Image("file:src/resources/image/Ghost.png");
        ImageView ghostView = new ImageView(ghostImage);
        ghostView.setOpacity(0.0); // Start fully transparent

        // Position the ghost in the room
        ghostView.setLayoutX(500);
        ghostView.setLayoutY(300);

        // Add the ghost to the game window
        gameWindow.getChildren().add(ghostView);

        // Create a fade-in transition for the ghost
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), ghostView);
        fadeIn.setFromValue(0.0); // Start at fully transparent
        fadeIn.setToValue(0.6);   // Fade to semi-transparent

        // Create a fade-out transition for the ghost
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), ghostView);
        fadeOut.setFromValue(0.6); // Start at semi-transparent
        fadeOut.setToValue(0.0);   // Fade to fully transparent

        // Set a delay for the fade-out to give the appearance some time
        fadeOut.setDelay(Duration.seconds(2));

        // After fade-in completes, play fade-out
        fadeIn.setOnFinished(e -> fadeOut.play());

        // After fade-out completes, remove the ghost from the scene
        fadeOut.setOnFinished(e -> gameWindow.getChildren().remove(ghostView));

        // Start the fade-in transition
        fadeIn.play();
    }

    private void initializeRooms() {
        rooms = new ArrayList<>();
        rooms.add(new Room(
                "Front Yard",
                "After crashing your car and walking for several hours, you saw some lights in the distance of what seemed to be a deserted road. " +
                        "The way is difficult, and your legs start to ache, but now that you've arrived, " +
                        "this 'house' seems to be empty, and a deep dread has seized your heart.  The front door awaits you. ",
                "file:src/resources/image/FrontYard.png",
                new String[]{"Entryway"}
        ));
        rooms.add(new Room(
                "Parlor",
                "You are now in the parlor. The room is empty, but does not feel so. " +
                        "Several paintings adorn the walls, some quite mundane, but others are disturbing in nature, " +
                        "and make you feel uneasy. There are two doors in this room, one leads to the entryway, " +
                        "the other leads to the kitchen. An old piano rests on the left side of the room, " +
                        "it is in remarkably pristine condition.",
                "file:src/resources/image/Parlor.png",
                new String[]{"Entryway", "Kitchen"}
        ));

        rooms.add(new Room(
                "Entryway",
                "You are now in the entryway. The air is chilly, and you feel a draft from somewhere above. " +
                        "There is a grand staircase leading up, and a door back to the parlor.  The front door, try as "+
                        "you might to open it, has somehow locked itself, and no force you can muster make it give way. ",
                "file:src/resources/image/Entryway.png",
                new String[]{"Parlor", "Upstairs", "Dining Room"}
        ));

        rooms.add(new Room(
                "Kitchen",
                "You are in the kitchen. Dust and cobwebs cover the surfaces. A strange odor fills the air. " +
                        "There is a door leading back to the parlor.",
                "file:src/resources/image/Kitchen.png",
                new String[]{"Parlor"}
        ));

        rooms.add(new Room(
                "Dining Room",
                "You stand in a dining room, some dinnerware still rests on the table, as if whomever last " +
                        "had a meal here had to leave in a hurry. "+
                        "Behind you, an open doorway leads back to the entryway. ",
                "file:src/resources/image/DiningRoom.png",
                new String[]{"Entryway"}
        ));

        rooms.add(new Room(
                "Upstairs",
                "You are in a living room at the top of the stairs.  As you enter this area, your blood freezes as " +
                        "you see a ghostly apparition appear in the center of the room momentarily.  Behind you, stairs go back"+
                        "to the entryway.",
                "file:src/resources/image/Upstairs.png",
                new String[]{"Entryway", "Bedroom"}
        ));

        rooms.add(new Room(
                "Bedroom",
                "(To do)  " +
                        " ",
                "file:src/resources/image/Bedroom.png",
                new String[]{"Upstairs", "Bathroom"}
        ));

        rooms.add(new Room(
                "Bathroom",
                "(To do)  " +
                        " ",
                "file:src/resources/image/Bathroom.png",
                new String[]{"Bedroom"}
        ));

        // Add more rooms as needed...
    }

    public static void main(String[] args) {
        launch(args);
    }


}
