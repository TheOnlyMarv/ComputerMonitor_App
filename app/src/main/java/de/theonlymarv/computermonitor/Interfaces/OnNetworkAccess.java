package de.theonlymarv.computermonitor.Interfaces;

import android.support.annotation.NonNull;

/**
 * Created by Marvin on 23.08.2016.
 */
public interface OnNetworkAccess {
    void OnSuccessful(@NonNull Object object);

    void OnError(Exception e);
}
