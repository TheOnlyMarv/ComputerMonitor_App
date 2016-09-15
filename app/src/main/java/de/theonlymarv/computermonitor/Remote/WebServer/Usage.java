package de.theonlymarv.computermonitor.Remote.WebServer;

import java.util.Date;

/**
 * Created by Marvin on 23.08.2016.
 */
public class Usage implements Comparable<Usage> {
    private int device_id;
    private float upload, download;
    private Date date;

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

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

    @Override
    public int compareTo(Usage another) {
        return getDate().compareTo(another.getDate());
    }
}
