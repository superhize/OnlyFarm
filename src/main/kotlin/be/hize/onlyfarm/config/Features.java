package be.hize.onlyfarm.config;

import be.hize.onlyfarm.OnlyFarmMod;
import be.hize.onlyfarm.config.features.Farming;
import be.hize.onlyfarm.config.features.Other;
import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.Social;
import io.github.moulberry.moulconfig.annotations.Category;
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
    public List<Social> getSocials() {
        return Arrays.asList(
            Social.forLink("SkyHanni Discord", DISCORD, "https://discord.com/servers/skyhanni-997079228510117908"),
            Social.forLink("Elite Skyblock Farmers Discord", DISCORD, "https://discord.gg/farms"),
            Social.forLink("Look at the code", GITHUB, "https://github.com/superhize/OnlyFarm")
        );
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
    public Farming farming = new Farming();

    @Expose
    @Category(name = "Other", desc = "Not farming related, so it matter less.")
    public Other other = new Other();


}
