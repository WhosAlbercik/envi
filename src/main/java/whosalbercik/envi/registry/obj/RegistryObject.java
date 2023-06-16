package whosalbercik.envi.registry.obj;

import com.electronwill.nightconfig.core.Config;
import net.minecraftforge.common.ForgeConfigSpec;


public abstract class RegistryObject<T extends RegistryObject> {

    private final String id;
    private final Class<T> type;
    private final Config config;

    // CONSTRUCTOR MUST MATCH THE ONE IN ~REGISTRY~
    protected RegistryObject(String id, Class<T> type, ForgeConfigSpec.ConfigValue<Config> config) {
        this.id = id;
        this.type = type;
        this.config = config.get();
    }

    public String getId() {
        return id;
    }

    public Class<T> getType() {
        return type;
    }

    protected Config getConfig() {
        return config;
    }



    protected abstract boolean loadAttributesFromConfig(Config config);
}
