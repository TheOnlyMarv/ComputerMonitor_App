package de.theonlymarv.computermonitor.Remote.WebServer;

import java.util.Date;

/**
 * Created by Marvin on 23.08.2016.
 */
public class Device {
    private int id;
    private String name;
    private Date last_used;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLast_used() {
        return last_used;
    }

    public void setLast_used(Date last_used) {
        this.last_used = last_used;
    }
}
