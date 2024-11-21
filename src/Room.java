/*
Author: Charles Carter
Date: 11/14/2024

This is the Room class for my Haunted House game, the constructor creates an object with a name for the room, a description,
an image path for the graphic, and a String array with possible exits.
*/

/**
 * The Room class represents a room in the haunted house, with properties such as its name, description, image path,
 * and available exits to other rooms.
 */
public class Room {
    private String name;
    private String description;
    private String imagePath;
    private String[] exits;

    /**
     * Constructs a new Room with the specified name, description, image path, and exits.
     *
     * @param name        The name of the room.
     * @param description A description of the room.
     * @param imagePath   The file path to the room's image.
     * @param exits       An array of strings representing the exits to other rooms.
     */
    public Room(String name, String description, String imagePath, String[] exits) {
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.exits = exits;
    }

    /**
     * Gets the name of the room.
     *
     * @return The name of the room.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the room.
     *
     * @return The description of the room.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the file path to the room's image.
     *
     * @return The file path to the room's image.
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Gets the exits available from this room.
     *
     * @return An array of strings representing the exits.
     */
    public String[] getExits() {
        return exits;
    }
}
