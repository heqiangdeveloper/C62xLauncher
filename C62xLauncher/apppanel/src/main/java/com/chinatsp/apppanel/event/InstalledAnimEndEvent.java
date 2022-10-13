package com.chinatsp.apppanel.event;

import com.anarchy.classifyview.event.Event;

import java.util.List;

public class InstalledAnimEndEvent extends Event {
    private List<String> installedPackages;

    public InstalledAnimEndEvent(List<String> installedPackages) {
        this.installedPackages = installedPackages;
    }

    public List<String> getInstalledPackages() {
        return installedPackages;
    }

    public void setInstalledPackages(List<String> installedPackages) {
        this.installedPackages = installedPackages;
    }
}
