package com.marcouberti.sonicboomwatchface;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Marco on 01/12/15.
 */
public class ConfigListModel implements Serializable{

    public static int TYPE_UNKNOWN = -1;
    public static int TYPE_GROUP = 0;
    public static int TYPE_SEPARATOR = 1;
    public static int TYPE_ACCENT_COLOR = 2;
    public static int TYPE_SECOND_TIMEZONE = 3;
    public static int TYPE_RATE_THIS_ASPP = 4;
    public static int TYPE_FOOTER = 5;

    private ArrayList<Item> items= new ArrayList<>();

    public void clear() {
        items.clear();
    }

    public Item getItemByAbsoluteIndex(int absoluteIndex) {
        for(int i=0; i<items.size(); i++) {
            if(i == absoluteIndex) return items.get(i);
        }
        return null;
    }

    public int getRowType(int absoluteIndex) {
        for(int i=0; i<items.size(); i++) {
            if(i == absoluteIndex) {
                Item item = items.get(i);
                if (item instanceof GroupItem) {
                    return TYPE_GROUP;
                } else if (item instanceof SeparatorItem) {
                    return TYPE_SEPARATOR;
                } else if (item instanceof AccentColorItem) {
                    return TYPE_ACCENT_COLOR;
                } else if (item instanceof SecondTimezoneItem) {
                    return TYPE_SECOND_TIMEZONE;
                }else if (item instanceof RateAppItem) {
                    return TYPE_RATE_THIS_ASPP;
                }else if (item instanceof FooterItem) {
                    return TYPE_FOOTER;
                }else{
                    return TYPE_UNKNOWN;
                }
            }
        }
        return TYPE_UNKNOWN;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public int getTotalRowCount() {
        return items.size();
    }

    //region items classes
    /**
     * Base list item type
     */
    public static class Item implements Serializable{
        public boolean disabled = false;
        public Item() {}
    }

    /**
     * Generic group item
     */
    public static class GroupItem extends Item {
        public int keyTitleRes;
        public GroupItem(int keyTitleRes) {
            super();
            this.keyTitleRes = keyTitleRes;
        }
    }

    /**
     * Generic separator item
     */
    public static class SeparatorItem extends Item {
        public SeparatorItem() {
            super();
        }
    }

    /**
     * Generic button item
     */
    public static class RateAppItem extends Item {
        public int keyTitleRes;
        public RateAppItem(int keyTitleRes) {
            super();
            this.keyTitleRes = keyTitleRes;
        }
    }

    /**
     * Generic action item
     */
    public static class AccentColorItem extends Item {
        public String colorName;
        public int colorID;
        public AccentColorItem(String colorName, int colorID) {
            super();
            this.colorName = colorName;
            this.colorID = colorID;
        }
    }

    /**
     * Custom footer item
     */
    public static class FooterItem extends Item {
        public FooterItem() {
            super();
        }
    }

    /**
     * Generica activity navigation item
     */
    public static class SecondTimezoneItem extends Item {
        public SecondTimezoneItem() {
            super();
        }
    }
    //endregion
}
