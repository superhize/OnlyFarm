package be.hize.onlyfarm.config;

import be.hize.onlyfarm.features.misc.CustomNotifications;
import com.google.gson.annotations.Expose;

import java.util.HashSet;

public class Notifications {
    @Expose
    public HashSet<CustomNotifications.Notification> customNotifications = new HashSet<>();
}
