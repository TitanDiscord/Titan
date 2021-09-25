package me.anutley.titan.util;

public class RegexUtil {
    public static boolean validHexColour(String colour) {
        return colour.matches("^([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$");
    }
}
