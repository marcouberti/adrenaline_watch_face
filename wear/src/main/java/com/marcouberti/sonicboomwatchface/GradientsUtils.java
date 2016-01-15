package com.marcouberti.sonicboomwatchface;

import android.content.Context;

import java.util.HashMap;

public class GradientsUtils {

    static HashMap<String, Integer> map = new HashMap<>();
    static {
        map.put("Blue",0);
        map.put("Violet",1);
        map.put("Yellow",2);
        map.put("Red",3);
        map.put("Dark green",4);
        map.put("Green",5);
        map.put("Aqua",6);
        map.put("Fruit",7);
        map.put("Orange",8);
        map.put("Silver",9);
    }

    public static int getGradients(Context ctx, int colorID) {
        if (colorID == 0) {
            return ctx.getResources().getColor(R.color.col_1);
        }else if (colorID == 1) {
            return ctx.getResources().getColor(R.color.col_2);
        }
        else if (colorID == 2) {
            return ctx.getResources().getColor(R.color.col_3);
        }
        else if (colorID == 3) {
            return ctx.getResources().getColor(R.color.col_4);
        }
        else if (colorID == 4) {
            return ctx.getResources().getColor(R.color.col_5);
        }
        else if (colorID == 5) {
            return ctx.getResources().getColor(R.color.col_6);
        }
        else if (colorID == 6) {
            return ctx.getResources().getColor(R.color.col_7);
        }
        else if (colorID == 7) {
            return ctx.getResources().getColor(R.color.col_8);
        }
        else if (colorID ==8) {
            return ctx.getResources().getColor(R.color.col_9);
        }
        else if (colorID == 9) {
            return ctx.getResources().getColor(R.color.col_10);
        }else {
            return ctx.getResources().getColor(R.color.col_4);
        }
    }

    public static int getGradients(Context ctx, String colorName) {
        if(colorName == null || !map.containsKey(colorName)) return ctx.getResources().getColor(R.color.col_4);
        return getGradients(ctx, map.get(colorName));
    }

    public static int getColorID(String colorName) {
        return map.get(colorName);
    }
}
