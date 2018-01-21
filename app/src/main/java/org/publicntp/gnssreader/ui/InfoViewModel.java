package org.publicntp.gnssreader.ui;

import android.arch.lifecycle.BuildConfig;
import android.arch.lifecycle.ViewModel;

public class InfoViewModel extends ViewModel {

    private String contactAddr;
    private String webSiteAddr;

    private String develperName;
    private String ownerName;

    public InfoViewModel(String contactAddr) {
        this.contactAddr = contactAddr;
    }

    public String getContactAddr() {
        return contactAddr;
    }

    public void setContactAddr(String contactAddr) {
        this.contactAddr = contactAddr;
    }

    public String getWebSiteAddr() {
        return webSiteAddr;
    }

    public void setWebSiteAddr(String webSiteAddr) {
        this.webSiteAddr = webSiteAddr;
    }

    public String getDevelperName() {
        return develperName;
    }

    public void setDevelperName(String develperName) {
        this.develperName = develperName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getVersionName() {
        return BuildConfig.VERSION_NAME;
    }
}
