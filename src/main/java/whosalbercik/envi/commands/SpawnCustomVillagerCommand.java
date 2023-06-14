package whosalbercik.envi.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import whosalbercik.envi.config.ServerConfig;
import whosalbercik.envi.registry.obj.NPC;
import whosalbercik.envi.registry.NPCRegistry;
import whosalbercik.envi.registry.QuestRegistry;

public class SpawnCustomVillagerCommand {

    public static void register(CommandDispatcher<CommandSourceStack> stack) {
        stack.register(Commands.literal("scv").requires((ctx) -> ctx.hasPermission(ServerConfig.PERMISSION_LEVEL.get()))
                .then(Commands.argument("npc", StringArgumentType.word()).executes(SpawnCustomVillagerCommand::spawnNPC)));
    }

    private static int spawnNPC(CommandContext<CommandSourceStack> stack) {
        Player p = stack.getSource().getPlayer();

        NPC npc = NPCRegistry.getNPC(stack.getArgument("npc", String.class));

        if (npc == null) {
            stack.getSource().sendFailure(Component.translatable("No NPC found named " + stack.getArgument("npc", String.class)));
            stack.getSource().sendFailure(Component.literal("Could be registered incorrectly!"));

            return 0;
        }

        for (String questRaw : npc.getRequiredQuests()) {
            if (QuestRegistry.getQuest(questRaw) == null) {
                stack.getSource().sendFailure(Component.translatable("NPC has required quest " + questRaw + "but quest not registered in registry!"));
                return 0;
            }
        }

        if (!npc.hasRequired(p)) {
            stack.getSource().sendFailure(Component.literal("You do not have the required quests to spawn this NPC!"));
            stack.getSource().sendFailure(Component.literal("Missing Quests: " + String.join(", ", npc.getMissing(p))));
            return 0;
        }


        npc.spawnNPC(stack.getSource().getPlayer().blockPosition(), stack.getSource().getLevel());


        return 1;
    }
}
