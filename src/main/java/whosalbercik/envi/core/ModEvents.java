package whosalbercik.envi.core;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import whosalbercik.envi.registry.AreaRegistry;
import whosalbercik.envi.registry.NPCRegistry;
import whosalbercik.envi.registry.QuestRegistry;
import whosalbercik.envi.registry.TradeRegistry;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
public class ModEvents {
    @SubscribeEvent
    public static void commonSetup(FMLDedicatedServerSetupEvent event) {
        NPCRegistry.load();
        QuestRegistry.load();
        TradeRegistry.load();
        AreaRegistry.load();
        ModPacketHandler.register();

    }


}
