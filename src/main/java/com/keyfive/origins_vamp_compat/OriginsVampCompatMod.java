package com.keyfive.origins_vamp_compat;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;

@Mod(OriginsVampCompatMod.MOD_ID)
public class OriginsVampCompatMod {
    public static final String MOD_ID = "origins_vamp_compat";

    public OriginsVampCompatMod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC, "origins_vamp_compat-server.toml");
        ModItems.register();
    }
}
