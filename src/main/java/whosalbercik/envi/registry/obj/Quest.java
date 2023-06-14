package whosalbercik.envi.registry.obj;

import com.electronwill.nightconfig.core.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import whosalbercik.envi.config.ServerConfig;
import whosalbercik.envi.handlers.ModPacketHandler;
import whosalbercik.envi.networking.CompleteQuestCS2Packet;
import whosalbercik.envi.networking.OpenBookS2CPacket;
import whosalbercik.envi.networking.SetQuestCS2Packet;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;

public class Quest extends RegistryObject<Quest> {
    String title;

    String description;

    Integer completeLimit;

    protected Quest(String id, Class<? extends Quest> type, ForgeConfigSpec.ConfigValue<Config> config) {
        super(id, (Class<Quest>) type, config);
    }


    String completeMessage;

    ArrayList<ItemStack> input;
    ArrayList<ItemStack> output;
    ItemStack icon;



    protected boolean loadAttributesFromConfig() {
        Config questData = ServerConfig.QUESTS.get().get(id);
        if (questData == null) {
            return false;}

        this.title = questData.get("title");
        this.description = questData.get("description");
        this.completeMessage = questData.get("completeMessage");

        this.completeLimit = questData.get("completeLimit") == null ? -1 : questData.get("completeLimit");


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


            // enchant input
            if (itemstackData.get("enchanted") != null) {
                Config enchantData = itemstackData.get("enchanted");

                if (ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantData.get("enchant"))) != null) {
                    inputStack.enchant(Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantData.get("enchant")))), enchantData.get("level") == null ? 1 : enchantData.get("level"));
                }
            }

            if (itemstackData.get("durability") != null) {
                inputStack.getOrCreateTag().put("durability", IntTag.valueOf(itemstackData.get("durability")));
            }

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

        return id != null && title != null && description != null && input != null && output != null && icon != null && completeMessage != null && completeLimit != null;
    }

    public ItemStack getBook() {
        ItemStack abstractBook = new ItemStack(Items.WRITTEN_BOOK);

        ListTag pages = new ListTag();

        // two pages
        if (this.description.length() > 256 && this.description.length() < 256 * 2) {
            pages.add(StringTag.valueOf(StringUtils.substring(this.description, 0,  256)));
            pages.add(StringTag.valueOf(StringUtils.substring(this.description, 256,  this.description.length())));

            // three pages
        } else if (this.description.length() > (256 * 2)) {
            pages.add(StringTag.valueOf(StringUtils.substring(this.description, 0,  256)));
            pages.add(StringTag.valueOf(StringUtils.substring(this.description, 256 ,  256 * 2)));
            pages.add(StringTag.valueOf(StringUtils.substring(this.description, 256 * 2 ,  this.description.length())));


            // one page
        } else {
            pages.add(StringTag.valueOf(this.getDescription()));
        }

        abstractBook.getOrCreateTag().put("pages", pages);
        abstractBook.getOrCreateTag().put("title", StringTag.valueOf(this.getTitle()));
        abstractBook.getOrCreateTag().put("author", StringTag.valueOf(this.getId()));

        return abstractBook;
    }

    public ItemStack notCompletedQuestBook() {
        ItemStack abstractBook = new ItemStack(Items.WRITTEN_BOOK);

        ListTag pages = new ListTag();
        StringBuilder text = new StringBuilder("You do not have all the required items yet!\nRequired Items:");

        for (ItemStack input: this.getInput()) {
            text.append(String.format("\n%sx %s", input.getCount(), input.getItem().getName(input).getString()));
        }

        pages.add(StringTag.valueOf(text.toString()));

        abstractBook.getOrCreateTag().put("pages", pages);
        abstractBook.getOrCreateTag().put("title", StringTag.valueOf(this.getTitle()));
        abstractBook.getOrCreateTag().put("author", StringTag.valueOf(this.getId()));

        return abstractBook;
    }

    public ItemStack completedQuestBook() {
        ItemStack abstractBook = new ItemStack(Items.WRITTEN_BOOK);

        ListTag pages = new ListTag();
        pages.add(StringTag.valueOf(this.completeMessage));

        abstractBook.getOrCreateTag().put("pages", pages);
        abstractBook.getOrCreateTag().put("title", StringTag.valueOf(this.getTitle()));
        abstractBook.getOrCreateTag().put("author", StringTag.valueOf(this.getId()));

        return abstractBook;
    }

    public boolean hasRequiredItems(Player p) {
        for (ItemStack input: this.input) {
            // if less items than required
            if (p.getInventory().clearOrCountMatchingItems((itemStack -> itemStack.getItem().equals(input.getItem())), 0, p.getInventory()) < input.getCount()) {
                return false;
            }
        }
        return true;
    }

    public void makeCurrent(@Nullable LocalPlayer p) {
        ModPacketHandler.sendToServer(new SetQuestCS2Packet(id));
    }

    public static void setQuestNone(@Nullable LocalPlayer p) {
        ModPacketHandler.sendToServer(new SetQuestCS2Packet(""));
    }

    public void complete(@Nullable LocalPlayer p) {
        ModPacketHandler.sendToServer(new CompleteQuestCS2Packet(id));
    }

    public ItemStack getIcon(ServerPlayer p) {
        ItemStack stack = icon.copy();

        stack.addTagElement("envi.gui", StringTag.valueOf("true"));
        stack.addTagElement("envi.type", StringTag.valueOf("quest"));
        stack.addTagElement("envi.id", StringTag.valueOf(id));

        MutableComponent name;

        // if quest is in progress
        if (id.equals(p.getPersistentData().getString("envi.currentQuest"))) {
            name = Component.translatable("[QUEST] " + title).withStyle(Style.EMPTY.withColor(TextColor.parseColor("#00d9fe")));
            stack.setHoverName(name);
        }

        else {
            // limit achieved
            if (completeLimit <= p.getPersistentData().getInt("envi.questCount") && completeLimit != -1) {
                stack.setHoverName(Component.translatable("[QUEST] " +title).withStyle(Style.EMPTY.withColor(TextColor.parseColor("#0edd00"))));

            } else {
                // not achieved and not started
                stack.setHoverName(Component.translatable("[QUEST] " +title).withStyle(Style.EMPTY.withColor(TextColor.parseColor("#fefd0b"))));
            }

        }




        return stack;
    }

    public ItemStack getIcon() {
        ItemStack stack = icon.copy();

        stack.addTagElement("envi.gui", StringTag.valueOf("true"));
        stack.addTagElement("envi.type", StringTag.valueOf("quest"));
        stack.addTagElement("envi.id", StringTag.valueOf(id));

        return stack;
    }

    public void iconClicked(ServerPlayer p) {
        // if clicked quest that is in progress
        if (p.getPersistentData().contains("envi.currentQuest") && p.getPersistentData().getString("envi.currentQuest").equals(id)) {
            p.closeContainer();

            // complete quest
            if (hasRequiredItems(p)) {
                complete(null);
                ModPacketHandler.sendToPlayer(new OpenBookS2CPacket(this.id, OpenBookS2CPacket.BookType.COMPLETE), p);

            } else {
                ModPacketHandler.sendToPlayer(new OpenBookS2CPacket(this.id, OpenBookS2CPacket.BookType.INCOMPLETE), p);

            }
            return;
        }
        // set new Quest
        else {
            // limit achieved
            if (completeLimit < p.getPersistentData().getInt("envi.questCount." + id)) {
                p.closeContainer();
                p.sendSystemMessage(Component.literal("Max usages for this quest have been achieved!").withStyle(ChatFormatting.RED));
                return;
            }


            p.closeContainer();
            ModPacketHandler.sendToPlayer(new OpenBookS2CPacket(this.id, OpenBookS2CPacket.BookType.DESCRIPTION), p);

            p.getPersistentData().putString("envi.currentQuest", id);
            return;
        }

    }

    public Integer getCompleteLimit() {
        return completeLimit;
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

    public String getDescription() {
        return description;
    }

    public String getCompleteMessage() {
        return completeMessage;
    }


}
