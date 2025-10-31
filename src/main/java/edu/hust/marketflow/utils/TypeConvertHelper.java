package edu.hust.marketflow.utils;

public class TypeConvertHelper {
    public static String safeLower(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    public static String safeUpper(String s) {
        return s == null ? "" : s.trim().toUpperCase();
    }

    public static double safeDouble(String s) {
        try {
            return s == null ? 0.0 : Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public static int safeInt(String s) {
        try {
            return s == null || s.isEmpty() ? 0 : Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }
}
