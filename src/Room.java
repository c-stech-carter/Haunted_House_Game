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

