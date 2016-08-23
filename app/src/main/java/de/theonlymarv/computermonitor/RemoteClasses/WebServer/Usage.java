package de.theonlymarv.computermonitor.RemoteClasses.WebServer;

import java.util.Date;

/**
 * Created by Marvin on 23.08.2016.
 */
public class Usage {
    private float upload, download;
    private Date date;

    public float getUpload() {
        return upload;
    }

    public void setUpload(float upload) {
        this.upload = upload;
    }

    public float getDownload() {
        return download;
    }

    public void setDownload(float download) {
        this.download = download;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
