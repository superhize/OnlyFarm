package be.hize.onlyfarm.config.features;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.ConfigEditorBoolean;
import io.github.moulberry.moulconfig.annotations.ConfigOption;

public class Farming {

    @Expose
    @ConfigOption(name = "1.12 Crop HitBox", desc = "Enable 1.12 Crop HitBox")
    @ConfigEditorBoolean
    public boolean changeCropHitbox = false;

    @Expose
    @ConfigOption(name = "Remove Break Particle", desc = "Remove particles when breaking blocks.")
    @ConfigEditorBoolean
    public boolean removeBreakParticle = false;
}
