package com.whisent.powerful_dummy.utils;

public class TimeUtils {
    public static String formatRelativeTime(long timestamp) {
        long current = System.currentTimeMillis();
        long diff = current - timestamp;
        double seconds = diff / 1000.0;

        if (seconds < 1) {
            return "刚刚";
        } else if (seconds < 60) {
            return String.format("%.1f秒前", seconds);
        } else if (seconds < 3600) {
            return String.format("%.1f分钟前", seconds / 60);
        } else if (seconds < 86400) {
            return String.format("%.1f小时前", seconds / 3600);
        } else {
            return String.format("%.1f天前", seconds / 86400);
        }
    }
}
