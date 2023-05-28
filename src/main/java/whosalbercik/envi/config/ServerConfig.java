package whosalbercik.envi.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;

public class ServerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Config> NPCS;
    public static final ForgeConfigSpec.ConfigValue<Config> QUESTS;
    public static final ForgeConfigSpec.ConfigValue<Config> TRADES;





    static {

        NPCS = BUILDER.define("npcs", TomlFormat.newConfig(HashMap::new));

        QUESTS = BUILDER.define("quests", TomlFormat.newConfig(HashMap::new));

        TRADES = BUILDER.define("trades", TomlFormat.newConfig(HashMap::new));


        SPEC = BUILDER.build();
    }
}
