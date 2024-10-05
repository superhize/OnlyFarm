package be.hize.onlyfarm.config.features;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class FarmingConfig {

    @Expose
    @ConfigOption(name = "1.12 Crop HitBox", desc = "Enable 1.12 Crop HitBox")
    @ConfigEditorBoolean
    public boolean changeCropHitbox = false;

    @Expose
    @ConfigOption(name = "Remove Break Particle", desc = "Remove particles when breaking blocks.")
    @ConfigEditorBoolean
    public boolean removeBreakParticle = false;
}
