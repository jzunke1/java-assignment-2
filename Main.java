package edu.wctc;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Interfaces
interface Lootable {
    String loot(Player player);
}

interface Interactable {
    String interact(Player player);
}

interface Exitable {
    String exit(Player player);
}

// Abstract Room Class
abstract class Room {
    private String name;
    private Room north;
    private Room south;
    private Room east;
    private Room west;
    private Room up;
    private Room down;

    public Room(String name) {
        this.name = name;
    }

    public abstract String getDescription();

    public Room getAdjoiningRoom(char direction) {
        switch (direction) {
            case 'n':
                return north;
            case 's':
                return south;
            case 'e':
                return east;
            case 'w':
                return west;
            case 'u':
                return up;
            case 'd':
                return down;
            default:
                return null;
        }
    }

    public String getExits() {
        StringBuilder exits = new StringBuilder("Exits: ");
        if (north != null) exits.append("north ");
        if (south != null) exits.append("south ");
        if (east != null) exits.append("east ");
        if (west != null) exits.append("west ");
        if (up != null) exits.append("up ");
        if (down != null) exits.append("down ");
        return exits.toString();
    }

    public String getName() {
        return name;
    }

    public boolean isValidDirection(char direction) {
        return getAdjoiningRoom(direction) != null;
    }

    public void setNorth(Room room) {
        this.north = room;
    }

    public void setSouth(Room room) {
        this.south = room;
    }

    public void setEast(Room room) {
        this.east = room;
    }

    public void setWest(Room room) {
        this.west = room;
    }

    public void setUp(Room room) {
        this.up = room;
    }

    public void setDown(Room room) {
        this.down = room;
    }
}

// Concrete Room Classes
class LootableRoom extends Room implements Lootable {
    private String item;

    public LootableRoom(String name, String item) {
        super(name);
        this.item = item;
    }

    @Override
    public String getDescription() {
        return "You are in a " + getName() + ".  There is something glinting in the corner.";
    }

    @Override
    public String loot(Player player) {
        player.addToInventory(item);
        return "You found a " + item + "!";
    }
}

class InteractableRoom extends Room implements Interactable {
    private String interactionResult;

    public InteractableRoom(String name, String interactionResult) {
        super(name);
        this.interactionResult = interactionResult;
    }

    @Override
    public String getDescription() {
        return "You are in a " + getName() + ". There's a strange device here.";
    }

    @Override
    public String interact(Player player) {
        return interactionResult;
    }
}

class ExitableRoom extends Room implements Exitable {
    public ExitableRoom(String name) {
        super(name);
    }

    @Override
    public String getDescription() {
        return "You are in a " + getName() + ". There's a shimmering portal.";
    }

    @Override
    public String exit(Player player) {
        return "You step through the portal and escape the maze!";
    }
}

// Player Class
class Player {
    private int score;
    private List<String> inventory;

    public Player() {
        this.score = 0;
        this.inventory = new ArrayList<>();
    }

    public void addToInventory(String item) {
        inventory.add(item);
    }

    public void addToScore(int points) {
        score += points;
    }

    public String getInventory() {
        if (inventory.isEmpty()) {
            return "Your inventory is empty.";
        } else {
            return "Inventory: " + String.join(", ", inventory);
        }
    }

    public int getScore() {
        return score;
    }
}

// Maze Class
class Maze {
    private Room currentRoom;
    private Player player;
    private boolean isFinished;

    public Maze() {
        player = new Player();
        isFinished = false;

        // Create Rooms
        Room startRoom = new InteractableRoom("Foyer", "You found a hidden switch!");
        LootableRoom treasureRoom = new LootableRoom("Treasure Room", "Golden Key");
        InteractableRoom puzzleRoom = new InteractableRoom("Puzzle Room", "A hidden passage opens!");
        ExitableRoom exitRoom = new ExitableRoom("Exit Room");

        // Connect Rooms (very basic maze)
        startRoom.setNorth(treasureRoom);
        treasureRoom.setSouth(startRoom);
        treasureRoom.setEast(puzzleRoom);
        puzzleRoom.setWest(treasureRoom);
        puzzleRoom.setEast(exitRoom);
        exitRoom.setWest(puzzleRoom);

        // Set starting room
        currentRoom = startRoom;
    }

    public String exitCurrentRoom() {
        if (currentRoom instanceof Exitable) {
            String result = ((Exitable) currentRoom).exit(player);
            isFinished = true;
            return result;
        } else {
            return "There's no way out of here!";
        }
    }

public String lootCurrentRoom() {
    if (currentRoom instanceof Lootable) {
        player.addToScore(10); // Add points for looting
        return ((Lootable) currentRoom).loot(player);
    } else {
        return "There's nothing to loot.";
    }
}

public String interactWithCurrentRoom() {
    if (currentRoom instanceof Interactable) {
        player.addToScore(5); // Add points for interacting
        return ((Interactable) currentRoom).interact(player);
    } else {
        return "Nothing to interact with here.";
    }
}

    public boolean move(char direction) {
        Room nextRoom = currentRoom.getAdjoiningRoom(direction);
        if (nextRoom != null) {
            currentRoom = nextRoom;
            return true;
        } else {
            return false;
        }
    }

    public int getPlayerScore() {
        return player.getScore();
    }

    public String getPlayerInventory() {
        return player.getInventory();
    }

    public String getCurrentRoomDescription() {
        return currentRoom.getDescription();
    }

    public String getCurrentRoomExits() {
        return currentRoom.getExits();
    }

    public boolean isFinished() {
        return isFinished;
    }
}

// Main Class
public class Main {
    public static void main(String[] args) {
        Maze maze = new Maze();
        Scanner scanner = new Scanner(System.in);

        while (!maze.isFinished()) {
            System.out.println(maze.getCurrentRoomDescription());
            System.out.println(maze.getCurrentRoomExits());
            System.out.print("Enter a command (n/s/e/w/u/d/i/l/x/v): ");
            char command = scanner.next().charAt(0);

            switch (command) {
                case 'n':
                case 's':
                case 'e':
                case 'w':
                case 'u':
                case 'd':
                    if (!maze.move(command)) {
                        System.out.println("Invalid direction.");
                    }
                    break;
                case 'i':
                    System.out.println(maze.interactWithCurrentRoom());
                    break;
                case 'l':
                    System.out.println(maze.lootCurrentRoom());
                    break;
                case 'x':
                    System.out.println(maze.exitCurrentRoom());
                    break;
                case 'v':
                    System.out.println(maze.getPlayerInventory());
                    break;
                default:
                    System.out.println("Invalid command.");
            }
        }

        System.out.println("Final Score: " + maze.getPlayerScore());
        scanner.close();
    }
}
