package de.theonlymarv.computermonitor.Remote.WebServer;

import android.support.annotation.NonNull;

import com.owlike.genson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;

/**
 * Created by Marvin on 23.08.2016.
 */
public class Device implements Comparable<Device> {
    private int id;
    private String name;
    private Date last_used;

    private List<Usage> usageList;

    @JsonIgnore
    public List<Usage> getUsageList() {
        return usageList;
    }

    @JsonIgnore
    public void setUsageList(List<Usage> usageList) {
        this.usageList = usageList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLast_used() {
        return last_used;
    }

    public void setLast_used(Date last_used) {
        this.last_used = last_used;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", last_used=" + last_used +
                '}';
    }

    @Override
    public int compareTo(@NonNull Device another) {
        return getId() - another.getId();
    }
}
