package com.github.vaerys.utils;

public class Enum {
    public static String toString(Priority priority) {
        switch (priority) {
            case LOW:
                return "Low";
            case HIGH:
                return "High";
            case MEDIUM:
                return "Medium";
            default:
                return null;
        }
    }

    public static String toString(Status status) {
        switch (status) {
            case STARTED:
                return "Started";
            case COMPLETED:
                return "Completed";
            case NOT_STARTED:
                return "NotStarted";
            default:
                return null;
        }
    }

    public static String toString(Channel channel) {
        switch (channel) {
            case NOTIF:
                return "Notifications";
            case GENERAL:
                return "General";
            default:
                return null;
        }
    }
}
