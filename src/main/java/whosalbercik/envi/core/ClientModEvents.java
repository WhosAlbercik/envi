package whosalbercik.envi.core;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import whosalbercik.envi.gui.QuestScreen;
import whosalbercik.envi.handlers.ModPacketHandler;
import whosalbercik.envi.registry.NPCRegistry;
import whosalbercik.envi.registry.QuestRegistry;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {

        event.enqueueWork(
                // Assume RegistryObject<MenuType<MyMenu>> MY_MENU
                // Assume MyContainerScreen<MyMenu> which takes in three parameters
                () -> MenuScreens.register(ModMenus.QUEST_MENU.get(), QuestScreen::new)
        );

    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        NPCRegistry.load();
        QuestRegistry.load();
        ModPacketHandler.register();

    }
}
