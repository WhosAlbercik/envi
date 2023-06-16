package whosalbercik.envi.core;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import whosalbercik.envi.ENVI;
import whosalbercik.envi.commands.EnviReloadCommand;
import whosalbercik.envi.commands.ResetUsagesCommand;
import whosalbercik.envi.commands.SpawnCustomVillagerCommand;
import whosalbercik.envi.gui.QuestMenu;
import whosalbercik.envi.registry.AreaRegistry;
import whosalbercik.envi.registry.obj.Area;
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
        for (Area area: AreaRegistry.getAreas()) {
            if (event.side == LogicalSide.SERVER && area.isInArea(event.player.blockPosition()) && !area.canEnter((ServerPlayer) event.player)) {
                event.player.moveTo(area.getTeleportTo().get().getCenter());

                if (area.getEntering() == Area.Entering.AFTERQUEST) {
                    event.player.sendSystemMessage(Component.literal("Complete " + area.getUnlockQuest().get().getTitle() + " To access this area").withStyle(ChatFormatting.RED));
                } else {
                    event.player.sendSystemMessage(Component.literal("Cannot access this area!").withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    @SubscribeEvent
    public static void breakBlock(BlockEvent.BreakEvent event) {
        for (Area area: AreaRegistry.getAreas()) {
            if (!event.getLevel().isClientSide() && area.isInArea(event.getPos()) && !area.canBreak((ServerPlayer) event.getPlayer())) {
                event.setCanceled(true);

                if (area.getBreaking() == Area.Breaking.AFTERQUEST) {
                    event.getPlayer().sendSystemMessage(Component.literal("Complete " + area.getUnlockQuest().get().getTitle() + " To break blocks").withStyle(ChatFormatting.RED));
                } else {
                    event.getPlayer().sendSystemMessage(Component.literal("Cannot break blocks in this area!").withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    @SubscribeEvent
    public static void placeBlock(BlockEvent.EntityPlaceEvent event) {
        for (Area area: AreaRegistry.getAreas()) {
            if (event.getEntity() instanceof ServerPlayer && area.isInArea(event.getPos()) && !area.canPlace((ServerPlayer) event.getEntity())) {
                event.setCanceled(true);

                if (area.getPlacing() == Area.Placing.AFTERQUEST) {
                    event.getEntity().sendSystemMessage(Component.literal("Complete " + area.getUnlockQuest().get().getTitle() + " To place blocks").withStyle(ChatFormatting.RED));
                } else {
                    event.getEntity().sendSystemMessage(Component.literal("Cannot place blocks in this area!").withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        SpawnCustomVillagerCommand.register(event.getDispatcher());
        EnviReloadCommand.register(event.getDispatcher());
        ResetUsagesCommand.register(event.getDispatcher());
    }


}
