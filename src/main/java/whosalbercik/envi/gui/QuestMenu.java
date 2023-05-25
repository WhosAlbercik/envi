package whosalbercik.envi.gui;

import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import whosalbercik.envi.core.ModMenus;
import whosalbercik.envi.obj.Quest;

import java.util.ArrayList;

public class QuestMenu extends ChestMenu {
    private ArrayList<Quest> quests = new ArrayList<Quest>();

    public QuestMenu(int id, Inventory playerInv) {
        super(ModMenus.QUEST_MENU.get(), id, playerInv, new SimpleContainer(54), 6);
    }

    public QuestMenu(int id, Inventory playerinv, Container container) {
        super(ModMenus.QUEST_MENU.get(), id, playerinv, container, 6);
    }

    public void addQuest(Quest quest, Player player) {
        ItemStack stack = quest.getIcon();

        stack.addTagElement("envi.gui", StringTag.valueOf("true"));
        stack.addTagElement("envi.type", StringTag.valueOf("quest"));
        stack.addTagElement("envi.id", StringTag.valueOf(quest.getId()));

        MutableComponent name;

        // if quest is in progress
        if (quest.getId().equals(player.getPersistentData().getString("envi.currentQuest"))) {
             name = Component.translatable(quest.getTitle()).withStyle(Style.EMPTY.withColor(TextColor.parseColor("#00d9fe")));
             stack.setHoverName(name);
        }
        // if completed
        else if (player.getPersistentData().getList("envi.completedQuests", 8).contains(StringTag.valueOf(quest.getId()))){
            stack.setHoverName(Component.translatable(quest.getTitle()).withStyle(Style.EMPTY.withColor(TextColor.parseColor("#0edd00"))));
        }
        else {
            stack.setHoverName(Component.translatable(quest.getTitle()).withStyle(Style.EMPTY.withColor(TextColor.parseColor("#fefd0b"))));
        }

        ((SimpleContainer) this.getContainer()).addItem(stack);

        quests.add(quest);
    }

}
