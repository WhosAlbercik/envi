package whosalbercik.envi.registry.obj;

import com.electronwill.nightconfig.core.Config;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import whosalbercik.envi.config.ServerConfig;

import java.util.ArrayList;

public class Trade extends Quest{

    final String description = null;
    final String completeMessage = null;

    protected Trade(String id) {
        super(id);
    }

    public static Trade load(String id) {
        Trade obj = new Trade(id);

        if (obj.loadAttributesFromConfig()) {
            return obj;
        }
        else {
            return null;
        }
    }

    @Override
    protected boolean loadAttributesFromConfig() {
        Config data = ServerConfig.TRADES.get().get(id);
        if (data == null) {
            return false;}

        this.title = data.get("title");

        this.input = new ArrayList<ItemStack>();
        this.output = new ArrayList<ItemStack>();

        this.icon = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(((String) data.get("icon")).split(":")[0], ((String) data.get("icon")).split(":")[1])));

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

        return id != null && title != null && input != null && output != null && icon != null;
    }

    @Override
    public ItemStack getIcon(Player p) {
        ItemStack stack = icon.copy();

        stack.addTagElement("envi.gui", StringTag.valueOf("true"));
        stack.addTagElement("envi.type", StringTag.valueOf("trade"));
        stack.addTagElement("envi.id", StringTag.valueOf(id));

        MutableComponent name;
        stack.setHoverName(Component.translatable(title).withStyle(Style.EMPTY.withColor(TextColor.parseColor("#ff00fd"))));

        return stack;
    }

    @Override
    public ItemStack getBook() {
        return null;
    }

    @Override
    public ItemStack notCompletedQuestBook() {
        return null;
    }

    @Override
    public ItemStack completedQuestBook() {
        return null;
    }

    @Override
    public void makeCurrent(LocalPlayer p) {

    }

    @Override
    public void complete(LocalPlayer p) {
        // TODO
    }
}
