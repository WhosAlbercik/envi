package whosalbercik.envi.obj;

import com.electronwill.nightconfig.core.Config;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import whosalbercik.envi.config.ServerConfig;

import java.util.ArrayList;

public class Quest {
    String id;
    String title;
    String description;

    ArrayList<ItemStack> input;
    ArrayList<ItemStack> output;

    private Quest(String id) {this.id = id;}
    public static Quest load(String id) {
        Quest obj = new Quest(id);

        if (obj.loadAttributesFromConfig()) {
            return obj;
        }
        else {
            return null;
        }
    }

    private boolean loadAttributesFromConfig() {
        Config data = ServerConfig.QUESTS.get().get(id);
        if (data == null) {
            return false;}

        this.title = data.get("title");
        this.description = data.get("description");

        this.input = new ArrayList<ItemStack>();
        this.output = new ArrayList<ItemStack>();


        for (Config itemstackData: (ArrayList<Config>) data.get("input")) {
            input.add(new ItemStack(
                    ForgeRegistries.ITEMS.getValue(new ResourceLocation(((String)itemstackData.get("item")).split(":")[0], ((String)itemstackData.get("item")).split(":")[1])),
                    itemstackData.get("amount")
            ));
        }

        for (Config itemstackData: (ArrayList<Config>) data.get("output")) {
            output.add(new ItemStack(
                    ForgeRegistries.ITEMS.getValue(new ResourceLocation(((String)itemstackData.get("item")).split(":")[0], ((String)itemstackData.get("item")).split(":")[1])),
                    itemstackData.get("amount")
            ));
        }

        return id != null && title != null && description != null && input != null && output != null;
    }

    public String getTitle() {
        return title;
    }
    public String getId() {
        return id;
    }
    public ArrayList<ItemStack> getInput() {
        return input;
    }
    public ArrayList<ItemStack> getOutput() {
        return output;
    }
}
