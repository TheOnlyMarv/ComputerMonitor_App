package de.theonlymarv.computermonitor.Interfaces;

/**
 * Created by Marvin on 23.08.2016.
 */
public interface OnNetworkAccess {
    void OnSuccessful(Object object);

    void OnError(Exception e);
}
