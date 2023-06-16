package whosalbercik.envi.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class CoordinateCopier extends Item {
    public CoordinateCopier(Properties p_41383_) {
        super(p_41383_);
    }


    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.getLevel().isClientSide) {
            Minecraft.getInstance().keyboardHandler.setClipboard(ctx.getClickedPos().getX() + "," + ctx.getClickedPos().getY() + "," + ctx.getClickedPos().getZ());
            ctx.getPlayer().sendSystemMessage(Component.literal("Copied coordinates to clipboard!").withStyle(ChatFormatting.AQUA));

        }

        return InteractionResult.SUCCESS;
    }
}
