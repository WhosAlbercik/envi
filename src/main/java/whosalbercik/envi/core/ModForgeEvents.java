package whosalbercik.envi.core;

import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import whosalbercik.envi.ENVI;
import whosalbercik.envi.commands.EnviReloadCommand;
import whosalbercik.envi.commands.ResetUsagesCommand;
import whosalbercik.envi.commands.SpawnCustomVillagerCommand;
import whosalbercik.envi.gui.QuestMenu;
import whosalbercik.envi.registry.obj.NPC;
import whosalbercik.envi.registry.obj.Quest;
import whosalbercik.envi.registry.NPCRegistry;
import whosalbercik.envi.registry.QuestRegistry;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid= ENVI.MODID, bus= Mod.EventBusSubscriber.Bus.FORGE)
public class ModForgeEvents {

    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
    }

    @SubscribeEvent
    public static void entityRightClick(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget().getPersistentData().contains("envi.npc") && !event.getLevel().isClientSide) {

            Player p = event.getEntity();
            NPC npc = NPCRegistry.getNPC(event.getTarget().getPersistentData().getString("envi.id"));


            if (npc == null) {
                event.getEntity().sendSystemMessage(Component.literal("NPC named " + event.getTarget().getPersistentData().getString("envi.id") + " cannot be found in registry!"));
                return;
            }

            ArrayList<Quest> quests = QuestRegistry.getQuests(npc.getQuests());

            // abstract container that will hold all the gui elements
            SimpleContainer abstractContainer = new SimpleContainer(54);

            QuestMenu menu = new QuestMenu(1, p.getInventory(), abstractContainer);

            menu.addQuests(quests, p);


            MenuConstructor constructor = (p_39954_, p_39955_, p_39956_) -> menu;

            SimpleMenuProvider gui = new SimpleMenuProvider(constructor, Component.literal("Quests"));



            p.openMenu(gui);
            event.setCanceled(true);

        }
    }



    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        return;
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        SpawnCustomVillagerCommand.register(event.getDispatcher());
        EnviReloadCommand.register(event.getDispatcher());
        ResetUsagesCommand.register(event.getDispatcher());
    }


}
