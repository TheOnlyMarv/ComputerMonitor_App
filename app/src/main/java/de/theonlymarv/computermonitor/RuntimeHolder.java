package de.theonlymarv.computermonitor;

import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.List;

import de.theonlymarv.computermonitor.Remote.WebServer.Device;

/**
 * Created by Marvin on 13.09.2016 for ComputerMonitor.
 */
public class RuntimeHolder {
    private static RuntimeHolder runtimeHolder;

    private List<Device> deviceList;

    private RuntimeHolder() {
    }

    public static RuntimeHolder getInstance() {
        if (runtimeHolder == null) {
            runtimeHolder = new RuntimeHolder();
        }
        return runtimeHolder;
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
        if (this.deviceList != null){
            Collections.sort(this.deviceList);
        }
    }

    @Nullable
    public Device getDeviceById(int id) {
        for (Device device : deviceList) {
            if (device.getId() == id) {
                return device;
            }
        }
        return null;
    }
}
