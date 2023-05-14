package whosalbercik.envi.core;

import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import whosalbercik.envi.ENVI;
import whosalbercik.envi.commands.SpawnCustomVillagerCommand;
import whosalbercik.envi.obj.NPC;
import whosalbercik.envi.obj.Quest;
import whosalbercik.envi.registry.NPCRegistry;
import whosalbercik.envi.registry.QuestRegistry;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid= ENVI.MODID, bus= Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    @SubscribeEvent
    public static void entityRightClick(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget().getPersistentData().contains("envi.npc") && !event.getLevel().isClientSide) {

            Player p = event.getEntity();
            NPC npc = NPCRegistry.getNPC(event.getTarget().getPersistentData().getString("envi.id"));
            ArrayList<Quest> quests = QuestRegistry.getQuests(npc.getQuests());

            if (npc == null) {
                event.getEntity().sendSystemMessage(Component.literal("NPC named " + event.getTarget().getPersistentData().getString("envi.id") + "cannot be found in registry!"));
                return;
            }


            // abstract container that will hold all the gui elements
            SimpleContainer abstractContainer = new SimpleContainer(54);

            // adding 'quests' to container
            for (Quest quest: quests) {
                ItemStack stack = new ItemStack(quest.getInput().get(0).getItem(), quest.getInput().get(0).getCount());

                abstractContainer.addItem(quest.getInput().get(0));
            }


            MenuConstructor constructor = new MenuConstructor() {
                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
                    return ChestMenu.sixRows(1, p.getInventory(), abstractContainer);
                }
            };

            SimpleMenuProvider gui = new SimpleMenuProvider(constructor, Component.literal("Quests"));



            p.openMenu(gui);
            event.setCanceled(true);

        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {

    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        SpawnCustomVillagerCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void setup(ServerStartedEvent event) {
        NPCRegistry.load();
        QuestRegistry.load();
    }

}
