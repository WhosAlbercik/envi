package whosalbercik.envi.registry.obj;

import com.electronwill.nightconfig.core.Config;
import net.minecraftforge.common.ForgeConfigSpec;


public abstract class RegistryObject<T extends RegistryObject> {

    protected final String id;
    protected final Class<T> type;
    protected final ForgeConfigSpec.ConfigValue<Config> config;

    // CONSTRUCTOR MUST MATCH THE ONE IN ~REGISTRY~
    protected RegistryObject(String id, Class<T> type, ForgeConfigSpec.ConfigValue<Config> config) {
        this.id = id;
        this.type = type;
        this.config = config;
    }

    public String getId() {
        return id;
    }

    public Class<T> getType() {
        return type;
    }

    public ForgeConfigSpec.ConfigValue<Config> getConfig() {
        return config;
    }



    protected abstract boolean loadAttributesFromConfig();
}
