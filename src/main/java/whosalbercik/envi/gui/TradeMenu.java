package whosalbercik.envi.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import whosalbercik.envi.core.ModMenus;
import whosalbercik.envi.registry.obj.Trade;

public class TradeMenu extends ChestMenu {
    private Trade trade;

    public TradeMenu(int id, Inventory playerInv) {
        super(ModMenus.TRADE_MENU.get(), id, playerInv, new SimpleContainer(54), 6);
    }

    public TradeMenu(int id, Inventory playerinv, Container container, Trade trade) {
        super(ModMenus.TRADE_MENU.get(), id, playerinv, container, 6);
        this.trade = trade;
    }


    public void addIcons() {
        SimpleContainer container = (SimpleContainer) this.getContainer();

        // icons
        ItemStack buy1 = new ItemStack(trade.getIcon().getItem(), 1);
        buy1.addTagElement("envi.tradeMultiplier", IntTag.valueOf(1));
        buy1.addTagElement("envi.id", StringTag.valueOf(trade.getId()));
        buy1.setHoverName(Component.literal("BUY x1").withStyle(ChatFormatting.AQUA));

        ItemStack buy10 = new ItemStack(trade.getIcon().getItem(), 10);
        buy10.addTagElement("envi.tradeMultiplier", IntTag.valueOf(10));
        buy10.addTagElement("envi.id", StringTag.valueOf(trade.getId()));
        buy10.setHoverName(Component.literal("BUY x10").withStyle(ChatFormatting.AQUA));

        ItemStack buy50 = new ItemStack(trade.getIcon().getItem(), 50);
        buy50.addTagElement("envi.tradeMultiplier", IntTag.valueOf(50));
        buy50.addTagElement("envi.id", StringTag.valueOf(trade.getId()));
        buy50.setHoverName(Component.literal("BUY x50").withStyle(ChatFormatting.AQUA));

        container.setItem(19, buy1);
        container.setItem(22, buy10);
        container.setItem(25, buy50);



        // input
        for (ItemStack stack: trade.getInput()) {
            ItemStack gui = stack.copy();
            gui.setHoverName(Component.literal("[INPUT] ").append(gui.getHoverName()).withStyle(ChatFormatting.DARK_RED));
            container.setItem(2 + trade.getInput().indexOf(stack), gui);
        }

        // output
        for (ItemStack stack: trade.getOutput()) {
            ItemStack gui = stack.copy();
            gui.setHoverName(Component.literal("[OUTPUT] ").append(gui.getHoverName()).withStyle(ChatFormatting.DARK_GREEN));
            container.setItem(47 + trade.getOutput().indexOf(stack), gui);
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
