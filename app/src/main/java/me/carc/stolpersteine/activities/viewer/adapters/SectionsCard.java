package me.carc.stolpersteine.activities.viewer.adapters;

import android.support.annotation.DrawableRes;

/**
 * Help to sort the sections from the web page
 * Created by bamptonm on 12/06/2018.
 */

public class SectionsCard {

    private ItemType type;
    private String displayString;
    private String linkString;
    @DrawableRes private int icon;

    public enum ItemType {
        NONE,
        INFO,
        EMAIL,
        PHONE,
        WEB,
        WIKI,
        CLIPBOARD
    }

    public SectionsCard(String display, String data, ItemType type) {
        this.displayString = display;
        this.linkString = data;
        this.type = type;
        this.icon = -1;
    }

    public SectionsCard(String data, ItemType type, @DrawableRes int icon) {
        this.displayString = data;
        this.linkString = data;
        this.type = type;
        this.icon = icon;
    }

    public String getDisplay() { return displayString; }
    public String getData() { return linkString; }
    public ItemType getType() {
        return type;
    }
    public int getIcon() {
        return icon;
    }
}
