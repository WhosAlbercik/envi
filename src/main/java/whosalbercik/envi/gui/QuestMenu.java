package whosalbercik.envi.gui;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import whosalbercik.envi.core.ModMenus;
import whosalbercik.envi.registry.obj.Quest;

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
        ((SimpleContainer) this.getContainer()).addItem(quest.getIcon(player));

        quests.add(quest);
    }

}
