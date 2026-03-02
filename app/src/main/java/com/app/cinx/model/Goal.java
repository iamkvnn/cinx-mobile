package com.app.cinx.model;

public class Goal {
    private int id;
    private String text;
    private boolean done;
    private String time;
    private String type; // "quiz", "video", "code", "rest", "add"

    public Goal(int id, String text, boolean done, String time, String type) {
        this.id = id;
        this.text = text;
        this.done = done;
        this.time = time;
        this.type = type;
    }

    public int getId() { return id; }
    public String getText() { return text; }
    public boolean isDone() { return done; }
    public String getTime() { return time; }
    public String getType() { return type; }

    public void setDone(boolean done) { this.done = done; }
}
