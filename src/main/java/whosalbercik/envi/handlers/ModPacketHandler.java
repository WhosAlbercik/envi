package whosalbercik.envi.handlers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import whosalbercik.envi.ENVI;
import whosalbercik.envi.networking.*;


public class ModPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;


    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(ENVI.MODID, "quests"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        net.messageBuilder(SetQuestCS2Packet.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SetQuestCS2Packet::decode)
                .encoder(SetQuestCS2Packet::encode)
                .consumerMainThread(SetQuestCS2Packet::handle)
                .add();

        net.messageBuilder(CompleteQuestCS2Packet.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(CompleteQuestCS2Packet::decode)
                .encoder(CompleteQuestCS2Packet::encode)
                .consumerMainThread(CompleteQuestCS2Packet::handle)
                .add();

        net.messageBuilder(CompleteTradeCS2Packet.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(CompleteTradeCS2Packet::decode)
                .encoder(CompleteTradeCS2Packet::encode)
                .consumerMainThread(CompleteTradeCS2Packet::handle)
                .add();

        net.messageBuilder(QuestClickedCS2Packet.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(QuestClickedCS2Packet::decode)
                .encoder(QuestClickedCS2Packet::encode)
                .consumerMainThread(QuestClickedCS2Packet::handle)
                .add();

        net.messageBuilder(OpenBookS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenBookS2CPacket::decode)
                .encoder(OpenBookS2CPacket::encode)
                .consumerMainThread(OpenBookS2CPacket::handle)
                .add();

        net.messageBuilder(OpenTradeS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenTradeS2CPacket::decode)
                .encoder(OpenTradeS2CPacket::encode)
                .consumerMainThread(OpenTradeS2CPacket::handle)
                .add();

        INSTANCE = net;
    }


    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
