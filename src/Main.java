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

/**
 * The Main class serves as the entry point for the haunted house game. It initializes the GUI elements using JavaFX
 * and provides methods for handling user interaction, updating the game state, and displaying different rooms.
 */
public class Main extends Application {
    //Initial declaration of some variables.
    private BorderPane borderPane;
    private Pane gameWindow;
    private TextArea textAreaStory;
    private ComboBox<String> exitsComboBox;
    private ImageView backgroundView;

    private List<Room> rooms;


    /**
     * The start method is called when the application is launched. It sets up the primary stage and initializes
     * the user interface components, such as the game window, text area for room descriptions, and interactive elements.
     *
     * @param primaryStage The primary stage for this JavaFX application.
     */
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

    /**
     * Searches for a specific room by its name in the rooms ArrayList.
     *
     * @param roomName The name of the room to find.
     * @return The Room object if found, otherwise null.
     */
    private Room findRoomByName(String roomName) {
        for (Room room : rooms) {
            if (room.getName().equalsIgnoreCase(roomName)) {
                return room;
            }
        }
        return null;
    }

    /**
     * Updates the game window to display the specified room's description and image, and updates the exits.
     *
     * @param room The room to be displayed.
     */
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

    /**
     * Animates the lights flickering in the kitchen using a fade transition.
     */
    private void animateLightsFlicker() {
        FadeTransition lightsFlicker = new FadeTransition(Duration.seconds(0.1), backgroundView);
        lightsFlicker.setFromValue(1.0);
        lightsFlicker.setToValue(0.2);
        lightsFlicker.setCycleCount(4); // Flicker five times
        lightsFlicker.setAutoReverse(true);
        lightsFlicker.play();
    }

    /**
     * Shows a ghost animation by fading in and out an image of a ghost.
     */
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



    /**
     * Initializes the rooms in the game by creating Room objects and adding them to the list.
     */
    private void initializeRooms() {
        rooms = new ArrayList<>();
        rooms.add(new Room(
                "Front Yard",
                "\tWhat a night... you've crashed your car in the middle of nowhere.  Whatever selfish " +
                        "reason you had to travel so late at night to visit your family across the state, seems " +
                        "silly now.  \"I should've waited until the morning,\" you say to yourself, as a bitter " +
                        "cold sets in.  After waiting for someone to pass by on the road for several hours, no " +
                        "help arrives, and the whole time there is no service for your phone, not even GPS.  " +
                        "Shivering, you begin to walk out of desperation, and after a long time you see a faint " +
                        "light down a very unkempt road that departs from the highway.  \n\t The cold begins to " +
                        "become unbearable, and your shivering is becoming violent as it shakes your whole body. "+
                        "Soon, you see a very old looking house, with many lights on, although you can see no one in " +
                        "any of them.  You rush to the door, nearly collapsing from the cold.  Momentarily you pause, " +
                        "as a very deep feeling of unease fills your mind.  Knocking at the door brings no answer, " +
                        "and you decide to enter the home, so that you will not die of hypothermia outside it, waiting. " +
                        "The door is unlocked.",

                "file:src/resources/image/FrontYard.png",
                new String[]{"Front Hall"}
        ));
        rooms.add(new Room(
                "Parlor",
                "\tYou stand in the parlor - a quaint room, albeit somewhat dusty and filled with cobwebs.  A " +
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
                "\tYou stand in the Front Hall of the house.  Calling out does nothing and no human presence " +
                "arrives to the room.   The door you entered this place with has somehow locked itself, " +
                "and no force you muster can open it.   The memory of the bitter cold stays with you. " +
                "Now in relative warmth a new feeling has replaced what should be relief.   Something " +
                "is very wrong here, and you can feel it.   Many lights are on but there is silence, a " +
                "suffocating silence that you can almost feel in your throat.  \n\tThe room itself is quite " +
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
                "\tYou stand in a dining room, where it seems the spiders have spun a tapestry of cobwebs.  " +
                        "Curiously, although the webs are plentiful, none of them seem to have any living " +
                        "residents, although there are plenty of dead ones.  Eerily, like most rooms in this place, " +
                        "the lights are on, and dinnerware is still present on the table, as if whomever last dined " +
                        "here had to leave in a hurry.   Some plates even have desiccated pieces of food on them.  " +
                        "\n\tTwo clocks hang on the walls, ticking, displaying times that don't match, and both seem to be " +
                        "stuck, despite their sound.  One clock is set in a gold picture frame, the other in an " +
                        "ornate wooden housing.   The dining chairs have all been pushed back, away from the table.  " +
                        "Behind you a doorway leads back to the front hall, and another in the corner leads to a " +
                        "kitchen.  Strangely your own name comes to your mind, very distinctly, as if someone had " +
                        "spoken it in your ear, but without sound. ",

                "file:src/resources/image/DiningRoom.png",
                new String[]{"Front Hall", "Kitchen"}
        ));

        rooms.add(new Room(
                "Kitchen",
                "\tAs you enter the kitchen a bright flash of light envelops the room, through the windows," +
                        "where out of the corner of your eye you catch a glimpse of a ray of lightning strike not " +
                        "very far from the house.   Strangely, no sound or familiar thunderclap accompanies it.  " +
                        "After the alarming surprise of the blast of light, you begin to notice the contents of the " +
                        "room.   \n\tImmediately a very musky odor assaults your senses coming from the sink.   This " +
                        "room seems to be a little more modern than other parts of the house, with newer-looking " +
                        "appliances, albeit in very poor condition.   The refrigerator in particular, on closer " +
                        "inspection, has strange markings on it, that almost cause pain to look at.   A gaping hole " +
                        "punctures its door.   Behind you, a doorway goes back to the dining room, " +
                        "and you notice a narrow door in the other corner of the room, with some tight stairs " +
                        "leading down into a basement.",

                "file:src/resources/image/Kitchen.png",
                new String[]{"Dining Room", "Basement"}
        ));

        rooms.add(new Room(
                "Basement",
                "\tDescending down some rickety stairs leads you to a basement.  The room seems more " +
                        "cheerful than others, with colorful drawings pasted on the walls, and craft materials " +
                        "stored in various containers.   A small furnace with a low flame is burning, most likely " +
                        "providing the weak heat of the house that saved your life.  Taking a closer look at the " +
                        "drawings reveals the work of a child, and they are quite charming—except for one on the " +
                        "right wall that reminds you of a skull or some grim, ghostly face.  At the back of the " +
                        "room behind some shelves, you notice a very old looking door, which you believe leads to a " +
                        "wine cellar.   In that dark part of the room it seems some effort was made to keep this " +
                        "particular door from casual view. ",

                "file:src/resources/image/Basement.png",
                new String[]{"Kitchen", "Wine Cellar"}
        ));

        rooms.add(new Room(
                "Wine Cellar",
                "\tAs you enter the wine cellar the first thing you notice is the sheer amount of dust, more " +
                        "so than other areas of the house.   It feels as if you are the first to enter this room " +
                        "in a very, very long time.   Within moments an almost audible scream fills your mind, " +
                        "although it doesn't seem human.  And before you can panic, your mind fixates unwillingly " +
                        "on a space of the floor, as if whatever invisible thing present here wants you to see what " +
                        "had happened there.   Shaken, you regain control, wishing you hadn't entered this room.  " +
                        "As you look around you notice the casks, and many full bottles of wine, some of which " +
                        "seem from very old years.  However, the experience of whatever evil fills this place " +
                        "makes it hard to concentrate and your only desire is to return to the other basement " +
                        "room.",

                "file:src/resources/image/WineCellar.png",
                new String[]{"Basement"}
        ));

        rooms.add(new Room(
                "Upstairs Loft",
                "\tAs you enter the upstairs loft you stop dead in your tracks as a very visible shape of a " +
                        "human-like figure appears momentarily.   It looks at you with cold white eyes, and its body " +
                        "is shrouded in flowing shadow.   After the initial shock of actually seeing the specter " +
                        "your heart begins to thump loudly in your chest.   After some time you calm down and " +
                        "survey the room, which features most noticeably, several paintings that seem to glare at " +
                        "you with dark sockets.  Two burning candles sit side-by-side on a table, and cobwebs and " +
                        "dust seem to cover almost every surface in the room.   Looking to the back of the room you " +
                        "see two hallways, one leads to master bedroom, and the other leads to two other rooms, " +
                        "which look to be a guest bedroom and some servant's quarters. ",

                "file:src/resources/image/Upstairs.png",
                new String[]{"Front Hall", "Master Bedroom", "Guest Bedroom", "Servant's Room"}
        ));

        rooms.add(new Room(
                "Master Bedroom",
                "\tYou enter the master bedroom and are surprised at its poor condition, even after seeing " +
                        "other areas of this house.  The bedsheets of the large bed are tattered and torn, and in " +
                        "the corner the fabric of a draped area looks to be torn to shreds.  The walls have great " +
                        "patches of decay all over them, and torn pieces of wallpaper hang from the ceiling.  " +
                        "Walking in the room is treacherous due to missing floorboards.  Disturbingly, two skulls " +
                        "casually rest on twin dressers with mirrors.   They look real, and placed intentionally.  " +
                        "You have no desire to look closer to see if they are actually authentic.   Behind you a " +
                        "door leads back to the upstairs loft, and another door leads to a small bathroom.  To your " +
                        "right, a doorway leads to a den or study area.",

                "file:src/resources/image/MasterBedroom.png",
                new String[]{"Upstairs Loft", "Washroom", "Study"}
        ));

        rooms.add(new Room(
                "Washroom",
                "\tAs you enter the small bathroom, to your great dismay, the apparition you saw in the " +
                        "loft appears again for a few moments.  You can feel anger emanating from it.   It seems " +
                        "especially displeased at your presence in this room in particular.   After it disappears, " +
                        "you notice a foul black liquid in a bathtub that has also spilled onto the floor. " +
                        "The stench is nauseating and you are surprised you could not smell it from the bedroom.  " +
                        "You can hear faint whispering echoing off of the tile and you notice glyphs and " +
                        "drawings on the walls.   The whispers seem to ooze from them.  Patches of black mold have " +
                        "grown in many places on the wall.   The specter seems to still be in the room, although " +
                        "not visible, and you feel that everything it wants at this moment is for you to leave.",

                "file:src/resources/image/Washroom.png",
                new String[]{"Master Bedroom"}
        ));

        rooms.add(new Room(
                "Study",
                "\tA you enter the study room, you notice how well-kept it looks.  Quite the difference from " +
                        "the bedroom.  Bookcases adorn the room and at the center stands a desk, with an open journal " +
                        "resting on it, the pages are all blank, although you can see the imprints as if something " +
                        "had written on the pages, yet not transferred any ink.  A candle rests on the desk as if " +
                        "someone had been recently here.  Many other books are on the shelves and are scattered about, and  " +
                        "perusing them reveals them all to be in neither a language or alphabet you can recognize.  Some " +
                        "pictures on the wall seem to be vintage anatomical sketches of a human face and an old photograph of a " +
                        "cadaver likely from an archeological site.  A globe of the Earth sits in spinning display " +
                        "but the country borders seem to be from a much older time period.",

                "file:src/resources/image/LibraryRoom.png",
                new String[]{"Master Bedroom"}
        ));

        rooms.add(new Room(
                "Guest Bedroom",
                "\tThe guest bedroom is dimly lit and in disarray.  It seems as if someone had rummaged " +
                        "through the chest of drawers hastily looking for something.  Some dried mud covers parts " +
                        "of the floor.  A small lone rocking chair sits in the middle of the room, and seems to " +
                        "sway when you are not directly looking at it.  Above the chair hangs a strange wire " +
                        "sculpture in the form of a wine-glass.  In the back of the room you see a door at the " +
                        "depths of a small closet, it looks like there is a stairway leading up behind it. ",

                "file:src/resources/image/GuestBedroom.png",
                new String[]{"Upstairs Loft", "Stairway"}
        ));

        rooms.add(new Room(
                "Stairway",
                "\tAs you enter the narrow stairway you see that it leads to an attic.  At the top of the " +
                        "stairs there seems to be a figure standing there, but it disappears from view when you try " +
                        "to focus on it.  Very bright moonlight comes from the doorway.  The stairs themselves " +
                        "seem old and precarious, you make a mental note to be careful if you decide to climb " +
                        "them.",

                "file:src/resources/image/Stairway.png",
                new String[]{"Guest Bedroom", "Attic"}
        ));

        rooms.add(new Room(
                "Attic",
                "\tThe ceiling is low in the attic and it is difficult to move around.  The most distinct " +
                        "part of this room is the window, which at first looked as if the moon impossibly filled " +
                        "it completely, but looking closer reveals it to be a very detailed frosted glass window " +
                        "with the surface of the moon on it.   It seems to glow with more light than physically " +
                        "possible for the real moon to provide behind it.  Several wooden chests and boxes contain " +
                        "old items like bottles and empty boxes of vintage detergent.   One chest in the center of " +
                        "the room is so heavy it can't be lifted at all, and is locked so tight you doubt you " +
                        "could open it without a tool.",

                "file:src/resources/image/Attic.png",
                new String[]{"Stairway"}
        ));

        rooms.add(new Room(
                "Servant's Room",
                "\tThe servant’s quarters appear bare and poorly furnished.  Huge cobwebs cover some of the " +
                        "corners of the room.   A small lit lamp sits on a nightstand, and you wonder how long it " +
                        "has been turned on.  The room seems very drab compared to other places in the house, and " +
                        "not much catches your interest until you see some grotesque totems pinned on the walls in " +
                        "the corner of the room.   As you inspect them you hear a faint knocking coming from the " +
                        "inside of a large armoire in the corner of the room.   Reluctantly you open it, finding " +
                        "it empty besides some dusty, poorly-folded bed linens, and the knocking stops.  You " +
                        "decide not to look at the totems any further.",

                "file:src/resources/image/ServantBedroom.png",
                new String[]{"Upstairs Loft"}
        ));

        // Add more rooms as needed...
    }

    /**
     * The main method to launch the JavaFX application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }


}
