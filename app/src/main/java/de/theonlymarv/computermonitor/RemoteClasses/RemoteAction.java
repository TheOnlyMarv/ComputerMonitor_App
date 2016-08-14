package de.theonlymarv.computermonitor.RemoteClasses;

/**
 * Created by Marvin on 14.08.2016.
 */
public class RemoteAction extends Remote {
    private Action action;
    private int value;

    public RemoteAction() {
    }

    public RemoteAction(Action action, int value) {
        this.action = action;
        this.value = value;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
