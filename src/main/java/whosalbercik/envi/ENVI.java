package whosalbercik.envi;

import com.mojang.logging.LogUtils;

import net.minecraftforge.eventbus.api.IEventBus;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;
import whosalbercik.envi.config.ServerConfig;
import whosalbercik.envi.core.ModMenus;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ENVI.MODID)
public class ENVI {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "envi";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();


    public ENVI() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ServerConfig.SPEC, "envi-common.toml");

        ModMenus.MENUS.register(modEventBus);

    }


}
