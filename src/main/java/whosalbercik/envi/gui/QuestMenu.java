package whosalbercik.envi.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import whosalbercik.envi.core.ModMenus;
import whosalbercik.envi.registry.obj.Quest;

import java.util.ArrayList;

public class QuestMenu extends ChestMenu {
    private final ArrayList<Quest> quests = new ArrayList<Quest>();

    public QuestMenu(int id, Inventory playerInv) {
        super(ModMenus.QUEST_MENU.get(), id, playerInv, new SimpleContainer(54), 6);
    }

    public QuestMenu(int id, Inventory playerinv, Container container) {
        super(ModMenus.QUEST_MENU.get(), id, playerinv, container, 6);
    }

    public void addQuests(ArrayList<Quest> quests, Player player) {
        SimpleContainer container = (SimpleContainer) this.getContainer();
        for (Quest quest: quests) {
            container.setItem(10 + (quests.indexOf(quest) == 7 ? 8 : quests.indexOf(quest)), quest.getIcon((ServerPlayer) player));
        }

        // decor
        container.setItem(0, new ItemStack(Items.PINK_STAINED_GLASS_PANE));
        container.setItem(1, new ItemStack(Items.RED_STAINED_GLASS_PANE));
        container.setItem(7, new ItemStack(Items.RED_STAINED_GLASS_PANE));
        container.setItem(8, new ItemStack(Items.PINK_STAINED_GLASS_PANE));

        container.setItem(9, new ItemStack(Items.RED_STAINED_GLASS_PANE));
        container.setItem(17, new ItemStack(Items.RED_STAINED_GLASS_PANE));

        container.setItem(36, new ItemStack(Items.RED_STAINED_GLASS_PANE));
        container.setItem(44, new ItemStack(Items.RED_STAINED_GLASS_PANE));

        container.setItem(45, new ItemStack(Items.PINK_STAINED_GLASS_PANE));
        container.setItem(46, new ItemStack(Items.RED_STAINED_GLASS_PANE));
        container.setItem(52, new ItemStack(Items.RED_STAINED_GLASS_PANE));
        container.setItem(53, new ItemStack(Items.PINK_STAINED_GLASS_PANE));

    }

}
