package whosalbercik.envi.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetQuestCS2Packet {
    private String questID;

    public SetQuestCS2Packet(String questID) {
        this.questID = questID;
    }

    public static void encode(SetQuestCS2Packet msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.questID);
    }

    public static SetQuestCS2Packet decode(FriendlyByteBuf buf) {
        String data = buf.readUtf();

        return new SetQuestCS2Packet(data);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            ServerLevel level = player.getLevel();

            player.getPersistentData().putString("envi.currentQuest", this.questID);

        });
        return true;
    }

}
