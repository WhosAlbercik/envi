package whosalbercik.envi.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import whosalbercik.envi.handlers.ModPacketHandler;
import whosalbercik.envi.networking.ReloadS2CPacket;
import whosalbercik.envi.registry.NPCRegistry;
import whosalbercik.envi.registry.QuestRegistry;
import whosalbercik.envi.registry.TradeRegistry;

public class EnviReloadCommand {
    public static void register(CommandDispatcher<CommandSourceStack> stack) {
        stack.register(Commands.literal("enviReload").executes(EnviReloadCommand::reload));
    }

    private static int reload(CommandContext<CommandSourceStack> ctx) {

        QuestRegistry.load();
        NPCRegistry.load();
        TradeRegistry.load();

        ModPacketHandler.sendToPlayer(new ReloadS2CPacket(), ctx.getSource().getPlayer());

        ctx.getSource().getPlayer().sendSystemMessage(Component.literal("Successfully Reloaded Config!").withStyle(ChatFormatting.AQUA));
        return 0;
    }

}
