/*
Author: Charles Carter
Date: 11/14/2024

This is the Room class for my Haunted House game, the constructor creates an object with a name for the room, a description,
an image path for the graphic, and a String array with possible exits.
 */



public class Room {
    private String name;
    private String description;
    private String imagePath;
    private String[] exits;

    public Room(String name, String description, String imagePath, String[] exits) {
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.exits = exits;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String[] getExits() {
        return exits;
    }
}

