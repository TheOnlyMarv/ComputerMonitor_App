package de.theonlymarv.computermonitor.Interfaces;

import de.theonlymarv.computermonitor.Remote.WebSocket.Remote;

/**
 * Created by Marvin on 14.08.2016.
 */
public interface WebSocketEvents {
    void onMessage(Remote remote);
    void onOpened();
    void onClosed();
    void onError(String error);
}
