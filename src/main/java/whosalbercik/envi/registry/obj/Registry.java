package whosalbercik.envi.registry.obj;

import com.electronwill.nightconfig.core.Config;
import net.minecraftforge.common.ForgeConfigSpec;

public class Registry {

    public static RegistryObject<? extends RegistryObject> load(String id, Class<? extends RegistryObject> type, ForgeConfigSpec.ConfigValue<Config> config) {

        RegistryObject<? extends RegistryObject> obj;
        try {
            obj = type.getDeclaredConstructor(String.class, Class.class, ForgeConfigSpec.ConfigValue.class).newInstance(id, type, config);

        } catch (Exception e) {
            return null;
        }

        if (obj.loadAttributesFromConfig()) {
            return obj;
        } else {
            return null;
        }
    }

}
