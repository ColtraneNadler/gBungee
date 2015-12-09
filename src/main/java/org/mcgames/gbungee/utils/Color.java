package org.mcgames.gbungee.utils;

import net.md_5.bungee.api.ChatColor;

/**
 * Created by coltrane on 12/7/15.
 */
public class Color {
    public static String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
