package be.hize.onlyfarm.config.features;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.ConfigEditorBoolean;
import io.github.moulberry.moulconfig.annotations.ConfigOption;

public class OtherConfig {
    @Expose
    @ConfigOption(name = "Config Button", desc = "Add a button to the pause menu to configure OnlyFarm")
    @ConfigEditorBoolean
    public boolean pauseButton = true;

    @Expose
    @ConfigOption(name = "Check for Updates", desc = "Check for updates on startup")
    @ConfigEditorBoolean
    public boolean enableAutoUpdateCheck = true;
}
