package whosalbercik.envi.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import whosalbercik.envi.registry.QuestRegistry;
import whosalbercik.envi.registry.TradeRegistry;
import whosalbercik.envi.registry.obj.Quest;


import java.util.function.Supplier;

public class QuestClickedCS2Packet {
    private String questID;

    public QuestClickedCS2Packet(String questID) {
        this.questID = questID;
    }

    public static void encode(QuestClickedCS2Packet msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.questID);
    }

    public static QuestClickedCS2Packet decode(FriendlyByteBuf buf) {
        String id = buf.readUtf();

        return new QuestClickedCS2Packet(id);
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
