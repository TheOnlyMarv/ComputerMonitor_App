package de.theonlymarv.computermonitor.Models;

/**
 * Created by Marvin on 14.08.2016.
 */
public class Connection {
    private String name;
    private String url;

    public Connection(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
