package whosalbercik.envi.registry.obj;

import com.electronwill.nightconfig.core.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import whosalbercik.envi.config.ServerConfig;
import whosalbercik.envi.handlers.ModPacketHandler;
import whosalbercik.envi.networking.CompleteTradeCS2Packet;
import whosalbercik.envi.networking.OpenTradeS2CPacket;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;

public class Trade extends Quest{

    final String description = null;
    final String completeMessage = null;

    protected Trade(String id, Class<Trade> type, ForgeConfigSpec.ConfigValue<Config> config) {
        super(id, type, config);
    }




    @Override
    protected boolean loadAttributesFromConfig() {
        Config tradeData = ServerConfig.TRADES.get().get(id);
        if (tradeData == null) {
            return false;}

        this.title = tradeData.get("title");


        this.input = new ArrayList<ItemStack>();
        this.output = new ArrayList<ItemStack>();

        this.icon = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(tradeData.get("icon"))));

        this.completeLimit = tradeData.get("completeLimit") == null ? -1 : tradeData.get("completeLimit");


        // if icon is enchanted
        if (tradeData.get("enchanted") != null) {
            Config enchantData = tradeData.get("enchanted");

            if (ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantData.get("enchant"))) != null) {
                this.icon.enchant(Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantData.get("enchant")))), enchantData.get("level"));
            }
        }

        for (Config itemstackData: (ArrayList<Config>) tradeData.get("input")) {
            ItemStack inputStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemstackData.get("item"))), itemstackData.get("amount"));

            input.add(inputStack);
        }

        for (Config itemstackData: (ArrayList<Config>) tradeData.get("output")) {
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

        return id != null && title != null && input != null && output != null && icon != null && input.stream().count() <= 5 && output.stream().count() <= 5;
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
    public void iconClicked(ServerPlayer p) {

        // limit achieved
        if (completeLimit < p.getPersistentData().getInt("envi.questCount." + id) + 1 && completeLimit != -1) {
            p.closeContainer();
            p.sendSystemMessage(Component.literal("Max usages for this quest have been achieved!").withStyle(ChatFormatting.RED));
            return;
        }

        ModPacketHandler.sendToPlayer(new OpenTradeS2CPacket(this.id), p);
    }

    @Override
    public ItemStack getBook() {
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
        this.complete(p, 1);
    }

    public void complete(@Nullable LocalPlayer p, int multiplier) {
        ModPacketHandler.sendToServer(new CompleteTradeCS2Packet(id, multiplier));
    }

    public boolean hasRequiredItems(Player p, int multiplier) {
        for (ItemStack input: this.input) {
            // if less items than required
            if (p.getInventory().clearOrCountMatchingItems((itemStack -> itemStack.getItem().equals(input.getItem())), 0, p.getInventory()) < (input.getCount() * multiplier)) {
                return false;
            }
        }
        return true;
    }

    public ItemStack notCompletedQuestBook(int multiplier) {
        ItemStack abstractBook = new ItemStack(Items.WRITTEN_BOOK);

        ListTag pages = new ListTag();
        StringBuilder text = new StringBuilder("You do not have all the required items yet!\nRequired Items:");

        for (ItemStack input: this.getInput()) {
            text.append(String.format("\n%sx %s", input.getCount() * multiplier, input.getItem().getName(input).getString()));
        }

        pages.add(StringTag.valueOf(text.toString()));

        abstractBook.getOrCreateTag().put("pages", pages);
        abstractBook.getOrCreateTag().put("title", StringTag.valueOf(this.getTitle()));
        abstractBook.getOrCreateTag().put("author", StringTag.valueOf(this.getId()));

        return abstractBook;
    }
}
