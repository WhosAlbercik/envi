package whosalbercik.envi.networking;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import whosalbercik.envi.registry.obj.Quest;
import whosalbercik.envi.registry.QuestRegistry;

import java.util.function.Supplier;

public class CompleteQuestCS2Packet {
    private String questID;

    public CompleteQuestCS2Packet(String questID) {
        this.questID = questID;
    }

    public static void encode(CompleteQuestCS2Packet msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.questID);
    }

    public static CompleteQuestCS2Packet decode(FriendlyByteBuf buf) {
        String data = buf.readUtf();

        return new CompleteQuestCS2Packet(data);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer p = ctx.getSender();
            Quest quest = QuestRegistry.getQuest(questID);

            // remove input
            quest.getInput().forEach(input -> {
                p.getInventory().clearOrCountMatchingItems((itemstack) -> input.getItem().equals(itemstack.getItem()), input.getCount(), p.getInventory());
            });

            // add output
            quest.getOutput().forEach(output -> p.getInventory().add(p.getInventory().getSlotWithRemainingSpace(output), output.copy()));

            ListTag tag = p.getPersistentData().getList("envi.completedQuests", 9);

            tag.add(StringTag.valueOf(questID));

            p.getPersistentData().put("envi.completedQuests", tag);
            p.getPersistentData().putString("envi.currentQuest", "");

        });
        return true;
    }
}
