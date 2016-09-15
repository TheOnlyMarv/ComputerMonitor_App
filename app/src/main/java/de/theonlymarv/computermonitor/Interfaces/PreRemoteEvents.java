package de.theonlymarv.computermonitor.Interfaces;

import android.view.KeyEvent;

/**
 * Created by Marvin on 15.09.2016 for ComputerMonitor.
 */
public interface PreRemoteEvents {
    void OnQrCodeScanned(String url);

    boolean OnDownUpPressed(KeyEvent keyEvent);
}
