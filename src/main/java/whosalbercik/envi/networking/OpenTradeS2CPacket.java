package whosalbercik.envi.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraftforge.network.NetworkEvent;
import whosalbercik.envi.gui.TradeMenu;
import whosalbercik.envi.gui.TradeScreen;
import whosalbercik.envi.registry.TradeRegistry;
import whosalbercik.envi.registry.obj.Trade;

import java.util.function.Supplier;

public class OpenTradeS2CPacket {

    private String tradeId;

    public OpenTradeS2CPacket(String tradeId) {
        this.tradeId = tradeId;
    }

    public static void encode(OpenTradeS2CPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.tradeId);
    }

    public static OpenTradeS2CPacket decode(FriendlyByteBuf buf) {
        String id = buf.readUtf();

        return new OpenTradeS2CPacket(id);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Trade quest = TradeRegistry.getTrade(tradeId);



            Minecraft.getInstance().doRunTask(new OpenTrade());
        });
        return true;
    }

    private class OpenTrade implements Runnable {

        @Override
        public void run() {

            Trade trade = TradeRegistry.getTrade(tradeId);

            if (trade == null) trade = TradeRegistry.getTrade(tradeId);


            TradeMenu menu = new TradeMenu(60, Minecraft.getInstance().player.getInventory(), new SimpleContainer(54), trade);

            menu.addIcons();

            Minecraft.getInstance().setScreen(new TradeScreen(menu, Minecraft.getInstance().player.getInventory(), Component.literal(trade.getTitle())));
        }
    }
}
