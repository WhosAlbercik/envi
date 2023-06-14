package whosalbercik.envi.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import whosalbercik.envi.registry.QuestRegistry;
import whosalbercik.envi.registry.TradeRegistry;
import whosalbercik.envi.registry.obj.Quest;

import java.util.function.Supplier;

public class IconClickedC2SPacket {
    private String questID;

    public IconClickedC2SPacket(String questID) {
        this.questID = questID;
    }

    public static void encode(IconClickedC2SPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.questID);
    }

    public static IconClickedC2SPacket decode(FriendlyByteBuf buf) {
        String id = buf.readUtf();

        return new IconClickedC2SPacket(id);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {

            Quest quest = QuestRegistry.getQuest(questID);

            if (quest == null) quest = TradeRegistry.getTrade(questID);

            quest.iconClicked(ctx.getSender());
        });
        return true;
    }
}
