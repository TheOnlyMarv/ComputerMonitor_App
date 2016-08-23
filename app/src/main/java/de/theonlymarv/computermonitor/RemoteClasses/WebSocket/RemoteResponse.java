package de.theonlymarv.computermonitor.RemoteClasses.WebSocket;

/**
 * Created by Marvin on 14.08.2016.
 */
public class RemoteResponse extends Remote {
    private int status;
    private String message;

    public RemoteResponse() {
    }

    public RemoteResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
