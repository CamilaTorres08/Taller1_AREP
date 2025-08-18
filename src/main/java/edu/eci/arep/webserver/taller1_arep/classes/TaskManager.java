package edu.eci.arep.webserver.taller1_arep.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskManager {
    List<Task> tasks;
    int lastId;
    public TaskManager() {
        this.tasks = new ArrayList<Task>();
        this.lastId = 0;
    }
    public Task addTask(String name, String description) {
        lastId++;
        Task task = new Task(name, description, lastId);
        this.tasks.add(task);
        return task;

    }
    public List<Task> getTasks() {
        return tasks;
    }
    public List<Task> getTasksByName(String name) {
        return this.tasks.stream().filter(x -> x.getName().contains(name)).collect(Collectors.toList());
    }
}
