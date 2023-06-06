package whosalbercik.envi.registry.obj;

import com.electronwill.nightconfig.core.Config;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import whosalbercik.envi.config.ServerConfig;

import java.util.ArrayList;
import java.util.Objects;

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
        Config questData = ServerConfig.TRADES.get().get(id);
        if (questData == null) {
            return false;}

        this.title = questData.get("title");


        this.input = new ArrayList<ItemStack>();
        this.output = new ArrayList<ItemStack>();

        this.icon = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(questData.get("icon"))));


        // if icon is enchanted
        if (questData.get("enchanted") != null) {
            Config enchantData = questData.get("enchanted");

            if (ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantData.get("enchant"))) != null) {
                this.icon.enchant(Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantData.get("enchant")))), enchantData.get("level"));
            }
        }

        for (Config itemstackData: (ArrayList<Config>) questData.get("input")) {
            ItemStack inputStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemstackData.get("item"))), itemstackData.get("amount"));

            input.add(inputStack);
        }

        for (Config itemstackData: (ArrayList<Config>) questData.get("output")) {
            ItemStack outputStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemstackData.get("item"))), itemstackData.get("amount"));

            // enchant output
            if (itemstackData.get("enchanted") != null) {
                Config enchantData = itemstackData.get("enchanted");

                if (ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantData.get("enchant"))) != null) {
                    outputStack.enchant(Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantData.get("enchant")))), enchantData.get("level") == null ? 1 : enchantData.get("level"));
                }
            }

            if (itemstackData.get("durability") != null) {
                outputStack.getOrCreateTag().put("durability", IntTag.valueOf(itemstackData.get("durability")));
            }

            output.add(outputStack);
        }

        return id != null && title != null && input != null && output != null && icon != null;
    }

    @Override
    public ItemStack getIcon(ServerPlayer p) {
        ItemStack stack = icon.copy();

        stack.addTagElement("envi.gui", StringTag.valueOf("true"));
        stack.addTagElement("envi.type", StringTag.valueOf("trade"));
        stack.addTagElement("envi.id", StringTag.valueOf(id));

        stack.setHoverName(Component.translatable("[TRADE] " + title).withStyle(Style.EMPTY.withColor(TextColor.parseColor("#ff00fd"))));

        return stack;
    }

    @Override
    public void iconClicked(LocalPlayer p) {

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
