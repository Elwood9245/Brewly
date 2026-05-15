package xyz.elwoodwjz.brewlybackend.util;

public final class Truncate {

    private Truncate() {
    }

    public static String truncate(String s, int maxLen) {
        if (s == null) return "null";
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }
}
