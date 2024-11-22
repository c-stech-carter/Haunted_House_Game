# Haunted House Game

**Author:** Charles T. Carter  
**Date:** 11/14/2024  
**Course:** CSCI 1112 at STECH

## Overview
This project is a game developed as a final project for CSCI 1112, utilizing Java and Open JavaFX. It simulates exploring a haunted house using pixel art-style graphics, where players must navigate through different rooms of the haunted house.
The game in its current state is unfinished, and I plan to add many more rooms.  Currently the game does not end, but allows a free exploration of the house until the program is closed.

## Features
- **Room Exploration:** Players can explore multiple rooms, each with unique descriptions and visuals.
- **Exits and Navigation:** Rooms have designated exits that lead to other rooms, accessible through a user interface.
- **Animations:** The game uses fade transitions for visuals like room changes and ghost appearances.
- **Background Music:** Continuous background music that loops indefinitely to enhance the atmosphere.   The current .mp3 came from pixabay.com, I'm still looking for the perfect soundtrack for this.

## Technologies Used
- **Java 17**: Core programming language.
- **JavaFX**: Used for building the user interface and for multimedia support.
  - **Controls Module**: For UI components like labels, combo boxes, and text areas.
  - **Media Module**: For playing background music.
- **IDE**: Developed using IntelliJ IDEA.

## How to Run the Project
### Prerequisites
- **Java 17** or later.
- **JavaFX SDK** installed. Ensure the JavaFX libraries are added to your project's module path.

### Running the Project
1. Clone or download the project to your local machine.
2. Make sure you have the JavaFX SDK available and correctly linked to the project.
3. Set the following VM options to add the JavaFX module path and necessary modules:
`--module-path "<path_to_javafx_sdk_lib>" --add-modules javafx.controls,javafx.fxml,javafx.media`

4. Run the `Main` class to start the game.

## Game Instructions
1. **Start the Game**: The game begins in the "Front Yard" of the haunted house. The player must explore to uncover more about the mysterious house.
2. **Navigate Rooms**: Use the exit combo box at the bottom of the screen to choose an available exit to the next room.
3. **Read Room Descriptions**: Each room has a detailed description that updates the story text area to help you understand your surroundings.
4. **Special Events**: Certain rooms contain special animations, like flickering lights or ghost appearances.

## Project Structure
- **Main.java**: The main entry point for the game. It sets up the UI and handles user interactions.
- **Room.java**: Defines the properties of each room, including name, description, image path, and exits.
- **resources/**
- **images/**: Contains the pixel art images for each room.
- **media/**: Contains background music (`Music.mp3`) to set the mood.

## Media Playback Issues
If you encounter issues with media playback:
- Ensure the MP3 file is in the correct location (`src/resources/audio/Music.mp3`).
- Make sure the VM options are correctly set to include the `javafx.media` module.
- Verify that your JavaFX SDK version matches your JDK version.

## Possible Future Enhancements
- **Interactive Objects**: Add clickable objects in each room for more interaction.
- **Inventory System**: Allow players to pick up and use items found throughout the house.
- **Expanded Story**: Add more rooms and richer storylines to deepen the game experience.


Enjoy exploring the haunted house!

---

If you have any questions or encounter any issues, feel free to reach out or contribute to the project.
