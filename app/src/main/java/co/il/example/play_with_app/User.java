package co.il.example.play_with_app;

public class User {
    // user details
    private String username;
    private String city;
    private String level;
    private String game;

    // constructor
    public User(String username, String city, String level, String game) {
        this.username = username;
        this.city = city;
        this.level = level;
        this.game = game;
    }

    // getter and setters

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getGame() {
        return this.game;
    }

    public void setGame(String game) {
        this.game = game;
    }
}
