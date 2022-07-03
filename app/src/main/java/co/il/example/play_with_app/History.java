package co.il.example.play_with_app;

public class History {
    // history details
    private String username;
    private String lastMsg;

    // constructor
    public History(String username, String lastMsg) {
        this.username = username;
        this.lastMsg = lastMsg;
    }

    // getter and setters

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastMsg() {
        return this.lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }
}
