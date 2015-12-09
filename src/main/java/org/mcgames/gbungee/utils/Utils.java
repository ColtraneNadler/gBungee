package org.mcgames.gbungee.utils;

/**
 * Created by coltrane on 12/7/15.
 */
public class Utils {
    private static String[] key = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");

    public static String randomStr(int num) {
        String str = "";

        for(int i = 0; i < num; i++) {
            str += key[(int) Math.floor(Math.round(Math.random() * (key.length - 1)))];
        }

        return str;
    }

}
