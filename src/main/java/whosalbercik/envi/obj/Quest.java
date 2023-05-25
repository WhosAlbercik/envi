package whosalbercik.envi.obj;

import com.electronwill.nightconfig.core.Config;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import whosalbercik.envi.config.ServerConfig;
import whosalbercik.envi.handlers.ModPacketHandler;
import whosalbercik.envi.networking.CompleteQuestCS2Packet;
import whosalbercik.envi.networking.SetQuestCS2Packet;

import java.util.ArrayList;

public class Quest {
    String id;
    String title;


    String description;



    String completeMessage;

    ArrayList<ItemStack> input;
    ArrayList<ItemStack> output;
    ItemStack icon;

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
        this.completeMessage = data.get("completeMessage");

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

        return id != null && title != null && description != null && input != null && output != null && icon != null && completeMessage != null;
    }

    public ItemStack getBook() {
        ItemStack abstractBook = new ItemStack(Items.WRITTEN_BOOK);

        ListTag pages = new ListTag();
        pages.add(StringTag.valueOf(this.getDescription()));

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
    public ItemStack getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public String getCompleteMessage() {
        return completeMessage;
    }


}
