package whosalbercik.envi.registry.obj;

import com.electronwill.nightconfig.core.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import whosalbercik.envi.config.ServerConfig;
import whosalbercik.envi.handlers.ModPacketHandler;
import whosalbercik.envi.networking.CompleteQuestCS2Packet;
import whosalbercik.envi.networking.SetQuestCS2Packet;

import java.util.ArrayList;
import java.util.Objects;

public class Quest {
    String id;
    String title;


    String description;



    String completeMessage;

    ArrayList<ItemStack> input;
    ArrayList<ItemStack> output;
    ItemStack icon;

    protected Quest(String id) {this.id = id;}
    public static Quest load(String id) {
        Quest obj = new Quest(id);

        if (obj.loadAttributesFromConfig()) {
            return obj;
        }
        else {
            return null;
        }
    }

    protected boolean loadAttributesFromConfig() {
        Config questData = ServerConfig.QUESTS.get().get(id);
        if (questData == null) {
            return false;}

        this.title = questData.get("title");
        this.description = questData.get("description");
        this.completeMessage = questData.get("completeMessage");

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

        return id != null && title != null && description != null && input != null && output != null && icon != null && completeMessage != null;
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

    public void makeCurrent(LocalPlayer p) {
        p.getPersistentData().putString("envi.currentQuest", id);
        ModPacketHandler.sendToServer(new SetQuestCS2Packet(id));
    }

    public static void setQuestNone(LocalPlayer p) {
        p.getPersistentData().putString("envi.currentQuest", "");
        ModPacketHandler.sendToServer(new SetQuestCS2Packet(""));
    }

    public void complete(LocalPlayer p) {
        p.getPersistentData().getList("envi.completedQuests", 8).add(StringTag.valueOf(id));
        p.getPersistentData().putString("envi.currentQuest", "");
        ModPacketHandler.sendToServer(new CompleteQuestCS2Packet(id));
    }

    public ItemStack getIcon(Player p) {
        ItemStack stack = icon.copy();

        stack.addTagElement("envi.gui", StringTag.valueOf("true"));
        stack.addTagElement("envi.type", StringTag.valueOf("quest"));
        stack.addTagElement("envi.id", StringTag.valueOf(id));

        MutableComponent name;

        // if quest is in progress
        if (id.equals(p.getPersistentData().getString("envi.currentQuest"))) {
            name = Component.translatable(title).withStyle(Style.EMPTY.withColor(TextColor.parseColor("#00d9fe")));
            stack.setHoverName(name);
        }
        // if completed
        else if (p.getPersistentData().getList("envi.completedQuests", 8).contains(StringTag.valueOf(id))){
            stack.setHoverName(Component.translatable(title).withStyle(Style.EMPTY.withColor(TextColor.parseColor("#0edd00"))));
        }
        else {
            stack.setHoverName(Component.translatable(title).withStyle(Style.EMPTY.withColor(TextColor.parseColor("#fefd0b"))));
        }

        return stack;
    }

    public void iconClicked(LocalPlayer p) {
        // if clicked quest that is in progress
        if (p.getPersistentData().contains("envi.currentQuest") && p.getPersistentData().getString("envi.currentQuest").equals(id)) {
            p.closeContainer();

            // complete quest
            if (hasRequiredItems(p)) {
                complete(p);
                Minecraft.getInstance().setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(completedQuestBook())));

            } else {
                Minecraft.getInstance().setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(notCompletedQuestBook())));
            }
            return;
        }
        // quest already completed
        else if (p.getPersistentData().getList("envi.completedQuests", 8) != null && p.getPersistentData().getList("envi.completedQuests", 8).contains(StringTag.valueOf(id))) {
            return;
        }

        // set new Quest
        else {

            p.closeContainer();
            Minecraft.getInstance().setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(getBook())));

            makeCurrent(p);
            return;
        }

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