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
    //Initial declaration of some variables.
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

        gameWindow = new Pane(); //Main window for graphic display
        backgroundView = new ImageView(); //ImageView for room graphic display
        gameWindow.getChildren().add(backgroundView);


        //TextArea for describing each room
        textAreaStory = new TextArea();
        textAreaStory.setWrapText(true);
        textAreaStory.setEditable(false);
        textAreaStory.setPrefHeight(130);
        textAreaStory.setStyle("-fx-control-inner-background: black; -fx-text-fill: yellow;");

        ScrollPane scrollPane = new ScrollPane(textAreaStory);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.setStyle("-fx-background: black;");


        //ComboBox for exits
        exitsComboBox = new ComboBox<>();
        exitsComboBox.setVisibleRowCount(3);
        exitsComboBox.setPrefWidth(200);
        exitsComboBox.setOnAction(e -> {
            String selectedExit = exitsComboBox.getValue();
            Room nextRoom = findRoomByName(selectedExit);
            if (nextRoom != null) {
                updateRoom(nextRoom);  //Method call for different rooms depending on selection
            }                          //The method also controls what ends up currently in the combobox
        });

        Label exitsLabel = new Label("EXITS", exitsComboBox);
        exitsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        exitsLabel.setContentDisplay(ContentDisplay.BOTTOM);
        HBox.setMargin(exitsLabel, new Insets(-70, 0, 0, 20));

        HBox optionsTab = new HBox();  //HBox that sits at bottom of the screen with all the interactive elements
        optionsTab.setAlignment(Pos.CENTER);
        optionsTab.getChildren().add(scrollPane);
        optionsTab.getChildren().add(exitsLabel);

        borderPane.setTop(gameWindow);
        borderPane.setBottom(optionsTab);


        //Setting up the main scene
        Scene scene = new Scene(borderPane, 1320, 900);
        primaryStage.setTitle("Haunted House Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Initialize Rooms
        initializeRooms();  //Method call that initializes all rooms.
        updateRoom(findRoomByName("Front Yard"));  //Method for controlling the graphics and exits
    }


    //Method for searching for specific rooms in 'rooms' Room ArrayList
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
        else if (room.getName().equalsIgnoreCase("Washroom") ||
                 room.getName().equalsIgnoreCase("Upstairs Loft")
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



    //Rooms are initialized here
    private void initializeRooms() {
        rooms = new ArrayList<>();
        rooms.add(new Room(
                "Front Yard",
                "What a night... You've crashed your car in the middle of nowhere.  Whatever selfish " +
                        "reason you had to travel so late at night to visit your family across the state, seems " +
                        "silly now.   \"I should've waited until the morning,\" you say to yourself, as a bitter " +
                        "cold sets in.  After waiting for someone to pass by on the road for several hours, no " +
                        "help arrives, and the whole time there is no service for your phone, not even GPS.  " +
                        "Shivering you begin to walk out of desperation, and after a long time you see a faint " +
                        "light down a very unkempt road that departs from the highway.   The cold begins to " +
                        "become unbearable, and your shivering is becoming violent as it shakes your whole body. "+
                        "Soon, you see a very old looking house, with many lights on, although you can see no one in " +
                        "any of them.  You rush to the door, nearly collapsing from the cold.  Momentarily you pause, " +
                        "as a very deep feeling of unease fills your mind. Knocking at the door brings no answer, " +
                        "and you decide to enter the home, so that you will not die of hypothermia outside it, waiting. " +
                        "The door is unlocked.",
                "file:src/resources/image/FrontYard.png",
                new String[]{"Front Hall"}
        ));
        rooms.add(new Room(
                "Parlor",
                "You stand in the parlor.   A quaint room, albeit somewhat dusty and full of cobwebs.  A " +
                        "grand piano rests in the center of the room, and a lit candle eerily rests on it, " +
                        "steadily burning.   Marks on the floor indicate there used to be much more furniture in " +
                        "this room.   As you gaze again at the piano your mind wanders, almost against your will, " +
                        "and you can picture a dark figure playing a somber song on the keys.  It takes several " +
                        "moments while lost in the imaginary music to regain control of your senses.  Feeling " +
                        "disturbed, you have a strong desire to return to the Front Hall through the door behind " +
                        "you.",
                "file:src/resources/image/Parlor.png",
                new String[]{"Front Hall"}
        ));

        rooms.add(new Room(
                "Front Hall",
                "You stand in the Front Hall of the house.  Calling out does nothing and no human presence " +
                "arrives to the room.   The door you entered this place with, has somehow locked itself, " +
                "and no force you can muster can open it.   The memory of the bitter cold stays with you. " +
                "Now in relative warmth a new feeling has replaced what should be relief.   Something " +
                "is very wrong here, and you can feel it.   Many lights are on but there is silence, a " +
                "suffocating silence that you can almost feel in your throat.  The room itself is quite " +
                "beautiful,  a grand staircase goes to the upper floor, a chandelier hangs from the " +
                "ceiling.  A door on the left goes to a parlor, and a door behind you goes to what seems " +
                "to be a dining room.   Two paintings hang here on the walls, one of a sad looking young " +
                "woman, and the other of a woman who seems to be in severe distress.  Her hair is shorn, " +
                "and her clothing is gray.  Your steps seem like thunder on the floor as you walk around. " +
                "Something unseen seems to be watching you, no matter how hard you try to brush away that "+
                "thought.",
                "file:src/resources/image/FrontHall.png",
                new String[]{"Parlor", "Upstairs Loft", "Dining Room"}
        ));

        rooms.add(new Room(
                "Dining Room",
                "You stand in a dining room, some dinnerware still rests on the table, as if whomever last " +
                        "had a meal here had to leave in a hurry. "+
                        "Behind you, an open doorway leads back to the entryway, and another doorway leads to a kitchen. ",
                "file:src/resources/image/DiningRoom.png",
                new String[]{"Front Hall", "Kitchen"}
        ));

        rooms.add(new Room(
                "Kitchen",
                "As you enter the kitchen a bright flash of light envelops the room, through the windows," +
                        "where out of the corner of your eye you catch a glimpse of a ray of lightning strike not " +
                        "very far from the house.   Strangely, no sound or familiar thunderclap accompanies it.  " +
                        "After the alarming surprise of the blast of light, you begin to notice the contents of the " +
                        "room.   Immediately a very musky odor assaults your senses coming from the sink.   This " +
                        "room seems to be a little more modern than other parts of the house, with newer-looking " +
                        "appliances, albeit in very poor condition.   The refrigerator in particular, on closer " +
                        "inspection, has strange markings on it, that almost cause pain to look at.   A gaping hole " +
                        "has been punctured into its door.   Behind you, a doorway goes back to the dining room, " +
                        "and you notice a narrow door in the other corner of the room, with some tight stairs " +
                        "leading down into a basement.",
                "file:src/resources/image/Kitchen.png",
                new String[]{"Dining Room", "Basement"}
        ));

        rooms.add(new Room(
                "Upstairs Loft",
                "(To do)",
                "file:src/resources/image/Upstairs.png",
                new String[]{"Front Hall", "Master Bedroom", "Guest Bedroom", "Servant's Room"}
        ));

        rooms.add(new Room(
                "Master Bedroom",
                "(To do)  " +
                        " ",
                "file:src/resources/image/MasterBedroom.png",
                new String[]{"Upstairs Loft", "Washroom", "Study"}
        ));

        rooms.add(new Room(
                "Washroom",
                "(To do)  " +
                        " ",
                "file:src/resources/image/Washroom.png",
                new String[]{"Master Bedroom"}
        ));

        rooms.add(new Room(
                "Basement",
                "(To do)  " +
                        " ",
                "file:src/resources/image/Basement.png",
                new String[]{"Kitchen", "Wine Cellar"}
        ));

        rooms.add(new Room(
                "Wine Cellar",
                "(To do)  " +
                        " ",
                "file:src/resources/image/WineCellar.png",
                new String[]{"Basement"}
        ));

        rooms.add(new Room(
                "Guest Bedroom",
                "(To do)  " +
                        " ",
                "file:src/resources/image/GuestBedroom.png",
                new String[]{"Upstairs Loft", "Stairway"}
        ));

        rooms.add(new Room(
                "Stairway",
                "(To do)  " +
                        " ",
                "file:src/resources/image/Stairway.png",
                new String[]{"Guest Bedroom", "Attic"}
        ));

        rooms.add(new Room(
                "Attic",
                "(To do)  " +
                        " ",
                "file:src/resources/image/Attic.png",
                new String[]{"Stairway"}
        ));

        rooms.add(new Room(
                "Study",
                "(To do)  " +
                        " ",
                "file:src/resources/image/LibraryRoom.png",
                new String[]{"Master Bedroom"}
        ));

        rooms.add(new Room(
                "Servant's Room",
                "(To do)  " +
                        " ",
                "file:src/resources/image/ServantBedroom.png",
                new String[]{"Upstairs Loft"}
        ));

        // Add more rooms as needed...
    }

    public static void main(String[] args) {
        launch(args);
    }


}
