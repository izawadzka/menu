package com.example.dell.menu.colors;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Dell on 09.08.2017.
 */

public class ColorsBase {
    private static final ArrayList<Integer> colorsList = new ArrayList<Integer>(){{
        add(Color.rgb(46, 9, 39));
        add(Color.rgb(217,0,0));
        add(Color.rgb(255,45,0));
        add(Color.rgb(255,140,0));
        add(Color.rgb(4,117,111));
        add(Color.rgb(107,12,34));
        add(Color.rgb(217, 4,43));
        add(Color.rgb(1,28,38));
        add(Color.rgb(115,0,70));
        add(Color.rgb(201,60,0));
        add(Color.rgb(232,136,1));
        add(Color.rgb(255,194,0));
        add(Color.rgb(191,187,17));
    }
    };

    public static int getRandomColor(){
        Random random = new Random();
        return colorsList.get(random.nextInt(colorsList.size()));
    }
}
