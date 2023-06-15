package whosalbercik.envi.networking;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import whosalbercik.envi.handlers.ModPacketHandler;
import whosalbercik.envi.registry.TradeRegistry;
import whosalbercik.envi.registry.obj.Trade;

import java.util.function.Supplier;

public class CompleteTradeCS2Packet {
    private final String tradeId;
    private final int multiplier;

    public CompleteTradeCS2Packet(String id, int multiplier) {
        this.tradeId = id;
        this.multiplier = multiplier;
    }

    public static void encode(CompleteTradeCS2Packet msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.tradeId);
        buf.writeInt(msg.multiplier);
    }

    public static CompleteTradeCS2Packet decode(FriendlyByteBuf buf) {
        String id = buf.readUtf();
        int multiplier = buf.readInt();

        return new CompleteTradeCS2Packet(id, multiplier);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {

            ServerPlayer p = ctx.getSender();
            Trade trade = TradeRegistry.getTrade(tradeId);

            if (trade.getCompleteLimit() < p.getPersistentData().getInt("envi.questCount." + trade.getId()) + multiplier) {
                p.closeContainer();
                p.sendSystemMessage(Component.literal("Completing this transaction would break the limit of completions for this quest!").withStyle(ChatFormatting.DARK_RED));
                return;
            }

            if (!trade.hasRequiredItems(p, multiplier)) {
                ModPacketHandler.sendToPlayer(new OpenBookS2CPacket(tradeId, OpenBookS2CPacket.BookType.INCOMPLETE), p);
                return;
            }

            // remove input
            trade.getInput().forEach(input -> {
                p.getInventory().clearOrCountMatchingItems((itemstack) -> input.getItem().equals(itemstack.getItem()), input.getCount() * multiplier, p.getInventory());
            });

            // add output
            trade.getOutput().forEach(output -> p.getInventory().add(p.getInventory().getSlotWithRemainingSpace(output), new ItemStack(output.getItem(), output.getCount() * multiplier)));

            // add 1 to how many times quest completed
            int playerCount = p.getPersistentData().getInt("envi.questCount." + trade.getId());
            playerCount += multiplier;

            p.getPersistentData().putInt("envi.questCount." + trade.getId(), playerCount);

        });

        return true;
    }
}
