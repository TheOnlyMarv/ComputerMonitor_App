package de.theonlymarv.computermonitor.RemoteClasses.WebServer;

/**
 * Created by Marvin on 23.08.2016.
 */
public class Status {
    private boolean status;
    private String message, token;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
