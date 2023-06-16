package whosalbercik.envi.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import whosalbercik.envi.registry.QuestRegistry;
import whosalbercik.envi.registry.obj.Quest;

import java.util.Collection;

public class ResetUsagesCommand {

    public static void register(CommandDispatcher<CommandSourceStack> stack) {
        stack.register(Commands.literal("resetUsages")
                .then(Commands.argument("target", EntityArgument.players())
                        .then(Commands.argument("quest", StringArgumentType.word()).executes((ctx) ->resetUsages(ctx, EntityArgument.getPlayers(ctx, "target"))))));


    }

    private static int resetUsages(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> players) {
        Quest quest = QuestRegistry.getQuest(ctx.getArgument("quest", String.class));

        for (ServerPlayer p: players) {
            p.getPersistentData().putInt("envi.questCount." + quest.getId(), 0);

            ListTag tag = p.getPersistentData().getList("envi.completedQuests", 8);
            tag.remove(StringTag.valueOf(quest.getId()));
            p.getPersistentData().put("envi.completedQuests", tag);
        }

        ctx.getSource().sendSuccess(Component.literal("Successfully reset usages for quest " + quest.getId()).withStyle(ChatFormatting.AQUA), true);
        return 0;
    }
}
