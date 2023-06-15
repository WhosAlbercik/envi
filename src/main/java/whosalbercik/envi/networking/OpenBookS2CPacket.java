package whosalbercik.envi.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import whosalbercik.envi.registry.QuestRegistry;
import whosalbercik.envi.registry.TradeRegistry;
import whosalbercik.envi.registry.obj.Quest;

import java.util.function.Supplier;

public class OpenBookS2CPacket {

    private final String questID;
    private final BookType type;

    public OpenBookS2CPacket(String questID, BookType type) {
        this.questID = questID;
        this.type = type;
    }

    public static void encode(OpenBookS2CPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.questID);
        buf.writeEnum(msg.type);
    }

    public static OpenBookS2CPacket decode(FriendlyByteBuf buf) {
        String id = buf.readUtf();
        BookType type = buf.readEnum(BookType.class);

        return new OpenBookS2CPacket(id, type);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Quest quest = QuestRegistry.getQuest(questID);

            if (quest == null) quest = TradeRegistry.getTrade(questID);


            Minecraft.getInstance().doRunTask(new OpenBook());
        });
        return true;
    }

    private class OpenBook implements Runnable {

        @Override
        public void run() {

            Quest quest = QuestRegistry.getQuest(questID);

            if (quest == null) quest = TradeRegistry.getTrade(questID);

            switch (type) {
                case DESCRIPTION -> Minecraft.getInstance().setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(quest.getBook())));
                case COMPLETE -> Minecraft.getInstance().setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(quest.completedQuestBook())));
                case INCOMPLETE -> Minecraft.getInstance().setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(quest.notCompletedQuestBook())));


            }
        }
    }
    public enum BookType {
        DESCRIPTION,
        COMPLETE,
        INCOMPLETE,
    }
}
