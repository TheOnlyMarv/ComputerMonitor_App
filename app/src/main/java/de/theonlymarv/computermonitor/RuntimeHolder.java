package de.theonlymarv.computermonitor;

import java.util.Collections;
import java.util.List;

import de.theonlymarv.computermonitor.Remote.WebServer.Device;
import de.theonlymarv.computermonitor.Remote.WebServer.Usage;

/**
 * Created by Marvin on 13.09.2016 for ComputerMonitor.
 */
public class RuntimeHolder {
    private static RuntimeHolder runtimeHolder;

    private List<Device> deviceList;
    private List<Usage> usagesList;

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

    public List<Usage> getUsagesList() {
        return usagesList;
    }

    public void setUsagesList(List<Usage> usagesList) {
        this.usagesList = usagesList;
        if (this.usagesList != null)
        {
            Collections.sort(this.usagesList);
        }
    }
}
