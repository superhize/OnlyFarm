package be.hize.onlyfarm.config;

import be.hize.onlyfarm.OnlyFarmMod;
import be.hize.onlyfarm.config.features.FarmingConfig;
import be.hize.onlyfarm.config.features.OtherConfig;
import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.Config;
import io.github.notenoughupdates.moulconfig.annotations.Category;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public class Features extends Config {

    public static final ResourceLocation DISCORD = new ResourceLocation("onlyfarm", "discord.png");
    public static final ResourceLocation GITHUB = new ResourceLocation("onlyfarm", "github.png");

    @Override
    public boolean shouldAutoFocusSearchbar() {
        return true;
    }

    @Override
    public void saveNow() {
        OnlyFarmMod.configManager.saveConfig(ConfigFileType.FEATURES, "close-gui");
    }

    @Override
    public String getTitle() {
        return "OnlyFarm " + OnlyFarmMod.getVersion() + " by §cHiZe§r, config by §5Moulberry §rand §5nea89";
    }

    @Expose
    @Category(name = "Farming", desc = "Only category that matter.")
    public FarmingConfig farming = new FarmingConfig();

    @Expose
    @Category(name = "Other", desc = "Not farming related, so it matter less.")
    public OtherConfig other = new OtherConfig();
}
