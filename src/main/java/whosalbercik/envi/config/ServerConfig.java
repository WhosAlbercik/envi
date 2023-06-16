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

    public static final ForgeConfigSpec.ConfigValue<Config> AREAS;

    public static final ForgeConfigSpec.ConfigValue<Integer> PERMISSION_LEVEL;




    static {

        NPCS = BUILDER.define("npcs", TomlFormat.newConfig(HashMap::new));

        QUESTS = BUILDER.define("quests", TomlFormat.newConfig(HashMap::new));

        TRADES = BUILDER.define("trades", TomlFormat.newConfig(HashMap::new));

        AREAS = BUILDER.define("areas", TomlFormat.newConfig(HashMap::new));

        PERMISSION_LEVEL = BUILDER.define("commandPermission", 4);

        SPEC = BUILDER.build();
    }
}
