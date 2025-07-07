package com.accelerate.napknbook.utils;

import com.accelerate.napknbook.R;

import java.util.HashMap;

public class ResourceMapSingleton {
    private static ResourceMapSingleton instance;
    private HashMap<String, Integer> resourceMap;

    private ResourceMapSingleton() {

        resourceMap = new HashMap<>();

        resourceMap.put("guy0_workshop_connect_177.mp4", R.raw.guy0_workshop_connect_177) ;
        resourceMap.put("guy0_workshop_connect_200.mp4", R.raw.guy0_workshop_connect_200) ;
        resourceMap.put("guy0_workshop_connect_216.mp4", R.raw.guy0_workshop_connect_216) ;
        resourceMap.put("guy0_workshop_connect_222.mp4", R.raw.guy0_workshop_connect_222) ;
        resourceMap.put("guy0_workshop_connect_112.mp4", R.raw.guy0_workshop_connect_112) ;
        resourceMap.put("guy0_workshop_connect_150.mp4", R.raw.guy0_workshop_connect_150) ;

        resourceMap.put("guy0_workshop_connect_reversed_177.mp4", R.raw.guy0_workshop_connect_reversed_177) ;
        resourceMap.put("guy0_workshop_connect_reversed_200.mp4", R.raw.guy0_workshop_connect_reversed_200) ;
        resourceMap.put("guy0_workshop_connect_reversed_216.mp4", R.raw.guy0_workshop_connect_reversed_216) ;
        resourceMap.put("guy0_workshop_connect_reversed_222.mp4", R.raw.guy0_workshop_connect_reversed_222) ;
        resourceMap.put("guy0_workshop_connect_reversed_112.mp4", R.raw.guy0_workshop_connect_reversed_112) ;
        resourceMap.put("guy0_workshop_connect_reversed_150.mp4", R.raw.guy0_workshop_connect_reversed_150) ;

        resourceMap.put("guy0_workshop_177.mp4", R.raw.guy0_workshop_177) ;
        resourceMap.put("guy0_workshop_200.mp4", R.raw.guy0_workshop_200) ;
        resourceMap.put("guy0_workshop_216.mp4", R.raw.guy0_workshop_216) ;
        resourceMap.put("guy0_workshop_222.mp4", R.raw.guy0_workshop_222) ;
        resourceMap.put("guy0_workshop_112.mp4", R.raw.guy0_workshop_112) ;
        resourceMap.put("guy0_workshop_150.mp4", R.raw.guy0_workshop_150) ;

        resourceMap.put("gal0_workshop_177.mp4", R.raw.gal0_workshop_177) ;
        resourceMap.put("gal0_workshop_200.mp4", R.raw.gal0_workshop_200) ;
        resourceMap.put("gal0_workshop_216.mp4", R.raw.gal0_workshop_216) ;
        resourceMap.put("gal0_workshop_222.mp4", R.raw.gal0_workshop_222) ;
        resourceMap.put("gal0_workshop_112.mp4", R.raw.gal0_workshop_112) ;
        resourceMap.put("gal0_workshop_150.mp4", R.raw.gal0_workshop_150) ;



        resourceMap.put("penguin1_workshop_177.mp4", R.raw.penguin1_workshop_177) ;
        resourceMap.put("penguin1_workshop_200.mp4", R.raw.penguin1_workshop_200) ;
        resourceMap.put("penguin1_workshop_216.mp4", R.raw.penguin1_workshop_216) ;
        resourceMap.put("penguin1_workshop_222.mp4", R.raw.penguin1_workshop_222) ;
        resourceMap.put("penguin1_workshop_112.mp4", R.raw.penguin1_workshop_112) ;
        resourceMap.put("penguin1_workshop_150.mp4", R.raw.penguin1_workshop_150) ;


        resourceMap.put("guy0_pp.webp", R.raw.guy0_pp) ;
        resourceMap.put("gal0_pp.webp", R.raw.gal0_pp) ;
        resourceMap.put("penguin1_pp.webp", R.raw.penguin1_pp) ;
/*


    resourceMap.put("guy1_workshop_177.mp4", R.raw.guy1_workshop_177) ;
        resourceMap.put("guy1_workshop_200.mp4", R.raw.guy1_workshop_200) ;
        resourceMap.put("guy1_workshop_216.mp4", R.raw.guy1_workshop_216) ;
        resourceMap.put("guy1_workshop_222.mp4", R.raw.guy1_workshop_222) ;
        resourceMap.put("guy1_workshop_112.mp4", R.raw.guy1_workshop_112) ;
        resourceMap.put("guy1_workshop_150.mp4", R.raw.guy1_workshop_150) ;


        resourceMap.put("cat_workshop_177.mp4", R.raw.cat_workshop_177) ;
        resourceMap.put("cat_workshop_200.mp4", R.raw.cat_workshop_200) ;
        resourceMap.put("cat_workshop_216.mp4", R.raw.cat_workshop_216) ;
        resourceMap.put("cat_workshop_222.mp4", R.raw.cat_workshop_222) ;
        resourceMap.put("cat_workshop_112.mp4", R.raw.cat_workshop_112) ;
        resourceMap.put("cat_workshop_150.mp4", R.raw.cat_workshop_150) ;
        resourceMap.put("gal1_workshop_177.mp4", R.raw.gal1_workshop_177) ;
        resourceMap.put("gal1_workshop_200.mp4", R.raw.gal1_workshop_200) ;
        resourceMap.put("gal1_workshop_216.mp4", R.raw.gal1_workshop_216) ;
        resourceMap.put("gal1_workshop_222.mp4", R.raw.gal1_workshop_222) ;
        resourceMap.put("gal1_workshop_112.mp4", R.raw.gal1_workshop_112) ;
        resourceMap.put("gal1_workshop_150.mp4", R.raw.gal1_workshop_150) ;

        resourceMap.put("guy1_park_177.mp4", R.raw.guy1_park_177) ;
        resourceMap.put("guy1_park_200.mp4", R.raw.guy1_park_200) ;
        resourceMap.put("guy1_park_216.mp4", R.raw.guy1_park_216) ;
        resourceMap.put("guy1_park_222.mp4", R.raw.guy1_park_222) ;
        resourceMap.put("guy1_park_112.mp4", R.raw.guy1_park_112) ;
        resourceMap.put("guy1_park_150.mp4", R.raw.guy1_park_150) ;

        resourceMap.put("guy0_park_177.mp4", R.raw.guy0_park_177) ;
        resourceMap.put("guy0_park_200.mp4", R.raw.guy0_park_200) ;
        resourceMap.put("guy0_park_216.mp4", R.raw.guy0_park_216) ;
        resourceMap.put("guy0_park_222.mp4", R.raw.guy0_park_222) ;
        resourceMap.put("guy0_park_112.mp4", R.raw.guy0_park_112) ;
        resourceMap.put("guy0_park_150.mp4", R.raw.guy0_park_150) ;

        resourceMap.put("gal0_park_177.mp4", R.raw.gal0_park_177) ;
        resourceMap.put("gal0_park_200.mp4", R.raw.gal0_park_200) ;
        resourceMap.put("gal0_park_216.mp4", R.raw.gal0_park_216) ;
        resourceMap.put("gal0_park_222.mp4", R.raw.gal0_park_222) ;
        resourceMap.put("gal0_park_112.mp4", R.raw.gal0_park_112) ;
        resourceMap.put("gal0_park_150.mp4", R.raw.gal0_park_150) ;

        resourceMap.put("gal1_park_177.mp4", R.raw.gal1_park_177) ;
        resourceMap.put("gal1_park_200.mp4", R.raw.gal1_park_200) ;
        resourceMap.put("gal1_park_216.mp4", R.raw.gal1_park_216) ;
        resourceMap.put("gal1_park_222.mp4", R.raw.gal1_park_222) ;
        resourceMap.put("gal1_park_112.mp4", R.raw.gal1_park_112) ;
        resourceMap.put("gal1_park_150.mp4", R.raw.gal1_park_150) ;

        resourceMap.put("penguin_park_177.mp4",R.raw.penguin_park_177) ;
        resourceMap.put("penguin_park_200.mp4", R.raw.penguin_park_200) ;
        resourceMap.put("penguin_park_216.mp4", R.raw.penguin_park_216) ;
        resourceMap.put("penguin_park_222.mp4", R.raw.penguin_park_222) ;
        resourceMap.put("penguin_park_112.mp4", R.raw.penguin_park_112) ;
        resourceMap.put("penguin_park_150.mp4", R.raw.penguin_park_150) ;

        resourceMap.put("cat_park_177.mp4",R.raw.cat_park_177) ;
        resourceMap.put("cat_park_200.mp4", R.raw.cat_park_200) ;
        resourceMap.put("cat_park_216.mp4", R.raw.cat_park_216) ;
        resourceMap.put("cat_park_222.mp4", R.raw.cat_park_222) ;
        resourceMap.put("cat_park_112.mp4", R.raw.cat_park_112) ;
        resourceMap.put("cat_park_150.mp4", R.raw.cat_park_150) ;
*/
    }

    public static synchronized ResourceMapSingleton getInstance() {
        if (instance == null) {
            instance = new ResourceMapSingleton();
        }
        return instance;
    }

    public HashMap<String, Integer> getResourceMap() {
        return resourceMap;
    }
}

