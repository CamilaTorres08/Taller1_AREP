package edu.eci.arep.webserver.taller1_arep.classes;

public class Task {
    int id;
    String name;
    String description;

    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getId() {
        return id;    }
}
