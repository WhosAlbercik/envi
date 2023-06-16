package whosalbercik.envi.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import whosalbercik.envi.registry.NPCRegistry;
import whosalbercik.envi.registry.QuestRegistry;
import whosalbercik.envi.registry.TradeRegistry;

import java.util.function.Supplier;

public class ReloadS2CPacket {


    public ReloadS2CPacket() {
    }

    public static void encode(ReloadS2CPacket msg, FriendlyByteBuf buf) {
    }

    public static ReloadS2CPacket decode(FriendlyByteBuf buf) {
        return new ReloadS2CPacket();
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {

            Minecraft.getInstance().doRunTask(new Runnable() {
                @Override
                public void run() {
                    QuestRegistry.load();
                    NPCRegistry.load();
                    TradeRegistry.load();
                }
            });

        });
        return true;
    }
}
